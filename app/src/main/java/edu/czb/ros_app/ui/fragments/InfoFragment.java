package edu.czb.ros_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.czb.ros_app.R;
import edu.czb.ros_app.databinding.FragmentInfoBinding;
import edu.czb.ros_app.viewmodel.InfoViewModel;
import edu.czb.ros_app.widgets.battey.BatteryView;
import std_msgs.Float32;

public class InfoFragment extends Fragment {
    private final static String TAG=InfoFragment.class.getSimpleName();
    private FragmentInfoBinding binding;
    private InfoViewModel viewModel;
    private Button button;
    private BatteryView batteryView;
    public static InfoFragment newInstance() {
        return new InfoFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentInfoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //button=getView().findViewById(R.id.addAllTopic);
        viewModel=new ViewModelProvider(requireActivity()).get(InfoViewModel.class);
        viewModel.setViewLifecycleOwner(getViewLifecycleOwner());
        batteryView=getView().findViewById(R.id.battery_view);
        viewModel.getRosDate().observe(getViewLifecycleOwner(),rosData -> {
            if(rosData.getTopic().name.toLowerCase().contains("battery")){
                batteryView.onNewMessage((rosData.getMessage()));
            }/*else if(rosData.getTopic().name.toLowerCase().contains("imu")){
                binding.imu.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("navSatFix")){
                binding.navSatFix.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("temperature")){
                binding.temperature.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().type.contains("Float32")){
                binding.other.setText(rosData.getTopic().name+":"+((Float32)rosData.getMessage()).getData());
            }*/
        });
        /*viewModel.getBattery().observe(getViewLifecycleOwner(),battery->{

        });
        viewModel.getImu().observe(getViewLifecycleOwner(),imu->{

        });
        viewModel.getTemperature().observe(getViewLifecycleOwner(),temperature->{
            binding.temperature.setText(temperature);
        });
        viewModel.getNavSatFix().observe(getViewLifecycleOwner(),navSatFix->{
            binding.navSatFix.setText(navSatFix);
        });*/

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"进入点击事件");
                viewModel.getRosDomain().addAllTopic();
            }
        });*/


    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }*/
}
