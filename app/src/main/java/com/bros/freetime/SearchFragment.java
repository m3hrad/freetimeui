package com.bros.freetime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class SearchFragment extends Fragment {

    private String TAG = SearchFragment.class.getSimpleName();
    private EditText searchEditText;
    private String userId;
    private ListView lv;
    private Button searchButton;
    private ArrayList<HashMap<String, String>> contactList;
    private String tokenId;
    private String searchStringEditText;
    private String friendId, friendEmail;
    View v;
    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        contactList = new ArrayList<>();
        lv = (ListView) v.findViewById(R.id.list);
        searchEditText = (EditText) v.findViewById(R.id.searchEditText);
        //adding search to keyboard and hiding
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            searchFriendsInfoRequest();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        searchButton = (Button) v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                searchFriendsInfoRequest();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                friendId = contactList.get(i).get("id").toString();
                friendEmail = contactList.get(i).get("email").toString();
                addFriend();
            }
        });
        return v;
    }

    private void goToHomeFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new HomeFragment())
                .addToBackStack(null).commit();
        ((MainActivity)getActivity()).setNavigationButtonToHome();
    }

    private void addFriend() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        final RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId + "/friends";
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            if(response.equals("OK")) {
                                Toast.makeText(getActivity(), "You are friend with " + friendEmail + " now.", Toast.LENGTH_SHORT).show();
                                goToHomeFragment();
                            }
                            JSONObject jsonObj = new JSONObject(response);
                        } catch (final Exception e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if (error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                if(statusCode.equals("400")) {
                                    Toast.makeText(getActivity(), body, Toast.LENGTH_SHORT).show();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friendId", friendId);
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

    private void searchFriendsInfoRequest() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        searchStringEditText = searchEditText.getText().toString();
        final String url = "https://freetime-backend-dev.herokuapp.com/user/?email=" + searchStringEditText;
        tokenId = getActivity().getIntent().getStringExtra("tokenId");

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
                            JSONArray contacts = jsonObj.getJSONArray("users");
                            // looping through All Contacts
                            contactList.clear();
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject contactsJSONObject = contacts.getJSONObject(i);
                                String email = contactsJSONObject.getString("email");
                                String id = contactsJSONObject.getString("id");
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("email", email);
                                contact.put("id",id);
                                contactList.add(contact);
                                ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
                                        R.layout.list_item, new String[]{"email"},
                                        new int[]{R.id.email});
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
        ){
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