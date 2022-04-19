package edu.czb.ros_app.widgets.imu;

import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.ros.internal.message.Message;

import edu.czb.ros_app.R;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.imu
 * @ClassName: MiCompass
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/17 8:23
 * @Version: 1.0
 */
public class MiCompass extends View {
    private float mWidth;
    private float mHeight;

    private Canvas mCanvas;
    private Context mContext;
    //View矩形的宽度
    private int width;
    //指南针圆心点坐标
    private int mCenterX;
    private int mCenterY;
    //外圆半径
    private int mOutSideRadius;
    //外接圆半径
    private int mCircumRadius;
    //指南针文字大小空间高度
    private int mTextHeight;
    //暗红色 外圈笔
    private Paint mDarkRedPaint;
    //深灰 外圈笔
    private Paint mDeepGrayPaint;
    //外三角笔
    private Paint mOutSideCircumPaint;
    //浅灰 外圈笔
    private Paint mLightGrayPaint;
    //指南针上面 文字笔
    private Paint mTextPaint;
    //外接圆，三角形笔
    private Paint mCircumPaint;

    private Paint destPaint;
    //指南针上面文字的外接矩形,用来测文字大小让文字居中
    private Rect mTextRect;
    //外圈小三角形的Path
    private Path mOutsideTriangle;
    //外接圆小三角形的Path
    private Path mCircumTriangle;

    private Path mTextDestTriangle;
    private Path mTextTriangle;

    //NESW 文字笔 和文字外接矩形
    private Paint mNorthPaint;
    private Paint mOthersPaint;
    private Rect  mPositionRect;
    //小刻度文字大小矩形和画笔
    private Paint mSamllDegreePaint;
    //两位数的
    private Rect mSencondRect;
    //三位数的
    private Rect mThirdRect;
    //圆心数字矩形
    private Rect mCenterTextRect;

    //中心文字笔
    private Paint mCenterPaint;

    //内心圆是一个颜色辐射渐变的圆
    private Shader mInnerShader;
    private Paint mInnerPaint;

    //定义个点击属性动画
    private ValueAnimator mValueAnimator;
    // camera绕X轴旋转的角度
    private float mCameraRotateX;
    // camera绕Y轴旋转的角度
    private float mCameraRotateY;
    //camera最大旋转角度
    private float mMaxCameraRotate = 10;

    // camera绕X轴旋转的角度
    private float mCameraTranslateX;
    // camera绕Y轴旋转的角度
    private float mCameraTranslateY;
    //camera最大旋转角度
    private float mMaxCameraTranslate;
    //camera矩阵
    private Matrix mCameraMatrix;
    //设置camera
    private Camera mCamera;

    private float val=0f;
    private float valCompare;
    private float destVal=0f;
    private float pitch;
    private float roll;
    //偏转角度红线笔
    private Paint mAnglePaint;

    //方位文字
    private String text="北";

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
        invalidate();
    }

    public void setDestVal(float val){
        this.destVal=val;
        invalidate();
    }

    public void onNewData(Message message){
        geometry_msgs.Vector3 vector3=(geometry_msgs.Vector3)message;
        roll=(float) vector3.getX();
        pitch=(float) vector3.getY();
        val=(float) vector3.getZ();
        this.invalidate();
    }

    /*public MiCompass(Context context) {
        this(context,null);
    }*/

    public MiCompass(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MiCompass(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mDarkRedPaint = new Paint();
        mDarkRedPaint.setStyle(Paint.Style.STROKE);
        mDarkRedPaint.setAntiAlias(true);
        mDarkRedPaint.setColor(context.getResources().getColor(R.color.darkRed));


        mDeepGrayPaint = new Paint();
        mDeepGrayPaint.setStyle(Paint.Style.STROKE);
        mDeepGrayPaint.setAntiAlias(true);
        mDeepGrayPaint.setColor(context.getResources().getColor(R.color.deepGray));


        mLightGrayPaint = new Paint();
        mLightGrayPaint.setStyle(Paint.Style.FILL);
        mLightGrayPaint.setAntiAlias(true);
        mLightGrayPaint.setColor(context.getResources().getColor(R.color.lightGray));

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(80);
        mTextPaint.setColor(context.getResources().getColor(R.color.white));

        mCircumPaint = new Paint();
        mCircumPaint.setStyle(Paint.Style.FILL);
        mCircumPaint.setAntiAlias(true);
        mCircumPaint.setColor(context.getResources().getColor(R.color.red));

        mOutSideCircumPaint = new Paint();
        mOutSideCircumPaint.setStyle(Paint.Style.FILL);
        mOutSideCircumPaint.setAntiAlias(true);
        mOutSideCircumPaint.setColor(context.getResources().getColor(R.color.lightGray));

        mTextRect = new Rect();
        mOutsideTriangle = new Path();
        mCircumTriangle = new Path();
        mTextTriangle=new Path();
        mTextDestTriangle=new Path();

        mNorthPaint = new Paint();
        mNorthPaint.setStyle(Paint.Style.STROKE);
        mNorthPaint.setAntiAlias(true);
        mNorthPaint.setTextSize(40);
        mNorthPaint.setColor(context.getResources().getColor(R.color.red));

        mOthersPaint = new Paint();
        mOthersPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOthersPaint.setAntiAlias(true);
        mOthersPaint.setTextSize(40);
        mOthersPaint.setColor(context.getResources().getColor(R.color.white));

        mPositionRect = new Rect();
        mCenterTextRect = new Rect();

        mCenterPaint = new Paint();
        mCenterPaint.setStyle(Paint.Style.STROKE);
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setTextSize(120);
        mCenterPaint.setColor(context.getResources().getColor(R.color.white));

        mSamllDegreePaint = new Paint();
        mSamllDegreePaint.setStyle(Paint.Style.STROKE);
        mSamllDegreePaint.setAntiAlias(true);
        mSamllDegreePaint.setTextSize(30);
        mSamllDegreePaint.setColor(context.getResources().getColor(R.color.lightGray));

        mSencondRect = new Rect();
        mThirdRect = new Rect();

        mInnerPaint = new Paint();
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setAntiAlias(true);

        mAnglePaint = new Paint();
        mAnglePaint.setStyle(Paint.Style.STROKE);
        mAnglePaint.setAntiAlias(true);
        mAnglePaint.setColor(context.getResources().getColor(R.color.red));

        destPaint=new Paint();
        destPaint.setColor(context.getResources().getColor(R.color.red));

        mCameraMatrix = new Matrix();
        mCamera = new Camera();

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        drawExtendText();
        //设置Camera矩阵 实现3D效果
        set3DMetrix();
        //画文字
       // drawText();
        //画指南针外圈
        drawCompassOutSide();
        //画指南针外接圆
        drawCompassCircum();
        //画内部渐变颜色圆
        drawInnerCricle();
        //画指南针内部刻度
        drawCompassDegreeScale();
        //画圆心数字
        drawCenterText();
    }



    /**
     * 设置camera相关
     */
    private void set3DMetrix() {
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);
        mCamera.rotateY(mCameraRotateY);
        mCamera.getMatrix(mCameraMatrix);
        mCamera.restore();
        //camera默认旋转是View左上角为旋转中心
        //所以动作之前要，设置矩阵位置 -mTextHeight-mOutSideRadius
        mCameraMatrix.preTranslate(-mWidth/2,-mHeight/2);
        //动作之后恢复位置
        mCameraMatrix.postTranslate(mWidth/2,mHeight/2);
        mCanvas.concat(mCameraMatrix);
    }

    private void drawExtendText(){
        int mTriangleHeight=40;
        //定义Path画小三角形
        mTextDestTriangle.moveTo(width*13/16,mTextHeight-mTriangleHeight);
        //小三角形的边长
        float mTriangleSide = 46.18f;
        //画出小三角形
        mTextDestTriangle.lineTo(width*13/16-mTriangleSide/2,mTextHeight);
        mTextDestTriangle.lineTo(width*13/16+mTriangleSide/2,mTextHeight);
        mTextDestTriangle.close();
        mCanvas.drawPath(mTextDestTriangle,mOutSideCircumPaint);
        mOutSideCircumPaint.setTextSize(mTriangleHeight);
        mCanvas.drawText("目标偏航角:",width*13/16+mTriangleSide/2,mTextHeight,mOutSideCircumPaint);
        mCanvas.drawText(String.format("%.2f",destVal),width*13/16+mTriangleSide/2,mTextHeight+mTriangleHeight,mOutSideCircumPaint);


        mTriangleHeight=(mOutSideRadius-mCircumRadius)/2;
        mTextTriangle.moveTo(width*13/16,mTriangleHeight+mTextHeight+mTriangleHeight);
        mTriangleSide = (float) ((mTriangleHeight/(Math.sqrt(3)))*2);
        mTextTriangle.lineTo(width*13/16-mTriangleSide/2,mTextHeight+mTriangleHeight*2+mTriangleHeight);
        mTextTriangle.lineTo(width*13/16+mTriangleSide/2,mTextHeight+mTriangleHeight*2+mTriangleHeight);
        mTextTriangle.close();
        mCircumPaint.setTextSize(mTriangleHeight);
        mCanvas.drawPath(mTextTriangle,mCircumPaint);
        mCanvas.drawText("偏航角:"+String.format("%.2f",val),width*13/16+mTriangleSide/2,mTextHeight+mTriangleHeight*2+mTriangleHeight,mCircumPaint);
        mCanvas.drawCircle(width*13/16,mTextHeight+mTriangleHeight*2+mTriangleHeight*2,mTriangleHeight/2,mCircumPaint);
        mCanvas.drawText("横滚角:"+String.format("%.2f",roll),width*13/16+mTriangleSide/2,mTextHeight+mTriangleHeight*2+mTriangleHeight*2,mCircumPaint);
        mCanvas.drawText("俯仰角:"+String.format("%.2f",pitch),width*13/16+mTriangleSide/2,mTextHeight+mTriangleHeight*3+mTriangleHeight*2,mCircumPaint);
    }

    private void drawInnerCricle() {

        mInnerShader = new RadialGradient(mCenterX,mOutSideRadius+mTextHeight,mCircumRadius-40, Color.parseColor("#323232"),
                Color.parseColor("#000000"), Shader.TileMode.CLAMP);
        mInnerPaint.setShader(mInnerShader);
        mCanvas.drawCircle(mCenterX,mOutSideRadius+mTextHeight,mCircumRadius-40,mInnerPaint);

    }

    private void drawCenterText() {
        /*String centerText=String.valueOf((int) val+"°");
        mCenterPaint.getTextBounds(centerText,0,centerText.length(),mCenterTextRect);
        int centerTextWidth = mCenterTextRect.width();
        int centerTextHeight = mCenterTextRect.height();
        mCanvas.drawText(centerText,mCenterX-centerTextWidth/2,mTextHeight+mOutSideRadius+centerTextHeight/5,mCenterPaint);
*/      mCanvas.rotate(-val,mCenterX,mOutSideRadius+mTextHeight);
        float radius=mCircumRadius-40;
        float centerX=(roll/180)*(mCircumRadius-40)+mCenterX;
        float centerY=(pitch/180)*(mCircumRadius-40)+mOutSideRadius+mTextHeight;
        mCanvas.drawLine(mCenterX,mOutSideRadius+mTextHeight-(mCircumRadius-40),mCenterX,mOutSideRadius+mTextHeight+(mCircumRadius-40),mCircumPaint);
        mCanvas.drawLine(mCenterX-(mCircumRadius-40),mOutSideRadius+mTextHeight,mCenterX+(mCircumRadius-40),mOutSideRadius+mTextHeight,mCircumPaint);
        mCanvas.drawCircle(centerX,centerY,radius/40,destPaint);



    }

    private void drawCompassDegreeScale() {
        mCanvas.save();
        //获取N文字的宽度
        mNorthPaint.getTextBounds("N",0,1,mPositionRect);
        int mPositionTextWidth = mPositionRect.width();
        int mPositionTextHeight = mPositionRect.height();
        //获取W文字宽度,因为W比较宽 所以要单独获取
        mNorthPaint.getTextBounds("W",0,1,mPositionRect);
        int mWPositionTextWidth = mPositionRect.width();
        int mWPositionTextHeight = mPositionRect.height();
        //获取小刻度，两位数的宽度
        mSamllDegreePaint.getTextBounds("30",0,1,mSencondRect);
        int mSencondTextWidth = mSencondRect.width();
        int mSencondTextHeight = mSencondRect.height();
        //获取小刻度，3位数的宽度
        mSamllDegreePaint.getTextBounds("30",0,1,mThirdRect);
        int mThirdTextWidth = mThirdRect.width();
        int mThirdTextHeight = mThirdRect.height();

        mCanvas.rotate(-0,mCenterX,mOutSideRadius+mTextHeight);


        //画刻度线
        for (int i = 0; i < 240; i++) {

            if (i==0||i==60||i==120||i==180){
                mCanvas.drawLine(mWidth / 2, mTextHeight+mOutSideRadius-mCircumRadius+10,
                        mWidth / 2,  mTextHeight+mOutSideRadius-mCircumRadius+30, mDeepGrayPaint);
            }else{
                mCanvas.drawLine(mWidth / 2, mTextHeight+mOutSideRadius-mCircumRadius+10,
                        mWidth/ 2,  mTextHeight+mOutSideRadius-mCircumRadius+30, mLightGrayPaint);
            }
            if (i==0){
                mCanvas.drawText("N", mCenterX-mPositionTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mPositionTextHeight,mOthersPaint);
            }else if (i==60){
                mCanvas.drawText("E", mCenterX-mPositionTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mPositionTextHeight,mOthersPaint);
            }else if (i==120){
                mCanvas.drawText("S", mCenterX-mPositionTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mPositionTextHeight,mOthersPaint);
            }else if (i==180){
                mCanvas.drawText("W", mCenterX-mWPositionTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mWPositionTextHeight,mOthersPaint);
            }/*else if (i==20){
                mCanvas.drawText("30", this.width /2-mSencondTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mSencondTextHeight,mSamllDegreePaint);
            }else if (i==40){
                mCanvas.drawText("60", this.width /2-mSencondTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mSencondTextHeight,mSamllDegreePaint);
            }else if (i==80){
                mCanvas.drawText("120", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }else if (i==100){
                mCanvas.drawText("150", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }else if (i==140){
                mCanvas.drawText("210", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }else if (i==160){
                mCanvas.drawText("240", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }else if (i==200){
                mCanvas.drawText("300", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }else if (i==220){
                mCanvas.drawText("330", this.width /2-mThirdTextWidth/2,mTextHeight+mOutSideRadius-mCircumRadius+40+mThirdTextHeight,mSamllDegreePaint);
            }*/
            mCanvas.rotate(1.5f, mCenterX, mOutSideRadius+mTextHeight);
        }
        mCanvas.restore();

    }

    /**
     * 指南针外接圆，和外部圆换道理差不多
     */
    private void drawCompassCircum() {
        mCanvas.save();
        //外接圆小三角形的高度
        int mTriangleHeight=(mOutSideRadius-mCircumRadius)/2;

        mCanvas.rotate(-val,mCenterX,mOutSideRadius+mTextHeight);
        mCircumTriangle.moveTo(mCenterX,mTriangleHeight+mTextHeight);
        //内接三角形的边长,简单数学运算
        float mTriangleSide = (float) ((mTriangleHeight/(Math.sqrt(3)))*2);
        mCircumTriangle.lineTo(mCenterX-mTriangleSide/2,mTextHeight+mTriangleHeight*2);
        mCircumTriangle.lineTo(mCenterX+mTriangleSide/2,mTextHeight+mTriangleHeight*2);
        mCircumTriangle.close();
        mCanvas.drawPath(mCircumTriangle,mCircumPaint);
        mCanvas.drawArc(mCenterX-mCircumRadius,mTextHeight+mOutSideRadius-mCircumRadius,
                mCenterX+mCircumRadius,mTextHeight+mOutSideRadius+mCircumRadius,-85,350,false,mDeepGrayPaint);
        mAnglePaint.setStrokeWidth(5f);
        if (val<=180){
            valCompare = val;
            mCanvas.drawArc(mCenterX-mCircumRadius,mTextHeight+mOutSideRadius-mCircumRadius,
                    mCenterX+mCircumRadius,mTextHeight+mOutSideRadius+mCircumRadius,-85,valCompare,false,mAnglePaint);
        }else{
            valCompare = 360-val;
            mCanvas.drawArc(mCenterX-mCircumRadius,mTextHeight+mOutSideRadius-mCircumRadius,
                    mCenterX+mCircumRadius,mTextHeight+mOutSideRadius+mCircumRadius,-95,-valCompare,false,mAnglePaint);
        }

        mCanvas.restore();
    }

    /**
     * 指南针外部可简单分为两部分
     * 1、用Path实现小三角形
     * 2、两个圆弧
     */
    private void drawCompassOutSide() {
        mCanvas.save();
        mCanvas.rotate(-destVal,mCenterX,mOutSideRadius+mTextHeight);
        //小三角形的高度
        int mTriangleHeight=40;
        //定义Path画小三角形
        mOutsideTriangle.moveTo(mCenterX,mTextHeight-mTriangleHeight);
        //小三角形的边长
        float mTriangleSide = 46.18f;
        //画出小三角形
        mOutsideTriangle.lineTo(mCenterX-mTriangleSide/2,mTextHeight);
        mOutsideTriangle.lineTo(mCenterX+mTriangleSide/2,mTextHeight);
        mOutsideTriangle.close();
        mCanvas.drawPath(mOutsideTriangle,mOutSideCircumPaint);

        //画圆弧
        mDarkRedPaint.setStrokeWidth((float) 5);
        mLightGrayPaint.setStrokeWidth((float)5);
        mDeepGrayPaint.setStrokeWidth((float)3);
        mLightGrayPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,-80,270,false,mLightGrayPaint);
        mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,40,20,false,mDeepGrayPaint);
        mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,-100,-20,false,mLightGrayPaint);
        mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,-120,-120,false,mDarkRedPaint);
        /*if (destVal<=180){
            valCompare = destVal;
            mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,-80,valCompare,false,mDarkRedPaint);
        }else{
            valCompare = 360-destVal;
            mCanvas.drawArc(mCenterX-mOutSideRadius,mTextHeight,mCenterX+mOutSideRadius,mTextHeight+mOutSideRadius*2,-80,-valCompare,false,mDarkRedPaint);
        }*/
        mCanvas.restore();
    }

    private void drawText() {
        if (val<=15||val>=345){
            text = "北";
        }else if (val>15&&val<=75){
            text= "东北";
        }else if (val>75&&val<=105){
            text= "东";
        }else if (val>105&&val<=165){
            text="东南";
        }else if (val>165&&val<=195){
            text = "南";
        }else if (val>195&&val<=255){
            text = "西南";
        }else if (val>255&&val<=285){
            text = "西";
        }else if (val>285&&val<345){
            text="西北";
        }

        mTextPaint.getTextBounds(text,0,text.length(),mTextRect);
        //文字宽度
        int mTextWidth = mTextRect.width();
        //让文字水平居中显示
        mCanvas.drawText(text,mCenterX-mTextWidth/2,mTextHeight/2,mTextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //为指南针上面的文字预留空间，定为1/3边张
        mTextHeight = width/5;
        //设置圆心点坐标
        mCenterX = width/2-(width/2-mOutSideRadius);
        mCenterY = width/2;
        //外部圆的外径
        mOutSideRadius = width*3/8;
        //外接圆的半径
        mCircumRadius = mOutSideRadius*4/5;
        //camera最大平移距离
        mMaxCameraTranslate = 0.02f*mOutSideRadius;
        mHeight=width+width/3;
        mWidth=mCenterX*2;
        setMeasuredDimension(width+width/2, width);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mValueAnimator!=null&&mValueAnimator.isRunning()){
                    mValueAnimator.cancel();
                }
                //3D 效果让Camera旋转,获取旋转偏移大小
                getCameraRotate(event);
                //获取平移大小
                getCameraTranslate(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //3D 效果让Camera旋转,获取旋转偏移大小
                getCameraRotate(event);
                //获取平移大小
                getCameraTranslate(event);
                break;
            case MotionEvent.ACTION_UP:
                //松开手 复原动画
                startRestore();
                break;
        }
        return true;

    }

    private void startRestore() {
        final String cameraRotateXName = "cameraRotateX";
        final String cameraRotateYName = "cameraRotateY";
        final String canvasTranslateXName = "canvasTranslateX";
        final String canvasTranslateYName = "canvasTranslateY";
        PropertyValuesHolder cameraRotateXHolder =
                PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0);
        PropertyValuesHolder cameraRotateYHolder =
                PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0);
        PropertyValuesHolder canvasTranslateXHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateXName, mCameraTranslateX, 0);
        PropertyValuesHolder canvasTranslateYHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateYName, mCameraTranslateY, 0);
        mValueAnimator = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder,
                cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder);
        mValueAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float f = 0.571429f;
                return (float) (Math.pow(2, -2 * input) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1);
            }
        });
        mValueAnimator.setDuration(1000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCameraRotateX = (float) animation.getAnimatedValue(cameraRotateXName);
                mCameraRotateY = (float) animation.getAnimatedValue(cameraRotateYName);
                mCameraTranslateX = (float) animation.getAnimatedValue(canvasTranslateXName);
                mCameraTranslateX = (float) animation.getAnimatedValue(canvasTranslateYName);
            }
        });
        mValueAnimator.start();
    }

    /**
     * 获取Camera，平移大小
     * @param event
     */
    private void getCameraTranslate(MotionEvent event) {
        float translateX = (event.getX() - mWidth / 2);
        float translateY = (event.getY() - mHeight/2);
        //求出此时位移的大小与半径之比
        float[] percentArr = getPercent(translateX, translateY);
        //最终位移的大小按比例匀称改变
        mCameraTranslateX = percentArr[0] * mMaxCameraTranslate;
        mCameraTranslateY = percentArr[1] * mMaxCameraTranslate;
    }

    /**
     * 让Camera旋转,获取旋转偏移大小
     * @param event
     */
    private void getCameraRotate(MotionEvent event) {
        float mRotateX = -(event.getY()-(mHeight)/2);
        float mRotateY = (event.getX()-mWidth/2);
        //求出旋转大小与半径之比
        float[] percentArr = getPercent(mRotateX,mRotateY);
        mCameraRotateX = percentArr[0]*mMaxCameraRotate;
        mCameraRotateY = percentArr[1]*mMaxCameraRotate;
    }

    /**
     * 获取比例
     * @param mCameraRotateX
     * @param mCameraRotateY
     * @return
     */
    private float[] getPercent(float mCameraRotateX, float mCameraRotateY) {
        float[] percentArr = new float[2];
        float percentX = mCameraRotateX/width;
        float percentY = mCameraRotateY/width;
        //处理一下比例值
        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }
        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }
        percentArr[0] = percentX;
        percentArr[1] = percentY;
        return percentArr;
    }

    public MiCompass(Context context) {
        super(context);
    }
}
