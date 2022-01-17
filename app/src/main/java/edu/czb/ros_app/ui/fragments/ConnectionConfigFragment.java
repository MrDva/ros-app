package edu.czb.ros_app.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import edu.czb.ros_app.R;
import edu.czb.ros_app.databinding.FragmentConnectionConfigBinding;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionCheckTask;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionListener;
import edu.czb.ros_app.utils.NetWorkUtil;
import edu.czb.ros_app.viewmodel.ConnectViewModel;


public class ConnectionConfigFragment extends Fragment implements TextView.OnEditorActionListener{

    private static final String TAG = ConnectionConfigFragment.class.getSimpleName();

    private FragmentConnectionConfigBinding binding;

    private ArrayList<String> ipItemList;
    private AutoCompleteTextView ipAddressField;
    private TextInputLayout ipAddressLayout;
    private ArrayAdapter<String> ipArrayAdapter;

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
            connectToMaster();
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
}
