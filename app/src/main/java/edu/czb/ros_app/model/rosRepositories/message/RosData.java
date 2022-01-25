package edu.czb.ros_app.model.rosRepositories.message;

import org.ros.internal.message.Message;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.message
 * @ClassName: RosData
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 17:43
 * @Version: 1.0
 */
public class RosData {
    private final Topic topic;
    private final Message message;


    public RosData(Topic topic, Message message) {
        this.topic = topic;
        this.message = message;
    }


    public Topic getTopic() {
        return this.topic;
    }

    public Message getMessage() {
        return this.message;
    }
}
