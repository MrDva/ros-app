package edu.czb.ros_app.widgets.map;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import geometry_msgs.PolygonStamped;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.map
 * @ClassName: MapPathEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/3/13 16:09
 * @Version: 1.0
 */
public class MapPathEntity extends BaseEntity {
    public MapPathEntity(){
        this.topic=new Topic(TopicName.MAP_PATH, PolygonStamped._TYPE);
    }
    public MapPathEntity(String topicName){
        this.topic=new Topic(topicName,PolygonStamped._TYPE);
    }
}
