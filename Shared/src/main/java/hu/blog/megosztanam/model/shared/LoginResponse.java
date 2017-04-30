package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class LoginResponse {
    protected User user;
    protected LoginStatus loginStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "user=" + user +
                ", loginStatus=" + loginStatus +
                '}';
    }
}
