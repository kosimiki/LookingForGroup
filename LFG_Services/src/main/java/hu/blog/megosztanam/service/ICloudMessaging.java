package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.post.PostNotification;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Miklós on 2017. 06. 05..
 */
@Service
@FeignClient(name = "messaging", url = "https://fcm.googleapis.com/fcm/send")
public interface ICloudMessaging {

    @RequestMapping(value = "", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String broadcastNewPost(@RequestHeader("Authorization") String serverKey,
                            @RequestBody PostNotification request
    );
}
