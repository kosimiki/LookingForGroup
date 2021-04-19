package hu.blog.megosztanam.rest;


import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * Created by kosimiki on 2016. 11. 26..
 */
@RestController
public class SummonerRest {

    private final ISummonerService summonerService;
    private final IUserService userService;

    private static final Logger log = LoggerFactory.getLogger(SummonerRest.class);

    public SummonerRest(ISummonerService summonerService, IUserService userService) {
        this.summonerService = summonerService;
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse doLogin(@RequestBody String idToken) {
        LoginResponse response = userService.doLogin(idToken.replaceAll("\"", ""));
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    public void updateMessagingToken(
            @PathVariable("userId") @Min(0) Integer userId,
            @RequestParam("firebaseId") @NotNull String messagingToken) {
        log.info("Updating token to: " + messagingToken + " _end");
        userService.updateMessagingToken(userId, messagingToken);
    }

    @RequestMapping(value = "/{server}/summoners/{name}", method = RequestMethod.GET)
    public Summoner getSummoner(@PathVariable String name, @PathVariable Server server) {
        return summonerService.getSummoner(name, server);
    }

    @RequestMapping(value = "/{server}/registration/{summonerId}", method = RequestMethod.POST)
    public LoginResponse doRegistration(@RequestBody String idToken,
                                        @PathVariable @NotNull @Min(1) String summonerId,
                                        @PathVariable Server server) {
        return userService.register(idToken, summonerId, server);
    }

    @RequestMapping(value = "/{server}/league/{summonerId}", method = RequestMethod.GET)
    public SummonerGameStatistics getStats(@PathVariable @NotNull @Min(1) String summonerId,
                                           @PathVariable Server server) {
        return summonerService.getStatistics(summonerId, server);
    }


}
