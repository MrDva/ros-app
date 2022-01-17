package edu.czb.ros_app.model.rosRepositories;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.ros.address.InetAddressFactory;

import java.lang.ref.WeakReference;
import java.net.URI;

import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionCheckTask;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionListener;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorService;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorServiceListener;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories
 * @ClassName: RosRepository
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/16 15:10
 * @Version: 1.0
 */
public class RosRepository {
    private static final String TAG=RosRepository.class.getSimpleName();

    private MasterEntity masterEntity;
    private final WeakReference<Context> contextReference;
    private final MutableLiveData<ConnectionStateType> rosConnected;
    private NodeMainExecutorService nodeMainExecutorService;

    public RosRepository(Context context){
        this.contextReference=new WeakReference<>(context);
        this.rosConnected = new MutableLiveData<>(ConnectionStateType.DISCONNECTED);
    }


    public void updateMaster(MasterEntity master){
        Log.i(TAG, "Update Master");

        if(master == null) {
            Log.i(TAG, "Master is null");
            return;
        }

        this.masterEntity = master;
    }

    public MutableLiveData<ConnectionStateType> getRosConnected() {
        return rosConnected;
    }

    public void connectToMaster(){
        Log.i(TAG, "Connect to Master");
        ConnectionStateType connectionType = rosConnected.getValue();
        if (connectionType == ConnectionStateType.CONNECTED || connectionType == ConnectionStateType.PENDING) {
            return;
        };

        rosConnected.setValue(ConnectionStateType.PENDING);

        // Check connection
        new ConnectionCheckTask(new ConnectionListener() {

            @Override
            public void onSuccess() {
                bindService();
            }

            @Override
            public void onFailed() {
                rosConnected.setValue(ConnectionStateType.FAILED);
            }
        }).execute(masterEntity);
    }


    private void bindService() {
        Context context = contextReference.get();
        if (context == null) {
            return;
        }

        RosServiceConnection serviceConnection = new RosServiceConnection(getMasterURI());

        // Create service intent
        Intent serviceIntent = new Intent(context, NodeMainExecutorService.class);
        serviceIntent.setAction(NodeMainExecutorService.ACTION_START);

        // Start service and check state
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private URI getMasterURI() {
        String masterString = String.format("http://%s:%s/", masterEntity.ip, masterEntity.port);
        return URI.create(masterString);
    }

    private String getDefaultHostAddress() {
        return InetAddressFactory.newNonLoopback().getHostAddress();
    }




    private final class RosServiceConnection implements ServiceConnection {

        NodeMainExecutorServiceListener serviceListener;
        URI customMasterUri;


        RosServiceConnection(URI customUri) {
            customMasterUri = customUri;
        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            nodeMainExecutorService = ((NodeMainExecutorService.LocalBinder) binder).getService();
            nodeMainExecutorService.setMasterUri(customMasterUri);
            nodeMainExecutorService.setRosHostname(getDefaultHostAddress());

            serviceListener = nodeMainExecutorService ->
                    rosConnected.postValue(ConnectionStateType.DISCONNECTED);

            nodeMainExecutorService.addListener(serviceListener);
            rosConnected.postValue(ConnectionStateType.CONNECTED);

            //registerAllNodes();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            nodeMainExecutorService.removeListener(serviceListener);
        }
    }


}
