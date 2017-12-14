package com.bros.freetime2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_ID = 1;
    private static final int HALF = 2;
    private Button saveButton;
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText;
    private String firstNameString, lasTNameString, emailString, phoneString, userId, tokenId;
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;

    String ba1 = "Hello";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://free-time-c6774.appspot.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imgView = (ImageView) findViewById(R.id.image_holder);
        imgView.setImageBitmap(null);
        try {

            BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream .toByteArray();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
            emailEditText.setHint("email");
                emailEditText.setText(emailString);
        firstNameString = intent.getStringExtra("userFirstnameString");
            firstNameEditText.setHint("firstname");
            firstNameEditText.setText(firstNameString);
        lasTNameString = intent.getStringExtra("userLastnameString");
            lastNameEditText.setHint("lastname");
        lastNameEditText.setText(lasTNameString);
        phoneString = intent.getStringExtra("userPhoneNumberString");
        phoneEditText.setHint("phone number");
        phoneEditText.setText(phoneString);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstNameEditText.setText(ba1.toString());
//                uploadImage();
                downloadImage();
            }
        });
        //browsing image
        findViewById(R.id.browse_button).setOnClickListener(this);
    }

    // download  image from fireBase
    private void downloadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://free-time-c6774.appspot.com").child("mahyar.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bitmap);
            }
        });
    }


    //upload image to firebase:
    private void uploadImage() {
        if (filePath != null) {

            StorageReference childRef = storageRef.child("shima.jpg");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//                filePath = data.getData();
//
//                try {
//                    //getting image from gallery
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//
//                    //Setting image to ImageView
//                    imgView.setImageBitmap(bitmap);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }


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
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            filePath = data.getData();
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap original = BitmapFactory.decodeStream(stream);
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgView.setImageBitmap(Bitmap.createScaledBitmap(original,
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
