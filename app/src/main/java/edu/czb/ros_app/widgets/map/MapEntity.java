package edu.czb.ros_app.widgets.map;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import sensor_msgs.BatteryState;
import sensor_msgs.NavSatFix;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.map
 * @ClassName: MapEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/4 12:57
 * @Version: 1.0
 */
public class MapEntity extends BaseEntity {
    public boolean displayVoltage;


    public MapEntity() {
        this.topic = new Topic(TopicName.MAP, NavSatFix._TYPE);
        this.displayVoltage = false;
    }
    public MapEntity(String topicName){
        this.topic=new Topic(topicName,NavSatFix._TYPE);
    }
}
