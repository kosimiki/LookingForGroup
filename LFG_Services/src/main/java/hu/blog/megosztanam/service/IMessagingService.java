package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;

/**
 * Created by Miklós on 2017. 06. 05..
 */
public interface IMessagingService {

    void newPostBroadcastMessage(Post post);
    void postDeleted(Integer postId);
    void newApplicationMessage(PostApplyRequest postApplyRequest);
    void acceptedApplication(Integer postId, Integer userId);
    void rejectedApplication(Integer postId, Integer applicantUserId);
    void revokedApplication(Integer postId, Integer applicantUserId);
    void confirmedApplication(Integer postId, Integer applicantUserId);
}
