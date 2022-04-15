package edu.czb.ros_app.widgets.battey;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import sensor_msgs.BatteryState;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.battey
 * @ClassName: BatterEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/19 14:53
 * @Version: 1.0
 */
public class BatteryEntity extends BaseEntity {
    public boolean displayVoltage;


    public BatteryEntity() {
        this.topic = new Topic(TopicName.BATTERY, BatteryState._TYPE);
        this.displayVoltage = false;
    }

    public BatteryEntity(String topicName){
        this.topic=new Topic(topicName,BatteryState._TYPE);
    }


}
