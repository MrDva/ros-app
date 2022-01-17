package edu.czb.ros_app.utils;

import android.os.AsyncTask;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.utils
 * @ClassName: LambdaTask
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 9:11
 * @Version: 1.0
 */
public class LambdaTask extends AsyncTask<Void, Void, Void> {
    TaskRunnable taskRunnable;


    public LambdaTask(TaskRunnable taskRunnable){
        this.taskRunnable = taskRunnable;
    }
    @Override
    protected Void doInBackground(Void... voids)
    {
        taskRunnable.run();
        return null;
    }

    public interface TaskRunnable {
        void run();
    }
}
