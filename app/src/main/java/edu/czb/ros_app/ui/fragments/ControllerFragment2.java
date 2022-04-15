package edu.czb.ros_app.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import edu.czb.ros_app.R;
import edu.czb.ros_app.viewmodel.ControllerViewModel;
import edu.czb.ros_app.widgets.joystick.JoystickView;
import edu.czb.ros_app.widgets.joystick.JoystickView1;
import edu.czb.ros_app.widgets.joystick.JoystickView2;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.fragments
 * @ClassName: ControllerFragment2
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/2 21:05
 * @Version: 1.0
 */
public class ControllerFragment2 extends Fragment {
    private static final String TAG = ControllerFragment.class.getSimpleName();
    private JoystickView1 joystickView;
    private JoystickView2 joystickView2;
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
        return inflater.inflate(R.layout.fragment_controller2, container, false);
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
        joystickView=getView().findViewById(R.id.joystick2_1);
        joystickView2=getView().findViewById(R.id.joystick2_2);
        floatingActionButton=getView().findViewById(R.id.switch_right);
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
        floatingActionButton.setOnClickListener(l->{
            controller.navigate(R.id.action_to_controller);
        });
        joystickView.getJoystickLiveData().observe(getViewLifecycleOwner(),joystickData -> {
            if(joystickView2.down){
            float[] axes= Objects.requireNonNull(joystickView2.getJoystickLiveData().getValue()).axes;
            for (int i = 0; i < 2; i++) {
                joystickData.axes[i]+=axes[i];
                Objects.requireNonNull(joystickView2.getJoystickLiveData().getValue()).axes=new float[6];
            }}else{
                Objects.requireNonNull(joystickView2.getJoystickLiveData().getValue()).axes=new float[6];
            }
            controllerViewModel.getRosDomain().publicData(joystickData);
        });
        joystickView2.getJoystickLiveData().observe(getViewLifecycleOwner(),joystickData -> {
            if(joystickView.down){
                float[] axes= Objects.requireNonNull(joystickView.getJoystickLiveData().getValue()).axes;
                for (int i = 2; i < 4; i++) {
                    joystickData.axes[i]+=axes[i];
                }
                Objects.requireNonNull(joystickView.getJoystickLiveData().getValue()).axes=new float[6];
            }else{
                Objects.requireNonNull(joystickView.getJoystickLiveData().getValue()).axes=new float[6];
            }
            controllerViewModel.getRosDomain().publicData(joystickData);

        });


    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }




}