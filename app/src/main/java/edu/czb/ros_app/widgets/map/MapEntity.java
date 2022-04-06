package edu.czb.ros_app.widgets.map;

import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
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
        this.topic = new Topic("navSatFix", NavSatFix._TYPE);
        this.displayVoltage = false;
    }
}
