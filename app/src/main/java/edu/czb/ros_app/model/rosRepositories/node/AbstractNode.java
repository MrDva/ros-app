package edu.czb.ros_app.model.rosRepositories.node;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

import edu.czb.ros_app.model.rosRepositories.message.Topic;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.node
 * @ClassName: AbstractNode
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 19:37
 * @Version: 1.0
 */
public class AbstractNode implements NodeMain {
    public static final String TAG = AbstractNode.class.getSimpleName();
    protected Topic topic;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(topic.name);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log.i(TAG, "On Start:  " + topic.name);
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(TAG, "On Shutdown:  " + topic.name);
    }

    @Override
    public void onShutdownComplete(Node node) {
        Log.i(TAG, "On Shutdown Complete: " + topic.name);
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        throwable.printStackTrace();
    }

    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
