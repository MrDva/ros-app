package edu.czb.ros_app.model.entities.info;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities.info
 * @ClassName: RpyDataEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 22:29
 * @Version: 1.0
 */
@Entity(tableName = "rpy_table")
public class RpyDataEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public double pitch;
    public double roll;
    public double yaw;
    public long createdTime;
}
