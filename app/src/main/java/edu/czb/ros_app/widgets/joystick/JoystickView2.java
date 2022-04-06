package edu.czb.ros_app.widgets.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import sensor_msgs.Joy;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.joystick
 * @ClassName: JoystickView2
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/2 20:52
 * @Version: 1.0
 */
public class JoystickView2 extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private static final String TAG=JoystickView.class.getSimpleName();
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private float ratio=6.18f;
    private MutableLiveData<float[]> axesLiveData;
    private MutableLiveData<int[]> buttonLiveData;
    private MutableLiveData<JoystickData> joystickLiveData;
    private boolean preState=true;
    public JoystickView2(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        axesLiveData=new MutableLiveData<>();
        buttonLiveData=new MutableLiveData<>();
        joystickLiveData=new MutableLiveData<>();
        JoystickData joystickData=new JoystickData(new float[6],new int[6]);
        Topic topic=new Topic("joy", Joy._TYPE);
        joystickData.setTopic(topic);
        joystickLiveData.setValue(joystickData);
    }

    /* @Override
     protected void onLayout(boolean changed, int l, int t, int r, int b) {

     }*/
    public JoystickView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        axesLiveData=new MutableLiveData<>();
        buttonLiveData=new MutableLiveData<>();
        joystickLiveData=new MutableLiveData<>();
        JoystickData joystickData=new JoystickData(new float[6],new int[6]);
        Topic topic=new Topic("joy", Joy._TYPE);
        joystickData.setTopic(topic);
        joystickLiveData.setValue(joystickData);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        setupDimensions();
        drawJoystick(centerX,centerY);
        this.setWillNotDraw(false);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }
    /*@Override
    public void onDraw(Canvas canvas){
        //drawJoystick(centerX,centerY);
        setupDimensions();
        drawJoystick(centerX,centerY);
        //canvas.drawCircle(newX,newY,hatRadius,colors);
    }*/

    private void setupDimensions(){
        centerX=getWidth()/2.0f;
        centerY=getHeight()/2.0f;
        baseRadius=Math.min(getWidth(),getHeight())/3.0f;
        hatRadius=Math.min(getWidth(),getHeight())/5.0f;
    }

    private void drawJoystick(float newX,float  newY){
        if(getHolder().getSurface().isValid()){
            Canvas myCanvas=this.getHolder().lockCanvas();
            Paint colors=new Paint();
            //@color/colorAccent
            colors.setColor(getResources().getColor(R.color.white));

            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//Clear the BG
            myCanvas.drawRect(getLeft(),getTop(),getRight(),getBottom(),colors);
            float hypotenuse=(float)Math.sqrt(Math.pow(newX-centerX,2)+Math.pow(newY-centerY,2));
            float sin=(newY-centerY)/hypotenuse;
            float cos=(newX-centerX)/hypotenuse;

            colors.setColor(getResources().getColor(R.color.colorAccent));
            myCanvas.drawCircle(centerX,centerY,baseRadius,colors);

            /*colors.setARGB(155,255,255,0);*/
           /* myCanvas.drawCircle(newX-cos*hypotenuse*(ratio/baseRadius)*5,
                    newY-sin*hypotenuse*(ratio/baseRadius)*5,
                    300,colors);*/
            for(int i=1;i<=(int)(baseRadius/ratio);i++){
                //Log.i(TAG,i+"radius:"+i*(hatRadius*ratio/baseRadius));
                colors.setARGB(255/i,255,0,0);
                myCanvas.drawCircle(newX-cos*hypotenuse*(ratio/baseRadius)*i,
                        newY-sin*hypotenuse*(ratio/baseRadius)*i,
                        i*(hatRadius*ratio/baseRadius),colors);
            }
            for(int i=0;i<(int)(hatRadius/ratio);i++){
                colors.setARGB(255,(int)(i*(255*ratio/hatRadius)),(int)(i*(255*ratio/hatRadius)),255);
                myCanvas.drawCircle(newX,newY,hatRadius-(float) i*(ratio)/2,colors);
            }
            /*colors.setARGB(255,50,50,50);
            myCanvas.drawCircle(centerX,centerY,baseRadius,colors);
            colors.setARGB(255,0,0,255);
            myCanvas.drawCircle(newX,newY,hatRadius,colors);*/
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
    @Override
    public boolean onTouch(View v, MotionEvent e){
        //Log.i(TAG,"onTouch");
        if(v.equals(this)){
            float displacement=(float) Math.sqrt((Math.pow(e.getX()-centerX,2))+Math.pow(e.getY()-centerY,2));
            if(e.getAction()==MotionEvent.ACTION_UP){
                drawJoystick(centerX,centerY);
                if(!preState){
                    preState=true;
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    updateJoyStickLiveDate(axes,button);
                }
            }else{
                preState=false;
                if(displacement<baseRadius){
                    drawJoystick(e.getX(),e.getY());
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    axes[0]=(e.getX()-centerX)/baseRadius;
                    axes[1]=(e.getY()-centerY)/baseRadius;
                    updateJoyStickLiveDate(axes,button);
                }else{
                    float ratio=baseRadius/displacement;
                    float constrainedX=centerX+(e.getX()-centerX)*ratio;
                    float constrainedY=centerY+(e.getY()-centerY)*ratio;
                    drawJoystick(constrainedX,constrainedY);
                    float[] axes=new float[6];
                    int[] button=new int[6];
                    axes[0]=(constrainedX-centerX)/baseRadius;
                    axes[1]=(constrainedY-centerY)/baseRadius;
                    /*button[0]=1;
                    button[1]=2;
                    button[2]=2;*/
                    updateJoyStickLiveDate(axes,button);
                }
            }
        }
        return true;
    }

    public void updateJoyStickLiveDate(float[] axes,int[] button){
        Topic topic=new Topic("joy", Joy._TYPE);
        axesLiveData.postValue(axes);
        buttonLiveData.postValue(button);
        JoystickData joystickData=new JoystickData(axes,button);
        joystickData.setTopic(topic);
        joystickLiveData.postValue(joystickData);
    }
    public MutableLiveData<float[]> getAxesLiveData() {
        return axesLiveData;
    }

    public MutableLiveData<int[]> getButtonLiveData() {
        return buttonLiveData;
    }

    public MutableLiveData<JoystickData> getJoystickLiveData(){
        return joystickLiveData;
    }
}
