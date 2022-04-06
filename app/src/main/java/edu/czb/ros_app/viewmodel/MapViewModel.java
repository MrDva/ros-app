package edu.czb.ros_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import edu.czb.ros_app.domain.RosDomain;
import edu.czb.ros_app.model.rosRepositories.message.RosData;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.viewmodel
 * @ClassName: MapViewModel
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/3 19:39
 * @Version: 1.0
 */
public class MapViewModel extends AndroidViewModel {
    private RosDomain rosDomain;
    private MutableLiveData<RosData> rosDate;
    public MapViewModel(@NonNull Application application) {
        super(application);
        rosDomain=RosDomain.getInstance(application);
        rosDate=rosDomain.getMapRosData();
    }

    public MutableLiveData<RosData> getRosDate(){
        return rosDate;
    }

    public RosDomain getRosDomain() {
        return rosDomain;
    }
}
