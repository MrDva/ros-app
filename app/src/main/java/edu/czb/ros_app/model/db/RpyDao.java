package edu.czb.ros_app.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import edu.czb.ros_app.model.entities.info.BatteryStateEntity;
import edu.czb.ros_app.model.entities.info.RpyDataEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: RpyDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 22:32
 * @Version: 1.0
 */
@Dao
public abstract class RpyDao implements BaseDao<RpyDataEntity> {
    @Query("select * from rpy_table")
    abstract List<RpyDataEntity> getAllData();

    @Query("delete  from rpy_table")
    abstract void deleteAllData();

    @Query("select * from rpy_table order by createdTime desc limit :limit")
    abstract LiveData<List<RpyDataEntity>> getLimitListData(long limit);
}
