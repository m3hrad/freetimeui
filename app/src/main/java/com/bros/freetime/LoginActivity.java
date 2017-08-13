package com.bros.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthstateListener;

    private static int RC_SIGN_IN = 0;
    private static String TAG = "Login Activity";

    TextView textView;

//    URL urlAuth;
//    HttpURLConnection client;
//    OutputStream outPutPost;

    TextView textView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = (TextView) findViewById(R.id.tVTest);

        textView2 = (TextView) findViewById(R.id.tVTest2);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthstateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    Log.d("AUTH", "user logged in: " + user.getEmail());
                    //For getting ID token
                    user.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if(task.isSuccessful()) {
                                        String idToken = task.getResult().getToken();
                                        Log.d("This is ID token: ", idToken);
                                        //textView.setText(idToken.toString());

                                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                        String url ="https://freetime-backend-dev.herokuapp.com/auth/";

                                    // Request a string response from the provided URL.
                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                                        textView2.setText("Response is: "+ response.substring(0,500));
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                textView2.setText("That didn't work!");
                                            }
                                        });
                                    // Add the request to the RequestQueue.
                                        queue.add(stringRequest);

                                    } else {

                                    }
                                }
                            });

                } else
                    Log.d("AUTH", "user logged out.");
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_ont_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthstateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthstateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthstateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseLoginWithGoogle(account);
            //getting ID token:
            String idToken = account.getIdToken();
            Log.d("TAG", idToken);

        } else {
            Log.d("TAG", "Google login failed");
        }
    }

    private void firebaseLoginWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   Log.d("AUTH", "Sign in with credential: oncomplete " + task.isSuccessful());
                    }
                });
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        textView.setText("");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "connection failed!");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_ont_button:
                signOut();
                break;
        }
    }

}
