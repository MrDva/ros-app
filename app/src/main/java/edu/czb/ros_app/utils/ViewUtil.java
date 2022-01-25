package edu.czb.ros_app.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.utils
 * @ClassName: ViewUtil
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/19 15:40
 * @Version: 1.0
 */
public class ViewUtil {
    public static float dpToPx(Context context, float dp){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);

        return px;
    }
}
