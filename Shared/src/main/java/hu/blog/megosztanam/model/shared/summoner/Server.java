package hu.blog.megosztanam.model.shared.summoner;

/**
 * Created by Miklós on 2017. 04. 30..
 */
public enum Server {
    EUNE("EUNE"),
    EUW("EUW");
    private final String value;
    Server(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
