package edu.czb.ros_app.widgets.imu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.GraphView;
import com.softmoore.android.graphlib.Point;

import org.ros.internal.message.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import geometry_msgs.Vector3;
import sensor_msgs.Imu;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: ImuAngView
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/3/23 11:28
 * @Version: 1.0
 */
public class ImuAngView extends GraphView {

    Graph angGraph;

    List<Vector3> angVector=new LinkedList<>();
    List<Point> points=new ArrayList<>();

    List<Point> angPointsX=new LinkedList<>();

    List<Point> angPointsY=new LinkedList<>();

    List<Point> angPointsZ=new LinkedList<>();
    double[] ticksX;
    public ImuAngView(Context context) {
        super(context);
        init();
    }

    public ImuAngView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    protected void init(){
        super.init();
        points=new ArrayList<>();
        ticksX=new double[15];
        for(int i=0;i<60;i++){
            Point point=new Point(Math.random()*60,(Math.random()-0.5)*5);
            points.add(point);
        }
        for(int i=0;i<15;i++){
            ticksX[i]=i*4;
        }
        /*graph=new Graph.Builder()
                .addLineGraph(points)
                .setWorldCoordinates(0,60,-5,5)
                .build();
        this.setGraph(graph);*/
        angGraph=new Graph.Builder()
                .setXTicks(ticksX)
                .setWorldCoordinates(-2,60,-5,5)
                .build();
        this.setGraph(angGraph);
        this.setWillNotDraw(false);

    }
    public void onNewMessage(Message message) {
        //supr.onNewMessage(message);
        Imu imu=(Imu) message;
        Vector3 angularVelocity = imu.getAngularVelocity();
        Vector3 linearAcceleration = imu.getLinearAcceleration();

        if(angPointsX.size()>=60){
            angVector.remove(0);
            angVector.add(angularVelocity);
        }else{
            angVector.add(angularVelocity);
        }
        addPointToList(angPointsX,angPointsY,angPointsZ,angVector);
        angGraph=new Graph.Builder()
                .addLineGraph(angPointsX, Color.parseColor("#99ffaa11"))
                .addLineGraph(angPointsY,Color.parseColor("#99ff22ee"))
                .addLineGraph(angPointsZ,Color.parseColor("#8822ffee"))
                .setXTicks(ticksX)
                .setWorldCoordinates(-2,60,-5,5)
                .build();
        this.setGraph(angGraph);
        //this.setGraph(accGraph);

        this.invalidate();
    }
    @Override
    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void addPointToList(List<Point> listx,List<Point> listy,List<Point> listz,List<Vector3> data){
        listx.clear();
        listy.clear();
        listz.clear();
        for (int i = 0; i < data.size(); i++) {
            listx.add(new Point(i,data.get(i).getX()));
            listy.add(new Point(i,data.get(i).getY()));
            listz.add(new Point(i,data.get(i).getZ()));
        }
    }
}
