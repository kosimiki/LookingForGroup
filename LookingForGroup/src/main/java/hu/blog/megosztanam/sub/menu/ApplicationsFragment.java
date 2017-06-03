package hu.blog.megosztanam.sub.menu;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.rest.LFGServicesImpl;
import hu.blog.megosztanam.sub.menu.application.ApplicationAdapter;
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
public class ApplicationsFragment extends Fragment {

    private ViewGroup rootView;
    private ParcelableLoginResponse userDetails;
    private RecyclerView recyclerView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.applications_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(llm);
        loadPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.applications_sub_menu, container, false);
        this.rootView = rootView;

        return rootView;
    }

    private void loadPosts() {
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<List<PostApplyResponse>> loginResponse = lfgServices.getApplications(userDetails.getUser().getUserId());
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
