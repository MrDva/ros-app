package edu.czb.ros_app.model.rosRepositories;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.model.entities.widgets.BaseEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories
 * @ClassName: ConfigRepository
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 10:36
 * @Version: 1.0
 */
public interface ConfigRepository {

    void updateMaster(MasterEntity master);

    LiveData<MasterEntity> getMaster(long configId);

    LiveData<List<BaseEntity>> getWidgets(long configId);
}
