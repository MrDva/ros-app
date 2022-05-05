package edu.czb.ros_app.model.rosRepositories;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import org.ros.rosjava_geometry.Vector3;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.czb.ros_app.model.db.DataStorage;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.entities.info.BatteryStateEntity;
import edu.czb.ros_app.model.entities.info.LatLngEntity;
import edu.czb.ros_app.model.entities.info.RpyDataEntity;
import edu.czb.ros_app.model.entities.info.TempDataEntity;
import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionCheckTask;
import edu.czb.ros_app.model.rosRepositories.connection.ConnectionListener;
import edu.czb.ros_app.model.rosRepositories.message.BaseData;
import edu.czb.ros_app.model.rosRepositories.message.RosData;

import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import edu.czb.ros_app.model.rosRepositories.node.AbstractNode;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorService;
import edu.czb.ros_app.model.rosRepositories.node.NodeMainExecutorServiceListener;
import edu.czb.ros_app.model.rosRepositories.node.PublisherNode;
import edu.czb.ros_app.model.rosRepositories.node.SubscriberNode;
import edu.czb.ros_app.widgets.battey.BatteryEntity;
import edu.czb.ros_app.widgets.battey.BatteryView;
import edu.czb.ros_app.widgets.imu.DestYawEntity;
import edu.czb.ros_app.widgets.imu.ImuEntity;
import edu.czb.ros_app.widgets.imu.RpyEntity;
import edu.czb.ros_app.widgets.joystick.JoystickEntity;
import edu.czb.ros_app.widgets.map.MapEntity;
import edu.czb.ros_app.widgets.map.MapPathEntity;
import edu.czb.ros_app.widgets.temperature.TemperatureEntity;
import geometry_msgs.TransformStamped;
import sensor_msgs.BatteryState;
import sensor_msgs.NavSatFix;
import sensor_msgs.Temperature;
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
    private static final String TAG = RosRepository.class.getSimpleName();
    private Context context;
    private MasterEntity masterEntity;
    private final WeakReference<Context> contextReference;
    private final MutableLiveData<ConnectionStateType> rosConnected;
    private NodeMainExecutorService nodeMainExecutorService;
    private final MutableLiveData<RosData> receivedData;
    private final MutableLiveData<RosData> receivedMapData;
    private final HashMap<Topic, AbstractNode> currentNodes;
    private final HashMap<Topic, AbstractNode> currentPublishNodes;
    private final HashMap<Topic, AbstractNode> additionNodes;
    private NodeConfiguration nodeConfiguration;
    public final DataStorage dataStorage;

    //private FrameTransformTree frameTransformTree;
    public RosRepository(Context context) {
        this.contextReference = new WeakReference<>(context);
        this.currentNodes = new HashMap<>();
        this.additionNodes = new HashMap<>();
        this.currentPublishNodes = new HashMap<>();
        this.rosConnected = new MutableLiveData<>(ConnectionStateType.DISCONNECTED);
        this.receivedData = new MutableLiveData<>();
        this.receivedMapData = new MutableLiveData<>();
        this.context = context;
        this.dataStorage = DataStorage.getInstance(context);
        // this.frameTransformTree = TransformProvider.getInstance().getTree();
        this.initStaticNodes();
    }

    public void additionNode(Topic topic) {
        SubscriberNode node = new SubscriberNode(this);
        node.setTopic(topic);
        additionNodes.put(topic, node);
    }

    public void registerAllAdditionNodes() {
        for (AbstractNode node : additionNodes.values()) {
            this.registerNode(node);
        }
    }

    public void unregisterAllAdditionNodes() {
        for (AbstractNode node : additionNodes.values()) {
            this.unregisterNode(node);
        }
        additionNodes.clear();
    }

    @Override
    public void onNewMessage(RosData message) {
        // Save transforms from tf messages
        if (message.getMessage() instanceof TFMessage) {
            TFMessage tf = (TFMessage) message.getMessage();

            for (TransformStamped transform : tf.getTransforms()) {
                //frameTransformTree.update(transform);
                Log.i(TAG, transform.toString());
            }
        }
        if (message.getTopic().name.equals(getTopicName(TopicName.MAP))) {
            this.receivedMapData.postValue(message);
        }
        this.receivedData.postValue(message);
        String type = message.getTopic().type;
        if (message.getTopic().name.equals(getTopicName(TopicName.BATTERY))) {
            BatteryState batteryState = (BatteryState) (message.getMessage());
            BatteryStateEntity batteryStateEntity = new BatteryStateEntity();
            batteryStateEntity.createdTime = System.currentTimeMillis();
            batteryStateEntity.current = batteryState.getCurrent();
            batteryStateEntity.capacity = batteryState.getCapacity();
            batteryStateEntity.voltage = batteryState.getVoltage();
            batteryStateEntity.charge = batteryState.getCharge();
            TempDataEntity tempDataEntity = new TempDataEntity();
            tempDataEntity.createdTime = batteryStateEntity.createdTime;
            tempDataEntity.temp = batteryState.getPercentage();
            dataStorage.addBattery(batteryStateEntity);
            dataStorage.addTempDataEntity(tempDataEntity);
        } else if (message.getTopic().name.equals((getTopicName(TopicName.RPY)))) {
            geometry_msgs.Vector3 vector3 = (geometry_msgs.Vector3) (message.getMessage());
            RpyDataEntity entity = new RpyDataEntity();
            entity.createdTime = System.currentTimeMillis();
            entity.roll = vector3.getX();
            entity.yaw = vector3.getZ();
            entity.pitch = vector3.getY();
            dataStorage.addRpy(entity);
        } else if (message.getTopic().name.equals(getTopicName(TopicName.MAP))) {
            NavSatFix navSatFix = (NavSatFix) message.getMessage();
            LatLngEntity latLngEntity = new LatLngEntity();
            latLngEntity.lat = navSatFix.getLatitude();
            latLngEntity.lng = navSatFix.getLongitude();
            latLngEntity.createdTime = System.currentTimeMillis();
            dataStorage.addLatLngDataEntity(latLngEntity);
        }
    }

    /**
     * Find the associated node and inform it about the changed data.
     *
     * @param data Widget data that has changed
     */
    public void publishData(BaseData data) {
        AbstractNode node = currentPublishNodes.get(data.getTopic());
        //Log.i(TAG,"topic:"+data.getTopic().name);
        if (node instanceof PublisherNode) {
            ((PublisherNode) node).setData(data);
        }
    }

    public void updateMaster(MasterEntity master) {
        Log.i(TAG, "Update Master");

        if (master == null) {
            Log.i(TAG, "Master is null");
            return;
        }

        this.masterEntity = master;
    }

    public MutableLiveData<ConnectionStateType> getRosConnected() {
        return rosConnected;
    }

    public MutableLiveData<RosData> getRosData() {
        return receivedData;
    }

    public MutableLiveData<RosData> getReceivedMapData() {
        return receivedMapData;
    }

    public void connectToMaster() {
        Log.i(TAG, "Connect to Master");
        ConnectionStateType connectionType = rosConnected.getValue();
        if (connectionType == ConnectionStateType.CONNECTED || connectionType == ConnectionStateType.PENDING) {
            return;
        }
        ;

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


    public void disconnectFromMaster() {
        Log.i(TAG, "Disconnect from Master");
        if (nodeMainExecutorService == null) {
            return;
        }
        this.unregisterAllNodes();
        nodeMainExecutorService.shutdown();
    }

    public void registerAllNodes() {
        for (AbstractNode node : currentNodes.values()) {
            this.registerNode(node);
        }
        for (AbstractNode node : currentPublishNodes.values()) {
            this.registerNode(node);
        }
    }

    public void clearAllNodes() {
        currentNodes.clear();
    }

    public void unregisterAllNodes() {
        for (AbstractNode node : currentNodes.values()) {
            this.unregisterNode(node);
        }
        for (AbstractNode node : currentPublishNodes.values()) {
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
     *
     * @return Topic list
     */
    public List<Topic> getTopicList() {
        ArrayList<Topic> topicList = new ArrayList<>();
        if (nodeMainExecutorService == null) {
            return topicList;
        }
        try {
            MasterClient masterClient = new MasterClient(nodeMainExecutorService.getMasterUri());
            GraphName graphName = GraphName.newAnonymous();
            Response<List<TopicType>> responseList = masterClient.getTopicTypes(graphName);
            for (TopicType topicType : responseList.getResult()) {
                String name = topicType.getName();
                String type = topicType.getMessageType();
                if (type.contains("gazebo_msgs")) {
                    continue;
                }
                topicList.add(new Topic(name, type));
            }
        } catch (Exception e) {

        }
        return topicList;

    }

    /**
     * Connect the node to ROS node graph if a connection to the ROS master is running.
     *
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
     *
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


    public void addAllTopic() {

        List<Topic> topicList = getTopicList();
        Log.i(TAG, "addAllTopic:" + topicList.size());
        AbstractNode node;
        for (Topic topic : topicList) {
            try {
                if (!topic.type.contains("std_msgs") || topic.name.contains("debug")) {
                    continue;
                }
                node = new SubscriberNode(this);
                currentNodes.put(topic, node);
                node.setTopic(topic);
                this.registerNode(node);
                Log.i(TAG, node.getTopic().name + ":" + node.getTopic().type);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.i(TAG, "addedTopic:" + currentNodes.size());
    }


    /**
     * Initialize static nodes eg. tf and tf_static.
     */
    public void initStaticNodes() {
        Log.i(TAG, "initStaticNodes");
        /*Topic tfTopic = new Topic("/tf", TFMessage._TYPE);
        SubscriberNode tfNode = new SubscriberNode(this);
        tfNode.setTopic(tfTopic);
        currentNodes.put(tfTopic, tfNode);

        Topic tfStaticTopic = new Topic("/tf_static", TFMessage._TYPE);
        SubscriberNode tfStaticNode = new SubscriberNode(this);
        tfStaticNode.setTopic(tfStaticTopic);
        currentNodes.put(tfStaticTopic, tfStaticNode);*/

        BatteryEntity batteryEntity = new BatteryEntity(getTopicName(TopicName.BATTERY));
        SubscriberNode node = new SubscriberNode(this);
        node.setTopic(batteryEntity.getTopic());
        currentNodes.put(batteryEntity.getTopic(), node);

        MapEntity mapEntity = new MapEntity(getTopicName(TopicName.MAP));
        node = new SubscriberNode(this);
        node.setTopic(mapEntity.getTopic());
        currentNodes.put(mapEntity.getTopic(), node);

        /*TemperatureEntity temperatureEntity=new TemperatureEntity(getTopicName(TopicName.TEMPERATURE));
        node=new SubscriberNode(this);
        node.setTopic(temperatureEntity.getTopic());
        currentNodes.put(temperatureEntity.getTopic(),node);*/

        /*ImuEntity imuEntity=new ImuEntity(getTopicName(TopicName.IMU));
        node=new SubscriberNode(this);
        node.setTopic(imuEntity.getTopic());
        currentNodes.put(imuEntity.getTopic(),node);*/

        RpyEntity rpyEntity = new RpyEntity(getTopicName(TopicName.RPY));
        node = new SubscriberNode(this);
        node.setTopic(rpyEntity.getTopic());
        currentNodes.put(rpyEntity.getTopic(), node);

        DestYawEntity destYawEntity = new DestYawEntity(getTopicName(TopicName.DEST_YAW));
        node = new SubscriberNode(this);
        node.setTopic(destYawEntity.getTopic());
        currentNodes.put(destYawEntity.getTopic(), node);

        JoystickEntity joystickEntity = new JoystickEntity(getTopicName(TopicName.JOY));
        PublisherNode publisherNode = new PublisherNode();
        publisherNode.setTopic(joystickEntity.getTopic());
        currentPublishNodes.put(joystickEntity.getTopic(), publisherNode);

        MapPathEntity mapPathEntity = new MapPathEntity(getTopicName(TopicName.MAP_PATH));
        publisherNode = new PublisherNode();
        publisherNode.setTopic(mapPathEntity.getTopic());
        currentPublishNodes.put(mapPathEntity.getTopic(), publisherNode);

    }

    private String getTopicName(String TopicNameKey) {
        SharedPreferences topicInfo = context.getSharedPreferences(TopicName.TOPIC_KEY, Context.MODE_PRIVATE);
        return topicInfo.getString(TopicNameKey, TopicNameKey);
    }

    public List<Topic> getCurrentTopic() {
        return new ArrayList<>(currentNodes.keySet());
    }

}
