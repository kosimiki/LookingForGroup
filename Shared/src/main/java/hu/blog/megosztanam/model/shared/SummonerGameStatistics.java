package hu.blog.megosztanam.model.shared;

import hu.blog.megosztanam.model.shared.elo.Rank;

/**
 * Created by Miklós on 2017. 04. 30..
 */
public class SummonerGameStatistics {
    private Rank soloRank;
    private Rank flexRank;
    private Rank twistedRank;
    private String summonerName;

    public SummonerGameStatistics() {
    }

    public SummonerGameStatistics(Rank soloRank, Rank flexRank, Rank twistedRank, String summonerName) {
        this.soloRank = soloRank;
        this.flexRank = flexRank;
        this.twistedRank = twistedRank;
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
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

    public Rank getTwistedRank() {
        return twistedRank;
    }

    public void setTwistedRank(Rank twistedRank) {
        this.twistedRank = twistedRank;
    }
}
