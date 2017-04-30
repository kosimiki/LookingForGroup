package hu.blog.megosztanam.model.shared;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class GameType {

    protected GameMap map;
    protected boolean ranked;

    public GameType(GameMap map, boolean ranked) {
        this.map = map;
        this.ranked = ranked;
    }

    public GameType() {
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean isRanked() {
        return ranked;
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }
}
