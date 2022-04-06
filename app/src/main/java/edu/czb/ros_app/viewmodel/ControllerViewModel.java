package edu.czb.ros_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import edu.czb.ros_app.domain.RosDomain;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.viewmodel
 * @ClassName: ControllerViewModel
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/26 11:05
 * @Version: 1.0
 */
public class ControllerViewModel extends AndroidViewModel {
    private static final String TAG=ConnectViewModel.class.getSimpleName();

    private RosDomain rosDomain;

    public ControllerViewModel(@NonNull Application application) {
        super(application);
        rosDomain=RosDomain.getInstance(application);
    }

    public RosDomain getRosDomain() {
        return rosDomain;
    }
}
