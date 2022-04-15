package edu.czb.ros_app.model.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import edu.czb.ros_app.model.entities.info.BatteryStateEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: BatteryStateDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 17:33
 * @Version: 1.0
 */
@Dao
public abstract class BatteryStateDao implements BaseDao<BatteryStateEntity>{
    @Query("select * from battery_table")
    abstract List<BatteryStateEntity> getAllData();

    @Query("delete  from battery_table")
    abstract void deleteAllData();

    @Query("select * from battery_table order by createdTime desc limit :limit")
    abstract LiveData<List<BatteryStateEntity>> getLimitListData(long limit);
}