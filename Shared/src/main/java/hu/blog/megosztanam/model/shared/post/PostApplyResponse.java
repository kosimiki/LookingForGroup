package hu.blog.megosztanam.model.shared.post;

import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.elo.Rank;

import java.util.List;

/**
 * Created by Miklós on 2017. 05. 14..
 */
public class PostApplyResponse {

    private Integer userId;
    private Integer postId;
    private String summonerName;
    private Integer summonerLevel;
    private Rank soloRank;
    private Rank flexRank;
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

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public Integer getSummonerLevel() {
        return summonerLevel;
    }

    public void setSummonerLevel(Integer summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    public Rank getSoloRank() {
        return soloRank;
    }

    public void setSoloRank(Rank soloRank) {
        this.soloRank = soloRank;
    }

    public Rank getFlexRank() {
        return flexRank;
    }

    public void setFlexRank(Rank flexRank) {
        this.flexRank = flexRank;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}