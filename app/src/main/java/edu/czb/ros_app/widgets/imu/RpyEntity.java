package edu.czb.ros_app.widgets.imu;

import android.content.Context;
import android.content.SharedPreferences;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
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
        this.topic=new Topic(TopicName.RPY, Vector3._TYPE);
    }
    public RpyEntity(String topicName){
        this.topic=new Topic(topicName,Vector3._TYPE);
    }
    public void setTopic(String topic){
        super.setTopic(new Topic(topic,Vector3._TYPE));
    }

}
