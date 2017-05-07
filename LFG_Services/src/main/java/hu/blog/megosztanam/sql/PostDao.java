package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.shared.OpenPosition;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.sql.mapper.RoleRowMapper;
import hu.blog.megosztanam.sql.mapper.SearchForMemberPostRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Repository
public class PostDao {

    @Autowired
    private SearchForMemberPostRowMapper rowMapper;
    private NamedParameterJdbcTemplate template;
    private SimpleJdbcInsert insert;
    private JdbcTemplate simpleTemplate;

    @Autowired
    public PostDao(JdbcTemplate simpleTemplate){
        this.insert = new SimpleJdbcInsert(simpleTemplate).withTableName("looking_for_member").usingGeneratedKeyColumns("id");
        this.simpleTemplate = simpleTemplate;
        this.template = new NamedParameterJdbcTemplate(simpleTemplate);
    }

    private static final String INSERT_OPEN_ROLES =
            "INSERT INTO open_position (looking_for_member_id, role) " +
                        "VALUES (:postId, :role)";

    private static final String SELECT_OPEN_ROLES =
            "SELECT role FROM open_position WHERE looking_for_member_id = :looking_for_member_id";

    private static final String SELECT_LFM_POST =
            "SELECT id, server, summoner_id,   map,  ranked,  min_tier,  max_tier,  min_div,  max_div,  description, created_at, user_id, persistent, post_type FROM looking_for_member";


    public Integer savePost(Post post){
        Boolean isRanked = post.getGameType().isRanked();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("summoner_id", post.getOwner().getId())
                .addValue("server",      post.getServer().getValue()) //todo save issue
                .addValue("user_id",     post.getUserId())
                .addValue("map",         post.getGameType().getMap().getValue())
                .addValue("ranked",      post.getGameType().isRanked())
                .addValue("min_tier",    isRanked ?post.getMinimumRank().getTier().getValue():null)
                .addValue("max_tier",    isRanked ?post.getMaximumRank().getTier().getValue():null)
                .addValue("min_div",     isRanked ?post.getMinimumRank().getDivision().getValue():null)
                .addValue("max_div",     isRanked ?post.getMaximumRank().getDivision().getValue():null)
                .addValue("description", post.getDescription())
                .addValue("post_type",   post.getPostType().getValue())
                .addValue("persistent",  post.getPersistent())
                .addValue("created_at", new Date());
        Integer postId = insert.executeAndReturnKey(parameters).intValue();

        List<OpenPosition> openPositions = post.getOpenPositions().stream().map(role -> new OpenPosition(postId, role.getValue())).collect(Collectors.toList());
        template.batchUpdate(INSERT_OPEN_ROLES, SqlParameterSourceUtils.createBatch(openPositions.toArray()));
        return postId;
    }

    public List<Post> getSearchForMemberPosts(){
        List<Post> posts = simpleTemplate.query(SELECT_LFM_POST, rowMapper);
        posts.forEach( post -> {
            post.setOpenPositions(template.query(SELECT_OPEN_ROLES, new MapSqlParameterSource("looking_for_member_id", post.getPostId()), new RoleRowMapper()));
        });
        return posts;
    }
}

