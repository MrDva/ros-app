package edu.czb.ros_app.widgets.map;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Point32;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.map
 * @ClassName: MyPoint32
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/3/13 12:44
 * @Version: 1.0
 */
public class MyPoint32 {
    private float x;
    private float y;
    private float z;
    public MyPoint32(){
    }
    public MyPoint32(float x,float y,float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public float getX() {
        return x;
    }


    public void setX(float v) {
        this.x=v;
    }


    public float getY() {
        return y;
    }


    public void setY(float v) {
        this.y=v;
    }


    public float getZ() {
        return z;
    }


    public void setZ(float v) {
        this.z=v;
    }

}
