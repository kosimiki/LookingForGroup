package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2017. 04. 22..
 */
public enum PostType {

    LOOKING_FOR_TEAM("LOOKING_FOR_TEAM"),
    LOOKING_FOR_MEMBER("LOOKING_FOR_MEMBER");

    private final String value;
    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
