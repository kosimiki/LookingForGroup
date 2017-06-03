package hu.blog.megosztanam.sub.menu.post;

import hu.blog.megosztanam.model.shared.GameMap;

public class PostFilter {
    public boolean showAllMaps;
    public GameMap map;
    public boolean showRanked;
    public boolean showNormal;

    public PostFilter(){
        showAllMaps = true;
        map = null;
        showRanked = true;
        showNormal = true;
    }

}