package edu.czb.ros_app.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import edu.czb.ros_app.model.entities.info.BatteryStateEntity;
import edu.czb.ros_app.model.entities.info.TempDataEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: TemperatureDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/15 11:20
 * @Version: 1.0
 */
@Dao
public abstract class TemperatureDao implements BaseDao<TempDataEntity> {
    @Query("select * from temp_table")
    abstract List<TempDataEntity> getAllData();

    @Query("delete  from temp_table")
    abstract void deleteAllData();

    @Query("select * from temp_table order by createdTime desc limit :limit")
    abstract LiveData<List<TempDataEntity>> getLimitListData(long limit);
}
