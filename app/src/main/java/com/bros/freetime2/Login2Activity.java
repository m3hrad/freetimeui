package com.bros.freetime2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class Login2Activity extends Activity implements View.OnClickListener{

    private EditText emailRegisterEditText, passwordRegisterEditText, passwordConfirmRegisterEditText, emailLoginEditText, passwordLoginEditText;
    private String emailRegisterString, passwordRegisterString, passwordConfirmRegisterString, emailLoginString, passwordLoginString;
    private String userIdString, userAvailable, userEmail;
    private Intent intent;
    private ProgressBar progressBar;
    private static final int RC_SIGN_IN = 123;
    Button buttonLogin, buttonRegister;
    private FirebaseAuth mAuth;
    private static final String PATH_TOS = "";
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

//        if (mAuth.getCurrentUser() != null) {
//            sendLoginRequestToBack();
//            startActivity(new Intent(Login2Activity.this, MainActivity.class));
//            finish();
//        }

        setContentView(R.layout.activity_login2);

        passwordRegisterEditText = (EditText) findViewById(R.id.passwordRegister);
        passwordConfirmRegisterEditText = (EditText) findViewById(R.id.conPassRegister);
        emailRegisterEditText = (EditText) findViewById(R.id.emailRegister);
        emailLoginEditText = (EditText) findViewById(R.id.emailLogin);
        passwordLoginEditText = (EditText) findViewById(R.id.passwordLogin);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        buttonLogin = (Button) findViewById(R.id.loginButton);
        buttonRegister = (Button) findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
//        findViewById(R.id.facebookButton).setOnClickListener(this);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                saveFacebookLoginData("facebook", loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//        //
//
//        //Add YOUR Firebase Reference URL instead of the following URL
//        myFirebaseRef = new Firebase("https://androidbashfirebase.firebaseio.com/");
//
    }

    @Override
    public void onClick(View v) {

        emailLoginString = emailLoginEditText.getText().toString();
        passwordLoginString = passwordLoginEditText.getText().toString();
        emailRegisterString = emailRegisterEditText.getText().toString().trim();
        passwordRegisterString = passwordRegisterEditText.getText().toString().trim();
        passwordConfirmRegisterString = passwordConfirmRegisterEditText.getText().toString().trim();

        switch (v.getId()) {
                case R.id.loginButton:
                    loginWithEmailAndPassword();
                  break;

                case R.id.registerButton:
                    signUpWithEmailAndPassword();
                    break;

                case R.id.facebookButton:

                break;
        }
    }

    private void loginWithEmailAndPassword() {
        if (TextUtils.isEmpty(emailLoginString)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordLoginString)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate usermahya
        mAuth.signInWithEmailAndPassword(emailLoginString, passwordLoginString)
                .addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (passwordLoginString.length() < 6) {
                                passwordLoginEditText.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(Login2Activity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            sendLoginRequestToBack();
//                            Intent intent = new Intent(Login2Activity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish()
                        }
                    }
                });
}

    protected void sendLoginRequestToBack() {

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
                                                intent = new Intent(Login2Activity.this, MainActivity.class);
                                                intent.putExtra("userId", userIdString);
                                                intent.putExtra("tokenId", idToken);
                                                intent.putExtra("availableStatus", userAvailable);
                                                intent.putExtra("userEmail", userEmail);
                                                startActivity(intent);
                                                finish();
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
    private void signUpWithEmailAndPassword() {
        if (TextUtils.isEmpty(emailRegisterString)) {
            Toast.makeText(Login2Activity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(passwordRegisterString)) {
            Toast.makeText(Login2Activity.this, "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordRegisterString.length() < 6) {
            Toast.makeText(Login2Activity.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!passwordRegisterString.equals(passwordConfirmRegisterString)) {
            Toast.makeText(Login2Activity.this, "Password does not match with confirm password!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isEmailValid(emailRegisterString)) {
            Toast.makeText(Login2Activity.this, "The email doesn't have email format.", Toast.LENGTH_LONG).show();
        }
        else {
        progressBar.setVisibility(View.VISIBLE);
        //create user
        mAuth.createUserWithEmailAndPassword(emailRegisterString, passwordRegisterString)
                .addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Login2Activity.this, "createUserWithEmail:onComplete:", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login2Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            startActivity(new Intent(Login2Activity.this, MainActivity.class));
                            sendLoginRequestToBack();
                        }
                    }
                });
    }
    }

    private static boolean isEmailValid(String emailReg) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailReg);
        return matcher.matches();
    }


@Override
protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        }
}
