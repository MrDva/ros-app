package edu.czb.ros_app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.entities
 * @ClassName: ConnectionEntity
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/11 16:26
 * @Version: 1.0
 */
@Entity(tableName = "master_table")
public class MasterEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long configId=1L;
    public String ip="192.168.0.1";
    public int port=11311;
}
