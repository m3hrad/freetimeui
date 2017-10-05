package com.bros.freetime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.bros.freetime.R.id.image;
import static com.bros.freetime.R.id.userStatusSwitch;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_ID = 1;
    private static final int HALF = 2;
    private Button saveButton;
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText;
    private String firstNameString, lasTNameString, emailString, phoneString, userId, tokenId;
    ImageView image;

    String ba1 = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        image = (ImageView) findViewById(R.id.image_holder);

        try {

            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream .toByteArray();

            ByteArrayOutputStream baos = new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            String ba1 =Base64.encodeToString(b, Base64.DEFAULT);

//            ba1 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (Exception e) {

        }
//        }
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        saveButton = (Button) findViewById(R.id.saveButton);
        Intent intent = getIntent();
        emailString = intent.getStringExtra("userEmailString");
        if(emailString.equals("null")) {
            emailEditText.setHint("email");} else {emailEditText.setText(emailString);}
        firstNameString = intent.getStringExtra("userFirstnameString");
        if(firstNameString.equals("null")) {
            firstNameEditText.setHint("firstname");} else {firstNameEditText.setText(firstNameString);}
        lasTNameString = intent.getStringExtra("userLastnameString");
        if(lasTNameString.equals("null")) {
            lastNameEditText.setHint("lastname");} else {lastNameEditText.setText(lasTNameString);}
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstNameEditText.setText(ba1.toString());
            }
        });
        //browsing image
        findViewById(R.id.browse_button).setOnClickListener(this);
    }
    //Browsing image from SD card
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ID);
    }

    //Browsing image from SD card
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap original = BitmapFactory.decodeStream(stream);
                image.setImageBitmap(Bitmap.createScaledBitmap(original,
                        original.getWidth() / HALF, original.getHeight() / HALF, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editProfileInfo() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getIntent().getStringExtra("userIdString");
        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId;
//        statusUser = String.valueOf(userNextStatus);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        String responseStr = response;
                        try {
                            JSONObject jsonObj = new JSONObject(responseStr);
//                            String availableString = jsonObj.getString("available");
//                            if (availableString.equals("true")) {
//                                userStatusSwitch.setText(R.string.available_status);
//                                userStatusSwitch.setChecked(true);
//                            } else if (availableString.equals("false")) {
//                                userStatusSwitch.setText(R.string.unavailable_status);
//                                userStatusSwitch.setChecked(false);
//                            } else {
//                                userStatusSwitch.setText(R.string.connection_error);
//                                Toast.makeText(getActivity(), "Connnection error!", Toast.LENGTH_SHORT).show();
//                            }
                        } catch (final JSONException e) {
//                            userStatusSwitch.setText(R.string.connection_error);
                            Toast.makeText(ProfileActivity.this, "Connnnection error!", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "Json parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        userStatusSwitch.setText(R.string.connection_error);
                        Toast.makeText(ProfileActivity.this, "Connection error!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("available", statusUser);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                tokenId = getIntent().getStringExtra("tokenIdString");
                headers.put("Authorization", tokenId);
                return headers;
            }
        };
        queue.add(putRequest);
    }

}