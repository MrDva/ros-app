package edu.czb.ros_app.model.rosRepositories.node;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeListener;
import org.ros.node.topic.Subscriber;

import edu.czb.ros_app.model.rosRepositories.message.RosData;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.node
 * @ClassName: SubscriberNode
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 19:40
 * @Version: 1.0
 */
public class SubscriberNode extends AbstractNode{


    private final MessageListener<RosData> listener;

    public SubscriberNode(MessageListener<RosData> listener) {
        this.listener = listener;
    }

    @Override
    public void onStart(ConnectedNode parentNode){
        super.onStart(parentNode);

        Subscriber<? extends Message> subscriber = parentNode.newSubscriber(topic.name, topic.type);

        subscriber.addMessageListener(data->{
            listener.onNewMessage(new RosData(topic,data));
        });
    }

    /*public interface NodeListener  {
        void onNewMessage(RosData message);
    }*/

}
