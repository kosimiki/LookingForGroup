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
        messaging.newPostBroadcastMessage(post);
        return postDao.savePost(post);
    }

    @Override
    public Boolean deletePost(Integer userId, Integer postId) {
        messaging.postDeleted(postId);
        return postDao.deletePost(postId, userId);
    }

    @Override
    public List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        return postDao.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @Override
    public Boolean applyForPost(PostApplyRequest request) {
        messaging.newApplicationMessage(request);
        return applicationDAO.saveApplication(request.getUserId(), request.getPostId(), request.getRoles());
    }

    @Override
    public List<PostApplyResponse> getPostAppliesForUser(Integer userId) {
        return applicationDAO.getApplications(userId);
    }

    @Override
    public Post getPostById(Integer postId) {
        return postDao.getPostById(postId);
    }
}
