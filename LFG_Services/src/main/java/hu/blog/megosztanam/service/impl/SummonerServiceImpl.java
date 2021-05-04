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
    private static final String SERVER_HOST = "https.%s.api.riotgames.com";
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonerServiceImpl.class);

    @Value("${lol.api.key}")
    private String apiKey;

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
        return riotFeignClient.getSummonerByName(serverHost, summonerName, apiKey);
    }

    @Override
    public Summoner getById(String summonerId, Server server) {
        URI serverHost = getServerHost(server);
        return riotFeignClient.getSummonerById(serverHost, summonerId, apiKey);
    }

    @Override
    public SummonerGameStatistics getStatistics(String summonerId, Server server) {
        String json = null;
        try {
            URI serverHost = getServerHost(server);
            json = riotFeignClient.getSummonerStatistics(serverHost, summonerId, apiKey);
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
        JSONObject jsonObject = new JSONObject(json);
        JSONArray types = jsonObject.getJSONArray(summonerId);
        for (int i = 0; i < types.length(); i++) {
            JSONObject type = types.getJSONObject(i);
            Tier tier = Tier.valueOf(type.getString("tier"));
            String queue = type.getString("queue");
            JSONArray entries = type.getJSONArray("entries");
            Division division = Division.I;
            if (entries.length() == 1) {
                division = Division.valueOf(entries.getJSONObject(0).getString("division"));
                gameStatistics.setSummonerName(entries.getJSONObject(0).getString("playerOrTeamName"));
            }
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
