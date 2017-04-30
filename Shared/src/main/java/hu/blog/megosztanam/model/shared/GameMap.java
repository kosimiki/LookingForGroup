package hu.blog.megosztanam.model.shared;

public enum GameMap {

    SUMMONERS_RIFT("SUMMONERS_RIFT"),
    HOWLING_FJORD("HOWLING_FJORD"),
    TWISTED_TREE_LINE("TWISTED_TREE_LINE"),
    SPECIAL("SPECIAL");

    private final String value;
    GameMap(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
