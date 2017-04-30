package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.LoginResponse;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IUserService {

    LoginResponse doLogin(String idTokenString);
    LoginResponse register(String idTokenString, Integer summonerId);
}
