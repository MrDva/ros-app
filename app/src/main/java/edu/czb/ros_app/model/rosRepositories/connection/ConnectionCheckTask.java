package edu.czb.ros_app.model.rosRepositories.connection;

import android.os.AsyncTask;

import edu.czb.ros_app.model.entities.MasterEntity;
import edu.czb.ros_app.utils.NetWorkUtil;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.connection
 * @ClassName: ConnectionCheckTask
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/15 15:54
 * @Version: 1.0
 */
public class ConnectionCheckTask extends AsyncTask<MasterEntity, Void, Boolean> {
    private final ConnectionListener listener;
    private static final int TIMEOUT_TIME = 2 * 1000;

    public ConnectionCheckTask(ConnectionListener listener) {
        this.listener = listener;
    }
    @Override
    protected Boolean doInBackground(MasterEntity... masterEntities) {
        MasterEntity masterEnt = masterEntities[0];
        return NetWorkUtil.isHostAvailable(masterEnt.ip, masterEnt.port, TIMEOUT_TIME);
    }


    @Override
    protected void onPostExecute(Boolean success) {
        if (success)
            listener.onSuccess();
        else
            listener.onFailed();
    }
}
