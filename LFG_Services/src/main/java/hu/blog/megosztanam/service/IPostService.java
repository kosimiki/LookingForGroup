package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IPostService {

    Integer saveLookingForMoreNotice(Post post);

    Boolean deletePost(Integer userId, Integer postId);

    List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked);

    Boolean applyForPost(PostApplyRequest request);

    List<PostApplyResponse> getPostAppliesForUser(Integer userId);

    Post getPostById(Integer postId);

}
