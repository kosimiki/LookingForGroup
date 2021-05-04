package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Rank;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.model.shared.summoner.Servers;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.RiotFeignClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;

/**
 * Created by Miklós on 2016. 11. 27..
 */
@Service
public class SummonerServiceImpl implements ISummonerService {
    public static final String RIOT_GAMES = "riotgames.com";
    private static final String SERVER_HOST = "https://%s.api." + RIOT_GAMES;
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonerServiceImpl.class);

    private final RiotFeignClient riotFeignClient;
    private final Rank unRanked;

    public SummonerServiceImpl(RiotFeignClient riotFeignClient) {
        this.riotFeignClient = riotFeignClient;
        unRanked = new Rank();
        unRanked.setDivision(Division.I);
        unRanked.setTier(Tier.UNRANKED);
    }

    @Override
    public Summoner getSummoner(String summonerName, Server server) {
        URI serverHost = getServerHost(server);
        return riotFeignClient.getSummonerByName(serverHost, summonerName);
    }

    @Override
    public Summoner getById(String summonerId, Server server) {
        URI serverHost = getServerHost(server);
        return riotFeignClient.getSummonerById(serverHost, summonerId);
    }

    @Override
    public SummonerGameStatistics getStatistics(String summonerId, Server server) {
        String json = null;
        try {
            URI serverHost = getServerHost(server);
            json = riotFeignClient.getStatsById(serverHost, summonerId);
        } catch (HttpClientErrorException exception) {
            LOGGER.warn("Failed to get competitive statistics", exception);
        }
        if (json != null) {
            return getSummonerGameStatistics(summonerId, json);
        } else {
            return getSummonerGameStatistics();
        }
    }

    private SummonerGameStatistics getSummonerGameStatistics(String summonerId, String json) {
        SummonerGameStatistics gameStatistics = getSummonerGameStatistics();
        JSONArray types = new JSONArray(json);
        for (int i = 0; i < types.length(); i++) {
            JSONObject type = types.getJSONObject(i);
            Tier tier = Tier.valueOf(type.getString("tier"));
            String queue = type.getString("queueType");
            Division division = Division.valueOf(type.getString("rank"));
            gameStatistics.setSummonerName(type.getString("summonerName"));
            Rank rank = new Rank();
            rank.setDivision(division);
            rank.setTier(tier);
            if (queue != null && queue.contains("RANKED_SOLO_5x5")) {
                gameStatistics.setSoloRank(rank);
            }
            if (queue != null && queue.contains("RANKED_FLEX_SR")) {
                gameStatistics.setFlexRank(rank);
            }
        }
        return gameStatistics;
    }

    private URI getServerHost(Server server) {
        return URI.create(String.format(SERVER_HOST, Servers.getServerV3(server)));
    }


    private SummonerGameStatistics getSummonerGameStatistics() {
        SummonerGameStatistics gameStatistics = new SummonerGameStatistics();
        gameStatistics.setTwistedRank(unRanked);
        gameStatistics.setFlexRank(unRanked);
        gameStatistics.setSoloRank(unRanked);
        return gameStatistics;
    }
}
