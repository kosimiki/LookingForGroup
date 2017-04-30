package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.service.IPostService;
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
    @Override
    public Integer saveLookingForMoreNotice(Post post) {
        return postDao.savePost(post);
    }

    @Override
    public List<Post> getSearchForMemberPosts() {
        return postDao.getSearchForMemberPosts();
    }
}
