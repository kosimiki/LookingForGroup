package hu.blog.megosztanam.sub.menu.post;

import android.content.Context;
import com.tech.freak.wizardpager.model.*;

public class PostWizardModel extends AbstractWizardModel {
    public PostWizardModel(Context context) {
        super(context);
    }

    public static final String MAP_KEY = "Map";
    public static final String SUMMONERS_RIFT = "Summoners Rift";
    public static final String TWISTED_TREELINE = "Twisted Treeline";
    public static final String HOWLING_FJORD = "Howling Fjord";
    public static final String OPEN_POSITIONS = "Open positions";
    public static final String MIN_TIER = "Minimum tier";
    public static final String MAX_TIER = "Maximum tier";
    public static final String MIN_DIV = "Minimum division";
    public static final String MAX_DIV = "Maximum division";
    public static final String GAME_TYPE = "Game type";
    public static final String RANKED = "Ranked";
    public static final String NORMAL = "Normal";
    public static final String COMMENT = "Comment";

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new BranchPage(this, MAP_KEY)
                        .addBranch(SUMMONERS_RIFT, getGameTypePage(SUMMONERS_RIFT ), getSummonersRiftRoles().setRequired(true))
                        .addBranch(TWISTED_TREELINE, getGameTypePage( TWISTED_TREELINE), getTwistedTreelineRoles().setRequired(true))
                        .addBranch(HOWLING_FJORD, getHowlingFjordRoles().setRequired(true))
                        .setRequired(true),
                new TextPage(this, COMMENT)
        );
    }

    private Page getSummonersRiftRoles() {
        return new MultipleFixedChoicePage(this, OPEN_POSITIONS).setChoices("TOP", "JUNGLER", "MID", "SUPPORT", "BOT");
    }
    private Page getHowlingFjordRoles() {
        return new MultipleFixedChoicePage(this, OPEN_POSITIONS).setChoices("ANY", "ANY", "ANY","ANY", "ANY");
    }
    private Page getTwistedTreelineRoles() {
        return new MultipleFixedChoicePage(this, OPEN_POSITIONS).setChoices("ANY", "ANY", "ANY");
    }

    private Page getGameTypePage(String map) {
        return new BranchPage(this, GAME_TYPE)
                .addBranch(RANKED +" : " +map ,
                        new SingleFixedChoicePage(this, MIN_TIER).setChoices("BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND").setRequired(true),
                        new SingleFixedChoicePage(this, MIN_DIV).setChoices("V", "IV", "III", "II", "I").setRequired(true),
                        new SingleFixedChoicePage(this, MAX_TIER).setChoices("BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND").setRequired(true),
                        new SingleFixedChoicePage(this, MAX_DIV).setChoices("V", "IV", "III", "II", "I").setRequired(true))
                .addBranch(NORMAL+" : " +map).setRequired(true);
    }
}
