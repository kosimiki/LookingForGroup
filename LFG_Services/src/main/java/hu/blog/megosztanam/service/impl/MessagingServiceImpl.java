package hu.blog.megosztanam.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import hu.blog.megosztanam.cache.SummonerCache;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.messaging.MessageType;
import hu.blog.megosztanam.model.shared.messaging.Messaging;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.service.IMessagingService;
import hu.blog.megosztanam.sql.PostDao;
import hu.blog.megosztanam.sql.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Created by Miklós on 2017. 06. 05..
 */
@Service
public class MessagingServiceImpl implements IMessagingService {

    private static final Logger log = LoggerFactory.getLogger(MessagingServiceImpl.class);

    private final SummonerCache summonerService;
    private final UserDao userDao;
    private final PostDao postDao;

    public MessagingServiceImpl(SummonerCache summonerService, UserDao userDao, PostDao postDao) {
        this.summonerService = summonerService;
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @PostConstruct
    public void init() {
        FileInputStream serviceAccount;
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL is = classloader.getResource("firebase-lookingforgroup-firebase-adminsdk-sgtlc-a7cff7d097.json");
            serviceAccount = new FileInputStream(is.getFile());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://fir-lookingforgroup.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void newPostBroadcastMessage(Post post) {
        log.info("broadcasting NEW_POST to /topics/" + Messaging.NEW_POSTS_TOPIC);
        Message message = Message.builder()
                .putData("postId", String.valueOf(post.getPostId()))
                .setTopic(Messaging.NEW_POSTS_TOPIC)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postDeleted(Integer postId) {
        log.info("broadcasting DELETE to /topics/" + Messaging.POST_DELETED);
        Message message = Message.builder()
                .putData("postId", String.valueOf(postId))
                .setTopic(Messaging.POST_DELETED)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newApplicationMessage(PostApplyRequest postApplyRequest) {
        String firebaseId = userDao.getFirebaseId(postDao.getOwnerId(postApplyRequest.getPostId()));
        Integer applicantUserId = postApplyRequest.getUserId();
        Summoner summoner = summonerService.getSummonerByUserId(applicantUserId);
        String title = "Summoner " + summoner.getName() + " wants to play with you!";
        Post post = postDao.getPostById(postApplyRequest.getPostId());
        String roles = postApplyRequest.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
        String gameType = post.getGameType().isRanked() ? "Ranked" : "Normal";
        String map = post.getGameType().getMap().name();
        String body = summoner.getName()
                + " (" + summoner.getSummonerLevel() + ")"
                + " applied for " + roles + " roles on " + "(" + gameType + ")" + map;

        sendMessage(firebaseId, MessageType.NEW_APPLICATION, title, body);

    }

    @Override
    public void acceptedApplication(Integer postId, Integer userId) {
        Post post = postDao.getPostById(postId);
        String firebaseId = userDao.getFirebaseId(userId);
        Summoner summoner = summonerService.getSummonerByUserId(post.getUserId());
        String title = "Summoner " + summoner.getName() + " accepted your application";
        String body = "Game mode: " + post.getGameType().getMap().getValue();
        sendMessage(firebaseId, MessageType.NEW_APPLICATION, title, body);
    }

    @Override
    public void rejectedApplication(Integer postId, Integer applicantUserId) {
        Post post = postDao.getPostById(postId);
        String firebaseId = userDao.getFirebaseId(applicantUserId);
        Summoner summoner = summonerService.getSummonerByUserId(post.getUserId());
        String title = "Summoner " + summoner.getName() + " rejected your application";
        String body = "Game mode: " + post.getGameType().getMap().getValue();
        sendMessage(firebaseId, MessageType.APPLICATION_REJECTED, title, body);

    }

    @Override
    public void revokedApplication(Integer postId, Integer applicantUserId) {
        Post post = postDao.getPostById(postId);
        String firebaseId = userDao.getFirebaseId(post.getUserId());
        Summoner summoner = summonerService.getSummonerByUserId(applicantUserId);
        String title = "Summoner " + summoner.getName() + " revoked their application";
        String body = "Game mode: " + post.getGameType().getMap().getValue();
        sendMessage(firebaseId, MessageType.APPLICATION_REJECTED, title, body);
    }

    @Override
    public void confirmedApplication(Integer postId, Integer applicantUserId) {
        Post post = postDao.getPostById(postId);
        String firebaseId = userDao.getFirebaseId(post.getUserId());
        Summoner summoner = summonerService.getSummonerByUserId(applicantUserId);
        String title = "Summoner " + summoner.getName() + " confirmed their application";
        String body = "Game mode: " + post.getGameType().getMap().getValue();
        sendMessage(firebaseId, MessageType.APPLICATION_CONFIRMED, title, body);
    }

    private void sendMessage(String toFirebaseId, String messageType, String title, String body) {
        log.info("sending message to " + toFirebaseId + " _END");
        Message message = Message.builder()
                .putData("message", body)
                .putData("message_type", messageType)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(toFirebaseId)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
