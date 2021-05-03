package hu.blog.megosztanam.rest;

import hu.blog.megosztanam.model.AuthenticatedUser;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.IPostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class PostRestController {

    private final IPostService postService;

    public PostRestController(IPostService postService) {
        this.postService = postService;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Integer savePost(
            @RequestBody @NotNull Post post) {
        return postService.saveLookingForMoreNotice(post);
    }

    @RequestMapping(value = "{server}/posts", method = RequestMethod.GET)
    public List<Post> searchPosts(@PathVariable Server server,
                                  @RequestParam("userId") Integer userId,
                                  @RequestParam(name = "map", required = false) GameMap map,
                                  @RequestParam(name = "isRanked", required = false) Boolean isRanked
    ) {
        return postService.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @RequestMapping(value = "{userId}/posts/{postId}", method = RequestMethod.DELETE)
    public void deletePost(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable("userId") Integer userId, @PathVariable("postId") Integer postId) {
        if(user.getUserId() == userId) {
            postService.deletePost(userId, postId);
        }
    }

}
