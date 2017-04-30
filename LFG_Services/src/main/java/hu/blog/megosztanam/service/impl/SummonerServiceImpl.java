package hu.blog.megosztanam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.service.ISummonerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String SUMMONER_DETAILS_BY_NAME = "https://euw.api.pvp.net/api/lol/euw/v1.4/summoner/by-name/";
    private static final String SUMMONER_DETAILS_BY_ID = "https://euw.api.pvp.net/api/lol/euw/v1.4/summoner/";
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonerServiceImpl.class);

    @Value("${lol.api.key}")
    private String apiKey;

    @Value("${google.oauth.client.id}")
    private String clientId;

    public Summoner getSummoner(String summonerName){
        Summoner summoner = new Summoner();
        summoner.setId(-1);
        summoner.setName("NAME NOT FOUND");
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = SUMMONER_DETAILS_BY_NAME + summonerName + apiKey;
            LOGGER.info("Calling GET on: " + url);
            String json = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(json);
            LOGGER.info("Received :" + jsonObject.toString());
            try {
                return mapper.readValue(jsonObject.getJSONObject(getNameKey(summonerName)).toString(), Summoner.class);
            } catch (IOException e) {
                LOGGER.error("Exception while get summoner: " + e.getMessage(), e);
            }
            return summoner;
        }catch (RestClientException e){
            LOGGER.error("REST CLIENT EX: " + e.getMessage(), e);
            return summoner;
        }
    }


    @Override
    public Summoner getSummoner(Integer summonerId){
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String url = SUMMONER_DETAILS_BY_ID + summonerId + apiKey;
        LOGGER.info("Calling GET on: " + url);
        String json = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(json);
        LOGGER.info("Received :" + jsonObject.toString());
        try {
            return mapper.readValue(jsonObject.getJSONObject(summonerId.toString()).toString(), Summoner.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }

    private String getNameKey(String name){
        return name.replace(" ","").toLowerCase().trim();
    }
}
