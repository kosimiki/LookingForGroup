package hu.blog.megosztanam.sub.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.messaging.MessageType;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.adapter.PostAdapter;
import hu.blog.megosztanam.sub.menu.dialog.ApplicationDialogService;
import hu.blog.megosztanam.sub.menu.dialog.DeleteConfirmDialog;
import hu.blog.megosztanam.sub.menu.post.PostActivity;
import hu.blog.megosztanam.sub.menu.post.PostFilter;
import hu.blog.megosztanam.sub.menu.post.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class NoticeBoardFragment extends Fragment {

    private static final String TAG = "NoticeBoardFragment";
    private ViewGroup rootView;
    private ParcelableLoginResponse userDetails;
    private ILFGService lfgService;
    private PostFilter postFilter;
    private static final String filter_all_maps = "All maps";
    private static final String filter_summoners_rift = "Summoners Rift";
    private static final String filter_howling_abyss = "Howling Abyss";
    private static final String filter_twisted_treeline = "Twisted Treeline";
    private RecyclerView recyclerView;
    private BroadcastReceiver mMessageReceiver;

    private DeleteConfirmDialog deleteConfirmDialog;
    private ApplicationDialogService applicationDialogService;
    private Map<Integer, PostApplyResponse> myApplications;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApplications = new HashMap<>();
        final FragmentActivity parentActivity = getActivity();
        final MainMenuActivity activity = (MainMenuActivity) parentActivity;
        this.lfgService = activity.getLfgService();
        postFilter = new PostFilter();

        FloatingActionButton newPostButton = parentActivity.findViewById(R.id.create_new_post_floating);
        final FloatingActionButton reloadPostsButton = parentActivity.findViewById(R.id.reload_posts);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        getMyApplications();

        reloadPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPosts("reload button");
                reloadPostsButton.setVisibility(View.INVISIBLE);
                reloadPostsButton.setEnabled(false);
            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(parentActivity, PostActivity.class);
                ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(userDetails);
                redirect.putExtra(LoginActivity.USER_DETAILS_EXTRA, parcelableLoginResponse);
                parentActivity.startActivity(redirect);
            }
        });

        loadPosts("activity created");
        loadFilter();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "fromMessageService: " + intent.getStringExtra("fromMessageService"));
                Toast toast = Toast.makeText(getActivity(), R.string.posts_changed, Toast.LENGTH_SHORT);
                toast.show();
                reloadPostsButton.setEnabled(true);
                reloadPostsButton.setVisibility(View.VISIBLE);
            }
        };
        IntentFilter intentFilter = new IntentFilter(MessagingService.NEW_POST);
        intentFilter.addAction(MessageType.APPLICATION_REJECTED);
        intentFilter.addAction(MessageType.APPLICATION_CONFIRMED);
        intentFilter.addAction(MessagingService.DELETED_POST);

        deleteConfirmDialog = new DeleteConfirmDialog(this);
        applicationDialogService = new ApplicationDialogService(this, lfgService);
        LocalBroadcastManager.getInstance(parentActivity).registerReceiver(mMessageReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_screen, container, false);
        recyclerView = rootView.findViewById(R.id.looking_for_members_list_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(llm);
        return rootView;
    }

    private void getMyApplications() {
        Call<List<PostApplyResponse>> applicationsOfApplicant = lfgService.getApplicationsOfApplicant(userDetails.getUser().getUserId());
        applicationsOfApplicant.enqueue(new Callback<List<PostApplyResponse>>() {
            @Override
            public void onResponse(Call<List<PostApplyResponse>> call, Response<List<PostApplyResponse>> response) {
                for (PostApplyResponse application : response.body()) {
                    myApplications.put(application.getPost().getPostId(), application);
                }
            }

            @Override
            public void onFailure(Call<List<PostApplyResponse>> call, Throwable t) {

            }
        });
    }

    private void loadFilter() {
        CheckBox rankedCheckbox = rootView.findViewById(R.id.ranked_checkbox);
        rankedCheckbox.setChecked(postFilter.showRanked);
        rankedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                postFilter.showRanked = b;
                loadPosts("checkbox");
            }
        });

        List<String> strings = new ArrayList<>();
        strings.add(filter_all_maps);
        strings.add(filter_summoners_rift);
        strings.add(filter_howling_abyss);
        strings.add(filter_twisted_treeline);
        Spinner spinner = rootView.findViewById(R.id.map_selector);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                switch (selected) {
                    case filter_all_maps:
                        postFilter.showAllMaps = true;
                        break;
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
                loadPosts("map selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                postFilter.showAllMaps = true;
                loadPosts("show all maps");
            }
        });
    }

    public void loadPosts(String reason) {
        Log.i(this.getClass().getName(), "loading posts - " + reason);


        final Integer userId = userDetails.getUser().getUserId();
        Call<List<Post>> posts = lfgService.getSearchForMemberPosts(
                userDetails.getUser().getServer(),
                userId,
                postFilter.showAllMaps ? null : postFilter.map,
                postFilter.showRanked ? null : false);

        posts.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    final PostAdapter adapter = new PostAdapter(response.body(), getActivity().getBaseContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
                            Post post = adapter.getPost(position);
                            if (post.getIsOwner()) {
                                deleteConfirmDialog.createDialog(getActivity(), userId, post, position).show();
                            } else if (post.getCanApply()) {
                                applicationDialogService.createApplicationDialog(new ArrayList<>(post.getOpenPositions()), userId, post.getPostId(), getActivity()).show();
                            } else {
                                PostApplyResponse application = myApplications.get(post.getPostId());
                                boolean accepted = application != null && application.isAccepted();
                                applicationDialogService.managementDialog(getActivity(), userId, post, accepted).show();
                            }
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    }));

                } else {
                    Log.i(this.getClass().getName(), "Is successful" + response.isSuccessful());
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.i(this.getClass().getName(), "Failure: " + t.toString());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void deletePost(Integer userId, final Post post, final int position) {
        Call<Boolean> response = lfgService.deletePost(userId, post.getPostId());
        response.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast toast = Toast.makeText(getActivity(), R.string.deleted_post + " " + post.getPostId(), Toast.LENGTH_SHORT);
                toast.show();
                PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
                adapter.remove(position);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });

    }


    public void revokeApplication(Integer userId, final Post post) {
        Call<Void> response = lfgService.revokeApplication(userId, post.getPostId());
        response.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast toast = Toast.makeText(getActivity(), R.string.application_revoked, Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });

    }


    public void confirmApplication(Integer userId, Post post) {
        Call<Void> response = lfgService.confirmApplication(userId, post.getPostId());
        response.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast toast = Toast.makeText(getActivity(), R.string.application_confirmed, Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });
    }
}
