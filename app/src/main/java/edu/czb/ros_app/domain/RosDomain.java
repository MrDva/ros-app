package edu.czb.ros_app.domain;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.HashMap;
import java.util.List;

import edu.czb.ros_app.model.db.DataStorage;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.ConfigRepository;
import edu.czb.ros_app.model.rosRepositories.ConfigRepositoryImpl;
import edu.czb.ros_app.model.rosRepositories.RosRepository;
import edu.czb.ros_app.model.rosRepositories.message.BaseData;
import edu.czb.ros_app.model.rosRepositories.message.RosData;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.node.AbstractNode;
import edu.czb.ros_app.model.rosRepositories.node.SubscriberNode;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.domain
 * @ClassName: RosDomain
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/16 15:05
 * @Version: 1.0
 */
public class RosDomain {
    private static final String TAG=RosDomain.class.getSimpleName();

    private LiveData<MasterEntity> master;
    private RosRepository rosRepository;
    private MutableLiveData<Long> id=new MutableLiveData<>(1L);
    private ConfigRepository configRepository;
    /*private final LiveData<HashMap<String,BaseEntity>> currentWidgets;*/
    private static RosDomain mInstance;

    public RosDomain(Application application){

        this.rosRepository=new RosRepository(application);
        this.configRepository = ConfigRepositoryImpl.getInstance(application);
        /*currentWidgets = Transformations.switchMap(id,
                configRepository::getWidgets);*/
        /*currentWidgets.observeForever(rosRepository::updateWidgets);*/
        master = Transformations.switchMap(id,configRepository::getMaster);
        master.observeForever(rosRepository::updateMaster);
    }

    public static RosDomain getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new RosDomain(application);
        }
        return mInstance;
    }

    public void updateMaster(MasterEntity masterEntity){
        MasterEntity value = this.master.getValue();
        value.ip=masterEntity.ip;
        value.port=masterEntity.port;
        Log.i(TAG,"更新master");
        configRepository.updateMaster(value);
        rosRepository.updateMaster(value);

    }
    public LiveData<MasterEntity> getMaster(){
        return this.master;
    }

    public MutableLiveData<ConnectionStateType> getConnectionStateType(){
        return rosRepository.getRosConnected();
    }

    public List<Topic> getTopicList() { return rosRepository.getTopicList(); }

    public MutableLiveData<RosData> getRosData(){
        return rosRepository.getRosData();
    }

    public MutableLiveData<RosData> getMapRosData(){
        return rosRepository.getRosData();
    }

    public void connectToMaster(){
        rosRepository.connectToMaster();
    }

    public void disconnectFromMaster(){
        rosRepository.disconnectFromMaster();
    }

    public void initStaticNodes(){
        rosRepository.initStaticNodes();
    }

    public void clearAllNodes(){
        rosRepository.clearAllNodes();
    }

    public void unregisterAllNodes(){
        rosRepository.unregisterAllNodes();
    }
    public void registerAllNodes(){
        rosRepository.registerAllNodes();
    }

    public void addAllTopic(){
        rosRepository.addAllTopic();
    }

    public void setMasterDeviceIp(String deviceIp){
        rosRepository.setMasterDeviceIp(deviceIp);
    }

    public void publicData(BaseData baseData){
        rosRepository.publishData(baseData);
    }

    public List<Topic> getCurrentTopic(){
        return rosRepository.getCurrentTopic();
    }

    public void additionNode(Topic topic){
        rosRepository.additionNode(topic);
    }

    public void registerAllAdditionNodes(){
        rosRepository.registerAllAdditionNodes();
    }

    public void unregisterAllAdditionNodes(){
        rosRepository.unregisterAllAdditionNodes();
    }

}
