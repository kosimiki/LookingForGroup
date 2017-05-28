package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Rank;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.model.shared.summoner.Servers;
import hu.blog.megosztanam.service.IRestHelper;
import hu.blog.megosztanam.service.ISummonerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Created by Miklós on 2016. 11. 27..
 *
 */
@Service
public class SummonerServiceImpl implements ISummonerService {
    private static final String SUMMONER_DETAILS_BY_NAME_V3 = ".api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String SUMMONER_DETAILS_BY_ID_V3 = ".api.riotgames.com/lol/summoner/v3/summoners/";
    private static final String SUMMONER_LEAGUE_V2  = ".api.riotgames.com/api/lol/SERVER/v2.5/league/by-summoner/";
    private static final String ENTRY = "/entry";
    private static final String HTTPS = "https://";
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonerServiceImpl.class);

    @Autowired
    private IRestHelper restHelper;

    @Value("${lol.api.key}")
    private String apiKey;

    @Value("${google.oauth.client.id}")
    private String clientId;

    @Override
    public Summoner getSummoner(String summonerName, Server server) {
        Summoner summoner = new Summoner();
        summoner.setId(-1);
        summoner.setName("NAME NOT FOUND");
        try {
            String url = HTTPS + Servers.getServerV3(server) + SUMMONER_DETAILS_BY_NAME_V3 + summonerName + apiKey;
            LOGGER.info("Calling GET on: " + url);
            return restHelper.getForObject(url, Summoner.class);
        }catch (RestClientException e){
            LOGGER.error("REST CLIENT EX: " + e.getMessage(), e);
            return summoner;
        }
    }

    @Override
    public Summoner getSummoner(Integer summonerId, Server server) {
        Summoner summoner = new Summoner();
        summoner.setId(-1);
        summoner.setName("NAME NOT FOUND");
        String url = HTTPS + Servers.getServerV3(server) + SUMMONER_DETAILS_BY_ID_V3 + summonerId + apiKey;
        LOGGER.info("Calling GET on: " + url);
        try {
            return restHelper.getForObject(url, Summoner.class);
        } catch (RestClientException e){
            LOGGER.error("REST CLIENT EX: " + e.getMessage(), e);
            return summoner;
        }
    }

    @Override
    public SummonerGameStatistics getStatistics(Integer summonerId, Server server) {

        SummonerGameStatistics gameStatistics = new SummonerGameStatistics();
        Rank unRanked = new Rank();
        unRanked.setDivision(Division.I);
        unRanked.setTier(Tier.UNRANKED);
        gameStatistics.setTwistedRank(unRanked);
        gameStatistics.setFlexRank(unRanked);
        gameStatistics.setSoloRank(unRanked);
        String serverName = Servers.getServerV2(server);
        String url = HTTPS + serverName + SUMMONER_LEAGUE_V2.replace("SERVER", serverName) + summonerId + ENTRY + apiKey;
        LOGGER.info("Calling GET on: " + url);
        String json = restHelper.getForObject(url, String.class);
        if(json != null){
            JSONObject jsonObject = new JSONObject(json);
            JSONArray types = jsonObject.getJSONArray(summonerId.toString());
            for(int i = 0; i<types.length(); i++){
                JSONObject type = types.getJSONObject(i);
                Tier tier = Tier.valueOf(type.getString("tier"));
                String queue = type.getString("queue");
                JSONArray entries = type.getJSONArray("entries");
                Division division = Division.I;
                if(entries.length()==1){
                    division = Division.valueOf(entries.getJSONObject(0).getString("division"));
                    gameStatistics.setSummonerName(entries.getJSONObject(0).getString("playerOrTeamName"));
                }
                Rank rank = new Rank();
                rank.setDivision(division);
                rank.setTier(tier);
                if(queue != null && queue.contains("SOLO")){
                    gameStatistics.setSoloRank(rank);
                }
                if(queue != null && queue.contains("FLEX")){
                    gameStatistics.setFlexRank(rank);
                }

                if(queue != null && queue.contains("3x3")){
                    gameStatistics.setTwistedRank(rank);
                }
            }
        }else{
            return gameStatistics;
        }
        return gameStatistics;
    }
}
