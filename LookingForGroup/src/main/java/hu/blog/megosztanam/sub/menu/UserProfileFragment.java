package hu.blog.megosztanam.sub.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.login.GoogleAuthService;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.login.SaveSharedPreference;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.util.LoLService;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class UserProfileFragment extends Fragment {

    private GoogleAuthService authService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        this.authService = activity.getAuthService();
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.user_profile, container, false);
        LoginResponse userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        if(userDetails == null) {
            Log.w("UserProfileFragment", "Missing user details");
            return rootView;
        }

        User user = userDetails.getUser();

        ImageView imageView = (ImageView) rootView.findViewById(R.id.profile_picture);
        Picasso.with(getActivity().getBaseContext()).load(user.getProfilePictureUrl()).into(imageView);

        TextView userName = (TextView) rootView.findViewById(R.id.username);
        userName.setText(user.getGivenName());

        TextView summonerName = (TextView) rootView.findViewById(R.id.summoner_name);
        Summoner summoner = user.getSummoner();
        summonerName.setText(summoner.getName());

        TextView summonerLevel = (TextView) rootView.findViewById(R.id.profile_lvl);
        summonerLevel.setText(String.valueOf(summoner.getSummonerLevel()));

        ImageView summonerIcon = (ImageView) rootView.findViewById(R.id.profile_summoner_icon);
        createLogoutButton(rootView);

        TextView summonerServer = (TextView) rootView.findViewById(R.id.server);
        summonerServer.setText(user.getServer().getValue());

        setSummonerIcon(summoner, summonerIcon);
        return rootView;
    }

    private void setSummonerIcon(Summoner summoner, ImageView summonerIcon) {
        Picasso.with(getActivity().getBaseContext())
                .load(LoLService.getSummonerIconUrl(summoner.getProfileIconId()))
                .into(summonerIcon);
    }

    private void createLogoutButton(ViewGroup viewGroup) {
        final Button logoutButton = (Button) viewGroup.findViewById(R.id.logou_button);
        logoutButton.setEnabled(false);
        authService.getGoogleApiClient().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                logoutButton.setEnabled(true);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleApiClient googleApiClient = authService.getGoogleApiClient();
                PendingResult<Status> statusPendingResult = Auth.GoogleSignInApi.signOut(googleApiClient);
                statusPendingResult.setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        SaveSharedPreference.setTokenId(getActivity().getBaseContext(), "");
                        final Intent redirect = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(redirect);
                    }
                });

            }
        });
    }
}
