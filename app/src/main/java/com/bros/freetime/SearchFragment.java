package com.bros.freetime;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;
import static com.google.android.gms.internal.zzahf.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private String TAG = SearchFragment.class.getSimpleName();
    private EditText searchEditText;
    private String userId;
    private ListView lv;
    private Button searchButton;
    private ArrayList<HashMap<String, String>> contactList;
    private String tokenId;
    private String searchStringEditText;
    View v;

    EditText textView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
 //   private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
  //  private String mParam1;
  //  private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    //        mParam1 = getArguments().getString(ARG_PARAM1);
      //      mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search, container, false);
        contactList = new ArrayList<>();
        lv = (ListView) v.findViewById(R.id.list);
        searchEditText = (EditText) v.findViewById(R.id.searchEditText);
        searchButton = (Button) v.findViewById(R.id.searchButton);

        textView = (EditText) v.findViewById(R.id.textView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFriendsInfoRequest();
            }
        });

        return v;
    }

    private void searchFriendsInfoRequest() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        userId = getActivity().getIntent().getStringExtra("userId");
        searchStringEditText = searchEditText.getText().toString();
//        final String url = "https://freetime-backend-dev.herokuapp.com/user/" + userId + "/friends/";
        final String url = "https://freetime-backend-dev.herokuapp.com/user/?email=" + searchStringEditText;
        tokenId = getActivity().getIntent().getStringExtra("tokenId");
        searchEditText.setText(userId);

        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        String responseStr = response;
//                        textView.setText(response);
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject json2 = json.getJSONObject("response");
                            String id = json2.getString("id");
                            textView.setText(id);

//                            JSONObject jsonObj = new JSONObject(response);
//                            String id = jsonObj.getString("id");
//                            String email = jsonObj.getString("email");
//                            textView.setText(id + email + "\n");

//                            JSONObject jsonObj = new JSONObject(responseStr);
//                            // Getting JSON Array node
//                            JSONArray contacts = jsonObj.getJSONArray("freinds");
//                            // looping through All Contacts
//                            for (int i = 0; i < contacts.length(); i++) {
//                                JSONObject c = contacts.getJSONObject(i);
//                                String id = c.getString("id");
////                                String first_name = c.getString("first_name");
//                                String email = c.getString("email");
//                                HashMap<String, String> contact = new HashMap<>();
//                                contact.put("id", id);
////                                contact.put("first_name", first_name);
//                                contact.put("email", email);
//                                contactList.add(contact);
//                                ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
//                                        R.layout.list_item, new String[]{"id", "email"},
//                                        new int[]{R.id.id, R.id.email});
//                                lv.setAdapter(adapter);
//                            }
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
//            @Override
//
//            protected Map<String, String> getParams()
//            {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", searchStringEditText);
//                return params;
//            }
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
