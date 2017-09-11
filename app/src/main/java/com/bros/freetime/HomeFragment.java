package com.bros.freetime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;
import static com.google.android.gms.internal.zzahf.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private String TAG = HomeFragment.class.getSimpleName();
    private ListView lv;
    private String userId;
    private String tokenId;
    private ArrayList<HashMap<String, String>> contactList;
    private String status;
    View v;
    TextView userStatusButton;
    private String futureUserStatus;
    private String userStatusValue, userStatus;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String ARG_PARAM1 = "param1";
    // private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    // private String mParam1;
    // private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //   mParam1 = getArguments().getString(ARG_PARAM1);
            //  mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        contactList = new ArrayList<>();
        lv = (ListView) v.findViewById(R.id.list);
        userStatusValue = getActivity().getIntent().getStringExtra("availableStatus");
        userFriendsInfoRequest();
        userStatusButton = (TextView) v.findViewById(R.id.userStatusButton);
        if(userStatusValue.equals("false")) {
            userStatus = "UnAvailable";
            futureUserStatus = "true";
        }
        else if(userStatusValue.equals("true")) {
            userStatus = "Available";
            futureUserStatus = "false";
        }
        else {
            Toast.makeText(getActivity(), "Network connection error, please try again", Toast.LENGTH_SHORT).show();
        }
        userStatusButton.setText(userStatus);
        userStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userStatusButton.getText().equals("UnAvailable")) {
                    futureUserStatus = "true";
                }
                else if(userStatusButton.getText().equals("Available")) {
                    futureUserStatus = "false";
                }
                    setAvailabeStatus();
            }
        });
        return v;
    }

    private void setAvailabeStatus() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId;
        status = futureUserStatus;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        String responseStr = response;
                            try {
//                                JSONObject jsonObj = new JSONObject(String.valueOf(response));
                                JSONObject jsonObj = new JSONObject(responseStr);
                                String availableString = jsonObj.getString("available");
                                if (availableString.equals("false")) {
                                    userStatusButton.setText("UnAvailable");
                                    Toast.makeText(getActivity(), "You are not available now", Toast.LENGTH_SHORT).show();
                                } else if (availableString.equals("true")) {
                                    userStatusButton.setText("Available");
                                    Toast.makeText(getActivity(), "You are available now", Toast.LENGTH_SHORT).show();
                                } else if (availableString.equals("")) {
                                    Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                                }

                            } catch (final JSONException e) {
                                Toast.makeText(getActivity(), "Network error!", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }));
                            }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // error
                        Toast.makeText(getActivity(), "Network error!", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", "volleyError");
                    }
                }
        ) {
            @Override

            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("available", status);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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
                                JSONObject c = contacts.getJSONObject(i);
                                String id = c.getString("id");
                                String first_name = c.getString("first_name");
                                String last_name = c.getString("last_name");
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("id", id);
                                contact.put("first_name", first_name);
                                contact.put("last_name", last_name);
                                contactList.add(contact);
                                ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
                                        R.layout.list_item, new String[]{"id", "first_name", "last_name"},
                                        new int[]{R.id.id, R.id.first_name, R.id.last_name});
                                lv.setAdapter(adapter);
                            }
                        } catch (final JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                            runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Network error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));
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
            private void runOnUiThread(Runnable runnable) {
    }
        };
        queue.add(getRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}
