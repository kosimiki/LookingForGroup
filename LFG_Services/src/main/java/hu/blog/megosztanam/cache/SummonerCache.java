package hu.blog.megosztanam.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hu.blog.megosztanam.model.SummonerId;
import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Miklós on 2017. 05. 14..
 */
@Component
public class SummonerCache {

    private final ISummonerService summonerService;
    private final IUserService userService;
    private LoadingCache<SummonerId, Summoner> cache;
    private LoadingCache<Integer, Summoner> userCache;

    public SummonerCache(ISummonerService summonerService, IUserService userService) {
        this.summonerService = summonerService;
        this.userService = userService;
    }

    @PostConstruct
    private void init() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(4, TimeUnit.HOURS)
                .build(new CacheLoader<SummonerId, Summoner>() {

                    @Override
                    public Summoner load(SummonerId summonerId) throws Exception {
                        return summonerService.getById(summonerId.getId(), summonerId.getServer());
                    }
                });

        userCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(4, TimeUnit.HOURS)
                .build(new CacheLoader<Integer, Summoner>() {

                    @Override
                    public Summoner load(Integer userId) throws Exception {
                        UserDetails summonerOfUser = userService.getSummonerOfUser(userId);
                        return summonerService.getById(summonerOfUser.getSummonerId(), summonerOfUser.getServer());
                    }
                });
    }

    public Summoner get(String summonerId, Server server){
        try {
            return cache.get(new SummonerId(summonerId, server));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Summoner getByUserId(Integer userId){
        try {
            return userCache.get(userId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
