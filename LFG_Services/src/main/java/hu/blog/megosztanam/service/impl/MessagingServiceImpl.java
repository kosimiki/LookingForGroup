package hu.blog.megosztanam.service.impl;

import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.messaging.Messaging;
import hu.blog.megosztanam.model.shared.post.Notification;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostNotification;
import hu.blog.megosztanam.service.ICloudMessaging;
import hu.blog.megosztanam.service.IMessagingService;
import hu.blog.megosztanam.service.IRestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Miklós on 2017. 06. 05..
 */
@Service
public class MessagingServiceImpl implements IMessagingService {

    @Autowired
    IRestHelper restHelper;

    @Value("${google.cloud.messaging.server.key}")
    String key;

    @Value("${google.cloud.messaging.server.id}")
    String id;

    @Autowired
    ICloudMessaging cloudMessaging;


    @Override
    public void newPostBroadcastMessage(Post post) {
        PostNotification postNotification = new PostNotification();
        Notification notification = new Notification();
        notification.setTitle("New posts available");
        notification.setBody("Summoner " + post.getOwner().getName() + " looking for teammates on " + post.getGameType().getMap().getValue() + ". " + post.getDescription());
        postNotification.setNotification(notification);

        postNotification.setTo("/topics/" + Messaging.NEW_POSTS_TOPIC);
        cloudMessaging.broadcastNewPost("key=" + key, postNotification);
    }

    @Override
    public void newApplicationMessage(PostApplyRequest postApplyRequest) {
    }
}
