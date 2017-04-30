package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2016. 11. 27..
 */
public enum Role {
    TOP("TOP"),
    MID("MID"),
    JUNGLER("JUNGLER"),
    BOT("BOT"),
    SUPPORT("SUPPORT"),
    ANY("ANY");

    private final String value;
    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
