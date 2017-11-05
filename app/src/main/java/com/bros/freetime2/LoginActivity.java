package com.bros.freetime2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.bros.freetime2.R.id.loginButton;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthstateListener;
    private static int RC_SIGN_IN = 0;
    private static String TAG = "Login Activity";
    EditText emailRegisterEditText, passwordRegisterEditText, passwordConfirmRegisterEditText, emailLoginEditText, passwordLoginEditText;
    private String emailRegisterString, passwordRegisterString, passwordConfirmRegisterString, emailLoginString, passwordLoginString;
    private String idToken;
    CallbackManager callbackManager;
    private String loginMethod = "";
    private String userIdString, userAvailable, userEmail;
    private String statusFriend;
    private ArrayList<HashMap<String, String>> contactList;
    Intent intent;
    private FirebaseAuth auth;
    private ProgressBar simpleProgressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordRegisterEditText = (EditText) findViewById(R.id.passwordRegister);
        passwordConfirmRegisterEditText = (EditText) findViewById(R.id.conPassRegister);
        emailRegisterEditText = (EditText) findViewById(R.id.emailRegister);
        emailLoginEditText = (EditText) findViewById(R.id.emailLogin);
        passwordLoginEditText = (EditText) findViewById(R.id.passwordLogin);
        mFirebaseAuth = FirebaseAuth.getInstance();
        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);

        findViewById(R.id.googleButton).setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.facebookButton).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //keep user logged in
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            sendLoginRequestToBack();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthstateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthstateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case loginButton:
                firebaseLoginWithEmailAndPassword();
                break;
            case R.id.registerButton:
                firebaseRegisterWithEmailAndPassword();
                break;
            case R.id.googleButton:
                signInGoogle();
                loginMethod = "google";
                break;
            case R.id.facebookButton:
                signInFacebook();
                loginMethod = "facebook";
                break;
        }
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //    firebaseAuthWithGoogle
    private void firebaseLoginWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendLoginRequestToBack();
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void firebaseLoginWithEmailAndPassword() {
        passwordLoginString = passwordLoginEditText.getText().toString();
        emailLoginString = emailLoginEditText.getText().toString();

        if(!(passwordLoginString.isEmpty()) && !(emailLoginString.isEmpty())) {
            mFirebaseAuth.signInWithEmailAndPassword(emailLoginString, passwordLoginString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                sendLoginRequestToBack();
                                Log.d(TAG, "signInWithEmail:success");
                                emailLoginEditText.setText("");
                                passwordLoginEditText.setText("");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "The user does not exist or the password is not correct.",
                                        Toast.LENGTH_LONG).show();
                                updateUI(null);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, "The login username or login password is blank.", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseRegisterWithEmailAndPassword() {
        emailRegisterString = emailRegisterEditText.getText().toString();
        passwordRegisterString = passwordRegisterEditText.getText().toString();
        passwordConfirmRegisterString = passwordConfirmRegisterEditText.getText().toString();

        if (passwordRegisterString.equals(passwordConfirmRegisterString) && passwordRegisterString.length() > 5 && isEmailValid(emailRegisterString)) {
            mFirebaseAuth.createUserWithEmailAndPassword(emailRegisterString, passwordRegisterString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                sendLoginRequestToBack();
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                updateUI(user);
                                emailRegisterEditText.setText("");
                                passwordRegisterEditText.setText("");
                                passwordConfirmRegisterEditText.setText("");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "The user exists, please Login",
                                        Toast.LENGTH_LONG).show();
                                emailRegisterEditText.setText("");
                                passwordRegisterEditText.setText("");
                                passwordConfirmRegisterEditText.setText("");
                                updateUI(null);
                            }
                        }
                    });
        }
        if(passwordRegisterString.length() < 6){
            Toast.makeText(LoginActivity.this, "The password should be more than 5 characters.",Toast.LENGTH_LONG).show();
        } else
        if(!passwordRegisterString.equals(passwordConfirmRegisterString)){
            Toast.makeText(LoginActivity.this, "The password doesn't match confirm password.",Toast.LENGTH_LONG).show();
        } else
        if(!isEmailValid(emailRegisterString)){
            Toast.makeText(LoginActivity.this, "The email doesn't have email format.",Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isEmailValid(String emailReg) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailReg);
        return matcher.matches();
    }

    private void signInFacebook() {

        simpleProgressBar.setVisibility(View.VISIBLE);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookButton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
                Log.d(TAG, "handleFacebookAccessToken:" + loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken());
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true);
                }
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        simpleProgressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendLoginRequestToBack();
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (loginMethod) {
            case "facebook":
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
                //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                break;
            case "google":
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == RC_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = result.getSignInAccount();
                        firebaseLoginWithGoogle(account);
                    }
                }
                break;
        }
    }

    private void updateUI(FirebaseUser user) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed!");
    }

    protected void sendLoginRequestToBack() {

        simpleProgressBar.setVisibility(View.VISIBLE);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "https://freetime-backend-dev.herokuapp.com/auth/";
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            final String idToken = task.getResult().getToken();
                            final String emailS = mUser.getEmail();
                            // Send token to your backend via HTTPS
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.d("Response", response);
                                            try {
                                                JSONObject jsonObj = new JSONObject(response);
                                                userIdString = jsonObj.getString("id");
                                                userAvailable = jsonObj.getString("available");
                                                userEmail = jsonObj.getString("email");
                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("userId", userIdString);
                                                intent.putExtra("tokenId", idToken);
                                                intent.putExtra("availableStatus", userAvailable);
                                                intent.putExtra("userEmail", userEmail);
                                                startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("Error.Response", error.getMessage());
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("email", emailS);
                                    return params;
                                }
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("Authorization", idToken);
                                    return headers;
                                }
                            };
                            queue.add(postRequest);

                        } else {
                            // Handle error -> task.getException();
                            Log.d("TAG", task.getException().toString());
                        }
                    }
                });
    }
}