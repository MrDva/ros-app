package edu.czb.ros_app.model.entities.info;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities.info
 * @ClassName: LatLngEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/22 13:19
 * @Version: 1.0
 */
@Entity(tableName = "lat_lng_table")
public class LatLngEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long createdTime;
    public double lat;
    public double lng;
}
