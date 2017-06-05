package hu.blog.megosztanam.sql.mapper;


import hu.blog.megosztanam.cache.SummonerCache;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.GameType;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.PostType;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Rank;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Miklós on 2017. 04. 20..
 */

@Component
public class SearchForMemberPostRowMapper implements RowMapper<Post> {

    @Autowired
    private SummonerCache summonerCache;

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setServer(Server.valueOf(rs.getString("server")));
        post.setPostId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setOwner(summonerCache.get(rs.getInt("summoner_id"), post.getServer()));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setDescription(rs.getString("description"));
        post.setGameType(new GameType(GameMap.valueOf(rs.getString("map")), rs.getBoolean("ranked")));
        if(post.getGameType().isRanked()){
            post.setMinimumRank( new Rank(Tier.valueOf(rs.getString("min_tier")), Division.valueOf(rs.getString("min_div"))));
            post.setMaximumRank( new Rank(Tier.valueOf(rs.getString("max_tier")), Division.valueOf(rs.getString("max_div"))));
        }
        post.setPostId(rs.getInt("id"));
        post.setPersistent(rs.getBoolean("persistent"));
        post.setPostType(PostType.valueOf(rs.getString("post_type")));

        Boolean isOwner = post.getUserId() == rs.getInt("query_user");
        Boolean alreadyApplied = rs.getBoolean("applied");
        post.setIsOwner(isOwner);
        post.setCanApply(!isOwner && !alreadyApplied);
        return post;
    }
}
