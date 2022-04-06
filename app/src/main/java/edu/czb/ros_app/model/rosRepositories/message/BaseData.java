package edu.czb.ros_app.model.rosRepositories.message;

import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.message
 * @ClassName: BaseData
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 20:16
 * @Version: 1.0
 */
public class BaseData {
    protected Topic topic;
    protected Message message;

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return this.topic;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message toRosMessage(Publisher<Message> publisher){
        return publisher.newMessage();
    }
}
