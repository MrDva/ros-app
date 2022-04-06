package edu.czb.ros_app.widgets.imu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.GraphView;
import com.softmoore.android.graphlib.Label;
import com.softmoore.android.graphlib.Point;

import org.ros.internal.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.czb.ros_app.R;
import geometry_msgs.Vector3;
import sensor_msgs.BatteryState;
import sensor_msgs.Imu;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: ImuView
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/2/4 18:32
 * @Version: 1.0
 */
public class ImuAccView extends GraphView {
    Graph accGraph;

    List<Vector3> accVector=new LinkedList<>();

    List<Point> points=new ArrayList<>();
    List<Point> accPointsX=new LinkedList<>();

    List<Point> accPointsY=new LinkedList<>();

    List<Point> accPointsZ=new LinkedList<>();

    double[] ticksX;
    public ImuAccView(Context context) {
        super(context);
        init();
    }

    public ImuAccView(Context context, AttributeSet attrs) {
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

        accGraph=new Graph.Builder()
                .setWorldCoordinates(-2,60,-5,5)
                .setXTicks(ticksX)
                .build();

        this.setGraph(accGraph);

        this.setWillNotDraw(false);

    }
    public void onNewMessage(Message message) {
        //supr.onNewMessage(message);
        Imu imu=(Imu) message;
        Vector3 angularVelocity = imu.getAngularVelocity();
        Vector3 linearAcceleration = imu.getLinearAcceleration();
        if(accVector.size()>=60){
            accVector.remove(0);
            accVector.add(linearAcceleration);
        }else{
            accVector.add(linearAcceleration);
        }

        addPointToList(accPointsX,accPointsY,accPointsZ,accVector);

        accGraph=new Graph.Builder()
                .addLineGraph(accPointsX, Color.parseColor("#99ffaa11"))
                .addLineGraph(accPointsY,Color.parseColor("#99ff22ee"))
                .addLineGraph(accPointsZ,Color.parseColor("#8822ffee"))
                .setXTicks(ticksX)
                .setWorldCoordinates(-2,60,-5,5)
                .build();
        this.setGraph(accGraph);
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
