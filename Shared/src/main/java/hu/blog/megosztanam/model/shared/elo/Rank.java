package hu.blog.megosztanam.model.shared.elo;

/**
 * Created by Miklós on 2016. 11. 27..
 */
public class Rank {

    protected Tier tier;
    protected Division division;

    public Rank() {
    }

    public Rank(Tier tier, Division division) {
        this.tier = tier;
        this.division = division;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    @Override
    public String toString() {
        return (tier==null?"null":tier.getValue()) + " " + (division==null?"null":division.getValue());
    }
}
