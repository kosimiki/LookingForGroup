package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.service.IPostService;
import hu.blog.megosztanam.sql.ApplicationDAO;
import hu.blog.megosztanam.sql.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Override
    public Integer saveLookingForMoreNotice(Post post) {
        return postDao.savePost(post);
    }

    @Override
    public List<Post> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        return postDao.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @Override
    public Boolean applyForPost(PostApplyRequest request) {
        return applicationDAO.saveApplication(request.getUserId(), request.getPostId(), request.getRoles());
    }

    @Override
    public List<PostApplyResponse> getPostAppliesForUser(Integer userId) {
        return applicationDAO.getApplications(userId);
    }
}
