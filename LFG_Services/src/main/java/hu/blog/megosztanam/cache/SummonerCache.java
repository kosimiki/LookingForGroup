package hu.blog.megosztanam.cache;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Created by Miklós on 2017. 05. 14..
 */
@Component
public class SummonerCache {

    private final ISummonerService summonerService;
    private final IUserService userService;

    public SummonerCache(ISummonerService summonerService, IUserService userService) {
        this.summonerService = summonerService;
        this.userService = userService;
    }

    @Cacheable("summonerBySummonerId")
    public Summoner getSummonerBySummonerId(String summonerId, Server server) {
        return summonerService.getById(summonerId, server);
    }

    @Cacheable("summonerByUserId")
    public Summoner getSummonerByUserId(Integer userId) {
        UserDetails summonerOfUser = userService.getSummonerOfUser(userId);
        return summonerService.getById(summonerOfUser.getSummonerId(), summonerOfUser.getServer());
    }

    @Cacheable("rank")
    public SummonerGameStatistics getRank(String summonerId, Server server){
        return summonerService.getStatistics(summonerId, server);
    }
}
