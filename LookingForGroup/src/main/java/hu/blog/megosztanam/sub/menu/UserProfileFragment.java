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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

        TextView summonerLevel = (TextView) rootView.findViewById(R.id.profile_lvl);
        summonerLevel.setText(userDetails.getUser().getSummoner().getSummonerLevel().toString());
        ImageView summonerIcon = (ImageView) rootView.findViewById(R.id.profile_summoner_icon);
        Button logoutButton = (Button) rootView.findViewById(R.id.logou_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreference.setTokenId(getActivity().getBaseContext(), "");
                Intent redirect = new Intent(getActivity(),LoginActivity.class);
                getActivity().startActivity(redirect);
            }
        });
        TextView summonerServer = (TextView) rootView.findViewById(R.id.server);
        summonerServer.setText(userDetails.getUser().getServer().getValue());
        try {
            Picasso.with(getActivity().getBaseContext()).load("http://avatar.leagueoflegends.com/"+ userDetails.getUser().getServer().getValue() +"/"+ URLEncoder.encode(userDetails.getUser().getSummoner().getName(), "utf-8") +".png").into(summonerIcon);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
