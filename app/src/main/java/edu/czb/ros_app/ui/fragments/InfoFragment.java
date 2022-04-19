package edu.czb.ros_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.czb.ros_app.R;
import edu.czb.ros_app.databinding.FragmentInfoBinding;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import edu.czb.ros_app.viewmodel.InfoViewModel;
import edu.czb.ros_app.widgets.battey.BatteryView;
import edu.czb.ros_app.widgets.imu.ImuAccView;
import edu.czb.ros_app.widgets.imu.ImuAngView;
import edu.czb.ros_app.widgets.imu.MiCompass;
import edu.czb.ros_app.widgets.imu.RpyView;
import edu.czb.ros_app.widgets.map.MapInfoVIew;
import edu.czb.ros_app.widgets.temperature.TemperatureView;
import std_msgs.Float32;
import std_msgs.Float64;

public class InfoFragment extends Fragment {
    private final static String TAG=InfoFragment.class.getSimpleName();
    private FragmentInfoBinding binding;
    private InfoViewModel viewModel;
    private BatteryView batteryView;
    private ImuAccView imuAccView;
    private RpyView rpyView;
    private MiCompass miCompass;
    private TemperatureView temperatureView;
    private MapInfoVIew mapInfoVIew;

    private SensorManager manager;
    private MySensorEventListener listener;
    private Sensor magneticSensor,accelerometerSensor;
    private float[] values,r ,gravity,geomagnetic;

    private FloatingActionButton bntToChart;
    private NavController controller;


   /* private Button buttonYamUp;
    private Button buttonYamDown;
    private Button buttonRollUp;
    private Button buttonRollDown;
    private Button buttonPitchUp;
    private Button buttonPitchDown;*/
    private double[] init=new double[]{0,0,0};

    private TextView rpyText;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentInfoBinding.inflate(inflater,container,false);
        controller= Navigation.findNavController(getActivity(),R.id.fragment_container);
        return binding.getRoot();
    }

    @Override
    public  void onResume(){
        super.onResume();
        manager.registerListener(listener,magneticSensor,SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(listener,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause(){
        manager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //button=getView().findViewById(R.id.addAllTopic);
        viewModel=new ViewModelProvider(requireActivity()).get(InfoViewModel.class);
        viewModel.setViewLifecycleOwner(getViewLifecycleOwner());
        batteryView=getView().findViewById(R.id.battery_view);
        //temperatureView=getView().findViewById(R.id.temperature_view);
        miCompass=getView().findViewById(R.id.micopass_view);
        //imuAccView=getView().findViewById(R.id.imu_acc_view);
        //rpyView=getView().findViewById(R.id.rpyView);

        bntToChart=getView().findViewById(R.id.switch_history);
        bntToChart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8881D4FA")));

        //rpyText=getView().findViewById(R.id.rpy_text);
        //mapInfoVIew=getView().findViewById(R.id.navS);
        viewModel.getRosDate().observe(getViewLifecycleOwner(),rosData -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(TopicName.TOPIC_KEY, Context.MODE_PRIVATE);
            if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.BATTERY,TopicName.BATTERY))){
                batteryView.onNewMessage((rosData.getMessage()));
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.DEST_YAW,TopicName.DEST_YAW))){
                Float64 destYaw=(Float64)rosData.getMessage();
                miCompass.setDestVal((float) destYaw.getData());
                //rpyView.onNewMessage(destYaw.getData());
            }/*else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.BATTERY,TopicName.BATTERY)){
                //mapInfoVIew.onNewMessage(rosData.getMessage());
            }*/else if(rosData.getTopic().name.toLowerCase().contains(sharedPreferences.getString(TopicName.TEMPERATURE,TopicName.TEMPERATURE))){
                //temperatureView.onNewMessage(rosData.getMessage());
            }/*else if(rosData.getTopic().name.contains("Float32")){
                //binding.other.setText(rosData.getTopic().name+":"+((Float32)rosData.getMessage()).getData());
            }*/else if(rosData.getTopic().name.contains(sharedPreferences.getString(TopicName.RPY,TopicName.RPY))){
                //rpyView.onNewMessage(rosData.getMessage());
                miCompass.onNewData(rosData.getMessage());
            }
        });



        manager=(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        listener=new MySensorEventListener();
        magneticSensor=manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        values=new float[3];
        gravity=new float[3];
        r=new float[9];
        geomagnetic=new float[3];


        /*buttonPitchDown=getView().findViewById(R.id.button_pitch_down);
        buttonPitchUp=getView().findViewById(R.id.button_pitch_up);
        buttonYamDown=getView().findViewById(R.id.button_yaw_down);
        buttonYamUp=getView().findViewById(R.id.button_yaw_up);
        buttonRollDown=getView().findViewById(R.id.button_roll_down);
        buttonRollUp=getView().findViewById(R.id.button_roll_up);*/
       /* buttonRollUp.setOnClickListener(v->{
            init[2]++;
            init[2]%=360;
            rpyView.onNewMessage(init);
            rpyView.onNewMessage(init[2]);
        });
        buttonRollDown.setOnClickListener(v->{
            init[2]--;
            init[2]%=360;
            rpyView.onNewMessage(init);
            rpyView.onNewMessage(init[2]);
        });
        buttonYamUp.setOnClickListener(v->{
            init[0]++;
            init[0]%=360;
            rpyView.onNewMessage(init);
        });
        buttonYamDown.setOnClickListener(v->{
            init[0]--;
            init[0]%=360;
            rpyView.onNewMessage(init);
        });
        buttonPitchUp.setOnClickListener(v->{
            init[1]++;
            init[1]%=360;
            rpyView.onNewMessage(init);
        });
        buttonPitchDown.setOnClickListener(v->{
            init[1]--;
            init[1]%=360;
            rpyView.onNewMessage(init);
        });*/
        bntToChart.setOnClickListener(v->{
            controller.navigate(R.id.action_to_info_chart);
        });

    }

    private class MySensorEventListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                geomagnetic=event.values;
            }
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                gravity=event.values;
                getValue();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public void getValue(){
        SensorManager.getRotationMatrix(r,null,gravity,geomagnetic);
        SensorManager.getOrientation(r,values);
        double azimuth=Math.toDegrees(values[0]);
        if(azimuth<0){
            azimuth=azimuth+360;
        }
        double pitch=Math.toDegrees(values[1]);
        double roll=Math.toDegrees(values[2]);
       // rpyText.setText("yam:"+(int)azimuth+"\nPitch:"+(int)pitch+"\nroll:"+(int)roll);
       // rpyView.onNewMessage(new double[]{azimuth,pitch,roll});
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding=null;
    }
}
