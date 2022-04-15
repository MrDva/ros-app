package edu.czb.ros_app.ui.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

import edu.czb.ros_app.R;
import edu.czb.ros_app.viewmodel.ControllerViewModel;
import edu.czb.ros_app.widgets.joystick.JoystickData;
import edu.czb.ros_app.widgets.joystick.JoystickView;

public class ControllerFragment extends Fragment  {
    private static final String TAG = ControllerFragment.class.getSimpleName();
    private JoystickView joystickView;
    private Button AButton;
    private Button BButton;
    private Button XButton;
    private Button YButton;
    private ControllerViewModel controllerViewModel;
    private FloatingActionButton floatingActionButton;
    private NavController controller;
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
        controller= Navigation.findNavController(getActivity(),R.id.fragment_container);
        //controller.setGraph(R.navigation.main_navigation);
        //controller.navigate(R.id.action_to_controller);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controllerViewModel=new ViewModelProvider(requireActivity()).get(ControllerViewModel.class);
        joystickView=getView().findViewById(R.id.joystick);
        AButton=getView().findViewById(R.id.button_A);
        BButton=getView().findViewById(R.id.button_B);
        XButton=getView().findViewById(R.id.button_X);
        YButton=getView().findViewById(R.id.button_Y);
        floatingActionButton=getView().findViewById(R.id.switch_left);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8881D4FA")));
        /*joystickView.getAxesLiveData().observe(getViewLifecycleOwner(),axes->{
            //Log.i(TAG,"axes:["+ Arrays.toString(axes)+"]");

            JoystickData joystickData=new JoystickData(axes,new int[6]);

            controllerViewModel.getRosDomain().publicData(joystickData);
        });
        joystickView.getButtonLiveData().observe(getViewLifecycleOwner(),button->{
            //Log.i(TAG,"button:["+Arrays.toString(button)+"]");
            JoystickData joystickData=new JoystickData(new float[6],button);
            controllerViewModel.getRosDomain().publicData(joystickData);
        });*/
        joystickView.getJoystickLiveData().observe(getViewLifecycleOwner(),joystickData -> {
            controllerViewModel.getRosDomain().publicData(joystickData);
            /*int sum=0;
            for (int i : joystickData.button) {
                sum+=i;
            }
            if(sum>0){
                joystickView.updateJoyStickLiveDate(new float[6],new int[6]);
            }*/
        });

        floatingActionButton.setOnClickListener(l->{
            controller.navigate(R.id.action_to_controller2);
        });
        AButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    joystickView.updateJoyStickLiveDate(new float[6],new int[6]);
                }else {
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    button[1]=1;
                    joystickView.updateJoyStickLiveDate(axes,button);
                }
                return false;
            }
        });
        BButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    joystickView.updateJoyStickLiveDate(new float[6],new int[6]);
                }else {
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    button[2]=1;
                    joystickView.updateJoyStickLiveDate(axes,button);
                }
                return false;
            }
        });
        XButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    joystickView.updateJoyStickLiveDate(new float[6],new int[6]);
                }else {
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    button[0]=1;
                    joystickView.updateJoyStickLiveDate(axes,button);
                }
                return false;
            }
        });
        YButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    joystickView.updateJoyStickLiveDate(new float[6],new int[6]);
                }else {
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    button[3]=1;
                    joystickView.updateJoyStickLiveDate(axes,button);
                }
                return false;
            }
        });

    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }


}
