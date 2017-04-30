package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2016. 11. 27..
 */
public interface ISummonerService {

    Summoner getSummoner(String summonerName, Server server);
    Summoner getSummoner(Integer summonerId, Server server);

}
