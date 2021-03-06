package hu.blog.megosztanam.sql.extractor;

import hu.blog.megosztanam.cache.SummonerCache;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.sql.mapper.PostRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Miklós on 2017. 05. 14..
 */
@Service
public class ApplicationExtractor implements ResultSetExtractor<List<PostApplyResponse>> {


    private final SummonerCache summonerCache;
    private final PostRowMapper postRowMapper;

    public ApplicationExtractor(SummonerCache summonerCache, PostRowMapper postRowMapper) {
        this.summonerCache = summonerCache;
        this.postRowMapper = postRowMapper;
    }

    @Override
    public List<PostApplyResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<PostApplyResponse> result = new ArrayList<>();
        Map<Ids, List<Role>> map = new HashMap<>();
        Map<Integer, Boolean> postAccepted = new HashMap<>();
        while (rs.next()) {
            Ids ids = new Ids();
            ids.userId = rs.getInt("user_id");
            ids.post = postRowMapper.mapRow(rs, 0);
            ids.summonerId = rs.getString("summoner_id");
            ids.server = Server.valueOf(rs.getString("region").replaceAll(" ", ""));
            ids.applicationDate = rs.getTimestamp("date_of_application");
            map.computeIfAbsent(ids, key -> new ArrayList<>());
            map.get(ids).add(Role.valueOf(rs.getString("role")));
            postAccepted.put(ids.post.getPostId(), rs.getBoolean("accepted"));
        }
        map.forEach((key, value) -> {
            PostApplyResponse response = new PostApplyResponse();
            SummonerGameStatistics gameStatistics = summonerCache.getRank(key.summonerId, key.server);
            Summoner summoner = summonerCache.getSummonerBySummonerId(key.summonerId, key.server);

            response.setFlexRank(gameStatistics.getFlexRank());
            response.setSoloRank(gameStatistics.getSoloRank());
            response.setAccepted(postAccepted.get(key.post.getPostId()));
            response.setPost(key.post);
            response.setUserId(key.userId);
            response.setCreatedAt(key.applicationDate);

            response.setSummonerName(summoner.getName());
            response.setSummonerLevel(summoner.getSummonerLevel());

            response.setRoles(value);
            result.add(response);
        });
        return result;
    }

    private static class Ids {
        int userId;
        Post post;
        String summonerId;
        Server server;
        Date applicationDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ids ids = (Ids) o;
            return userId == ids.userId &&
                    Objects.equals(post, ids.post) &&
                    Objects.equals(summonerId, ids.summonerId) &&
                    server == ids.server &&
                    Objects.equals(applicationDate, ids.applicationDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, post, summonerId, server, applicationDate);
        }
    }
}
