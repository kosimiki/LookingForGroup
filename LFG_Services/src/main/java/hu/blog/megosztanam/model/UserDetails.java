package hu.blog.megosztanam.model;

import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class UserDetails {
    private String summonerId;
    private Integer userId;
    private Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
