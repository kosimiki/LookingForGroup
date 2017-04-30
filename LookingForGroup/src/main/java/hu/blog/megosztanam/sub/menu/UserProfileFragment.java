package hu.blog.megosztanam.sub.menu;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lookingforgroup.R;
import com.squareup.picasso.Picasso;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.login.SaveSharedPreference;
import hu.blog.megosztanam.model.shared.LoginResponse;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class UserProfileFragment extends Fragment {

    private LoginResponse userDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.user_profile, container, false);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.profile_picture);
        Log.i(this.getTag(), userDetails.getUser().getProfilePictureUrl());
        Picasso.with(getActivity().getBaseContext()).load( userDetails.getUser().getProfilePictureUrl()).into(imageView);


        TextView userName = (TextView) rootView.findViewById(R.id.username);
        userName.setText(userDetails.getUser().getGivenName());

        TextView summonerName = (TextView) rootView.findViewById(R.id.summoner_name);
        summonerName.setText(userDetails.getUser().getSummoner().getName());

        TextView summonerLevel = (TextView) rootView.findViewById(R.id.summoner_level);
        summonerLevel.setText(userDetails.getUser().getSummoner().getSummonerLevel().toString());
        Button logoutButton = (Button) rootView.findViewById(R.id.logou_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreference.setTokenId(getActivity().getBaseContext(), "");
                Intent redirect = new Intent(getActivity(),LoginActivity.class);
                getActivity().startActivity(redirect);
            }
        });
        return rootView;
    }
}
