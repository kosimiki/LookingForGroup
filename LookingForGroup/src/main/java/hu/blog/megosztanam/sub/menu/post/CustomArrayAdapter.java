package hu.blog.megosztanam.sub.menu.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v13.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomArrayAdapter extends ArrayAdapter<Post> {
    private ArrayList<Post> list;
    private Drawable summonersRiftMap;
    private Drawable howlingAbyssMap;
    private Drawable anyRole;
    private Drawable twistedTreelineMap;

    //this custom adapter receives an ArrayList of Post objects.
    //Post is my class that represents the data for a single row and could be anything.
    public CustomArrayAdapter(Context context, int textViewResourceId, ArrayList<Post> postList) {
        //populate the local list with data.
        super(context, textViewResourceId, postList);
         summonersRiftMap = context.getResources().getDrawable(R.drawable.summoners_rift_map_small);
         howlingAbyssMap  =context.getResources().getDrawable(R.drawable.howling_abyss_map_small);
         twistedTreelineMap = context.getResources().getDrawable(R.drawable.twisted_treeline_map_small);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anyRole = getContext().getApplicationContext().getDrawable(R.drawable.role_any);
        } else {
            anyRole = getContext().getResources().getDrawable(R.drawable.role_any);
        }

        this.list = new ArrayList<>();
        this.list.addAll(postList);
    }

    @Override
    public void remove(Post object) {
        this.list.remove(object);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflator.inflate(R.layout.looking_for_member_row, null);
        Post post = list.get(position);
        //setting the views into the ViewHolder.
//        TextView postType = (TextView) convertView.findViewById(R.id.post_type);
//        postType.setText(post.getPostType().getValue());
        if(!post.getCanApply() && !post.getIsOwner()){
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            linearLayout.setAlpha(0.5f);
            TextView alreadyApplied = (TextView) convertView.findViewById(R.id.already_applied);
            alreadyApplied.setVisibility(View.VISIBLE);
        }
        TextView postDate = (TextView) convertView.findViewById(R.id.post_date);
        postDate.setText(DateFormat.getDateTimeInstance().format(post.getCreatedAt()));
        holder.summonerName = (TextView) convertView.findViewById(R.id.owner_summoner_name);
        holder.summonerName.setText(post.getOwner().getName());
        holder.summonerLevel = (TextView) convertView.findViewById(R.id.summoner_level_in_row);
        holder.summonerLevel.setText(String.format(Locale.ENGLISH,"%d",post.getOwner().getSummonerLevel()));
        holder.map = (TextView) convertView.findViewById(R.id.map_name);
        setMapName(post.getGameType().getMap(), holder.map);
        holder.ranked = (TextView) convertView.findViewById(R.id.ranked);
        holder.ranked.setText(post.getGameType().isRanked() ? "RANKED" : "NORMAL");
        holder.mapIcon = (ImageView) convertView.findViewById(R.id.map_icon);
        setMapImage(post.getGameType().getMap(),holder.mapIcon);
        TableRow positionsParentRow = (TableRow) convertView.findViewById(R.id.positions_row);
        TextView openPositionCount = (TextView) convertView.findViewById(R.id.open_position_count);
        openPositionCount.setText(String.valueOf(list.get(position).getOpenPositions().size()));
        setOpenPositions(post.getGameType().getMap(), list.get(position).getOpenPositions(), positionsParentRow);

        holder.minimumRank = (TextView) convertView.findViewById(R.id.min_required_rank);
        holder.maximumRank = (TextView) convertView.findViewById(R.id.max_required_rank);
        if (post.getGameType().isRanked()) {
            holder.minimumRank.setText(post.getMinimumRank().toString());
            holder.maximumRank.setText(post.getMaximumRank().toString());
        } else {
            TableRow rankRow = (TableRow) convertView.findViewById(R.id.rank_row);
            ((ViewGroup) convertView.findViewById(R.id.looking_for_members_table)).removeView(rankRow);
        }

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.description.setText(post.getDescription());
        } else {
            LinearLayout descriptionView = (LinearLayout) convertView.findViewById(R.id.description_layout);
            ((ViewGroup) convertView).removeView(descriptionView);
        }


        convertView.setTag(position);
        return convertView;
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
    void setOpenPositions(GameMap map, List<Role> roles, TableRow parent){
        if(map.equals(GameMap.SUMMONERS_RIFT)){
            for(Role role: roles){
                switch (role){
                    case SUPPORT:  ViewCompat.setAlpha(parent.findViewById(R.id.role_support_view), 1);break;
                    case TOP:      ViewCompat.setAlpha(parent.findViewById(R.id.role_top_view),     1);break;
                    case JUNGLER:  ViewCompat.setAlpha(parent.findViewById(R.id.role_jugnler_view), 1);break;
                    case BOT:      ViewCompat.setAlpha(parent.findViewById(R.id.role_bot_view),     1);break;
                    case MID:      ViewCompat.setAlpha(parent.findViewById(R.id.role_mid_view),     1);break;
                }
            }
        }else{
            List<ImageView> roleIcons = getImageViews(parent);
            for(int i = 0; i<roleIcons.size(); i++){
                roleIcons.get(i).setImageDrawable(anyRole);
                if(i<roles.size()){
                    ViewCompat.setAlpha(roleIcons.get(i),1);
                }
                if(map.equals(GameMap.TWISTED_TREE_LINE) && i>2){
                    ViewCompat.setAlpha(roleIcons.get(i),0);
                }
            }

        }

    }

    private List<ImageView> getImageViews(TableRow parent){
        List<ImageView> imageViews = new ArrayList<>();
                imageViews.add((ImageView) parent.findViewById(R.id.role_top_view));
                imageViews.add((ImageView) parent.findViewById(R.id.role_jugnler_view));
                imageViews.add((ImageView) parent.findViewById(R.id.role_mid_view));
                imageViews.add((ImageView) parent.findViewById(R.id.role_support_view));
                imageViews.add((ImageView) parent.findViewById(R.id.role_bot_view));
        return imageViews;
    }
}