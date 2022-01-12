package edu.czb.ros_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.czb.ros_app.databinding.FragmentConnectionConfigBinding;


public class ConnectionConfigFragment extends Fragment implements TextView.OnEditorActionListener{
    private static final String TAG = ConnectionConfigFragment.class.getSimpleName();

    private FragmentConnectionConfigBinding binding;

    public static ConnectionConfigFragment newInstance() {
        return new ConnectionConfigFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        binding=FragmentConnectionConfigBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}
