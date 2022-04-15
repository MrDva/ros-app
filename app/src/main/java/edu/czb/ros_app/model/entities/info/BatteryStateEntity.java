package edu.czb.ros_app.model.entities.info;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities.info
 * @ClassName: BatteryStateEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 17:31
 * @Version: 1.0
 */
@Entity(tableName = "battery_table")
public class BatteryStateEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public double voltage;
    public double current;
    public double capacity;
    public double charge;
    public long createdTime;
}
