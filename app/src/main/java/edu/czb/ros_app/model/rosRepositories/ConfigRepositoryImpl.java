package edu.czb.ros_app.model.rosRepositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import edu.czb.ros_app.model.db.DataStorage;
import edu.czb.ros_app.model.entities.MasterEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories
 * @ClassName: ConfigRepositoryImpl
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 10:37
 * @Version: 1.0
 */
public class ConfigRepositoryImpl implements ConfigRepository{
    private static final String TAG = ConfigRepositoryImpl.class.getSimpleName();
    private final DataStorage mDataStorage;
    private static ConfigRepositoryImpl mInstance;

    /*private final MediatorLiveData<Long> mCurrentConfigId;
*/
    private ConfigRepositoryImpl(Application application) {
        mDataStorage = DataStorage.getInstance(application);

        /*mCurrentConfigId = new MediatorLiveData<>();
        mCurrentConfigId.addSource(mDataStorage.getLatestConfig(), config -> {
            Log.i(TAG, "New Config: " + config);

            if (config != null)
                mCurrentConfigId.postValue(config.id);
        });*/
    }
    @Override
    public void updateMaster(MasterEntity master) {
        mDataStorage.updateMaster(master);
    }

    @Override
    public LiveData<MasterEntity> getMaster(long configId) {
        LiveData<MasterEntity> master = mDataStorage.getMaster(configId);
        if(master.getValue()==null){
            MasterEntity masterEntity = new MasterEntity();
            mDataStorage.addMaster(masterEntity);
            master = mDataStorage.getMaster(masterEntity.configId);
        }
        return master;

    }


    public static ConfigRepositoryImpl getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new ConfigRepositoryImpl(application);
        }

        return mInstance;
    }
}
