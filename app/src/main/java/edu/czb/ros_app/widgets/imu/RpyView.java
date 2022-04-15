package edu.czb.ros_app.widgets.imu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.Point;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ros.internal.message.Message;
import org.ros.rosjava_geometry.Vector3;

import java.util.List;

import edu.czb.ros_app.R;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: RpyView
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/3 19:57
 * @Version: 1.0
 */
public class RpyView extends ViewGroup {
    private static final int FOCAL_DISTANCE = 1024;
    private static final Paint boatPaint=new Paint();
    private static final Paint textPaint=new Paint();
    private int bgHeight,bgWidth;
    private double pitch;
    private double roll;
    private double yaw;

    private double destPitch=0;
    private double destRoll=0;
    private double destYaw;
    private Vector3 dest;
    private static final Paint destPaint=new Paint();
    private static final Paint destTextPaint=new Paint();

    private RealMatrix matrix;
    private double sx;
    private double sy;
    private double sz;
    private Vector3[] boatVectors;
    private int[][] lines=new int[][]{{0,1},{0,3},{1,2},{2,3}/*,{0,2},{1,3},{0,7},{3,4},{0,5},{1,4}*//*,{8,9},{8,10},{8,11}*/};

    /*private RealMatrix matrixX;
    private RealMatrix matrixY;
    private RealMatrix matrixZ;*/

    public RpyView(Context context) {
        super(context);
    }

    public RpyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.setWillNotDraw(false);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void init(){
        boatPaint.setColor((getResources().getColor(R.color.ok_green)));
        textPaint.setColor((getResources().getColor(R.color.ok_green)));
        boatPaint.setStrokeWidth(5);
        textPaint.setStrokeWidth(100);
        textPaint.setTextSize(30);
        destPaint.setStrokeWidth(10);
        destTextPaint.setColor((getResources().getColor(R.color.delete_red)));
        destPaint.setColor((getResources().getColor(R.color.delete_red)));
        destTextPaint.setStrokeWidth(10);
        destTextPaint.setTextSize(30);
        matrix=MatrixUtils.createRealDiagonalMatrix(new double[]{1.0,1.0,1.0,1.0});
        pitch=0;
        roll=0;
        yaw=0;
        sx=1;
        sz=1;
        sy=1;
        boatVectors=new Vector3[7];
    }


    public void onNewMessage(Message message){
        geometry_msgs.Vector3 vector3=(geometry_msgs.Vector3)message;
        roll=vector3.getX();
        pitch=vector3.getY();
        yaw=vector3.getZ();
        this.invalidate();
    }
    public void onNewMessage(double destYaw){
        this.destYaw=destYaw;
        this.invalidate();
    }
    public void onNewMessage(double[] value){
        yaw=value[0];
        pitch=value[1];
        roll=value[2];
        this.invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        bgHeight=getHeight();
        bgWidth=getWidth();

        int x0=bgWidth/2;
        int y0=bgHeight/2;
        boatVectors=initBoatPoints(bgWidth,bgHeight);
        RealMatrix scaleMatrix = getScaleMatrix(1, 1, 1, matrix);
        RealMatrix rotateMatrix = getRotateMatrix(pitch, roll, yaw, scaleMatrix);
        Point2[] point2s=new Point2[boatVectors.length];
        for(int i=0;i<boatVectors.length;i++){
            boatVectors[i]=Vector3MultiplyMatrix(boatVectors[i],rotateMatrix);
            point2s[i]=perProject(boatVectors[i],x0,y0,bgWidth,bgHeight);
        }

        RealMatrix destScaleMatrix=getScaleMatrix(1,1,1,MatrixUtils.createRealDiagonalMatrix(new double[]{1.0,1.0,1.0,1.0}));
        RealMatrix destRotateMatrix=getRotateMatrix(destPitch,destRoll,destYaw,destScaleMatrix);
        dest=Vector3MultiplyMatrix(dest,destRotateMatrix);
        Point2 destPoint=perProject(dest,x0,y0,bgWidth,bgHeight);
        canvas.drawCircle((float) destPoint.x,(float)destPoint.y,10,destPaint);

        Vector3 originPoint=new Vector3(0,0,0);
        Point2 point2=perProject(originPoint,x0,y0,bgWidth,bgHeight);
        canvas.drawCircle((float) point2.x,(float) point2.y,10,boatPaint);

        canvas.drawText("destYaw:"+String.format("%.2f",destYaw),bgWidth-240,bgHeight/2,destTextPaint);

        for (int[] line : lines) {
            canvas.drawLine((float) point2s[line[0]].x,(float) point2s[line[0]].y,(float) point2s[line[1]].x,(float) point2s[line[1]].y,boatPaint);
            //drawLine(point2s[line[0]],point2s[line[1]],canvas);
        }
        for (int i = 0; i < point2s.length; i++) {
            canvas.drawText((char)(i+'A')+"",(float) point2s[i].x,(float)point2s[i].y,textPaint);
        }
        /*canvas.drawText("O",(float) point2s[8].x,(float) point2s[8].y,textPaint);
        canvas.drawText("X",(float) point2s[9].x,(float) point2s[9].y,textPaint);
        canvas.drawText("Y",(float) point2s[10].x,(float) point2s[10].y,textPaint);
        canvas.drawText("Z",(float) point2s[11].x,(float) point2s[11].y,textPaint);*/

        //textPaint.setTextSize(30);
        canvas.drawText("yaw:"+String.format("%.2f",yaw),20,bgHeight/2-30,textPaint);
        canvas.drawText("ptich:"+String.format("%.2f",pitch),20,bgHeight/2,textPaint);
        canvas.drawText("roll:"+String.format("%.2f",roll),20,bgHeight/2+30,textPaint);

    }

    private RealMatrix getRotateMatrix(double pitch,double roll,double yaw,RealMatrix matrix){
        double ax=(Math.PI*roll)/180.0;
        double ay=(Math.PI*pitch)/180.0;
        double az=(Math.PI*yaw)/180;
        double[][] matX=new double[4][4];
        matX[0][0]=1;matX[1][1]=Math.cos(ax);matX[1][2]=-Math.sin(ax);matX[2][1]=Math.sin(ax);matX[2][2]=Math.cos(ax);matX[3][3]=1;
        double[][] matY=new double[4][4];
        matY[0][0]=Math.cos(ay);matY[0][2]=Math.sin(ay);matY[1][1]=1;matY[2][0]=-Math.sin(ay);matY[2][2]=Math.cos(ay);matY[3][3]=1;
        double[][] matZ=new double[4][4];
        matZ[0][0]=Math.cos(az);matZ[0][1]=-Math.sin(az);matZ[1][0]=Math.sin(az);matZ[1][1]=Math.cos(az);matZ[2][2]=1;matZ[3][3]=1;
        RealMatrix matrixX=new BlockRealMatrix(matX);
        RealMatrix matrixY=new BlockRealMatrix(matY);
        RealMatrix matrixZ=new BlockRealMatrix(matZ);
        return matrix.multiply(matrixX).multiply(matrixY).multiply(matrixZ);
    }

    private RealMatrix getScaleMatrix(int sx,int sy,int sz,RealMatrix matrix){
        double[][] tMat=new double[4][4];
        tMat[0][0]=sx;
        tMat[1][1]=sy;
        tMat[2][2]=sz;
        tMat[3][3]=1;
        return matrix.multiply(new BlockRealMatrix(tMat));
    }

    private org.ros.rosjava_geometry.Vector3 Vector3MultiplyMatrix(Vector3 vector3, RealMatrix realMatrix){
        double x=vector3.getX();
        double y=vector3.getY();
        double z=vector3.getZ();
        double[][] data = realMatrix.getData();
        double resx=x*data[0][0]+y*data[1][0]+z*data[2][0]+data[3][0];
        double resy=x*data[0][1]+y*data[1][1]+z*data[2][1]+data[3][1];
        double resz=x*data[0][2]+y*data[1][2]+z*data[2][2]+data[3][2];
        return new Vector3(resx,resy,resz);
    }

    private Point2 perProject(Vector3 vector3, int xo, int yo,int width,int height){
        int min=Math.min(width, height);

        double x=vector3.getX();
        double y=vector3.getY();
        double z=vector3.getZ();
        //if(Math.abs(z-0)<0.000000001) z=1;
        double resx=((FOCAL_DISTANCE*x)/(z+FOCAL_DISTANCE))+xo;
        double resy=((FOCAL_DISTANCE*y)/(z+FOCAL_DISTANCE))+yo;
        /*resx+=min;
        resx%=min;
        resy+=min;
        resy%=min;*/
        return new Point2(resx,resy);
    }

    private void drawLine(Point2 p1,Point2 p2,Canvas canvas){
        canvas.drawLine((float)p1.getX(),(float)p1.y,(float) p2.x,(float)p2.y,boatPaint);
    }

    private Vector3[] initBoatPoints(int width,int height){
        int min=Math.min(width, height)/2;
        double scale=0.25;
        Vector3[] vector3=new Vector3[4];
        vector3[0]=new Vector3(min*scale,0,0);
        vector3[1]=new Vector3(-min*scale,min*scale,0);
        vector3[2]=new Vector3(-min*scale*0.4,0,0);
        vector3[3]=new Vector3(-min*scale,-min*scale,0);
        dest=new Vector3(min*scale *1.5,0,0);
        /*vector3[4]=new Vector3(min*scale*2,min*scale*2,-min*scale*2);
        vector3[5]=new Vector3(min*scale*2,-min*scale*2,-min*scale*2);
        vector3[6]=new Vector3(-min*scale*2,-min*scale*2,-min*scale*2);
        vector3[7]=new Vector3(-min*scale*2,min*scale*2,-min*scale*2);
        vector3[8]=new Vector3(0,0,0);
        vector3[9]=new Vector3(min*scale*4,0,0);
        vector3[10]=new Vector3(0,min*scale*4,0);
        vector3[11]=new Vector3(0,0,min*scale*4);*/
        return vector3;
    }

}
class Point2{
    double x;
    double y;
    Point2(double x ,double y){
        this.x=x;
        this.y=y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
