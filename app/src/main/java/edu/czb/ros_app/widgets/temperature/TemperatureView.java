package edu.czb.ros_app.widgets.temperature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.ros.internal.message.Message;

import edu.czb.ros_app.R;
import edu.czb.ros_app.widgets.battey.BatteryView;
import sensor_msgs.Temperature;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.temperature
 * @ClassName: TemperatureVIew
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/20 20:12
 * @Version: 1.0
 */
public class TemperatureView extends ViewGroup {
    public static final String TAG = TemperatureView.class.getSimpleName();

    private static final Paint temPaint=new Paint();
    private static final Paint paintCircle=new Paint();
    private static final Paint paintLine=new Paint();
    private static final Paint textPaint=new Paint();
    private static final int textSize=30;
    private   float bgHeight;
    private   float radius;
    private   float xDegree;
    private   float yDegree;
    private   int temMax;
    private   int temMin;
    private   float gap;
    private float hGap;
    private float bgWidth;
    private double hTemMax;
    private double hTemMin;
    private float temperature;


    public TemperatureView(Context context){
        super(context);
    }
    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        init();
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }
    @Override
    protected void drawableStateChanged(){
        super.drawableStateChanged();
    }

    private void init(){


        temPaint.setColor(getResources().getColor(R.color.delete_red));
        temPaint.setStrokeWidth(20);
        paintCircle.setColor(getResources().getColor(R.color.delete_red));
        paintLine.setColor(getResources().getColor(R.color.ok_green));
        paintLine.setStrokeWidth(5);
        textPaint.setColor(getResources().getColor(R.color.ok_green));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
    }
    public void onNewMessage(Message message){
        Temperature temperature=(Temperature) message;
        updateTemperature((float) temperature.getTemperature());
        this.invalidate();
    }
    public void updateTemperature(float temperature){
        this.temperature=temperature;
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        bgHeight=getHeight();
        bgWidth=getWidth();
        radius=getHeight()/48;
        temMin=-20;
        temMax=55;
        gap=(bgHeight-radius*5)/(temMax-temMin);
        yDegree=bgHeight-radius*3;
        canvas.drawLine(bgWidth /2,yDegree-(temperature-temMin)*gap,bgWidth /2,bgHeight-radius*2,temPaint);
        hTemMax=32+temMax*1.8;
        hTemMin=32+temMin*1.8;
        hGap=Float.parseFloat(String.valueOf((bgHeight-radius*5)/(hTemMax-hTemMin)));
       /* background.setColor(getResources().getColor(R.color.bgColor));*/
       // canvas.drawCircle(leftPadding+50,topPadding-20+18,20,background);
        //canvas.drawRect(leftPadding,topPadding,leftPadding+bgWidth,bgHeight+topPadding,background);
       // canvas.drawCircle(leftPadding+50,yDegree+18+topPadding,30,background);

        canvas.drawCircle(  bgWidth /2,bgHeight-radius*2,radius,paintCircle);
        canvas.drawLine( bgWidth /2- bgWidth /24- bgWidth /6,yDegree,bgWidth /2- bgWidth /24,yDegree,paintLine);
        paintLine.setColor(getResources().getColor(R.color.ok_green));
        float end =yDegree;
        while (temMin<temMax){
            if(temMin%10==0){
                canvas.drawLine( bgWidth /2- bgWidth /24- bgWidth /6, yDegree,bgWidth /2- bgWidth /24,   yDegree,paintLine);
                int text= (int) (32+temMin*1.8);
                canvas.drawText(String.valueOf(temMin),   bgWidth /2- bgWidth /24- bgWidth /6- bgWidth /20,yDegree,textPaint);
            }else if(temMin%5==0){

                canvas.drawLine(    bgWidth /2- bgWidth /12- bgWidth /12,yDegree, bgWidth /2- bgWidth /24,yDegree,paintLine);
            }else {
                canvas.drawLine(  bgWidth /2- bgWidth /12- bgWidth /24,yDegree,   bgWidth /2- bgWidth /24,yDegree,paintLine);
            }
            temMin++;
            yDegree-=gap;
        }
        canvas.drawText(String.valueOf("℃"),   bgWidth /2- bgWidth /24- bgWidth /12- bgWidth /20,yDegree,textPaint);


    }

    private float paintGraduatedLine(Canvas canvas,int num,float begin,float end,int text){
        if(begin>=end)
            return end;
        float temp=begin;
        float gap=(end-begin)/num;
        int i=0;
        while(i<num){
            if(text%10==0){
                canvas.drawLine(  bgWidth /2+ bgWidth /24, begin,bgWidth /2+ bgWidth /24+ bgWidth /6,begin,paintLine);
                canvas.drawText(String.valueOf(text),   bgWidth /2+ bgWidth /24+ bgWidth /6+bgWidth /20,begin,textPaint);
            }else if(text%5==0){
                canvas.drawLine(    bgWidth /2+ bgWidth /24,begin, bgWidth /2+ bgWidth /12+ bgWidth /12,begin,paintLine);
            }else {
                canvas.drawLine( bgWidth /2+ bgWidth /24, begin,  bgWidth /2+ bgWidth /12+ bgWidth /24, begin,paintLine);
            }
            text--;
            i++;
            begin+=gap;
            if(i==num-1){
                begin=end;
            }
        }
        return temp;
    }
}
