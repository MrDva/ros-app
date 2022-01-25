package edu.czb.ros_app.model.rosRepositories;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import org.ros.address.InetAddressFactory;
import org.ros.internal.node.client.MasterClient;
import org.ros.internal.node.response.Response;
import org.ros.master.client.TopicType;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeListener;
import org.ros.rosjava_geometry.FrameTransformTree;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionCheckTask;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionListener;
import edu.czb.ros_app.model.rosRepositories.message.RosData;

import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.node.AbstractNode;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorService;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorServiceListener;
import edu.czb.ros_app.model.rosRepositories.node.SubscriberNode;
import edu.czb.ros_app.widgets.battey.BatteryEntity;
import edu.czb.ros_app.widgets.battey.BatteryView;
import geometry_msgs.TransformStamped;
import tf2_msgs.TFMessage;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories
 * @ClassName: RosRepository
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/16 15:10
 * @Version: 1.0
 */
public class RosRepository implements MessageListener<RosData> {
    private static final String TAG=RosRepository.class.getSimpleName();

    private MasterEntity masterEntity;
    private final WeakReference<Context> contextReference;
    private final MutableLiveData<ConnectionStateType> rosConnected;
    private NodeMainExecutorService nodeMainExecutorService;
    private final MutableLiveData<RosData> receivedData;
    private final HashMap<Topic, AbstractNode> currentNodes;
    private NodeConfiguration nodeConfiguration;
    //private FrameTransformTree frameTransformTree;
    public RosRepository(Context context){
        this.contextReference=new WeakReference<>(context);
        this.currentNodes = new HashMap<>();
        this.rosConnected = new MutableLiveData<>(ConnectionStateType.DISCONNECTED);
        this.receivedData = new MutableLiveData<>();
       // this.frameTransformTree = TransformProvider.getInstance().getTree();
        this.initStaticNodes();
    }

    @Override
    public void onNewMessage(RosData message) {
        // Save transforms from tf messages
        if (message.getMessage() instanceof TFMessage) {
            TFMessage tf = (TFMessage) message.getMessage();

            for (TransformStamped transform: tf.getTransforms()) {
                //frameTransformTree.update(transform);
                Log.i(TAG,transform.toString());
            }
        }
        this.receivedData.postValue(message);
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

    public MutableLiveData<RosData> getRosData(){
        return receivedData;
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


    public void disconnectFromMaster(){
        Log.i(TAG, "Disconnect from Master");
        if (nodeMainExecutorService == null) {
            return;
        }
        this.unregisterAllNodes();
        nodeMainExecutorService.shutdown();
    }

    private void registerAllNodes() {
        for (AbstractNode node: currentNodes.values()) {
            this.registerNode(node);
        }
    }

    private void unregisterAllNodes() {
        for (AbstractNode node: currentNodes.values()) {
            this.unregisterNode(node);
        }
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

    /*@Override
    public void onNewMessage(RosData message) {
        receivedData.postValue(message);
    }*/


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
            rosConnected.setValue(ConnectionStateType.CONNECTED);

            registerAllNodes();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            nodeMainExecutorService.removeListener(serviceListener);
        }
    }

    /**
     * Get a list from the ROS Master with all available topics.
     * @return Topic list
     */
    public List<Topic> getTopicList(){
        ArrayList<Topic> topicList=new ArrayList<>();
        if(nodeMainExecutorService==null){
            return topicList;
        }
        MasterClient masterClient=new MasterClient(nodeMainExecutorService.getMasterUri());
        GraphName graphName=GraphName.newAnonymous();
        Response<List<TopicType>>responseList=masterClient.getTopicTypes(graphName);
        for(TopicType topicType:responseList.getResult()){
            String name=topicType.getName();
            String type=topicType.getMessageType();
            if(type.contains("gazebo_msgs")){
                continue;
            }
            topicList.add(new Topic(name,type));
        }
        return topicList;

    }
    /**
     * Connect the node to ROS node graph if a connection to the ROS master is running.
     * @param node Node to connect
     */
    private void registerNode(AbstractNode node) {
        Log.i(TAG, "Register Node: " + node.getTopic().name);

        if (rosConnected.getValue() != ConnectionStateType.CONNECTED) {
            Log.w(TAG, "Not connected with master");
            return;
        }
        nodeMainExecutorService.execute(node, nodeConfiguration);
    }

    /**
     * Set the master device IP in the Nodeconfiguration
     */
    public void setMasterDeviceIp(String deviceIp) {
        nodeConfiguration = NodeConfiguration.newPublic(deviceIp, getMasterURI());
    }

    /**
     * Disconnect the node from ROS node graph if a connection to the ROS master is running.
     * @param node Node to disconnect
     */
    private void unregisterNode(AbstractNode node) {
        if (node == null) return;

        Log.i(TAG, "Unregister Node: " + node.getTopic().name);

        if (rosConnected.getValue() != ConnectionStateType.CONNECTED) {
            Log.w(TAG, "Not connected with master");
            return;
        }

        nodeMainExecutorService.shutdownNodeMain(node);
    }
    

    public void addAllTopic(){

        List<Topic> topicList = getTopicList();
        Log.i(TAG,"addAllTopic:"+topicList.size());
        AbstractNode node;
        for (Topic topic : topicList) {
            try {
                if(!topic.type.contains("std_msgs")||topic.name.contains("debug")){
                    continue;
                }
                node =new SubscriberNode(this);
                currentNodes.put(topic,node);
                node.setTopic(topic);
                this.registerNode(node);
                Log.i(TAG,node.getTopic().name+":"+node.getTopic().type);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        Log.i(TAG,"addedTopic:"+currentNodes.size());
    }


    /**
     * Initialize static nodes eg. tf and tf_static.
     */
    private void initStaticNodes() {
        Log.i(TAG,"initStaticNodes");
        Topic tfTopic = new Topic("/tf", TFMessage._TYPE);
        SubscriberNode tfNode = new SubscriberNode(this);
        tfNode.setTopic(tfTopic);
        currentNodes.put(tfTopic, tfNode);

        Topic tfStaticTopic = new Topic("/tf_static", TFMessage._TYPE);
        SubscriberNode tfStaticNode = new SubscriberNode(this);
        tfStaticNode.setTopic(tfStaticTopic);
        currentNodes.put(tfStaticTopic, tfStaticNode);

        BatteryEntity batteryEntity=new BatteryEntity();
        SubscriberNode node=new SubscriberNode(this);
        node.setTopic(batteryEntity.getTopic());
        currentNodes.put(batteryEntity.getTopic(),node);

    }

}
