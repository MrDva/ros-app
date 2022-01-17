package edu.czb.ros_app.model.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.utils.Constants;
import edu.czb.ros_app.utils.LambdaTask;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.db
 * @ClassName: DataStorage
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 9:04
 * @Version: 1.0
 */
@Database(entities =
        { MasterEntity.class},
        version = 6, exportSchema = false)
public abstract class DataStorage extends RoomDatabase {
    private static DataStorage instance;
    public static synchronized DataStorage getInstance(final Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataStorage.class, Constants.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        /*widgetNames = context.getResources().getStringArray(R.array.widget_names);*/

        return instance;
    }

    public abstract MasterDao masterDao();

    // Master methods ------------------------------------------------------------------------------

    public void addMaster(MasterEntity master) {
        new LambdaTask(() -> masterDao().insert(master)).execute();
    }

    public void updateMaster(MasterEntity master) {
        new LambdaTask(() -> masterDao().update(master)).execute();
    }

    public void deleteMaster(long configId) {
        new LambdaTask(() -> masterDao().delete(configId)).execute();
    }

    public LiveData<MasterEntity> getMaster(long id) {
        return masterDao().getMaster(id);
    }

    /*@NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }*/
}
