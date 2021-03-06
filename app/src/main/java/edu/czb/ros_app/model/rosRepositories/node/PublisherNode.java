package edu.czb.ros_app.model.rosRepositories.node;

import android.util.Log;

import org.apache.commons.lang.ObjectUtils;
import org.ros.internal.message.DefaultMessageFactory;
import org.ros.internal.message.Message;
import org.ros.internal.message.action.ActionDefinitionFileProvider;
import org.ros.internal.message.definition.MessageDefinitionFileProvider;
import org.ros.message.MessageDeclaration;
import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.czb.ros_app.model.rosRepositories.message.BaseData;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.viewmodel.MapViewModel;
import edu.czb.ros_app.widgets.joystick.JoystickData;
import edu.czb.ros_app.widgets.map.MapPathData;
import edu.czb.ros_app.widgets.map.MyPoint32;
import geometry_msgs.Point32;
import geometry_msgs.Polygon;
import geometry_msgs.PolygonStamped;
import sensor_msgs.Joy;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.node
 * @ClassName: publisherNode
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 20:15
 * @Version: 1.0
 */
public class PublisherNode extends AbstractNode{
    private Publisher<Message> publisher;
    private Publisher<Message> publisherPoint;
    private BaseData lastData;
    private Timer pubTimer;
    private long pubPeriod = 100L;
    private boolean immediatePublish = true;

    @Override
    public void onStart(ConnectedNode parentNode) {
        publisher = parentNode.newPublisher(topic.name, topic.type);
        publisherPoint =parentNode.newPublisher("point32",Point32._TYPE);
        this.createAndStartSchedule();
    }

    /**
     * Call this method to publish a ROS message.
     *
     * @param data Data to publish
     */
    public void setData(BaseData data) {
        this.lastData = data;

        if (immediatePublish) {
            publish();
        }
    }

    /**
     * Set publishing frequency.
     * E.g. With a value of 10 the node will publish 10 times per second.
     *
     * @param hz Frequency in hertz
     */
    public void setFrequency(float hz) {
        this.pubPeriod = (long) (1000 / hz);
    }

    /**
     * Enable or disable immediate publishing.
     * In the enabled state the node will create und send a ros message as soon as
     * @link #setData(Object) is called.
     *
     * @param flag Enable immediate publishing
     */
    public void setImmediatePublish(boolean flag) {
        this.immediatePublish = flag;
    }

    private void createAndStartSchedule() {
        if (pubTimer != null) {
            pubTimer.cancel();
        }

        if (immediatePublish) {
            return;
        }

        pubTimer = new Timer();
        pubTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                publish();
            }
        }, pubPeriod, pubPeriod);
    }


    private void publish() {
        if (publisher == null) {
            return;
        }
        if (lastData == null) {
            return;
        }
        Message message = lastData.toRosMessage(publisher);
        if(lastData instanceof JoystickData){
            JoystickData joystickData=(JoystickData) lastData;
            Joy joy=(Joy) message;
            joy.setAxes(joystickData.axes);
            joy.setButtons(joystickData.button);
            message=joy;
            Log.i(TAG,"JoyMessage:"+Arrays.toString(joy.getAxes())+","+Arrays.toString(joy.getButtons()));
        }
        if(lastData instanceof MapPathData){
            MapPathData mapPathData=(MapPathData) lastData;
            PolygonStamped polygonStamped=(PolygonStamped) message;
            Polygon polygon = polygonStamped.getPolygon();
            List<Point32> points = polygon.getPoints();



            for (MyPoint32 dataPoint : mapPathData.getPoints()) {
                Point32 point = (Point32) publisherPoint.newMessage();
                point.setX(dataPoint.getX());
                point.setY(dataPoint.getY());
                point.setZ(dataPoint.getZ());
                points.add(point);
            }
            polygon.setPoints(points);
            polygonStamped.setPolygon(polygon);
            Log.i(TAG,"PolygonStamped"+Arrays.toString(points.toArray()));
        }

        publisher.publish(message);
        //Log.i(TAG,"public"+ Arrays.toString(((Joy)message).getAxes()));
    }
}
