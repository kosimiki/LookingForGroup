package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.filter.GoogleVerifier;
import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.LoginStatus;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import hu.blog.megosztanam.sql.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final ISummonerService summonerService;
    private final GoogleVerifier verifier;


    public UserServiceImpl(UserDao userDao, ISummonerService summonerService, GoogleVerifier verifier) {
        this.userDao = userDao;
        this.summonerService = summonerService;
        this.verifier = verifier;
    }


    @Override
    public UserDetails getSummonerOfUser(Integer userId) {
        return userDao.getSummonerId(userId);
    }

    @Transactional
    @Override
    public LoginResponse doLogin(String idTokenString) {
        LoginResponse loginResponse = new LoginResponse();
        Optional<User> user = verifier.parseToken(idTokenString);
        if (user.isPresent()) {
            user.get().setIdToken(idTokenString);
            loginResponse.setUser(user.get());
            if (userDao.isRegisteredUser(user.get().getEmail())) {
                UserDetails userDetails = userDao.getSummonerId(user.get().getEmail());
                user.get().setUserId(userDetails.getUserId());
                user.get().setServer(userDetails.getServer());
                user.get().setSummoner(summonerService.getById(userDetails.getSummonerId(), userDetails.getServer()));
                loginResponse.setLoginStatus(LoginStatus.SUCCESSFUL);
            } else {
                loginResponse.setLoginStatus(LoginStatus.REGISTRATION_REQUIRED);
            }
        } else {
            loginResponse.setLoginStatus(LoginStatus.FAILED);
        }
        LOGGER.info(loginResponse.toString());
        return loginResponse;
    }

    @Transactional
    @Override
    public LoginResponse register(String idTokenString, String summonerId, Server server) {
        LoginResponse registrationResponse = new LoginResponse();
        Optional<User> user = verifier.parseToken(idTokenString);
        if (user.isPresent()) {
            user.get().setUserId(userDao.saveUser(user.get().getEmail(), summonerId, server));
            user.get().setSummoner(summonerService.getById(summonerId, server));
            user.get().setServer(server);
            registrationResponse.setUser(user.get());
            registrationResponse.setLoginStatus(LoginStatus.SUCCESSFUL);
            LOGGER.info("SUCCESS REG");
        } else {
            registrationResponse.setLoginStatus(LoginStatus.FAILED);
            LOGGER.info("FAILED REG");
        }
        return registrationResponse;
    }

    @Override
    public void updateMessagingToken(Integer userId, String token) {
        userDao.saveMessagingToken(userId, token);
    }


}
