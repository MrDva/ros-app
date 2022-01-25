package edu.czb.ros_app.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import edu.czb.ros_app.domain.RosDomain;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.utils.NetWorkUtil;

public class ConnectViewModel extends AndroidViewModel {
    private static final String TAG=ConnectViewModel.class.getSimpleName();

    private  RosDomain rosDomain;

    private  LiveData<MasterEntity> currentMaster;

    private MutableLiveData<String> networkSSIDLiveData;


    public ConnectViewModel(@NonNull Application application) {
        super(application);
        rosDomain = RosDomain.getInstance(application);
        currentMaster = rosDomain.getMaster();
    }

    /**
     * 更新设置MasterIp
     * */
    public void setMasterIp(String ip){
        MasterEntity masterEntity=currentMaster.getValue();
        if(masterEntity==null){
            Log.d(TAG,"master为null,无法更新ip");
            return;
        }
        masterEntity.ip=ip;
        rosDomain.updateMaster(masterEntity);
    }

    /**
     * 更新设置MasterPort*/
    public void setMasterPort(String portString){
        int port = Integer.parseInt(portString);
        MasterEntity master = currentMaster.getValue();
        if (master == null) return;

        master.port = port;
        rosDomain.updateMaster(master);
    }


    public LiveData<MasterEntity> getCurrentMaster() {
        return currentMaster;
    }

    public MutableLiveData<String> getNetworkSSIDLiveData() {
        if (networkSSIDLiveData == null) {
            networkSSIDLiveData = new MutableLiveData<>();
        }
        setWifiSSID();
        return networkSSIDLiveData;
    }

    public LiveData<ConnectionStateType> getRosConnection() {
        return rosDomain.getConnectionStateType();
    }

    public RosDomain getRosDomain(){
        return rosDomain;
    }

    private void setWifiSSID() {
        WifiManager wifiManager = (WifiManager) getApplication().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        String ssid = NetWorkUtil.getWifiSSID(wifiManager);

        if (ssid == null) {
            ssid = "None";
        }

        networkSSIDLiveData.postValue(ssid);
    }

    public void setMasterDeviceIp(String deviceIp){
        rosDomain.setMasterDeviceIp(deviceIp);
    }

    public void disconnectFromMaster(){
        rosDomain.disconnectFromMaster();
    }
}
