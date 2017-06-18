package hu.blog.megosztanam.sub.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.rest.LFGServicesImpl;
import hu.blog.megosztanam.sub.menu.post.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class NoticeBoardFragment extends Fragment {

    private ViewGroup rootView;
    private ParcelableLoginResponse userDetails;
    private BroadcastReceiver receiver;
    private PostFilter postFilter;
    private ExpandableListView filterView;
    private static final String filter_all_maps = "All maps";
    private static final String filter_summoners_rift = "Summoners Rift";
    private static final String filter_howling_abyss = "Howling Abyss";
    private static final String filter_twisted_treeline = "Twisted Treeline";
    private CustomArrayAdapter dataAdapter;
    private Boolean firstLoadDone = false;
    private Boolean isLoading = false;
    private Boolean shouldReload = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(MessagingService.MESSAGE);
            Boolean isPost = intent.getBooleanExtra(MessagingService.MESSAGE_TYPE_IS_POST, false);
            if(isPost){
                loadPosts();
            }
            Log.i(NoticeBoardFragment.class.getName(), "Got message: " + message);
        }
    };



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        postFilter = new PostFilter();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(MessagingService.MESSAGE);
                // do something here.
            }
        };

        FloatingActionButton newPostButton = (FloatingActionButton) getActivity().findViewById(R.id.create_new_post_floating);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        shouldReload = getArguments().getBoolean(PostActivity.NEW_POST_EXTRA);

        Log.i(this.getClass().getName(), "User from intent: " + userDetails.toString());
        Log.i(this.getClass().getName(), "should reload from intent: " + shouldReload);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(getActivity(), PostActivity.class);
                ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(userDetails);
                redirect.putExtra(LoginActivity.USER_DETAILS_EXTRA, parcelableLoginResponse);
                getActivity().startActivity(redirect);
            }
        });

        loadPosts();
        loadFilter();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(MessagingService.RESULT));
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_screen, container, false);
        return rootView;
    }

    private void loadFilter(){
        CheckBox rankedCheckbox = (CheckBox)rootView.findViewById(R.id.ranked_checkbox);
        rankedCheckbox.setChecked(postFilter.showRanked);
        rankedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                postFilter.showRanked = b;
                loadPosts();
            }
        });

        CheckBox normalCheckbox = (CheckBox)rootView.findViewById(R.id.normal_checkbox);
        normalCheckbox.setChecked(postFilter.showNormal);
        normalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                postFilter.showNormal = b;
                loadPosts();
            }
        });
        List<String> strings = new ArrayList<>();
         strings.add(filter_all_maps);
         strings.add(filter_summoners_rift);
         strings.add(filter_howling_abyss);
         strings.add(filter_twisted_treeline);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.map_selector);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                switch (selected){
                    case filter_all_maps:
                        postFilter.showAllMaps = selected.equals("All maps"); break;
                    case filter_summoners_rift:
                        postFilter.showAllMaps = false;
                        postFilter.map = GameMap.SUMMONERS_RIFT;
                        break;
                    case filter_howling_abyss:
                        postFilter.showAllMaps = false;
                        postFilter.map = GameMap.HOWLING_FJORD;
                        break;
                    case filter_twisted_treeline:
                        postFilter.showAllMaps = false;
                        postFilter.map = GameMap.TWISTED_TREE_LINE;
                        break;
                }
                loadPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                postFilter.showAllMaps = true;
                loadPosts();
            }
        });
    }

    public boolean canLoad(){
        return firstLoadDone && !isLoading && shouldReload;
    }

    public void loadPosts() {
        isLoading = true;
        Log.i(this.getClass().getName(), "loading posts " );

        final DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(this);
        final ApplicationConfirmDialog applicationConfirmDialog = new ApplicationConfirmDialog(this);

        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<List<Post>> loginResponse = lfgServices.getSearchForMemberPosts(
                userDetails.getUser().getServer(),
                userDetails.getUser().getUserId(),
                postFilter.showAllMaps?null:postFilter.map,
                postFilter.showRanked&&postFilter.showNormal?null:postFilter.showRanked);

        loginResponse.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                isLoading = false;
                firstLoadDone=true;
                shouldReload = false;
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    final List<Post> posts = response.body();
                    ListView list = (ListView) rootView.findViewById(R.id.looking_for_members_list_view);
                    dataAdapter = new CustomArrayAdapter(rootView.getContext(), R.id.summoner_name, new ArrayList<>(response.body()));

                    list.setAdapter(dataAdapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.i("ITEM_CLICK", "onItemClick started");

                            Post post = posts.get(i);
                            if(post.getIsOwner()){
                                Log.i("ITEM_CLICK DELETE", "clicked post: " + post.toString());

                                deleteConfirmDialog.createDialog(getActivity(), userDetails.getUser().getUserId(), post).show();
                            }else if(post.getCanApply()){
                                Log.i("ITEM_CLICK", "clicked post: " + post.toString());
                                applicationConfirmDialog.createDialog(new ArrayList<>(post.getOpenPositions()), userDetails.getUser().getUserId(), post.getPostId(), getActivity()).show();
                                Log.i("ITEM_CLICK", "AFTER SHOW");
                            }


                        }
                    });
                } else {
                    Log.i(this.getClass().getName(), "Is successful" + response.isSuccessful());
                }
                Log.i(this.getClass().getName(), "Response body" + response.body().size());

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                isLoading = false;
                firstLoadDone=true;
                shouldReload = false;
                Log.i(this.getClass().getName(), "Failure: " + t.toString());
            }
        });

    }

    public void deletePost(Integer userId,final Post post) {
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<Boolean> response = lfgServices.deletePost(userId, post.getPostId());
        response.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.i(PostActivity.class.getName(), "Response: " + response.isSuccessful());
                Toast toast = Toast.makeText(getActivity(), "deleted: " + post.getPostId(), Toast.LENGTH_SHORT);
                toast.show();
//                dataAdapter.remove(post);
                loadPosts();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });

    }

    public void addPost(){

    }


}
