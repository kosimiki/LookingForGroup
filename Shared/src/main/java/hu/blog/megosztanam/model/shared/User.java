package hu.blog.megosztanam.model.shared;

import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public class User {

    protected Integer userId;
    protected String idTokenString;
    protected String googleId;
    protected Boolean authenticated;
    protected Summoner summoner;
    protected String idToken;
    protected Server server;
    protected String messageToken;

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

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

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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
                ", googleId='" + googleId + '\'' +
                ", authenticated=" + authenticated +
                ", summoner=" + summoner +
                ", idToken='" + idToken + '\'' +
                ", server ='" + (server == null ? null : server.getValue()) + '\'' +
                '}';
    }
}
