package hu.blog.megosztanam.model.shared.post;

/**
 * Created by Miklós on 2017. 06. 05..
 */
public class Notification {

    private String body;
    private String title;
    private String icon;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
