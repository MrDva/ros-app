package edu.czb.ros_app.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.czb.ros_app.R;
import edu.czb.ros_app.databinding.FragmentConnectionConfigBinding;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionCheckTask;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionListener;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import edu.czb.ros_app.ui.dialog.CorrectLatLngDialog;
import edu.czb.ros_app.ui.dialog.TopicNameSettingDialog;
import edu.czb.ros_app.utils.NetWorkUtil;
import edu.czb.ros_app.viewmodel.ConnectViewModel;


public class ConnectionConfigFragment extends Fragment implements TextView.OnEditorActionListener{

    private static final String TAG = ConnectionConfigFragment.class.getSimpleName();

    private FragmentConnectionConfigBinding binding;

    private ArrayList<String> ipItemList;
    private AutoCompleteTextView ipAddressField;
    private TextInputLayout ipAddressLayout;
    private ArrayAdapter<String> ipArrayAdapter;
    private FloatingActionButton buttonSettings;
    private TopicNameSettingDialog topicNameSettingDialog;

    private ConnectViewModel connectViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentConnectionConfigBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectViewModel=new ViewModelProvider(requireActivity()/*,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())*/).get(ConnectViewModel.class);

        //Define Views
        ipAddressField=getView().findViewById(R.id.ipAddressTextView);
        ipAddressLayout=getView().findViewById(R.id.ipAddressLayout);

        buttonSettings=getView().findViewById(R.id.button_settings);
        buttonSettings.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff81D4FA")));
        ipItemList=new ArrayList<>();
        ipArrayAdapter=new ArrayAdapter<>(this.getContext(),R.layout.dropdown_menu_item,ipItemList);
        ipAddressField.setAdapter(ipArrayAdapter);

        String firstDeviceIp= NetWorkUtil.getIpAddress(true);

        ipAddressField.setText(firstDeviceIp,false);

        ipAddressField.setOnClickListener(clickedView->{
            updateIpSpinner();
            ipAddressField.showDropDown();
        });

        ipAddressLayout.setEndIconOnClickListener(v->{
            ipAddressField.requestFocus();
            ipAddressField.callOnClick();
        });

        ipAddressField.setOnItemClickListener(((parent, view, position, id) -> {
            ipAddressField.clearFocus();
        }));
        getViewLifecycleOwner();

        //view model connection
        connectViewModel.getCurrentMaster().observe(getViewLifecycleOwner(),master->{
            if(master==null){
                binding.masterIpEditText.getText().clear();
                binding.masterPortEditText.getText().clear();
                return;
            }
            binding.masterIpEditText.setText(master.ip);
            binding.masterPortEditText.setText(String.valueOf(master.port));
        });
        connectViewModel.getRosConnection().observe(getViewLifecycleOwner(), this::setRosConnection);

        connectViewModel.getNetworkSSIDLiveData().observe(getViewLifecycleOwner(),ssid->{
            if(ssid==null){
                binding.NetworkSSIDText.setText("");
                return;
            }
            binding.NetworkSSIDText.setText(ssid);
        });

        //user input
        binding.connectButton.setOnClickListener(v->{
            updateMasterDetails();
            connectViewModel.setMasterDeviceIp(ipAddressField.getText().toString());
            connectToMaster();
        });

        binding.disconnectButton.setOnClickListener(v->{
            connectViewModel.getRosDomain().getTopicList();
            connectViewModel.disconnectFromMaster();
        });

        buttonSettings.setOnClickListener(v->{
            showEditDialog(v);
        });

    }

    /**
     * 更新本地ip显示
     * */
    private void updateIpSpinner(){
        ipItemList=new ArrayList<>();
        ipItemList=NetWorkUtil.getIpAddressList(true);
        ipArrayAdapter.clear();
        ipArrayAdapter.addAll(ipItemList);
    }

    /**
     * 更新Master连接端口和连接IP*/
    private void updateMasterDetails(){
        Editable masterIp=binding.masterIpEditText.getText();
        if(binding.masterIpEditText!=null){
            connectViewModel.setMasterIp(masterIp.toString());
        }
        Editable masterPort=binding.masterPortEditText.getText();
        if(masterPort!=null&&masterPort.length()>0){
            connectViewModel.setMasterPort(masterPort.toString());
        }
    }

    /**
     * Master连接调用
     * */
    public void connectToMaster() {
        Log.i(TAG, "Connect to Master");
        connectViewModel.getRosDomain().connectToMaster();
    }

    /**
     * 更新连接状态显示
     * */
    private void setRosConnection(ConnectionStateType connectionType) {
        int connectVisibility = View.INVISIBLE;
        int disconnectVisibility = View.INVISIBLE;
        int pendingVisibility = View.INVISIBLE;
        String statustext = getContext().getString(R.string.connected);

        if (connectionType == ConnectionStateType.DISCONNECTED
                || connectionType == ConnectionStateType.FAILED) {
            connectVisibility = View.VISIBLE;
            statustext = getContext().getString(R.string.disconnected);

        } else if (connectionType == ConnectionStateType.CONNECTED) {
            disconnectVisibility = View.VISIBLE;

        } else if (connectionType == ConnectionStateType.PENDING) {
            pendingVisibility = View.VISIBLE;
            statustext = getContext().getString(R.string.pending);
        }

        binding.statusText.setText(statustext);
        binding.connectedImage.setVisibility(disconnectVisibility);
        binding.disconnectedImage.setVisibility(connectVisibility);
        binding.connectButton.setVisibility(connectVisibility);
        binding.disconnectButton.setVisibility(disconnectVisibility);
        binding.pendingBar.setVisibility(pendingVisibility);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    public void saveTopic(String preTopicName,String topicName){
        if(Pattern.compile("\\s+").matcher(topicName).find()){
            Toast.makeText(getContext(),"设置的话题名称不合，检查是否存在空格",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences topicInfo=getContext().getSharedPreferences(TopicName.TOPIC_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=topicInfo.edit();
        editor.putString(preTopicName,Strings.isNullOrEmpty(topicName)?topicInfo.getString(preTopicName,preTopicName):topicName);
        editor.commit();
    }

    public void showEditDialog(View view){
        topicNameSettingDialog=new TopicNameSettingDialog(getActivity(),R.style.AppTheme,onClickListener);
        topicNameSettingDialog.show();

    }
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save_topic_name:
                    try {
                        saveTopic(TopicName.BATTERY,getTopicName(topicNameSettingDialog.text_battery,TopicName.BATTERY));
                        saveTopic(TopicName.MAP,getTopicName(topicNameSettingDialog.text_map,TopicName.MAP));
                        saveTopic(TopicName.MAP_PATH,getTopicName(topicNameSettingDialog.text_map_path,TopicName.MAP_PATH));
                        //saveTopic(TopicName.IMU,getTopicName(topicNameSettingDialog.text_imu,TopicName.IMU));
                        saveTopic(TopicName.JOY,getTopicName(topicNameSettingDialog.text_joy,TopicName.JOY));
                        //saveTopic(TopicName.TEMPERATURE,getTopicName(topicNameSettingDialog.text_temp,TopicName.TEMPERATURE));
                        saveTopic(TopicName.RPY,getTopicName(topicNameSettingDialog.text_rpy,TopicName.RPY));
                        if(getContext().getString(R.string.connected).equals(binding.statusText.getText().toString())){
                            connectViewModel.getRosDomain().unregisterAllNodes();
                            connectViewModel.getRosDomain().clearAllNodes();
                            connectViewModel.getRosDomain().initStaticNodes();
                            connectViewModel.getRosDomain().registerAllNodes();
                        }else{
                            connectViewModel.getRosDomain().clearAllNodes();
                            connectViewModel.getRosDomain().initStaticNodes();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    topicNameSettingDialog.cancel();
                    break;
                case R.id.btn_cancel_topic_name:
                    topicNameSettingDialog.cancel();
                    break;
            }

        }
    };
    private String getTopicName(EditText editText,String topicName){
        if(!Strings.isNullOrEmpty(editText.getText().toString())){
            return editText.getText().toString();
        }
        return topicName;
    }
}
