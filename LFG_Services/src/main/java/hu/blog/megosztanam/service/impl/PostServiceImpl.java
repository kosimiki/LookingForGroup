package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.IMessagingService;
import hu.blog.megosztanam.service.IPostService;
import hu.blog.megosztanam.sql.ApplicationDAO;
import hu.blog.megosztanam.sql.PostDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Service
public class PostServiceImpl implements IPostService {

    private final PostDao postDao;
    private final ApplicationDAO applicationDAO;
    private final IMessagingService messaging;

    public PostServiceImpl(PostDao postDao, ApplicationDAO applicationDAO, IMessagingService messaging) {
        this.postDao = postDao;
        this.applicationDAO = applicationDAO;
        this.messaging = messaging;
    }

    @Override
    public Integer saveLookingForMoreNotice(Post post) {
        Integer integer = postDao.savePost(post);
        messaging.newPostBroadcastMessage(post);
        return integer;
    }

    @Override
    public void deletePost(Integer userId, Integer postId) {
        applicationDAO.deleteApplications(postId);
        postDao.deletePost(postId, userId);
        messaging.postDeleted(postId);
    }

    @Override
    public List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        return postDao.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @Override
    public void applyForPost(PostApplyRequest request) {
        applicationDAO.saveApplication(request.getUserId(), request.getPostId(), request.getRoles());
        messaging.newApplicationMessage(request);
    }

    @Override
    public List<PostApplyResponse> getPostAppliesForUser(Integer userId) {
        return applicationDAO.getApplicationsByPostOwner(userId);
    }

    @Override
    public List<PostApplyResponse> getApplicationsByApplicant(Integer userId) {
        return applicationDAO.getApplicationsByApplicant(userId);
    }


    @Override
    public Post getPostById(Integer postId) {
        return postDao.getPostById(postId);
    }

    @Override
    public void acceptApplication(Integer postId, Integer userId) {
        applicationDAO.acceptApplication(postId, userId);
        messaging.acceptedApplication(postId, userId);
    }

    @Override
    public void rejectApplication(Integer postId, Integer applicantUserId) {
        applicationDAO.deleteApplication(postId, applicantUserId);
        messaging.rejectedApplication(postId, applicantUserId);
    }

    @Override
    public void revokeApplication(Integer postId, Integer applicantUserId) {
        applicationDAO.deleteApplication(postId, applicantUserId);
        messaging.revokedApplication(postId, applicantUserId);
    }

    @Override
    public void confirmApplication(Integer postId, Integer applicantUserId) {
        applicationDAO.deleteApplication(postId, applicantUserId);
        messaging.confirmedApplication(postId, applicantUserId);
    }

    @Transactional
    @Override
    public void deletePostsAndApplications(Integer userId) {

    }
}
