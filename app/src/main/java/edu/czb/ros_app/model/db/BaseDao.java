package edu.czb.ros_app.model.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: BaseDao
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/16 17:48
 * @Version: 1.0
 */
public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T obj);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(T obj);

    @Delete
    int delete(T obj);
}
