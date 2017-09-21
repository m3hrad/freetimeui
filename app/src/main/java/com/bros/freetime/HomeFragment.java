package com.bros.freetime;

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
import java.util.HashMap;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

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
    private boolean userNextStatus;
    private String userStatusValue;
    Switch userStatusSwitch;

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
        userStatusSwitch = (Switch) v.findViewById(R.id.userStatusSwitch);
        if(userStatusValue.equals("false")) {
            userStatusSwitch.setChecked(false);
            userStatusSwitch.setText(R.string.unavailable_status);
        }
        else if(userStatusValue.equals("true")) {
            userStatusSwitch.setChecked(true);
            userStatusSwitch.setText(R.string.available_status);
        }
        else {
            Toast.makeText(getActivity(), "Network connection error, please try again", Toast.LENGTH_SHORT).show();
        }
        userStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                userNextStatus = isChecked;
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
        status = String.valueOf(userNextStatus);

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
                                    Toast.makeText(getActivity(), "You are available now", Toast.LENGTH_SHORT).show();
                                } else if (availableString.equals("false")) {
                                    userStatusSwitch.setText(R.string.unavailable_status);
                                    userStatusSwitch.setChecked(false);
                                    Toast.makeText(getActivity(), "You are not available now", Toast.LENGTH_SHORT).show();
                                } else {
                                        userStatusSwitch.setText(R.string.connection_error);
                                    Toast.makeText(getActivity(), "Connection error!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (final JSONException e) {
                                userStatusSwitch.setText(R.string.connection_error);
                                Toast.makeText(getActivity(), "Connection error!", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        userStatusSwitch.setText(R.string.connection_error);
                        Toast.makeText(getActivity(), "Connection error!", Toast.LENGTH_SHORT).show();
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
                                JSONObject contactJSONObject = contacts.getJSONObject(i);
                                String email = contactJSONObject.getString("email");
                                String first_name = contactJSONObject.getString("first_name");
                                first_name = (first_name.equals("null")) ? "" : first_name;
                                String last_name = contactJSONObject.getString("last_name");
                                last_name = (last_name.equals("null")) ? "" : last_name;
                                String status = contactJSONObject.getString("available");
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("email", email);
                                contact.put("first_name", first_name);
                                contact.put("last_name", last_name);
                                status = (status.equals("true")) ? "Available" : "UnAvailable";
                                contact.put("status", status);
                                contactList.add(contact);
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
