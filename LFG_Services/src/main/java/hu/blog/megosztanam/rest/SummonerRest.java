package hu.blog.megosztanam.rest;



import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.SummonerGameStatistics;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.IPostService;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Created by kosimiki on 2016. 11. 26..
 *
 */
@RestController
public class SummonerRest {

    @Autowired private ISummonerService summonerService;
    @Autowired private IPostService postService;
    @Autowired private IUserService userService;

    private static final Logger log = LoggerFactory.getLogger(SummonerRest.class);

    @RequestMapping(value = "/{server}/summoners/{name}", method = RequestMethod.GET)
    public Summoner getSummoner(@PathVariable String name, @PathVariable Server server){
        return summonerService.getSummoner(name, server);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Integer saveLookingForMemberPost(
            @RequestBody @NotNull Post post){
        log.info("summoner from rest: " + post.toString());
        return postService.saveLookingForMoreNotice(post);
    }

    @RequestMapping(value = "/post", method = RequestMethod.GET)
    public List<Post> getLookingForMemberPost(){
        log.info("GET POSTS CALLED");
        return postService.getSearchForMemberPosts();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse doLogin(@RequestBody String idToken){
        LoginResponse response = userService.doLogin(idToken);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    public void updateMessagingToken(
            @PathVariable("userId") @Min(0) Integer userId,
            @RequestBody @NotNull String messagingToken){
        log.info("Updating user: " + userId);
        userService.updateMessagingToken(userId,messagingToken);
    }

    @RequestMapping(value = "/{server}/registration/{summonerId}", method = RequestMethod.POST)
    public LoginResponse doRegistration(@RequestBody String idToken,
                                        @PathVariable @NotNull @Min(1) Integer summonerId,
                                        @PathVariable Server server){
        return userService.register(idToken,summonerId, server);
    }
    @RequestMapping(value = "/{server}/league/{summonerId}", method = RequestMethod.GET)
    public SummonerGameStatistics getStats(@PathVariable @NotNull @Min(1) Integer summonerId,
                                           @PathVariable Server server){
        return summonerService.getStatistics(summonerId, server);
    }

}
