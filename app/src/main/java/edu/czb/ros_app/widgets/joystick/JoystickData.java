package edu.czb.ros_app.widgets.joystick;

import edu.czb.ros_app.model.rosRepositories.message.BaseData;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.joystick
 * @ClassName: JoystickData
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/21 12:25
 * @Version: 1.0
 */
public class JoystickData extends BaseData {
    public static final String TAG = JoystickData.class.getSimpleName();
    public float x;
    public float y;


    public JoystickData(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
