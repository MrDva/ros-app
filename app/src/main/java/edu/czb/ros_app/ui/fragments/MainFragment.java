package edu.czb.ros_app.ui.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NonNls;

import edu.czb.ros_app.R;


public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private NavController navController;
    private BottomNavigationView bottomNav;
    DrawerLayout drawerLayout;
    //private MainViewModel mViewModel;


    public static MainFragment newInstance(){
        return new MainFragment();
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_container);
        Log.i(TAG,"On item selected: " +item.getTitle());
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }*/


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){


        drawerLayout = view.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.ok_green));
        navController= Navigation.findNavController(requireActivity(),R.id.fragment_container);
        bottomNav=view.findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav,navController);
        //getActivity().onMenuItemSelected()
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.i(TAG,"On destination selected: " +destination.getId()+destination.getLabel());

            }
        });



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        return inflate;
    }
}
