package edu.czb.ros_app.widgets.imu;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import geometry_msgs.Vector3;
import sensor_msgs.Imu;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: RpyEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/3 19:31
 * @Version: 1.0
 */
public class RpyEntity extends BaseEntity {
    public RpyEntity(){
        this.topic=new Topic("/sensor/imu/rpy", Vector3._TYPE);
    }
}
