package edu.czb.ros_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.Locale;

import edu.czb.ros_app.domain.RosDomain;
import edu.czb.ros_app.model.rosRepositories.message.RosData;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.viewmodel
 * @ClassName: InfoViewModel
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/18 14:52
 * @Version: 1.0
 */
public class InfoViewModel extends AndroidViewModel {

    private RosDomain rosDomain;

    private MutableLiveData<String> battery;
    private MutableLiveData<String> imu;
    private MutableLiveData<String> navSatFix;
    private MutableLiveData<String> temperature;
    private MutableLiveData<RosData> rosDate;
    private LifecycleOwner viewLifecycleOwner;

    public InfoViewModel(@NonNull Application application) {
        super(application);
        rosDomain = RosDomain.getInstance(application);
        rosDate=rosDomain.getRosData();
        /*rosDate.observe(viewLifecycleOwner,rosData -> {
            if(rosData.getTopic().name.toLowerCase().contains("battery")){
                battery.postValue(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("imu")){
                imu.postValue(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("navSatFix")){
                navSatFix.postValue(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("temperature")){
                temperature.postValue(rosData.getMessage().toString());
            }
        });*/
    }

    public void setViewLifecycleOwner(LifecycleOwner viewLifecycleOwner) {
        this.viewLifecycleOwner = viewLifecycleOwner;
    }

    public MutableLiveData<RosData> getRosDate(){
        return rosDate;
    }

    public RosDomain getRosDomain() {
        return rosDomain;
    }

    public MutableLiveData<String> getBattery() {
        return battery;
    }

    public MutableLiveData<String> getImu() {
        return imu;
    }

    public MutableLiveData<String> getNavSatFix() {
        return navSatFix;
    }

    public MutableLiveData<String> getTemperature() {
        return temperature;
    }

}
