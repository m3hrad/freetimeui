package com.bros.freetime;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.fragment;
import static android.R.attr.theme;
import static android.R.id.toggle;
import static com.bros.freetime.R.id.navigation;
import static com.bros.freetime.R.id.navigation_search;
import static com.bros.freetime.R.id.textView;

public class MainActivity extends AppCompatActivity{

    private long mLastClickTime = 0;
    private Fragment selectedFragment;
    private String selectedFragmentName = null;
    private MenuItem item, item1;
    private Menu menu, menu1;
    private int i = 0;
    private int j = 0;
    private int h;
    private BottomNavigationView navigation, navigation1;
//    private HomeFragment homeFragment;
//    private SearchFragment searchFragment;
//    private AddEventFragment addEventFragment;
    String result = "";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            TextView textView = (TextView) findViewById(R.id.textView);
            TextView textView1 = (TextView) findViewById(R.id.textView);
//            Adding delay for clicking:
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            String homeMenu = "Home";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance();
                        result = "home";
                    i = 1;
                    j = i;
//                    Fragment homeFragment = new HomeFragment();
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.content, homeFragment, "homeFragment");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("searchFragmentName")).commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("addFragmentName")).commit();

                    break;
                case navigation_search:
                    selectedFragment = SearchFragment.newInstance();
                        result = "search";
                    i = 0;
                    j = i;
//                    Fragment searchFragment = new SearchFragment();
//                    selectedFragmentName = "SearchFragment";
//                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
//                    transaction1.replace(R.id.content, searchFragment, "searchFragmentName");
//                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("searchFragmentName");
//                    transaction1.addToBackStack(null);
//                    transaction1.commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("homeFragmentName")).commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("addFragmentName")).commit();

                    break;
                case R.id.navigation_add:
                    selectedFragment = AddEventFragment.newInstance();
                    result = "add";
                    i = 2;
                    j = i;
//                    selectedFragmentName = "AddEventFragment";
//                    Fragment addFragment = new AddEventFragment();
//                    selectedFragmentName = "SearchFragment";
//                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
//                    transaction2.replace(R.id.content, addEventFragment, "addFragmentName");
//                    transaction2.addToBackStack(null);
//                    transaction2.commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("searchFragmentName")).commit();
//                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("homeFragmentName")).commit();
                    break;
            }
//            if(get)

//            String currentValue = menu.getItem(i).toString();
//            if((currentValue.equals(homeMenu)) && (i == 1))
//            {
//                textView.setText(menu.getItem(i).toString() + "home Again???");
//
//            }
//            textView1.setText(menu.getItem(j).toString());
            h = item.getItemId();
            textView.setText(getSelectedItem(navigation) + "&&" + item.getItemId() + "&&" + item.toString() + "&&" + h);
            if(item.toString().equals("")) {
            return true;
            }else {
                if (getSelectedItem(navigation) != item.getItemId()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, selectedFragment, selectedFragmentName);
//            transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);
//        TextView textView1 = (TextView) findViewById(R.id.textView);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        selectedFragment =HomeFragment.newInstance();
//        h = item.getItemId();
//        h = 2131689854;
        textView.setText(getSelectedItem(navigation) + "&&" + item.getItemId() + "&&" + item.toString() + "&&" + h);
        h = 2131689854;
//        getSelectedItem(navigation) = 5;
//        item.getItemId() = h;
        if(!item.toString().equals(null)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
//        transaction.addToBackStack(null);
            transaction.commit();
        }
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        getSelectedItem(navigation);

        getSelectedItem(navigation);
//        getSelectedItem(navigation1);

//        textView.setText(item.getItemId() + "&&" + menu.toString() + "&&" +menu.getItem(i).toString() + "&&" + i);

//        textView.setText("Hello");
    }

    private int getSelectedItem(BottomNavigationView bottomNavigationView){
        menu = bottomNavigationView.getMenu();
        for (int i=0;i<bottomNavigationView.getMenu().size();i++){
            item = menu.getItem(i);
//            menu.removeItem(i);
            if (item.isChecked()){
                return item.getItemId();
            }
        }
        return 0;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sign_out) {
            signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }
}