package edu.czb.ros_app.widgets.imu;

import android.provider.ContactsContract;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import sensor_msgs.Imu;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: ImuEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/4 17:28
 * @Version: 1.0
 */
public class ImuEntity extends BaseEntity {
    public ImuEntity(){
        this.topic=new Topic("/sensor/data_raw", Imu._TYPE);
    }
}
