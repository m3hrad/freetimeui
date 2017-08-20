package com.bros.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static com.bros.freetime.R.id.googleButton;
import static com.bros.freetime.R.id.loginButton;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthstateListener;

    private static int RC_SIGN_IN = 0;
    private static String TAG = "Login Activity";

    EditText emailRegET, passRegET, passConRegET;

    EditText emailLogET, passLogET;

    String emailReg, passwordReg, passConReg;

    String emailLog, passwordLog;

    String idToken = "";

    CallbackManager callbackManager;

//    Button googleButtonP, facebookButtonP;

    int clicked = 0 ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passRegET = (EditText) findViewById(R.id.passwordRegister);
        passConRegET = (EditText) findViewById(R.id.conPassRegister);
        emailRegET = (EditText) findViewById(R.id.emailRegister);
        emailLogET = (EditText) findViewById(R.id.emailLogin);
        passLogET = (EditText) findViewById(R.id.passwordLogin);
        emailReg = emailRegET.getText().toString();
        passwordReg = passRegET.getText().toString();
        passConReg = passConRegET.getText().toString();
        passwordLog = passLogET.getText().toString();
        emailLog = emailLogET.getText().toString();
        mFirebaseAuth = FirebaseAuth.getInstance();

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

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         //case 1 is related to facebook Login.
        switch (clicked) {
            case 1:
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
                //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            break;
            case 2:
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == RC_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = result.getSignInAccount();
                        firebaseLoginWithGoogle(account);
                    } else {

                    }
                }
                break;
            }
    }

    private void loginEmailPass() {

        emailLogET = (EditText) findViewById(R.id.emailLogin);
        passLogET = (EditText) findViewById(R.id.passwordLogin);

        passwordLog = passLogET.getText().toString();
        emailLog = emailLogET.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(emailLog, passwordLog)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            emailLogET.setText("");
                            passLogET.setText("");
                            sendReqAndRecieveRes();
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

    public static boolean isEmailValid(String emailReg) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailReg);
        return matcher.matches();
    }

    private void registerEmailPass() {

        emailReg = emailRegET.getText().toString();
        passwordReg = passRegET.getText().toString();
        passConReg = passConRegET.getText().toString();

        if (passwordReg.equals(passConReg) && passwordReg.length() > 5 && isEmailValid(emailReg)) {

            mFirebaseAuth.createUserWithEmailAndPassword(emailReg, passwordReg)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                updateUI(user);
                                login();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "The user exist, please Login",
                                        Toast.LENGTH_LONG).show();
                                emailRegET.setText("");
                                passRegET.setText("");
                                passConRegET.setText("");
                                updateUI(null);
                            }
                        }
                    });
        }
        if(!passwordReg.equals(passConReg)){
            Toast.makeText(LoginActivity.this, "The password doesnt match confirm password.",Toast.LENGTH_LONG).show();
        }
        if(passwordReg.length()<6){
            Toast.makeText(LoginActivity.this, "The password should be more than 5 character.",Toast.LENGTH_LONG).show();
        }
        if (!isEmailValid(emailReg)) {
            Toast.makeText(LoginActivity.this, "The email is not email format.",Toast.LENGTH_LONG).show();
        }
    }

    private void signInFacebook() {

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookButton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true);
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
                    Log.d(TAG, "handleFacebookAccessToken:" + loginResult.getAccessToken());
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

        } else {

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case loginButton:
                loginEmailPass();
                break;

            case R.id.registerButton:
                registerEmailPass();
                break;

            case R.id.googleButton:
                signInGoogle();
                clicked = 2;
                break;

            case R.id.facebookButton:
                signInFacebook();
                clicked = 1;
                break;
        }
    }

    //for sending idToken and recieving response also change the activity:
    private void sendReqAndRecieveRes() {

    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
    Network network = new BasicNetwork(new HurlStack());
    RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
    final String url = "https://freetime-backend-dev.herokuapp.com/auth/";
    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("Response", response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.getMessage());
                }
            }
    )
        {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("idtoken", idToken);
            params.put("email", "email");

            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("idToken", idToken);

            return headers;
        }
    };
        login();
        queue.add(postRequest);
}

    //firebaseAuthWithGoogle
    private void firebaseLoginWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            sendReqAndRecieveRes();
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            sendReqAndRecieveRes();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed!!!!!!!!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //for chenging the activity from login to another activity
    private void login() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}