package hu.blog.megosztanam.model.shared.post;

/**
 * Created by Miklós on 2017. 06. 05..
 */
public class PostNotification {

    private String to;
    private Notification notification;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
