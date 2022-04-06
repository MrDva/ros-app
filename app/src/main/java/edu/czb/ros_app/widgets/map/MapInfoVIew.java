package edu.czb.ros_app.widgets.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.ros.internal.message.Message;

import java.util.Arrays;

import edu.czb.ros_app.R;
import sensor_msgs.Imu;
import sensor_msgs.NavSatFix;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.map
 * @ClassName: MapInfoVIew
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/4 20:49
 * @Version: 1.0
 */
public class MapInfoVIew extends ViewGroup {
    Paint textPaint;
    String content="null";

    public MapInfoVIew(Context context) {
        super(context);
        init();
    }

    public MapInfoVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.bgColor));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(20);
        this.setWillNotDraw(false);
        content="null";
    }
    public void onNewMessage(Message message) {
        //supr.onNewMessage(message);
        NavSatFix navSatFix=(NavSatFix) message;
        content="Latitude:"+navSatFix.getLatitude()+"\n"
                +"Longitude"+navSatFix.getLongitude()+"\n"
                +"Altitude"+navSatFix.getAltitude()+"\n";
        this.invalidate();
    }
    @Override
    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawRect(0, 0, getWidth(), getHeight(), highlightPaint);
        float width = getWidth();
        int i=0;
        float x=textPaint.getTextSize();
        float y=textPaint.getTextSize();
        while(i<content.length()){
            canvas.drawText(content.substring(i,i+1), x, y, textPaint);
            i++;
            x+=textPaint.getTextSize();
            if(x>=getWidth()){
                x=textPaint.getTextSize();
                y+=textPaint.getTextSize();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
