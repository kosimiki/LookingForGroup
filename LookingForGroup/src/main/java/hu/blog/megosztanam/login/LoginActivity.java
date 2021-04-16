package hu.blog.megosztanam.login;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import hu.blog.megosztanam.MainApplication;
import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.model.shared.messaging.Messaging;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.util.LoLService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener {


    private static final String TAG = "LoginActivity";
    public static final String USER_DETAILS_EXTRA = "hu.blog.megosztanam.user.extra";
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;

    private SearchView summonerName;
    private boolean registrationRequired;
    private String idToken;
    private GoogleSignInAccount acct;
    private Server server;
    private RadioGroup radioGroup;
    private Button acceptSummonerButton;
    private Button searchSummonerButton;
    private SignInButton googleSignInButton;
    private ImageView summonerIcon;
    private TextView summonerLevel;
    private TextView foundSummonerName;
    private LinearLayout regForm;
    private GoogleAuthService authService;
    private ILFGService lfgService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication application = (MainApplication) getApplication();
        authService = application.getAppContainer().getAuthService();
        lfgService = application.getAppContainer().getLfgService();

        FirebaseMessaging.getInstance().subscribeToTopic(Messaging.NEW_POSTS_TOPIC);
        server = Server.EUW;
        registrationRequired = false;
        setContentView(R.layout.activity_login);
        foundSummonerName = findViewById(R.id.found_summoner_name);
        mStatusTextView = findViewById(R.id.status);
        summonerName = findViewById(R.id.summonerName);
        summonerName.setQueryHint("Summoner name");
        summonerName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                @Override
                                                public boolean onQueryTextSubmit(String s) {
                                                    getSummonerDetails(s);
                                                    return true;
                                                }

                                                @Override
                                                public boolean onQueryTextChange(String s) {
                                                    return false;
                                                }
                                            }
        );

        Log.i(TAG, "FireBase: " + FirebaseInstanceId.getInstance().getToken());


        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(this);

        acceptSummonerButton = findViewById(R.id.button_accept_summoner);
        searchSummonerButton = findViewById(R.id.button_search_new);
        acceptSummonerButton.setOnClickListener(this);
        acceptSummonerButton.setEnabled(false);
        searchSummonerButton.setOnClickListener(this);

        summonerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptSummonerButton.setEnabled(false);
            }
        });

        summonerIcon = findViewById(R.id.summoner_icon);
        summonerLevel = findViewById(R.id.summoner_level);
        summonerIcon.setVisibility(View.INVISIBLE);

        radioGroup = findViewById(R.id.region_group);

        Log.i(TAG, "SET ON CHECK");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_eune:
                        server = Server.EUNE;
                        break;
                    case R.id.radio_button_euw:
                        server = Server.EUW;
                        break;
                }
            }
        });
        regForm = findViewById(R.id.email_login_form);
        regForm.setVisibility(View.GONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.button_search_new:
                getSummonerDetails(summonerName.getQuery().toString());
                break;
            case R.id.button_accept_summoner:
                handleSummonerName(summonerName.getQuery().toString());
                break;
        }
    }

    private void signIn() {
        if (registrationRequired) {
            handleSummonerName(summonerName.getQuery().toString());
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(authService.getGoogleApiClient());
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void doBackEndLogin(String token) {
        idToken = token;
        Log.i(TAG, "THIS IS NEW THREAD ");
        Log.i(TAG, "GOT TOKEN: " + idToken);
        Call<LoginResponse> loginResponse = lfgService.doLogin(idToken);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                Log.i(TAG, "AFTER LOGIN: " + loginResponse.getLoginStatus());
                switch (loginResponse.getLoginStatus()) {
                    case SUCCESSFUL:
                        registrationRequired = false;
                        updateFirebaseId(loginResponse, idToken);
                        break;
                    case REGISTRATION_REQUIRED:
                        updateForReg(loginResponse.getUser().getGivenName() + " has no summoner registered");
                        break;
                    case FAILED:
                        updateUI(loginResponse.getUser().getGivenName());
                        break;
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.w(t.getMessage(), t);

            }
        });

    }

    private void updateUISuccessfulLogin(LoginResponse response) {
        User user = response.getUser();
        String text = user.getGivenName() + " " + user.getSummoner().getName();
        mStatusTextView.setText(text);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        summonerName.setVisibility(View.GONE);
        Log.i(TAG, "START NEW ACTIVITY");
        startMainMenu(response);
    }

    private void startMainMenu(LoginResponse response) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        Log.i(TAG, response.toString());
        ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(response);
        Log.i(TAG, parcelableLoginResponse.toString());

        intent.putExtra(USER_DETAILS_EXTRA, parcelableLoginResponse);
        startActivity(intent);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess() && result.getSignInAccount() != null) {
            acct = result.getSignInAccount();
            idToken = acct.getIdToken();
            doBackEndLogin(acct.getIdToken());
        } else {
            updateUI(result.getStatus().toString());
        }
    }

    private void updateUI(Boolean accept, String msg, Summoner summoner) {
        if (accept) {
            foundSummonerName.setText(summoner.getName());
            summonerLevel.setText(String.format(Locale.ENGLISH, "LVL: %d", summoner.getSummonerLevel()));
        } else {
            googleSignInButton.setVisibility(View.GONE);
        }
        acceptSummonerButton.setEnabled(accept);
        mStatusTextView.setText(msg);
        setDetailViewsVisibility(accept);

    }

    private void setDetailViewsVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.INVISIBLE;
        foundSummonerName.setVisibility(visibility);
        summonerLevel.setVisibility(visibility);
        summonerIcon.setVisibility(visibility);
    }

    private void updateUI(String msg) {
        updateUI(false, msg, null);
    }


    private void updateForReg(String msg) {
        regForm.setVisibility(View.VISIBLE);
        googleSignInButton.setVisibility(View.INVISIBLE);
        googleSignInButton.setEnabled(false);
        mStatusTextView.setText(msg);
        registrationRequired = true;
    }

    private void handleSummonerName(String name) {
        Call<Summoner> summonerCall = lfgService.getSummoner(name, server);
        summonerCall.enqueue(new Callback<Summoner>() {
            @Override
            public void onResponse(Call<Summoner> call, Response<Summoner> response) {
                if (response.isSuccessful()) {
                    Summoner summoner = response.body();
                    if (summoner.getId() == null) {
                        updateUI("NO SUMMONER FOUND");
                    } else {
                        handleRegistration(summoner.getId());
                    }
                } else {
                    updateUI("NO SUMMONER FOUND");
                }
            }

            @Override
            public void onFailure(Call<Summoner> call, Throwable t) {
                updateUI("NO SUMMONER FOUND");
            }
        });
    }


    private void getSummonerDetails(String name) {
        Call<Summoner> summonerCall = lfgService.getSummoner(name, server);
        summonerCall.enqueue(new Callback<Summoner>() {
            @Override
            public void onResponse(Call<Summoner> call, Response<Summoner> response) {
                if (response.isSuccessful()) {
                    Summoner summoner = response.body();
                    if (summoner.getId() == null) {
                        updateUI("NO SUMMONER FOUND");

                    } else {
                        Picasso.with(getBaseContext())
                                .load(LoLService.getSummonerIconUrl(summoner.getProfileIconId()))
                                .into(summonerIcon);
                        updateUI(true, "Summoner found!", summoner);

                    }
                } else {
                    Log.i(TAG, "GET SUMMONER DETAILS not successful:" + response.body());
                    updateUI("NO SUMMONER FOUND");
                }
            }

            @Override
            public void onFailure(Call<Summoner> call, Throwable t) {
                Log.i(TAG, "GET SUMMONER DETAILS FAILED:" + t.getMessage());
                updateUI("NO SUMMONER FOUND");
            }
        });
    }

    private void handleRegistration(String summonerId) {
        Log.i(TAG, "REG SUMMONER:  " + summonerId);
        Call<LoginResponse> loginResponse = lfgService.doRegistration(idToken, summonerId, server);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.i(TAG, response.message());
                Log.i(TAG, response.body().toString());
                final LoginResponse loginResponse = response.body();
                switch (loginResponse.getLoginStatus()) {
                    case SUCCESSFUL:
                        updateUI(loginResponse.getUser().getGivenName() + "\n " + loginResponse.getUser().getSummoner().getName() + "\n " + loginResponse.getUser().getSummoner().getId());
                        registrationRequired = false;
                        Log.i(TAG, "SUCCESSFUL REG " + loginResponse.toString());
                        updateFirebaseId(loginResponse, idToken);
                        break;
                    case REGISTRATION_REQUIRED:
                        updateForReg(loginResponse.getUser().getGivenName() + " has no summoner registered");
                        break;
                    case FAILED:
                        updateUI(loginResponse.getUser().getGivenName());
                        break;
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                updateUI("NO SUMMONER FOUND");

            }
        });
    }

    private void updateFirebaseId(final LoginResponse loginResponse, final String token) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String firebaseToken = task.getResult();
                        Log.i(TAG, "Firebase token: " + firebaseToken);
                        Integer userId = loginResponse.getUser().getUserId();
                        updateFirebaseId(userId, firebaseToken);
                        SaveSharedPreference.setFirebaseId(getBaseContext(), firebaseToken);
                        SaveSharedPreference.setUserId(getBaseContext(), userId);
                        SaveSharedPreference.setTokenId(getBaseContext(), token);
                        updateUISuccessfulLogin(loginResponse);
                    }
                });
    }

    private void updateFirebaseId(Integer userId, String firebaseId) {
        Call<Void> updateFirebaseId = lfgService.updateFirebaseId(userId, firebaseId);
        updateFirebaseId.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(TAG, "Firebase id update failed", t);
            }
        });
    }
}

