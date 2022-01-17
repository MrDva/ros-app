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

import edu.czb.ros_app.model.db.DataStorage;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.enums.ConnectionStateType;
import edu.czb.ros_app.model.rosRepositories.ConfigRepository;
import edu.czb.ros_app.model.rosRepositories.ConfigRepositoryImpl;
import edu.czb.ros_app.model.rosRepositories.RosRepository;

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

    private static RosDomain mInstance;

    public RosDomain(Application application){

        this.rosRepository=new RosRepository(application);
        this.configRepository = ConfigRepositoryImpl.getInstance(application);

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

    public void connectToMaster(){
        rosRepository.connectToMaster();
    }

}
