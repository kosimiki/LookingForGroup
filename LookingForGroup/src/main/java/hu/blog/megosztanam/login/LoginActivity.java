package hu.blog.megosztanam.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.util.LoLService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "LoginActivity";
    public static final String USER_DETAILS_EXTRA = "hu.blog.megosztanam.user.extra";
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;

    private SearchView summonerName;
    private boolean registrationRequired;
    private String idToken;
    private Server server = Server.EUW;
    private Button acceptSummonerButton;
    private Button searchSummonerButton;
    private SignInButton googleSignInButton;
    private ImageView summonerIcon;
    private TextView summonerLevel;
    private TextView foundSummonerName;
    private LinearLayout regForm;
    private GoogleAuthService authService;
    private ILFGService lfgService;
    private boolean privacyAccepted = false;
    private boolean foundSummoner = false;
    private Button backToAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication application = (MainApplication) getApplication();
        authService = application.getAppContainer().getAuthService();
        lfgService = application.getAppContainer().getLfgService();
        registrationRequired = false;
        setContentView(R.layout.activity_login);
        foundSummonerName = findViewById(R.id.found_summoner_name);
        CheckBox privacyAndPolicyCheckbox = findViewById(R.id.privacy_and_policy_checkbox);
        privacyAndPolicyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                privacyAccepted = isChecked;
                acceptSummonerButton.setEnabled(foundSummoner && privacyAccepted);
            }
        });
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

        final Activity activity = this;
        backToAcc = findViewById(R.id.back_to_account_selection);
        backToAcc.setVisibility(View.INVISIBLE);
        backToAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authService.logoutUser(activity);
            }
        });

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

        RadioGroup radioGroup = findViewById(R.id.region_group);

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

        authService.getGoogleSignInClient().silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            Intent signInIntent = authService.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void doBackEndLogin(String token) {
        idToken = token;
        Call<LoginResponse> loginResponse = lfgService.doLogin(idToken);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                switch (loginResponse.getLoginStatus()) {
                    case SUCCESSFUL:
                        registrationRequired = false;
                        updateFirebaseId(loginResponse, idToken);
                        break;
                    case REGISTRATION_REQUIRED:
                        updateForReg(getString(R.string.user_has_no_summoner));
                        break;
                    case FAILED:
                        handleSummonerResponse(getString(R.string.failed_to_login));
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
        String text = user.getSummoner().getName();
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


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            idToken = account.getIdToken();
            doBackEndLogin(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            handleSummonerResponse("Logged off");
        }
    }

    private void handleSummonerResponse(String msg, Summoner summoner) {
        foundSummonerName.setText(summoner.getName());
        summonerLevel.setText(String.format(Locale.ENGLISH, "LVL: %d", summoner.getSummonerLevel()));
        handleSummonerResponse(msg);
    }


    private void handleSummonerResponse(String message) {
        acceptSummonerButton.setEnabled(foundSummoner && privacyAccepted);
        mStatusTextView.setText(message);
        setSummonerDetailVisibility(foundSummoner);
    }

    private void setSummonerDetailVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.INVISIBLE;
        foundSummonerName.setVisibility(visibility);
        summonerLevel.setVisibility(visibility);
        summonerIcon.setVisibility(visibility);
    }


    private void updateForReg(String msg) {
        regForm.setVisibility(View.VISIBLE);
        backToAcc.setVisibility(View.VISIBLE);
        googleSignInButton.setVisibility(View.INVISIBLE);
        googleSignInButton.setEnabled(false);
        mStatusTextView.setText(msg);
        registrationRequired = true;
    }


    private void handleSummoner(String name, final ISummonerResponseHandler handler) {
        Call<Summoner> summonerCall = lfgService.getSummoner(name, server);
        summonerCall.enqueue(new Callback<Summoner>() {
            @Override
            public void onResponse(Call<Summoner> call, Response<Summoner> response) {
                if (response.isSuccessful()) {
                    Summoner summoner = response.body();
                    if (summoner.getId() == null) {
                        handler.notFound();
                        foundSummoner = false;
                    } else {
                        foundSummoner = true;
                        handler.found(summoner);
                    }
                } else {
                    foundSummoner = false;
                    handleSummonerResponse(getString(R.string.summoner_not_found));
                }
            }

            @Override
            public void onFailure(Call<Summoner> call, Throwable t) {
                foundSummoner = false;
                handleSummonerResponse(getString(R.string.summoner_not_found));
            }
        });
    }

    private void handleSummonerName(String name) {
        handleSummoner(name, new ISummonerResponseHandler() {
            @Override
            public void notFound() {
                handleSummonerResponse(getString(R.string.summoner_not_found));
            }

            @Override
            public void found(Summoner summoner) {
                handleRegistration(summoner.getId());
            }
        });
    }

    private void getSummonerDetails(String name) {
        handleSummoner(name, new ISummonerResponseHandler() {
            @Override
            public void notFound() {
                handleSummonerResponse(getString(R.string.summoner_not_found));
            }

            @Override
            public void found(Summoner summoner) {
                Picasso.with(getBaseContext())
                        .load(LoLService.getSummonerIconUrl(summoner.getProfileIconId()))
                        .into(summonerIcon);
                handleSummonerResponse(getString(R.string.summoner_found), summoner);
            }
        });
    }

    private void handleRegistration(String summonerId) {
        Call<LoginResponse> loginResponse = lfgService.doRegistration(idToken, summonerId, server);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final LoginResponse loginResponse = response.body();
                    switch (loginResponse.getLoginStatus()) {
                        case SUCCESSFUL:
                            handleSummonerResponse(loginResponse.getUser().getSummoner().getName() + "\n " + loginResponse.getUser().getSummoner().getId());
                            registrationRequired = false;
                            updateFirebaseId(loginResponse, idToken);
                            break;
                        case REGISTRATION_REQUIRED:
                            int userHasNoSummoner = R.string.user_has_no_summoner;
                            updateForReg(getString(userHasNoSummoner));
                            break;
                        case FAILED:
                            int failedToLogin = R.string.failed_to_login;
                            handleSummonerResponse(getString(failedToLogin));
                            break;
                    }
                } else {
                    try {
                        Log.i(TAG, response.errorBody().toString());
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getLocalizedMessage(), ex);
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handleSummonerResponse(getString(R.string.summoner_not_found));
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
                        SaveSharedPreference.setFirebaseId(getBaseContext(), firebaseToken);
                        SaveSharedPreference.setUserId(getBaseContext(), userId);
                        SaveSharedPreference.setTokenId(getBaseContext(), token);
                        updateFirebaseId(userId, firebaseToken);
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

