package edu.czb.ros_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.czb.ros_app.R;
import edu.czb.ros_app.widgets.joystick.JoystickView;

public class ControllerFragment extends Fragment implements JoystickView.JoystickListener {
    private static final String TAG = ControllerFragment.class.getSimpleName();
    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onJoystickMoved(float[] axes, int[] buttons) {
        Log.i(TAG,"axes["+axes.toString()+"],["+buttons.toString()+"]");
    }
}
