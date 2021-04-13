package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.messaging.Messaging;
import hu.blog.megosztanam.model.shared.post.Notification;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostNotification;
import hu.blog.megosztanam.rest.SummonerRest;
import hu.blog.megosztanam.service.ICloudMessaging;
import hu.blog.megosztanam.service.IMessagingService;
import hu.blog.megosztanam.service.IRestHelper;
import hu.blog.megosztanam.service.ISummonerService;
import hu.blog.megosztanam.sql.PostDao;
import hu.blog.megosztanam.sql.UserDao;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Miklós on 2017. 06. 05..
 */
@Service
public class MessagingServiceImpl implements IMessagingService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessagingServiceImpl.class);

    @Autowired
    IRestHelper restHelper;

    @Value("${google.cloud.messaging.server.key}")
    String key;

    @Value("${google.cloud.messaging.server.id}")
    String id;

    @Autowired
    ICloudMessaging cloudMessaging;

    @Autowired
    UserDao userDao;

    @Autowired
    ISummonerService service;

    @Autowired
    PostDao postDao;


    @Override
    public void newPostBroadcastMessage(Post post) {
        PostNotification postNotification = new PostNotification();
        Notification notification = new Notification();
        notification.setTitle("New posts available");
        notification.setBody(Messaging.NEW_POST + post.getOwner().getName()
                + " is looking for teammates on "
                + post.getGameType().getMap().getValue()
                + ". " + post.getDescription());
        postNotification.setNotification(notification);

        postNotification.setTo("/topics/" + Messaging.NEW_POSTS_TOPIC);
        cloudMessaging.sendMessage("key=" + key, postNotification);
    }

    @Override
    public void postDeleted() {
        PostNotification postNotification = new PostNotification();
        Notification notification = new Notification();
        notification.setTitle("Post deleted");
        notification.setBody(Messaging.POST_DELETED);
        postNotification.setNotification(notification);

        postNotification.setTo("/topics/" + Messaging.NEW_POSTS_TOPIC);
        cloudMessaging.sendMessage("key=" + key, postNotification);
    }

    @Override
    public void newApplicationMessage(PostApplyRequest postApplyRequest) {
        PostNotification postNotification = new PostNotification();
        Notification notification = new Notification();
        notification.setTitle("A summoner wants to play with you!");
        postNotification.setNotification(notification);
        log.info("user id " + postApplyRequest.getUserId());
        log.info("post id " + postApplyRequest.getPostId());
        UserDetails details = userDao.getSummoner(postApplyRequest.getUserId());
        Summoner summoner = service.getById(details.getSummonerId(), details.getServer());
        Post post = postDao.getPostById(postApplyRequest.getPostId());
        notification.setBody(summoner.getName()
                + " (" + summoner.getSummonerLevel() + ")"
                + " applied for " +
                postApplyRequest.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","))
                + " roles on " + "(" + (post.getGameType().isRanked()?"Ranked":"Normal")+ ")" + post.getGameType().getMap().name());
        postNotification.setTo(userDao.getFirebaseId(postDao.getOwnerId(postApplyRequest.getPostId())));
        log.info(postNotification.toString());
        cloudMessaging.sendMessage("key=" + key, postNotification);
    }
}
