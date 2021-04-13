package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IUserService {

    UserDetails getSummonerOfUser(Integer userId);
    LoginResponse doLogin(String idTokenString);
    LoginResponse register(String idTokenString, String summonerId, Server server);
    void updateMessagingToken(Integer userId, String token);
}
