package hu.blog.megosztanam.sub.menu.post;

import android.content.Context;
import android.graphics.Color;
import android.support.v13.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Post> {
    private ArrayList<Post> list;
    //this custom adapter receives an ArrayList of Post objects.
    //Post is my class that represents the data for a single row and could be anything.
    public CustomArrayAdapter(Context context, int textViewResourceId, ArrayList<Post> postList) {
        //populate the local list with data.
        super(context, textViewResourceId, postList);
        this.list = new ArrayList<>();
        this.list.addAll(postList);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(this.getClass().getName(), "getView runs");
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflator.inflate(R.layout.looking_for_member_row, null);

        Post post = list.get(position);
        //setting the views into the ViewHolder.
        Log.i(this.getClass().getName(), "before sets end");
        TextView postType = (TextView) convertView.findViewById(R.id.post_type);
        postType.setText(post.getPostType().getValue());
        if(position % 2 == 0){
            convertView.setBackgroundColor(Color.parseColor("#AF3342A1"));
        }else {
            convertView.setBackgroundColor(Color.parseColor("#BF3342A1"));
        }

        holder.summonerName = (TextView) convertView.findViewById(R.id.owner_summoner_name);
        holder.summonerName.setText(post.getOwner().getName());
        holder.summonerLevel = (TextView) convertView.findViewById(R.id.summoner_level_in_row);
        holder.summonerLevel.setText(post.getOwner().getSummonerLevel().toString());
        holder.map = (TextView) convertView.findViewById(R.id.map_name);
        holder.map.setText(post.getGameType().getMap().getValue());
        holder.ranked = (TextView) convertView.findViewById(R.id.ranked);
        holder.ranked.setText(post.getGameType().isRanked() ? "Ranked" : "Normal");
        Log.i(this.getClass().getName(), "after strings");
        TableRow positionsParentRow = (TableRow) convertView.findViewById(R.id.positions_row);
        TextView openPositionCount = (TextView) convertView.findViewById(R.id.open_position_count);
        openPositionCount.setText(String.valueOf(list.get(position).getOpenPositions().size()));
        setOpenPositions(post.getGameType().getMap(), list.get(position).getOpenPositions(), positionsParentRow);
        Log.i(this.getClass().getName(), "after  positions");

        holder.minimumRank = (TextView) convertView.findViewById(R.id.min_required_rank);
        holder.maximumRank = (TextView) convertView.findViewById(R.id.max_required_rank);
        if (post.getGameType().isRanked()) {
            holder.minimumRank.setText(post.getMinimumRank().toString());
            holder.maximumRank.setText(post.getMaximumRank().toString());
        } else {
            TableRow rankRow = (TableRow) convertView.findViewById(R.id.rank_row);
            ((ViewGroup) convertView.findViewById(R.id.looking_for_members_table)).removeView(rankRow);
        }

        Log.i(this.getClass().getName(), "after ranks");
        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.description.setText(post.getDescription());
        } else {
            LinearLayout descriptionView = (LinearLayout) convertView.findViewById(R.id.description_layout);
            ((ViewGroup) convertView).removeView(descriptionView);
        }
        Log.i(this.getClass().getName(), "after description");


        convertView.setTag(position);
        Log.i(this.getClass().getName(), "getView end");
        return convertView;
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    roleIcons.get(i).setImageDrawable(getContext().getApplicationContext().getDrawable(R.drawable.role_any));
                } else {
                    roleIcons.get(i).setImageDrawable(getContext().getResources().getDrawable(R.drawable.role_any));
                }
                if(i<roles.size()){
                    ViewCompat.setAlpha(roleIcons.get(i),1);
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