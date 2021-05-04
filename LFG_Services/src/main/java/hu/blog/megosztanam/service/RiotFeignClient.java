package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.Summoner;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(name = "LolAPI")
public interface RiotFeignClient {

    @RequestMapping(method = RequestMethod.GET, path = "lol/summoner/v4/summoners/by-name/{name}")
    Summoner getSummonerByName(URI host, @PathVariable("name") String name, @RequestParam("api_key") String apiKey);

    @RequestMapping(method = RequestMethod.GET, path = "lol/summoner/v4/summoners/{id}")
    Summoner getSummonerById(URI host, @PathVariable("id") String id, @RequestParam("api_key") String apiKey);

    @RequestMapping(method = RequestMethod.GET, path = "api/lol/league/v4/entries/by-summoner/{id}")
    String getSummonerStatistics(URI host, @PathVariable("id") String id, @RequestParam("api_key") String apiKey);
}
