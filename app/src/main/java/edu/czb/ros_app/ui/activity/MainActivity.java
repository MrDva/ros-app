package edu.czb.ros_app.ui.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.tabs.TabLayout;

import edu.czb.ros_app.R;
import edu.czb.ros_app.ui.fragments.MainFragment;


public class MainActivity extends AppCompatActivity {

     public static final String TAG=MainActivity.class.getSimpleName();

     private static final int LOCATION_PERM = 101;

     private TabLayout tabLayout;
     private Toolbar toolbar;
     private NavController navController;
     DrawerLayout drawerLayout;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.ativity_main);


          //Toolbar myToolbar = findViewById(R.id.toolbar);
          //setSupportActionBar(myToolbar);
          if (savedInstanceState == null) {
               MainFragment mainFragment=MainFragment.newInstance();
               FragmentManager fragmentManager= getSupportFragmentManager();
               FragmentTransaction transaction=fragmentManager.beginTransaction();
               transaction.replace(R.id.main_contain,mainFragment,"fragment_main");
               transaction.commitNow();
          }
          this.requestPermissions();

          //tabLayout=view.findViewById(R.id.tabs);
          //toolbar=view.findViewById(R.id.toolbar);
       /* drawerLayout = this.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.ok_green));
        navController= Navigation.findNavController(this,R.id.fragment_container);

        //选择连接配置作为主界面
        tabLayout.selectTab(tabLayout.getTabAt(0));
        navController.navigate(R.id.action_to_connectionConfig);

        //为不同界面设置导航
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Log.i(TAG,"On Tab selected: " +tab.getText());
                switch (tab.getText().toString()){
                    case "连接配置":
                        navController.navigate(R.id.action_to_connectionConfig);
                        break;
                    case "控制界面":
                        navController.navigate(R.id.action_to_controller);
                        break;
                    default:
                        navController.navigate(R.id.action_to_connectionConfig);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

          /*Toolbar myToolbar=findViewById(R.id.toolbar);
          setSupportActionBar(myToolbar);
          if(savedInstanceState==null){
               getSupportFragmentManager().beginTransaction()
                       .replace(R.id.main_contain,MainFragment.newInstance())
                       .commitNow();
          }*/


     }
     @Override
     public void onBackPressed(){
          Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_contain);

          /*if(fragment instanceof OnBackPressedListener) {
               OnBackPressedListener listener = (OnBackPressedListener)fragment;

               if (listener.onBackPressed()){
                    return;
               }
          }*/

          super.onBackPressed();

     }

     private void requestPermissions() {
          String[] permissions = new String[] {
                  Manifest.permission.ACCESS_NETWORK_STATE,
                  Manifest.permission.ACCESS_FINE_LOCATION};
          ActivityCompat.requestPermissions(this, permissions, LOCATION_PERM);
     }


     // Check in required if update is available or onboarding has not been done yet
     private boolean requiresIntro() throws PackageManager.NameNotFoundException {

          SharedPreferences pref = getApplicationContext().getSharedPreferences("introPrefs", MODE_PRIVATE);

          return (pref.getInt("VersionNumber", 0) != getPackageManager().getPackageInfo(getPackageName(),0).versionCode) ||
                  !pref.getBoolean("CheckedIn", false);

     }

    /* @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          NavController navController = Navigation.findNavController(this, R.id.fragment_container);
          Log.i(TAG,"On item selected: " +item.getTitle());
          return NavigationUI.onNavDestinationSelected(item, navController)
                  || super.onOptionsItemSelected(item);
     }*/

}
