package hu.blog.megosztanam.rest;


import hu.blog.megosztanam.model.AuthenticatedUser;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.service.IPostService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ApplicationRestController {

    private static final Logger LOGGER = Logger.getLogger(ApplicationRestController.class.getName());
    private final IPostService postService;

    public ApplicationRestController(IPostService postService) {
        this.postService = postService;
    }

    @RequestMapping(value = "/posts/apply", method = RequestMethod.POST)
    public Boolean applyForPost(
            @RequestBody @NotNull PostApplyRequest request) {
        return postService.applyForPost(request);
    }

    @RequestMapping(value = "/posts/{postId}/applications/{userId}", method = RequestMethod.PUT)
    public void acceptApplication(@PathVariable Integer postId, @PathVariable Integer userId) {
        AuthenticatedUser user = getPrincipal();
        if (user.getUserId() == postService.getPostById(postId).getUserId()) {
            postService.acceptApplication(postId, userId);
        } else {
            LOGGER.warning(user.getUserId() + " is not the owner of the post " + postId);
        }
    }

    @RequestMapping(value = "/posts/{postId}/applications/{userId}", method = RequestMethod.DELETE)
    public void rejectApplication(@PathVariable Integer postId, @PathVariable Integer userId) {
        AuthenticatedUser user = getPrincipal();
        if (user.getUserId() == postService.getPostById(postId).getUserId()) {
            postService.rejectApplication(postId, userId);
        } else {
            LOGGER.warning(user.getUserId() + " is not the owner of the post " + postId);
        }
    }

    @RequestMapping(value = "/users/{userId}/applications/{postId}", method = RequestMethod.DELETE)
    public void revokeApplication(@PathVariable Integer postId, @PathVariable Integer userId) {
        AuthenticatedUser user = getPrincipal();
        if (user.getUserId() == userId) {
            postService.revokeApplication(postId, userId);
        } else {
            LOGGER.warning(user.getUserId() + " is not the application for " + postId);
        }
    }

    @RequestMapping(value = "/users/{userId}/applications/{postId}", method = RequestMethod.PUT)
    public void confirmApplication(@PathVariable Integer postId, @PathVariable Integer userId) {
        AuthenticatedUser user = getPrincipal();
        if (user.getUserId() == userId) {
            postService.confirmApplication(postId, userId);
        } else {
            LOGGER.warning(user.getUserId() + " is not the application for " + postId);
        }
    }

    private AuthenticatedUser getPrincipal() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @RequestMapping(value = "/users/{userId}/posts/applications", method = RequestMethod.GET)
    public List<PostApplyResponse> getApplicationsForPostsOfUser(
            @PathVariable @Min(0) Integer userId) {
        return postService.getPostAppliesForUser(userId);
    }

    @RequestMapping(value = "/users/{userId}/applications", method = RequestMethod.GET)
    public List<PostApplyResponse> getApplicationsByApplicant(
            @PathVariable @Min(0) Integer userId) {
        return postService.getApplicationsByApplicant(userId);
    }
}
