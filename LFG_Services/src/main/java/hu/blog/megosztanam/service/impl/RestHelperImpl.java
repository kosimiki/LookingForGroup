package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.service.IRestHelper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Miklós on 2017. 04. 30..
 */
@Service
public class RestHelperImpl implements IRestHelper {

    private static final Logger LOGGER = Logger.getLogger(RestHelperImpl.class.getName());
    private RestTemplate template;

    @PostConstruct
    private void init(){
        this.template = new RestTemplate();
    }

    public <T> T getForObject(String url, Class<T> c) throws RestClientException{
        return template.getForObject(url, c);
    }

    public <T> T postForObject(String url, Object body, Class<T> c, Object ...queryVariables) throws RestClientException{
        return template.postForObject(url, body, c, queryVariables);
    }

    public <T> T postForObject(String url, Object body, Class<T> c) throws RestClientException{
        return template.postForObject(url, body, c);
    }

    public <T> T post(String url, Object body, String authorization, Class<T> c){
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, c);
            return response.getBody();
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

}
