package hu.blog.megosztanam.sub.menu.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.legacy.view.ViewCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miklós on 2017. 06. 25..
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final ArrayList<Post> list;
    private final Drawable summonersRiftMap;
    private final Drawable howlingAbyssMap;
    private final Drawable anyRole;
    private final Drawable twistedTreelineMap;

    public PostAdapter(List<Post> posts, Context context) {
        summonersRiftMap = context.getResources().getDrawable(R.drawable.summoners_rift_map_small);
        howlingAbyssMap = context.getResources().getDrawable(R.drawable.howling_abyss_map_small);
        twistedTreelineMap = context.getResources().getDrawable(R.drawable.twisted_treeline_map_small);
        anyRole = context.getResources().getDrawable(R.drawable.role_any);

        this.list = new ArrayList<>();
        this.list.addAll(posts);

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.looking_for_member_row, parent, false);
        return new PostAdapter.PostViewHolder(v);
    }

    public Post getPost(int position) {
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = list.get(position);

        if(!post.getCanApply() && !post.getIsOwner()){
            LinearLayout linearLayout = holder.rowItemLayout.findViewById(R.id.item_layout);
            linearLayout.setAlpha(0.5f);
            TextView alreadyApplied = holder.rowItemLayout.findViewById(R.id.already_applied);
            alreadyApplied.setVisibility(View.VISIBLE);
        }
        holder.dateView.setText(DateFormat.getDateTimeInstance().format(post.getCreatedAt()));
        holder.summonerName.setText(post.getOwner().getName());
        holder.summonerLevel.setText(String.format(Locale.ENGLISH,"%d",post.getOwner().getSummonerLevel()));
        setMapName(post.getGameType().getMap(), holder.mapName);
        holder.ranked.setText(post.getGameType().isRanked() ? "RANKED" : "NORMAL");
        setMapImage(post.getGameType().getMap(),holder.mapIcon);

        holder.openPositionCount.setText(String.valueOf(list.get(position).getOpenPositions().size()));
        setRoles(post.getGameType().getMap(), holder, post.getOpenPositions());

        if (post.getGameType().isRanked()) {
            holder.minimumRank.setText(post.getMinimumRank().toString());
            holder.maximumRank.setText(post.getMaximumRank().toString());
        } else {
            TableRow rankRow = holder.rowItemLayout.findViewById(R.id.rank_row);
            ((ViewGroup) holder.rowItemLayout.findViewById(R.id.looking_for_members_table)).removeView(rankRow);
        }

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            holder.description.setText(post.getDescription());
        } else {
            LinearLayout descriptionView = holder.rowItemLayout.findViewById(R.id.description_layout);
            holder.rowItemLayout.removeView(descriptionView);
        }


    }

    private void setRoles(GameMap map, PostAdapter.PostViewHolder holder, List<Role> roles) {
        if (map.equals(GameMap.SUMMONERS_RIFT)) {
            for (Role role : roles) {
                switch (role) {
                    case SUPPORT:
                        holder.supportRoleView.setAlpha(1.0f);
                        break;
                    case TOP:
                        holder.topRoleView.setAlpha(1.0f);
                        break;
                    case JUNGLER:
                        holder.junglerRoleView.setAlpha(1.0f);
                        break;
                    case BOT:
                        holder.botRoleView.setAlpha(1.0f);
                        break;
                    case MID:
                        holder.midRoleView.setAlpha(1.0f);
                        break;
                }
            }
        } else {
            List<ImageView> roleIcons = getImageViews(holder);
            for (int i = 0; i < roleIcons.size(); i++) {
                roleIcons.get(i).setImageDrawable(anyRole);
                if (i < roles.size()) {
                    roleIcons.get(i).setAlpha(1.0f);
                }
                if (map.equals(GameMap.TWISTED_TREE_LINE) && i > 2) {
                    roleIcons.get(i).setAlpha(0.0f);
                }
            }
        }
    }


    private List<ImageView> getImageViews(PostAdapter.PostViewHolder holder){
        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(holder.topRoleView);
        imageViews.add(holder.junglerRoleView);
        imageViews.add(holder.midRoleView);
        imageViews.add(holder.supportRoleView);
        imageViews.add(holder.botRoleView);
        return imageViews;
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
            case SUMMONERS_RIFT:    icon.setImageDrawable(summonersRiftMap);break;
            case HOWLING_FJORD:     icon.setImageDrawable(howlingAbyssMap);break;
            default:    icon.setImageDrawable(twistedTreelineMap);break;
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
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

        TextView dateView;
        TextView openPositionCount;
        TextView minimumRank;
        TextView maximumRank;
        TextView description;

        PostViewHolder(View itemView) {
            super(itemView);
            rowItemLayout = itemView.findViewById(R.id.row_item_layout);
            mapName = itemView.findViewById(R.id.map_name);
            ranked = itemView.findViewById(R.id.ranked);
            summonerName = itemView.findViewById(R.id.owner_summoner_name);
            summonerLevel = itemView.findViewById(R.id.summoner_level_in_row);
            dateView = itemView.findViewById(R.id.post_date);
            openPositionCount = itemView.findViewById(R.id.open_position_count);
            minimumRank = itemView.findViewById(R.id.min_required_rank);
            maximumRank = itemView.findViewById(R.id.max_required_rank);
            description = itemView.findViewById(R.id.description);

            mapIcon = itemView.findViewById(R.id.map_icon);

            topRoleView = itemView.findViewById(R.id.role_top_view);
            midRoleView = itemView.findViewById(R.id.role_mid_view);
            junglerRoleView = itemView.findViewById(R.id.role_jugnler_view);
            supportRoleView = itemView.findViewById(R.id.role_support_view);
            botRoleView = itemView.findViewById(R.id.role_bot_view);

        }
    }
}
