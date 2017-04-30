package hu.blog.megosztanam.model.shared;

import hu.blog.megosztanam.model.shared.elo.Rank;

/**
 * Created by Miklós on 2017. 04. 30..
 */
public class SummonerGameStatistics {
    private Rank soloRank;
    private Rank flexRank;
    private Rank twistedRank;


    public SummonerGameStatistics() {
    }

    public SummonerGameStatistics(Rank soloRank, Rank flexRank, Rank twistedRank) {
        this.soloRank = soloRank;
        this.flexRank = flexRank;
        this.twistedRank = twistedRank;
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
