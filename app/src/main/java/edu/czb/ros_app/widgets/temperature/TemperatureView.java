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

import edu.czb.ros_app.R;
import edu.czb.ros_app.widgets.battey.BatteryView;

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

    private static final String temp="100";
    private static final Paint mPaint=new Paint();
    private static final Paint paintCircle=new Paint();
    private static final Paint paintLine=new Paint();
    private static final Paint paintCircle2=new Paint();
    private static final Paint background=new Paint();
    private static final Paint textPaint=new Paint();
    private static final int textSize=50;
    private   float bgHeight;
    private   float radius;
    private   float xDegree;
    private   float yDegree;
    private   int temMax;
    private   int temMin;
    private   float gap;
    private float hGap;
    private float bgWidth;
    private float hDegree;
    private double hTemMax;
    private double hTemMin;


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



        paintCircle.setColor(getResources().getColor(R.color.delete_red));
        paintLine.setColor(getResources().getColor(R.color.ok_green));
        paintLine.setStrokeWidth(5);
        textPaint.setColor(getResources().getColor(R.color.ok_green));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
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
                end=paintGraduatedLine(canvas,9,yDegree,end,text);
                canvas.drawText(String.valueOf(temMin),   bgWidth /2- bgWidth /24- bgWidth /6- bgWidth /20,yDegree,textPaint);

            }else if(temMin%5==0){

                canvas.drawLine(    bgWidth /2- bgWidth /12- bgWidth /12,yDegree, bgWidth /2- bgWidth /24,yDegree,paintLine);
            }else {
                canvas.drawLine(  bgWidth /2- bgWidth /12- bgWidth /24,yDegree,   bgWidth /2- bgWidth /24,yDegree,paintLine);
            }
            temMin++;
            yDegree-=gap;
        }

        /*paintLine.setColor(getResources().getColor(R.color.delete_red));
        while (temMin<=temMax){
            if(temMin%10==0){
                canvas.drawLine( bgWidth /2- bgWidth /24- bgWidth /6, yDegree,bgWidth /2- bgWidth /24,   yDegree,paintLine);
                canvas.drawText(String.valueOf(temMin),   bgWidth /2- bgWidth /24- bgWidth /6- bgWidth /20,yDegree,textPaint);
            }else if(temMin%5==0){
                canvas.drawLine(    bgWidth /2- bgWidth /12- bgWidth /12,yDegree, bgWidth /2- bgWidth /24,yDegree,paintLine);
            }else {
                canvas.drawLine(  bgWidth /2- bgWidth /12- bgWidth /24,yDegree,   bgWidth /2- bgWidth /24,yDegree,paintLine);
            }
            temMin++;
            yDegree-=gap;
        }*/


        /*
        mPaint.setColor(Color.rgb(0,0,0));
        paintCircle2.setColor(Color.GREEN);
        canvas.drawRect(leftPadding+40,topPadding,leftPadding+60,yDegree+topPadding,paintCircle2);
        paintLine.setColor((Color.rgb(0,0,255)));
        paintCircle.setColor(Color.rgb(255,0,0));
        canvas.drawCircle(leftPadding+50,topPadding+yDegree-(tem1*2),10,paintCircle);
        canvas.drawCircle(leftPadding+50,topPadding+yDegree+18+topPadding,20,paintCircle);
        canvas.drawRect(leftPadding+40,topPadding+yDegree-(tem1*20),leftPadding+60,yDegree+topPadding,paintCircle);
        while (yDegree>-1){
            canvas.drawLine(leftPadding+xDegree,yDegree+topPadding,leftPadding+xDegree+5,yDegree+topPadding,mPaint);
            if(yDegree%20==0){
                canvas.drawLine(leftPadding+xDegree,yDegree+topPadding,leftPadding+xDegree+10,yDegree+topPadding,paintLine);
                canvas.drawText(temMin+"",leftPadding+70,yDegree+topPadding,mPaint);
                temMax+=10;
            }
            yDegree-=4;
        }*/

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
