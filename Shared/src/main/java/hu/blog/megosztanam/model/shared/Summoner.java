package hu.blog.megosztanam.model.shared;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kosimiki on 2016. 11. 26..
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summoner {

    protected String id;
    protected String name;
    protected Integer profileIconId;
    protected Integer summonerLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(Integer profileIconId) {
        this.profileIconId = profileIconId;
    }


    public Integer getSummonerLevel() {
        return summonerLevel;
    }

    public void setSummonerLevel(Integer summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    @Override
    public String toString() {
        return "Summoner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profileIconId=" + profileIconId +
                ", summonerLevel=" + summonerLevel +
                '}';
    }
}
