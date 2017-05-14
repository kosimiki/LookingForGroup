package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IUserService {

    LoginResponse doLogin(String idTokenString);
    LoginResponse register(String idTokenString, Integer summonerId, Server server);
    void updateMessagingToken(Integer userId, String token);
}
