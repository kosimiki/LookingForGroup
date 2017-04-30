package hu.blog.megosztanam.sub.menu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.rest.LFGServicesImpl;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button newPostButton = (Button) getActivity().findViewById(R.id.new_post);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(getActivity(),PostActivity.class);
                ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(userDetails);
                redirect.putExtra(LoginActivity.USER_DETAILS_EXTRA, parcelableLoginResponse);
                getActivity().startActivity(redirect);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_screen, container, false);
        loadPosts();
        return rootView;
    }

    private void loadPosts(){
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<List<Post>> loginResponse = lfgServices.getSearchForMemberPosts();
        loginResponse.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful() && response.body()!= null && !response.body().isEmpty()){
                    ListView list = (ListView) rootView.findViewById(R.id.looking_for_members_list_view);
                    CustomArrayAdapter dataAdapter = new CustomArrayAdapter(rootView.getContext(), R.id.summoner_name, new ArrayList<>(response.body()));
                    list.setAdapter(dataAdapter);
                }else {
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
