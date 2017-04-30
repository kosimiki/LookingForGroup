package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.service.IRestHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Created by Miklós on 2017. 04. 30..
 */
@Service
public class RestHelperImpl implements IRestHelper {

    private RestTemplate template;

    @PostConstruct
    private void init(){
        this.template = new RestTemplate();
    }

    public <T> T getForObject(String url, Class<T> c) throws RestClientException{
        return template.getForObject(url, c);
    }
}
