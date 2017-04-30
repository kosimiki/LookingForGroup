package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.Summoner;

/**
 * Created by Miklós on 2016. 11. 27..
 */
public interface ISummonerService {

    Summoner getSummoner(String summonerName);
    Summoner getSummoner(Integer summonerId);

}
