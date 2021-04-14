package hu.blog.megosztanam.sub.menu;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.application.ApplicationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class ApplicationsFragment extends Fragment {

    private List<PostApplyResponse> applications;
    private ParcelableLoginResponse userDetails;
    private RecyclerView recyclerView;
    private ILFGService ilfgService;
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MessagingService.MESSAGE);
            boolean isPost = intent.getBooleanExtra(MessagingService.MESSAGE_TYPE_IS_POST, true);
            if (!isPost) {
                loadPosts();
            }
            Log.i(ApplicationsFragment.class.getName(), "Got message: " + message);
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        this.ilfgService = activity.getLfgService();
        loadPosts();
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter(MessagingService.RESULT));
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.applications_sub_menu, container, false);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.applications_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(llm);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    public void loadPosts() {
        Call<List<PostApplyResponse>> loginResponse = ilfgService.getApplications(userDetails.getUser().getUserId());
        loginResponse.enqueue(new Callback<List<PostApplyResponse>>() {
            @Override
            public void onResponse(Call<List<PostApplyResponse>> call, Response<List<PostApplyResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ApplicationAdapter adapter = new ApplicationAdapter(response.body(), getActivity().getBaseContext());
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.i(this.getClass().getName(), "Is successful " + response.isSuccessful());
                }
                Log.i(this.getClass().getName(), "Response body " + response.body().size());
            }

            @Override
            public void onFailure(Call<List<PostApplyResponse>> call, Throwable t) {
                Log.i(this.getClass().getName(), "Failure: " + t.toString());
            }
        });
    }
}
