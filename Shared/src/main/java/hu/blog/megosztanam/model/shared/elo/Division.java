package hu.blog.megosztanam.model.shared.elo;
/**
 * Created by Miklós on 2017. 04. 20..
 */
public enum Division {

    I("I"),
    II("II"),
    III("III"),
    IV("IV"),
    V("V");

    private final String value;
    Division(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
