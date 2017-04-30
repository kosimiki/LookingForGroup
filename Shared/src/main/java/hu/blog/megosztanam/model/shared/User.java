package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public class User {

    protected Integer userId;
    protected String idTokenString;
    protected String email;
    protected String givenName;
    protected String profilePictureUrl;
    protected Boolean authenticated;
    protected Summoner summoner;
    protected String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIdTokenString() {
        return idTokenString;
    }

    public void setIdTokenString(String idTokenString) {
        this.idTokenString = idTokenString;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Summoner getSummoner() {
        return summoner;
    }

    public void setSummoner(Summoner summoner) {
        this.summoner = summoner;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", idTokenString='" + idTokenString + '\'' +
                ", email='" + email + '\'' +
                ", givenName='" + givenName + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", authenticated=" + authenticated +
                ", summoner=" + summoner +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}
