package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class OpenPosition {
    protected Integer postId;
    protected String role;

    public OpenPosition(Integer postId, String role) {
        this.postId = postId;
        this.role = role;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
