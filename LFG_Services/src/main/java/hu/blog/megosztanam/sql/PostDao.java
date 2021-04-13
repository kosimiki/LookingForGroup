package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.OpenPosition;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.sql.mapper.PostRowMapper;
import hu.blog.megosztanam.sql.mapper.RoleRowMapper;
import hu.blog.megosztanam.sql.mapper.SearchForMemberPostRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Repository
public class PostDao {

    private final SearchForMemberPostRowMapper rowMapper;
    private final PostRowMapper simplePostMapper;
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert insert;

    @Autowired
    public PostDao(SearchForMemberPostRowMapper rowMapper,
                   PostRowMapper simplePostMapper,
                   JdbcTemplate simpleTemplate) {
        this.rowMapper = rowMapper;
        this.simplePostMapper = simplePostMapper;
        this.insert = new SimpleJdbcInsert(simpleTemplate).withTableName("looking_for_member").usingGeneratedKeyColumns("id");
        this.template = new NamedParameterJdbcTemplate(simpleTemplate);
    }

    private static final String INSERT_OPEN_ROLES =
            "INSERT INTO open_position (looking_for_member_id, role) " +
                    "VALUES (:postId, :role)";

    private static final String SELECT_OPEN_ROLES =
            "SELECT role FROM open_position WHERE looking_for_member_id = :looking_for_member_id";

    private static final String SELECT_LFM_POST =
            "SELECT l.*, :queryUser as query_user,  COALESCE(a.user_id, 0) as applied " +
                    " FROM looking_for_member l" +
                    " LEFT JOIN applications a on l.id = a.post_id AND a.user_id = :queryUser" +
                    " WHERE l.server = :server " +
                    "AND (:map is null or map = :map) " +
                    "AND (:isRanked is null or ranked = :isRanked) " +
                    " GROUP BY l.id, l.map, l.ranked, l.min_tier, l.max_tier, l.min_div, l.max_div, l.description, l.created_at, :queryUser, COALESCE(a.user_id, 0) " +
                    " ORDER BY created_at DESC";

    private static final String SELECT_LFM_POST_BY_ID =
            "SELECT * FROM looking_for_member WHERE id = :postId ";

    private static final String DELETE_POST =
            "DELETE FROM looking_for_member where id = :postId";
    private static final String DELETE_POSITIONS =
            "DELETE FROM open_position where looking_for_member_id = :postId";
    private static final String DELETE_APPLICATION =
            "DELETE FROM applications where post_id = :postId AND user_id = :userId";

    public Integer getOwnerId(Integer postId) {
        return template.queryForObject("SELECT user_id FROM looking_for_member WHERE id = :postId", new MapSqlParameterSource("postId", postId), Integer.class);
    }


    public Integer savePost(Post post) {
        Boolean isRanked = post.getGameType().isRanked();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("server", post.getServer().getValue())
                .addValue("user_id", post.getUserId())
                .addValue("map", post.getGameType().getMap().getValue())
                .addValue("ranked", post.getGameType().isRanked())
                .addValue("min_tier", isRanked ? post.getMinimumRank().getTier().getValue() : null)
                .addValue("max_tier", isRanked ? post.getMaximumRank().getTier().getValue() : null)
                .addValue("min_div", isRanked ? post.getMinimumRank().getDivision().getValue() : null)
                .addValue("max_div", isRanked ? post.getMaximumRank().getDivision().getValue() : null)
                .addValue("description", post.getDescription())
                .addValue("post_type", post.getPostType().getValue())
                .addValue("persistent", post.getPersistent())
                .addValue("created_at", new Date());
        Integer postId = insert.executeAndReturnKey(parameters).intValue();

        Object[] params = post.getOpenPositions()
                .stream()
                .map(role -> new OpenPosition(postId, role.getValue()))
                .toArray();
        template.batchUpdate(INSERT_OPEN_ROLES, SqlParameterSourceUtils.createBatch(params));
        return postId;
    }

    public List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("server", server.getValue())
                .addValue("queryUser", userId)
                .addValue("map", Optional.ofNullable(map).map(GameMap::getValue).orElse(null), JDBCType.VARCHAR.getVendorTypeNumber())
                .addValue("isRanked", Optional.ofNullable(isRanked).orElse(null), JDBCType.BOOLEAN.getVendorTypeNumber());

        List<Post> posts = template.query(SELECT_LFM_POST, parameterSource, rowMapper);
        posts.forEach(post -> {
            post.setOpenPositions(template.query(SELECT_OPEN_ROLES, new MapSqlParameterSource("looking_for_member_id", post.getPostId()), new RoleRowMapper()));
        });
        return posts;
    }

    @Transactional
    public Boolean deletePost(Integer postId, Integer userId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId);
        return template.update(DELETE_POST, parameterSource) > 0 && template.update(DELETE_POSITIONS, parameterSource) > 0 && template.update(DELETE_APPLICATION, parameterSource) > 0;
    }

    public Post getPostById(Integer postId) {
        Post post = template.queryForObject(SELECT_LFM_POST_BY_ID, new MapSqlParameterSource("postId", postId), simplePostMapper);
        post.setOpenPositions(template.query(SELECT_OPEN_ROLES, new MapSqlParameterSource("looking_for_member_id", post.getPostId()), new RoleRowMapper()));
        return post;
    }
}

