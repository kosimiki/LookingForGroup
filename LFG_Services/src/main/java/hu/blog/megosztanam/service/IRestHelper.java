package hu.blog.megosztanam.service;

import org.springframework.web.client.RestClientException;

/**
 * Created by Miklós on 2017. 04. 30..
 */
public interface IRestHelper {

    <T> T getForObject(String url, Class<T> o) throws RestClientException;
}
