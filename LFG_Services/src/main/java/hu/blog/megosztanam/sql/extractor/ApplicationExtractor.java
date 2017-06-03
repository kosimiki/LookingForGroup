package hu.blog.megosztanam.sql.extractor;

import hu.blog.megosztanam.cache.SummonerCache;
import hu.blog.megosztanam.cache.SummonerRankCache;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.sql.mapper.PostRowMapper;
import hu.blog.megosztanam.sql.mapper.SearchForMemberPostRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Miklós on 2017. 05. 14..
 */
@Service
public class ApplicationExtractor implements ResultSetExtractor<List<PostApplyResponse>> {


    @Autowired
    SummonerRankCache rankCache;

    @Autowired
    SummonerCache summonerCache;

    @Autowired
    PostRowMapper postRowMapper;

    @Override
    public List<PostApplyResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<PostApplyResponse> result = new ArrayList<>();
        Map<Ids, List<Role>> map = new HashMap<>();
        while (rs.next()){
            Ids ids = new Ids();
            ids.userId = rs.getInt("user_id");
            ids.post = postRowMapper.mapRow(rs,0);
            ids.summonerId = rs.getInt("summoner_id");
            ids.server = Server.valueOf(rs.getString("region"));
            ids.applicationDate = rs.getTimestamp("date_of_application");
            map.computeIfAbsent(ids, key-> new ArrayList<>());
            map.get(ids).add(Role.valueOf(rs.getString("role")));
        }
        map.forEach((key, value) -> {
            PostApplyResponse response = new PostApplyResponse();
            SummonerGameStatistics gameStatistics = rankCache.get(key.summonerId, key.server);
            Summoner summoner = summonerCache.get(key.summonerId, key.server);

            response.setFlexRank(gameStatistics.getFlexRank());
            response.setSoloRank(gameStatistics.getSoloRank());
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

    private class Ids{
        int userId;
        Post post;
        int summonerId;
        Server server;
        Date applicationDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Ids ids = (Ids) o;

            if (userId != ids.userId) return false;
            if (summonerId != ids.summonerId) return false;
            if (!post.equals(ids.post)) return false;
            if (server != ids.server) return false;
            return applicationDate.equals(ids.applicationDate);
        }

        @Override
        public int hashCode() {
            int result = userId;
            result = 31 * result + post.hashCode();
            result = 31 * result + summonerId;
            result = 31 * result + server.hashCode();
            result = 31 * result + applicationDate.hashCode();
            return result;
        }
    }
}
