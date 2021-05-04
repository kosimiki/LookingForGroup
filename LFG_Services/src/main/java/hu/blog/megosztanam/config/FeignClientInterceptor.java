package hu.blog.megosztanam.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static hu.blog.megosztanam.service.impl.SummonerServiceImpl.RIOT_GAMES;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(FeignClientInterceptor.class);

    private final String apiKey;

    public FeignClientInterceptor(@Value("${lol.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void apply(RequestTemplate request) {
        LOGGER.info("[" + request.method() + "] " + request.url() + request.queryLine());
        if (request.url().contains(RIOT_GAMES)) {
            request.query("api_key", apiKey);
        }
    }


}
