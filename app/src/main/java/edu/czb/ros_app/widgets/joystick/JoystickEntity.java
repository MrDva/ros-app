package edu.czb.ros_app.widgets.joystick;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import geometry_msgs.Twist;
import sensor_msgs.Joy;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.joystick
 * @ClassName: JsystickEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/21 12:25
 * @Version: 1.0
 */
public class JoystickEntity extends BaseEntity {
    public String xAxisMapping;
    public String yAxisMapping;
    public float xScaleLeft;
    public float xScaleRight;
    public float yScaleLeft;
    public float yScaleRight;
    public float publishRate = 1f;
    public boolean immediatePublish = false;

    public JoystickEntity() {
        this.topic = new Topic(TopicName.JOY, Joy._TYPE);
        this.immediatePublish = false;
        this.publishRate = 20f;
        this.xAxisMapping = "Angular/Z";
        this.yAxisMapping = "Linear/X";
        this.xScaleLeft = 1;
        this.xScaleRight = -1;
        this.yScaleLeft = -1;
        this.yScaleRight = 1;
    }

    public JoystickEntity(String topicName){
        this.topic=new Topic(topicName, Joy._TYPE);
    }
}
