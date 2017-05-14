package hu.blog.megosztanam.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hu.blog.megosztanam.model.SummonerId;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Miklós on 2017. 05. 14..
 */
@Component
public class SummonerCache {

    @Autowired
    private ISummonerService summonerService;
    private LoadingCache<SummonerId, Summoner> cache;

    @PostConstruct
    private void init() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(4, TimeUnit.HOURS)
                .build(new CacheLoader<SummonerId, Summoner>() {

                    @Override
                    public Summoner load(SummonerId summonerId) throws Exception {
                        return summonerService.getSummoner(summonerId.getId(), summonerId.getServer());
                    }
                });
    }

    public Summoner get(Integer summonerId, Server server){
        try {
            return cache.get(new SummonerId(summonerId, server));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
