package edu.czb.ros_app.widgets.temperature;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import sensor_msgs.BatteryState;
import sensor_msgs.Temperature;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.temperature
 * @ClassName: Temperature
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/20 20:08
 * @Version: 1.0
 */
public class TemperatureEntity extends BaseEntity {

    public TemperatureEntity() {
        this.topic = new Topic(TopicName.TEMPERATURE, Temperature._TYPE);
    }
    public TemperatureEntity(String topicName){
        this.topic=new Topic(topicName,Temperature._TYPE);
    }

}
