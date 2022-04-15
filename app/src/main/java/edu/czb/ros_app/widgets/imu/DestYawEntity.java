package edu.czb.ros_app.widgets.imu;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import std_msgs.Float64;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: DestYawEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 13:42
 * @Version: 1.0
 */
public class DestYawEntity extends BaseEntity {
    public DestYawEntity(){
        this(TopicName.DEST_YAW);
    }

    public DestYawEntity(String topicName){
        this.topic=new Topic(topicName, Float64._TYPE);
    }
}
