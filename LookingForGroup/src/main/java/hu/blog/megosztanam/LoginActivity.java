package hu.blog.megosztanam;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.lookingforgroup.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.rest.LFGServicesImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "LoginActivity";
    public static final String USER_DETAILS_EXTRA = "hu.blog.megosztanam.user.extra";
    private static final String CLIENT_ID = "212782821519-icf9ol2h2pqi847ba88q49ccui5aqva7.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;

    private EditText summonerName;
    private boolean registrationRequired;
    private String idToken;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registrationRequired = false;
        setContentView(R.layout.activity_login);
        mStatusTextView = (TextView) findViewById(R.id.status);
        summonerName = (EditText) findViewById(R.id.summonerName);
        summonerName.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {}

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        if(registrationRequired){
            handleSummonerName();
        }else{
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
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

    private void doBackEndLogin(String token){
        idToken = token;
        Log.i(TAG,"THIS IS NEW THREAD ");
        Log.i(TAG, "GOT TOKEN: " + idToken);
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<LoginResponse> loginResponse = lfgServices.doLogin(idToken);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                Log.i(TAG, "AFTER LOGIN: " + loginResponse.getLoginStatus());
                switch (loginResponse.getLoginStatus()){
                    case SUCCESSFUL: updateUI(true, loginResponse.getUser().getGivenName() + "\n "+ loginResponse.getUser().getSummoner().getName() );
                        registrationRequired = false;
                        updateUISuccessfulLogin(loginResponse);
                        break;
                    case REGISTRATION_REQUIRED:
                        updateForReg(loginResponse.getUser().getGivenName() + " has no summoner registered");
                        break;
                    case FAILED:
                        updateUI(false, loginResponse.getUser().getGivenName());
                        break;
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });

    }

    private void updateUISuccessfulLogin(LoginResponse response){
        mStatusTextView.setText(response.getUser().getGivenName() + " " + response.getUser().getSummoner().getName());
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        summonerName.setVisibility(View.GONE);
        Log.i(TAG,"START NEW ACTIVITY");
        startMainMenu(response);
    }

    private void startMainMenu(LoginResponse response){
        Intent intent = new Intent(this, MainMenuActivity.class);
        ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(response);
        intent.putExtra(USER_DETAILS_EXTRA, parcelableLoginResponse);
        startActivity(intent);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            acct = result.getSignInAccount();
            idToken = acct.getIdToken();
            doBackEndLogin(acct.getIdToken());
        } else {
            updateUI(false,result.getStatus().toString() );
        }
    }
    private void updateUI(boolean signedIn, String msg) {
        if (signedIn) {
            mStatusTextView.setText(msg);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            mStatusTextView.setText(msg);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private void updateForReg(String msg){
        summonerName.setVisibility(View.VISIBLE);
        mStatusTextView.setText(msg);
        registrationRequired = true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void handleSummonerName(){
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<Summoner> summonerCall = lfgServices.getSummoner( summonerName.getText().toString());
        summonerCall.enqueue(new Callback<Summoner>() {
            @Override
            public void onResponse(Call<Summoner> call, Response<Summoner> response) {
                if(response.isSuccessful()){
                    Summoner summoner = response.body();
                    if(summoner.getId() == -1){
                        updateUI(false, "NO SUMMONER FOUND");
                    }else{
                        handleRegistration(summoner.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<Summoner> call, Throwable t) {
            }
        });
    }

    private void handleRegistration(Integer summonerId){
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<LoginResponse> loginResponse = lfgServices.doRegistration(idToken, summonerId);
        loginResponse.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                switch (loginResponse.getLoginStatus()){
                    case SUCCESSFUL: updateUI(true, loginResponse.getUser().getGivenName() + "\n "+ loginResponse.getUser().getSummoner().getName()  + "\n "+ loginResponse.getUser().getSummoner().getId() );
                        registrationRequired = false;
                        updateUISuccessfulLogin(loginResponse);
                        break;
                    case REGISTRATION_REQUIRED:
                        updateForReg(loginResponse.getUser().getGivenName() + " has no summoner registered");
                        break;
                    case FAILED:
                        updateUI(false, loginResponse.getUser().getGivenName());
                        break;
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }
}

