package hu.blog.megosztanam.rest;


import hu.blog.megosztanam.model.shared.*;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.IPostService;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Created by kosimiki on 2016. 11. 26..
 */
@RestController
public class SummonerRest {

    private final ISummonerService summonerService;
    private final IPostService postService;
    private final IUserService userService;

    private static final Logger log = LoggerFactory.getLogger(SummonerRest.class);

    public SummonerRest(ISummonerService summonerService, IPostService postService, IUserService userService) {
        this.summonerService = summonerService;
        this.postService = postService;
        this.userService = userService;
    }

    @RequestMapping(value = "/{server}/summoners/{name}", method = RequestMethod.GET)
    public Summoner getSummoner(@PathVariable String name, @PathVariable Server server) {
        return summonerService.getSummoner(name, server);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Integer saveLookingForMemberPost(
            @RequestBody @NotNull Post post) {
        log.info("summoner from rest: " + post.toString());
        return postService.saveLookingForMoreNotice(post);
    }

    @RequestMapping(value = "{server}/posts", method = RequestMethod.GET)
    public List<Post> getLookingForMemberPost(@PathVariable Server server,
                                              @RequestParam("userId") Integer userId,
                                              @RequestParam(name = "map", required = false) GameMap map,
                                              @RequestParam(name = "isRanked",required = false) Boolean isRanked
    ) {
        return postService.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @RequestMapping(value = "{userId}/posts/{postId}", method = RequestMethod.DELETE)
    public Boolean deletePost(@PathVariable("userId") Integer userId, @PathVariable("postId") Integer postId){
        return postService.deletePost(userId, postId);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse doLogin(@RequestBody String idToken) {
        LoginResponse response = userService.doLogin(idToken);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    public void updateMessagingToken(
            @PathVariable("userId") @Min(0) Integer userId,
            @RequestParam("firebaseId") @NotNull  String messagingToken) {
        log.info("Updating token to: " + messagingToken + " _end");
        userService.updateMessagingToken(userId, messagingToken);
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

    @RequestMapping(value = "/posts/apply", method = RequestMethod.POST)
    public Boolean applyForPost(
            @RequestBody @NotNull PostApplyRequest request) {
        return postService.applyForPost(request);
    }

    @RequestMapping(value = "/users/{userId}/applications", method = RequestMethod.GET)
    public List<PostApplyResponse> applyForPost(
            @PathVariable @Min(0) Integer userId) {
        return postService.getPostAppliesForUser(userId);
    }
}
