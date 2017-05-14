package hu.blog.megosztanam.model.shared.post;

import hu.blog.megosztanam.model.shared.Role;

import java.util.List;

/**
 * Created by Miklós on 2017. 05. 14..
 */
public class PostApplyRequest {
    private Integer userId;
    private Integer postId;
    private List<Role> roles;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
