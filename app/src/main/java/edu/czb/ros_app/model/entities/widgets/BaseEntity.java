package edu.czb.ros_app.model.entities.widgets;

import java.util.ArrayList;

import edu.czb.ros_app.model.rosRepositories.message.Topic;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities.widgets
 * @ClassName: BaseEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 20:10
 * @Version: 1.0
 */
public class BaseEntity {
    public long id;
    public String name;
    public String type;
    public long configId=1;
    public long creationTime;
    public Topic topic;
    public boolean validMessage;
    public ArrayList<BaseEntity> childEntities;

    public BaseEntity() {
        childEntities = new ArrayList<>();
    }

    public Topic getTopic() {
        return topic;
    }
    public void setTopic(Topic topic) {
        this.topic=topic;
    }
}
