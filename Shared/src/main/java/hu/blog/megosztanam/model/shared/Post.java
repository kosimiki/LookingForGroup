package hu.blog.megosztanam.model.shared;


import hu.blog.megosztanam.model.shared.elo.Rank;
import hu.blog.megosztanam.model.shared.summoner.Server;

import java.util.Date;
import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class Post {

    protected Integer userId;
    protected Integer postId;
    protected Summoner owner;
    protected GameType gameType;
    protected List<Role> openPositions;
    protected Rank minimumRank;
    protected Rank maximumRank;
    protected String description;
    protected Date createdAt;
    protected Boolean persistent;
    protected Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Boolean getPersistent() {
        return persistent;
    }

    public void setPersistent(Boolean persistent) {
        this.persistent = persistent;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    protected PostType postType;

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

    public Summoner getOwner() {
        return owner;
    }

    public void setOwner(Summoner owner) {
        this.owner = owner;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public List<Role> getOpenPositions() {
        return openPositions;
    }

    public void setOpenPositions(List<Role> openPositions) {
        this.openPositions = openPositions;
    }

    public Rank getMinimumRank() {
        return minimumRank;
    }

    public void setMinimumRank(Rank minimumRank) {
        this.minimumRank = minimumRank;
    }

    public Rank getMaximumRank() {
        return maximumRank;
    }

    public void setMaximumRank(Rank maximumRank) {
        this.maximumRank = maximumRank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId=" + userId +
                ", postId=" + postId +
                ", owner=" + owner +
                ", gameType=" + gameType +
                ", openPositions=" + openPositions +
                ", minimumRank=" + minimumRank +
                ", maximumRank=" + maximumRank +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", postType=" + postType +
                '}';
    }
}
