package hu.blog.megosztanam.sub.menu.application;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v13.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miklós on 2017. 05. 28..
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<PostApplyResponse> applicants;
    private Drawable summonersRiftMap;
    private Drawable howlingAbyssMap;
    private Drawable anyRole;
    private Drawable twistedTreelineMap;

    public ApplicationAdapter(List<PostApplyResponse> applicants, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anyRole = context.getApplicationContext().getDrawable(R.drawable.role_any);
            summonersRiftMap = context.getApplicationContext().getDrawable(R.drawable.summoners_rift_map_small);
            howlingAbyssMap = context.getApplicationContext().getDrawable(R.drawable.howling_abyss_map_small);
            twistedTreelineMap = context.getApplicationContext().getDrawable(R.drawable.twisted_treeline_map_small);
        } else {
            summonersRiftMap = context.getResources().getDrawable(R.drawable.summoners_rift_map_small);
            howlingAbyssMap = context.getResources().getDrawable(R.drawable.howling_abyss_map_small);
            twistedTreelineMap = context.getResources().getDrawable(R.drawable.twisted_treeline_map_small);
            anyRole = context.getResources().getDrawable(R.drawable.role_any);
        }
        this.applicants = applicants;
        Log.i(this.getClass().getName(), "SUCCESSFUL CONSTRUCTOR CALL ");

    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(this.getClass().getName(), "begin onCreateViewHolder ");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.applications_card, parent, false);
        Log.i(this.getClass().getName(), "end onCreateViewHolder ");

        return new ApplicationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder holder, int position) {
        Log.i(this.getClass().getName(), "begin onBindViewHolder ");
        PostApplyResponse applicant =  applicants.get(position);
        holder.ranked.setText(applicant.getPost().getGameType().isRanked() ? R.string.ranked : R.string.normal);
        holder.summonerName.setText(applicant.getSummonerName());
        holder.summonerLevel.setText(String.format(Locale.ENGLISH, "%d",applicant.getSummonerLevel()));
        setRoles(applicant.getPost().getGameType().getMap(), holder, applicant.getRoles());
        setMapImage(applicant.getPost().getGameType().getMap(), holder.mapIcon);
        setMapName(applicant.getPost().getGameType().getMap(), holder.mapName);
        holder.flexRank.setText(applicant.getFlexRank().toString());
        holder.soloRank.setText(applicant.getSoloRank().toString());
        holder.dateView.setText(DateFormat.getDateTimeInstance().format(applicant.getCreatedAt()));
        Log.i(this.getClass().getName(), "end onBindViewHolder ");
    }

    private void setMapName(GameMap gameMap, TextView map){
        switch (gameMap){
            case TWISTED_TREE_LINE: map.setText(R.string.twisted_treeline_map_name);break;
            case SUMMONERS_RIFT:    map.setText(R.string.summoners_rift_map_name);break;
            case HOWLING_FJORD:     map.setText(R.string.howling_abyss_map_name);break;
            default:     map.setText(R.string.special_map_name);break;
        }
    }

    void setMapImage(GameMap map, ImageView icon){
        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        switch (map){
            case TWISTED_TREE_LINE: icon.setImageDrawable(twistedTreelineMap); break;
            case SUMMONERS_RIFT:    icon.setImageDrawable(summonersRiftMap);break;
            case HOWLING_FJORD:     icon.setImageDrawable(howlingAbyssMap);break;
            default:    icon.setImageDrawable(twistedTreelineMap);break;
        }
    }

    private void setRoles(GameMap map, ApplicationViewHolder holder, List<Role> roles) {
        if (map.equals(GameMap.SUMMONERS_RIFT)) {
            for (Role role : roles) {
                switch (role) {
                    case SUPPORT:
                        ViewCompat.setAlpha(holder.supportRoleView, 1);
                        break;
                    case TOP:
                        ViewCompat.setAlpha(holder.topRoleView, 1);
                        break;
                    case JUNGLER:
                        ViewCompat.setAlpha(holder.junglerRoleView, 1);
                        break;
                    case BOT:
                        ViewCompat.setAlpha(holder.botRoleView, 1);
                        break;
                    case MID:
                        ViewCompat.setAlpha(holder.midRoleView, 1);
                        break;
                }
            }
        } else {
            List<ImageView> roleIcons = getImageViews(holder);
            for (int i = 0; i < roleIcons.size(); i++) {
                roleIcons.get(i).setImageDrawable(anyRole);
                if (i < roles.size()) {
                    ViewCompat.setAlpha(roleIcons.get(i), 1);
                }
                if (map.equals(GameMap.TWISTED_TREE_LINE) && i > 2) {
                    ViewCompat.setAlpha(roleIcons.get(i), 0);
                }
            }
        }
    }

    private List<ImageView> getImageViews(ApplicationViewHolder holder){
        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(holder.topRoleView);
        imageViews.add(holder.junglerRoleView);
        imageViews.add(holder.midRoleView);
        imageViews.add(holder.supportRoleView);
        imageViews.add(holder.botRoleView);
        return imageViews;
    }

    @Override
    public int getItemCount() {
        return applicants.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        CardView rowItemLayout;
        TextView mapName;
        TextView ranked;
        TextView summonerName;
        TextView summonerLevel;
        ImageView topRoleView;
        ImageView midRoleView;
        ImageView junglerRoleView;
        ImageView supportRoleView;
        ImageView botRoleView;
        ImageView mapIcon;
        TextView flexRank;
        TextView soloRank;
        TextView dateView;

        ApplicationViewHolder(View itemView) {
            super(itemView);
            Log.i(this.getClass().getName(), "begin ApplicationViewHolder ");
            rowItemLayout = (CardView) itemView.findViewById(R.id.row_item_layout);
            mapName = (TextView) itemView.findViewById(R.id.map_name);
            ranked = (TextView) itemView.findViewById(R.id.ranked);
            summonerName = (TextView) itemView.findViewById(R.id.summoner_name);
            summonerLevel = (TextView) itemView.findViewById(R.id.summoner_level);
            flexRank = (TextView) itemView.findViewById(R.id.flex_rank);
            soloRank = (TextView) itemView.findViewById(R.id.solo_rank);
            dateView = (TextView) itemView.findViewById(R.id.date_view);

            mapIcon = (ImageView) itemView.findViewById(R.id.map_icon);

            topRoleView = (ImageView) itemView.findViewById(R.id.top_role_view);
            midRoleView = (ImageView) itemView.findViewById(R.id.mid_role_view);
            junglerRoleView = (ImageView) itemView.findViewById(R.id.jugnler_role_view);
            supportRoleView = (ImageView) itemView.findViewById(R.id.support_role_view);
            botRoleView = (ImageView) itemView.findViewById(R.id.bot_role_view);
            Log.i(this.getClass().getName(), "begin ApplicationViewHolder ");

        }
    }
}
