package edu.czb.ros_app.widgets.map;

import java.util.ArrayList;
import java.util.List;

import edu.czb.ros_app.model.rosRepositories.message.BaseData;
import geometry_msgs.Point32;
import geometry_msgs.Polygon;
import geometry_msgs.PolygonStamped;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.map
 * @ClassName: MapPathData
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/3/13 11:11
 * @Version: 1.0
 */
public class MapPathData  extends BaseData {
    private List<MyPoint32> point32s=new ArrayList<>();
    public MapPathData(List<MyPoint32> polygon){
        this.point32s=polygon;
    }
    public List<MyPoint32> getPoints(){
        return this.point32s;
    }
}
