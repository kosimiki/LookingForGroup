package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.sql.ApplicationDAO;
import hu.blog.megosztanam.sql.PostDao;
import hu.blog.megosztanam.sql.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeleteService {

    private final UserDao userDao;
    private final PostDao postDao;
    private final ApplicationDAO applicationDAO;

    public DeleteService(UserDao userDao, PostDao postDao, ApplicationDAO applicationDAO) {
        this.userDao = userDao;
        this.postDao = postDao;
        this.applicationDAO = applicationDAO;
    }


    @Transactional
    public void deleteUser(Integer userId) {
        List<Integer> postIds = postDao.getPostsOfUser(userId);
        postIds.forEach(postId -> postDao.deletePost(postId, userId));
        applicationDAO.deleteApplicationsOfUser(userId);
        userDao.deleteUser(userId);
    }
}
