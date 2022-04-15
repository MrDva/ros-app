package edu.czb.ros_app.model.entities.info;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities.info
 * @ClassName: TempDataEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/15 10:53
 * @Version: 1.0
 */
@Entity(tableName = "temp_table")
public class TempDataEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public double temp;
    public long createdTime;
}
