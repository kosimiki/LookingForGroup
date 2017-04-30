package hu.blog.megosztanam.model.shared.elo;

/**
 * Created by Miklós on 2016. 11. 27..
 */
public enum Tier {
    BRONZE("BRONZE"),
    SILVER("SILVER"),
    GOLD("GOLD"),
    PLATINUM("PLATINUM"),
    DIAMOND("DIAMOND"),
    CHALLENGER("CHALLENGER"),
    NORMAL("NORMAL");

    private final String value;
    Tier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
