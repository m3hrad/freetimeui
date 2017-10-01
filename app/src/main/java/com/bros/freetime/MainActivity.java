package com.bros.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import static com.bros.freetime.R.id.navigation_search;

public class MainActivity extends AppCompatActivity{
    private long mLastClickTime = 0;
    private Fragment selectedFragment;
    private String selectedFragmentName = null;
    private MenuItem item;
    private Menu menu;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Adding delay for clicking:
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case navigation_search:
                    selectedFragment = SearchFragment.newInstance();
                    break;
//                case R.id.navigation_add:
//                    selectedFragment = AddEventFragment.newInstance();
//                    break;
            }
            if(item.toString().equals("")) {
            return true;
            }else {
                if (getSelectedItem(navigation) != item.getItemId()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, selectedFragment, selectedFragmentName);
                    transaction.commit();
                }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setNavigationButtonToHome();
        selectedFragment =HomeFragment.newInstance();
        getSelectedItem(navigation);
        String itemTostring = item.toString();
        item.getItemId();

        if(!itemTostring.equals(null)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
        }
        getSelectedItem(navigation);
    }

    public void setNavigationButtonToHome() {
        navigation.getMenu().getItem(1).setChecked(true);
    }

    private int getSelectedItem(BottomNavigationView bottomNavigationView){
        menu = bottomNavigationView.getMenu();
        for (int i=0;i<bottomNavigationView.getMenu().size();i++){
            item = menu.getItem(i);
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