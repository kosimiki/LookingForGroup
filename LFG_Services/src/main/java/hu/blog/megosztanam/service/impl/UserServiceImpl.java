package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.LoginStatus;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import hu.blog.megosztanam.sql.UserDao;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final ISummonerService summonerService;

    public UserServiceImpl(UserDao userDao, ISummonerService summonerService) {
        this.userDao = userDao;
        this.summonerService = summonerService;
    }


    @Override
    public UserDetails getSummonerOfUser(Integer userId) {
        return userDao.getSummonerId(userId);
    }

    @Transactional
    @Override
    public LoginResponse doLogin(String idTokenString)  {
        LoginResponse loginResponse = new LoginResponse();
        User user = authenticateWithRest(idTokenString);
        user.setIdToken(idTokenString);
        if (user.getAuthenticated()) {
            loginResponse.setUser(user);
            if(userDao.isRegisteredUser(user.getEmail())){
                UserDetails userDetails = userDao.getSummonerId(user.getEmail());
                user.setUserId(userDetails.getUserId());
                user.setServer(userDetails.getServer());
                user.setSummoner(summonerService.getById(userDetails.getSummonerId(), userDetails.getServer()));
                loginResponse.setLoginStatus(LoginStatus.SUCCESSFUL);
                LOGGER.info(loginResponse.toString());
                return loginResponse;
            }else{
                loginResponse.setLoginStatus(LoginStatus.REGISTRATION_REQUIRED);
                LOGGER.info(loginResponse.toString());
                return loginResponse;
            }

        } else {
            loginResponse.setLoginStatus(LoginStatus.FAILED);
            LOGGER.info(loginResponse.toString());

            return loginResponse;
        }
    }

    @Transactional
    @Override
    public LoginResponse register(String idTokenString, String summonerId, Server server) {
        LoginResponse registrationResponse = new LoginResponse();
        User user = authenticateWithRest(idTokenString);
        if(user.getAuthenticated()){
            user.setUserId(userDao.saveUser(user.getEmail(), summonerId ,server));
            user.setSummoner(summonerService.getById(summonerId, server));
            user.setServer(server);
            registrationResponse.setUser(user);
            registrationResponse.setLoginStatus(LoginStatus.SUCCESSFUL);
            LOGGER.info("SUCCESS REG");
        }else {
            registrationResponse.setLoginStatus(LoginStatus.FAILED);
            LOGGER.info("FAILED REG");
        }
        return registrationResponse;
    }

    @Override
    public void updateMessagingToken(Integer userId, String token) {
        userDao.saveMessagingToken(userId, token);
    }

    private User authenticateWithRest(String id){
        RestTemplate restTemplate = new RestTemplate();
        User user = new User();
        LOGGER.info("Calling GET on: " + id);
        id = id.replaceAll("\"","");
        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + id;
        LOGGER.info("Calling GET on: " + url);
        String json = restTemplate.getForObject(url, String.class);
        if(json != null){
            JSONObject jsonObject = new JSONObject(json);
            LOGGER.info("EMAIL :" + jsonObject.getString("email"));
            user.setEmail(jsonObject.getString("email"));
            user.setProfilePictureUrl(jsonObject.getString("picture"));
            user.setGivenName(jsonObject.getString("given_name"));
            user.setAuthenticated(true);
            return user;
        }else{
            LOGGER.error("Authentication failed");
            user.setAuthenticated(false);
            return user;
        }
    }
}
