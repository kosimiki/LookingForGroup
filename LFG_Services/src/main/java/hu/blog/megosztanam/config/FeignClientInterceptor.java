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

    private final Logger LOGGER = LoggerFactory.getLogger(FeignClientInterceptor.class.getName());

    private final String apiKey;

    public FeignClientInterceptor(@Value("${lol.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        LOGGER.info("[" + requestTemplate.method() + "] " + requestTemplate.url() + requestTemplate.queryLine());
        if (requestTemplate.url().contains(RIOT_GAMES)) {
            requestTemplate.query("api_key", apiKey);
        }
    }


}
