package hu.blog.megosztanam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.model.shared.summoner.Servers;
import hu.blog.megosztanam.service.IRestHelper;
import hu.blog.megosztanam.service.ISummonerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by Miklós on 2016. 11. 27..
 *
 */
@Service
public class SummonerServiceImpl implements ISummonerService {
    private static final String SUMMONER_DETAILS_BY_NAME_V3 = ".api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String SUMMONER_DETAILS_BY_ID_V3 = ".api.riotgames.com/lol/summoner/v3/summoners/";
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

}
