package com.bros.freetime2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class HomeFragment extends Fragment {
    private String TAG = HomeFragment.class.getSimpleName();
    private ListView lv;
    private String userId;
    private String tokenId;
    private ArrayList<HashMap<String, String>> contactList;
    private String statusUser;
    private String statusFriend;
    View v;
    private boolean userNextStatus;
    Switch userStatusSwitch;
    private String userFirstStatus;
    private static boolean m_iAmVisible;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        RecieveUserFirstStatus();
        userFriendsInfoRequest();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_home, container, false);
            contactList = new ArrayList<>();
            userStatusSwitch = (Switch) v.findViewById(R.id.userStatusSwitch);
            lv = (ListView) v.findViewById(R.id.list);
                userStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        userNextStatus = isChecked;
                        setAvailabeStatus();
                    }
                });
        return v;
    }

    protected void RecieveUserFirstStatus() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "https://freetime-backend-dev.herokuapp.com/auth/";
                            // Send token to your backend via HTTPS
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.d("Response", response);
                                            try {
                                                JSONObject jsonObj = new JSONObject(response);
                                                userFirstStatus = jsonObj.getString("available");
                                                if (userFirstStatus.equals("false")) {
                                                    userStatusSwitch.setChecked(false);
                                                    userStatusSwitch.setText(R.string.unavailable_status);
                                                } else if (userFirstStatus.equals("true")) {
                                                    userStatusSwitch.setChecked(true);
                                                    userStatusSwitch.setText(R.string.available_status);
                                                } else {
                                                    Toast.makeText(getActivity(), "Network connection error, please try again", Toast.LENGTH_SHORT).show();
                                                }
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
                                    String emailS = getActivity().getIntent().getStringExtra("userEmail");
                                    params.put("email", emailS);
                                    return params;
                                }
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    String idToken = getActivity().getIntent().getStringExtra("tokenId");
                                    headers.put("Authorization", idToken);
                                    return headers;
                                }
                            };
                            queue.add(postRequest);
    }

    private void setAvailabeStatus() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId;
        statusUser = String.valueOf(userNextStatus);
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
                            String availableString = jsonObj.getString("available");
                            if (availableString.equals("true")) {
                                userStatusSwitch.setText(R.string.available_status);
                                userStatusSwitch.setChecked(true);
                            } else if (availableString.equals("false")) {
                                userStatusSwitch.setText(R.string.unavailable_status);
                                userStatusSwitch.setChecked(false);
                            } else {
                                userStatusSwitch.setText(R.string.connection_error);
                                Toast.makeText(getActivity(), "Connnection error!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (final JSONException e) {
                            userStatusSwitch.setText(R.string.connection_error);
                            Toast.makeText(getActivity(), "Connnnection error!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        userStatusSwitch.setText(R.string.connection_error);
                        Toast.makeText(getActivity(), "Connection error!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("available", statusUser);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                tokenId = getActivity().getIntent().getStringExtra("tokenId");
                headers.put("Authorization", tokenId);
                return headers;
            }
        };
        queue.add(putRequest);
    }

    private void userFriendsInfoRequest() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId + "/friends/";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        String responseStr = response;
                        try {
                            JSONObject jsonObj = new JSONObject(responseStr);
                            // Getting JSON Array node
                            JSONArray contacts = jsonObj.getJSONArray("friends");
                            // looping through All Contacts
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject contactJSONObject = contacts.getJSONObject(i);
                                String email = contactJSONObject.getString("email");
                                String first_name = contactJSONObject.getString("first_name");
                                first_name = (first_name.equals("null")) ? "" : first_name;
                                String last_name = contactJSONObject.getString("last_name");
                                last_name = (last_name.equals("null")) ? "" : last_name;
                                statusFriend = contactJSONObject.getString("available");
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("email", email);
                                contact.put("first_name", first_name);
                                contact.put("last_name", last_name);
                                statusFriend = (statusFriend.equals("true")) ? "Available" : "UnAvailable";
                                contact.put("status", statusFriend);
                                contactList.add(contact);
                                //Sorting contactlist by status
                                        try {
                                            Collections.sort(contactList, new Comparator<HashMap<String, String>>() {
                                                @Override
                                                public int compare(HashMap<String, String> stringHashMapEmail, HashMap<String, String> stringHashMapEmail1) {
                                                    return stringHashMapEmail.get("status").compareTo(stringHashMapEmail1.get("status"));
                                                }
                                            });
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
                                        R.layout.list_item, new String[]{"email", "first_name", "last_name", "status"},
                                        new int[]{R.id.email, R.id.first_name, R.id.last_name, R.id.status});
                                lv.setAdapter(adapter);
                            }
                        } catch (final JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                tokenId = getActivity().getIntent().getStringExtra("tokenId");
                headers.put("Authorization", tokenId);
                return headers;
            }
        };
        queue.add(getRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}