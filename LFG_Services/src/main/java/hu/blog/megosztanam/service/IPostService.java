package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IPostService {

    Integer saveLookingForMoreNotice(Post post);

    void deletePost(Integer userId, Integer postId);

    List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked);

    void applyForPost(PostApplyRequest request);

    List<PostApplyResponse> getPostAppliesForUser(Integer userId);

    List<PostApplyResponse> getApplicationsByApplicant(Integer userId);

    Post getPostById(Integer postId);

    void acceptApplication(Integer postId, Integer userId);

    void rejectApplication(Integer postId, Integer userId);

    void revokeApplication(Integer postId, Integer applicantUserId);

    void confirmApplication(Integer postId, Integer applicantUserId);

}
