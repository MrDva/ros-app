package edu.czb.ros_app.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import edu.czb.ros_app.model.entities.info.LatLngEntity;
import edu.czb.ros_app.model.entities.info.RpyDataEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: LatLngDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/22 13:21
 * @Version: 1.0
 */
@Dao
public abstract class LatLngDao implements BaseDao<LatLngEntity>{
    @Query("select * from lat_lng_table")
    abstract List<LatLngEntity> getAllData();

    @Query("delete  from lat_lng_table")
    abstract void deleteAllData();

    @Query("select * from lat_lng_table order by createdTime desc limit :limit")
    abstract LiveData<List<LatLngEntity>> getLimitListData(long limit);
}
