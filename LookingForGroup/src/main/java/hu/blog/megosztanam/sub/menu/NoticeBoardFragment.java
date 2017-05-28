package hu.blog.megosztanam.sub.menu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.rest.LFGServicesImpl;
import hu.blog.megosztanam.sub.menu.post.ApplicationConfirmDialog;
import hu.blog.megosztanam.sub.menu.post.CustomArrayAdapter;
import hu.blog.megosztanam.sub.menu.post.PostActivity;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(MessagingService.MESSAGE);
                // do something here.
            }
        };

        Button newPostButton = (Button) getActivity().findViewById(R.id.new_post);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        Log.i(this.getTag(), "User from intent: " + userDetails.toString());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_screen, container, false);
        return rootView;
    }

    private void loadPosts() {
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<List<Post>> loginResponse = lfgServices.getSearchForMemberPosts(userDetails.getUser().getServer());
        loginResponse.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    final List<Post> posts = response.body();
                    ListView list = (ListView) rootView.findViewById(R.id.looking_for_members_list_view);
                    CustomArrayAdapter dataAdapter = new CustomArrayAdapter(rootView.getContext(), R.id.summoner_name, new ArrayList<>(response.body()));
                    list.setAdapter(dataAdapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.i("ITEM_CLICK", "onItemClick started");

                            Post post = posts.get(i);
                            Log.i("ITEM_CLICK", "clicked post: " + post.toString());
                            ApplicationConfirmDialog confirmDialog = new ApplicationConfirmDialog();
                            confirmDialog.createDialog(new ArrayList<>(post.getOpenPositions()), userDetails.getUser().getUserId(), post.getPostId(), getActivity()).show();
                            Log.i("ITEM_CLICK", "AFTER SHOW");

                        }
                    });
                } else {
                    Log.i(this.getClass().getName(), "Is successful" + response.isSuccessful());
                }
                Log.i(this.getClass().getName(), "Response body" + response.body().size());

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.i(this.getClass().getName(), "Failure: " + t.toString());
            }
        });

    }



}
