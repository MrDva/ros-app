package edu.czb.ros_app.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import edu.czb.ros_app.model.entities.MasterEntity;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: MasterDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/16 17:48
 * @Version: 1.0
 */
@Dao
public abstract class MasterDao implements BaseDao<MasterEntity>{
    @Query("SELECT * FROM master_table WHERE configId = :configId LIMIT 1")
    abstract LiveData<MasterEntity> getMaster(long configId);

    @Query("DELETE FROM master_table WHERE configId = :configId")
    abstract void delete(long configId);

    @Query("DELETE FROM master_table")
    abstract void deleteAll();
}
