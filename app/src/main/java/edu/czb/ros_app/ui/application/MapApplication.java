package edu.czb.ros_app.ui.application;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.application
 * @ClassName: MapApplication
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/2 11:56
 * @Version: 1.0
 */
public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
