package edu.czb.ros_app.widgets.battey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.ros.internal.message.Message;

import edu.czb.ros_app.R;
import sensor_msgs.BatteryState;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.widgets.battey
 * @ClassName: BatteryView
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/19 14:53
 * @Version: 1.0
 */
public class BatteryView extends ViewGroup {

    public static final String TAG = BatteryView.class.getSimpleName();
    public static final int MAX_LEVEL = 5;

    Paint outerPaint;
    Paint innerPaint;
    Paint textPaint;
    Paint bgPaint;
    private Paint highlightPaint;
    int level;
    boolean charging;
    float textSize;
    float borderWidth;
    String displayedText;
    private boolean displayVoltage=true;

    public BatteryView(Context context) {
        super(context);
        init();
    }
    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        float textDip = 18f;
        float borderDip = 10f;
        displayVoltage=true;
        displayedText=String.format("%.1fV", 0.3f);
        highlightPaint = new Paint();
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textDip,
                getResources().getDisplayMetrics());
        borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderDip,
                getResources().getDisplayMetrics());

        borderWidth = 10;
        level = 3;

        // Init paints
        innerPaint = new Paint();
        innerPaint.setColor(getResources().getColor(R.color.battery5));
        innerPaint.setStrokeWidth(borderWidth);
        innerPaint.setStrokeCap(Paint.Cap.ROUND);

        outerPaint = new Paint();
        outerPaint.setColor(getResources().getColor(R.color.whiteHigh));
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth(borderWidth);
        outerPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.whiteHigh));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);

        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.white));
        this.setWillNotDraw(false);

        updateColor();

    }
    public void onNewMessage(Message message) {
        //supr.onNewMessage(message);

        BatteryState state = (BatteryState)message;

        this.charging = state.getPowerSupplyStatus() == BatteryState.POWER_SUPPLY_STATUS_CHARGING;

        if (displayVoltage) {
            this.updateVoltage(state.getVoltage());
        }
        this.updatePercentage(state.getPercentage());
        this.invalidate();
    }
    private void updatePercentage(float value) {
        int perc = (int)(value * 100);
        displayedText = perc + "%";
        level = Math.min(5, perc / 20 + 1);
        Log.i(TAG, "percentage:"+perc+" Level"+level);
        updateColor();
    }

    private void updateVoltage(float value) {
        if (value >= 10) {
            displayedText = String.format("%.1fV", value);
        } else {
            displayedText = String.format("%.2fV", value);
        }

        level = -1;
        updateColor();
    }
    private void updateColor() {
        int color;

        if (level == 1)         color = R.color.battery1;
        else if (level == 2)    color = R.color.battery2;
        else if (level == 3)    color = R.color.battery3;
        else if (level == 4)    color = R.color.battery4;
        else if (level == 5)    color = R.color.battery5;
        else                    color = R.color.colorPrimary;

        innerPaint.setColor(getResources().getColor(color));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
       /* final int width=getMeasuredWidth();
        final int height=getMeasuredHeight();
        final int childLeft=getPaddingLeft();
        final int childTop=getPaddingTop();
        View child=this.getChildAt(0);
        child.layout(childLeft,childTop,width-getPaddingRight(),height-getPaddingBottom());*/
    }
    @Override
    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), highlightPaint);
        float width = getWidth();
        float height = getHeight();
        float middleX = width/2;

        float left = borderWidth/2;
        float right = width - borderWidth/2;
        float top = borderWidth * 2;
        float bottom = height - borderWidth - textSize;

        canvas.drawRect(0,0,getWidth(),getHeight(),bgPaint);

        // Draw pad
        canvas.drawRoundRect(middleX - width/4, top - borderWidth,
                middleX + width/4, top,
                borderWidth, borderWidth,
                outerPaint);

        // Draw body
        canvas.drawRoundRect(left, top, right, bottom, borderWidth, borderWidth, outerPaint);

        if (this.charging) {
            // Draw lightning
            float batWidth = right - left;
            float batHeight = bottom - top;
            middleX = (right + left) /2;
            float middleY = (bottom + top) /2;
            float partWidth = batWidth / 5;
            float partHeight = batHeight / 6;


            @SuppressLint("DrawAllocation") float[][] path = new float[][]{
                    {middleX, middleY - partHeight},
                    {middleX - partWidth, middleY + partHeight/5},
                    {middleX + partWidth, middleY - partHeight/5},
                    {middleX, middleY + partHeight}
            };

            for(int i = 0; i < path.length-1; i++) {
                canvas.drawLine(path[i][0], path[i][1], path[i+1][0], path[i+1][1], innerPaint);
            }

        } else {
            // Draw Bat level
            int batLevel = level == -1? 5 : level;
            float innerLeft = left + borderWidth * 1.5f;
            float innerRight = right - borderWidth * 1.5f;
            float innerTop = top + borderWidth * 1.5f;
            float innerBottom = bottom - borderWidth * 1.5f;
            float heightStep = (innerBottom - innerTop + borderWidth) / MAX_LEVEL;

            for (int i = 0; i < batLevel; i++) {
                float b = innerBottom - heightStep * i;
                float t = b - heightStep + borderWidth;

                canvas.drawRect(innerLeft, t, innerRight, b, innerPaint);
            }
        }

        // Draw status text
        canvas.drawText(displayedText, middleX, height, textPaint);
    }

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        *//**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         *//*
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


        // 计算出所有的childView的宽和高，调用后，它所有的childView的宽和高的值就被确定，也即getMeasuredWidth（）有值了。
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        *//**
         * 记录如果是wrap_content是设置的宽和高
         *//*
        int width = 0;
        int height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        // 用于计算左边两个childView的高度
        int lHeight = 0;
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        int rHeight = 0;

        // 用于计算上边两个childView的宽度
        int tWidth = 0;
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        int bWidth = 0;

        *//**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         *//*
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            // 上面两个childView
            if (i == 0 || i == 1)
            {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3)
            {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2)
            {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3)
            {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);

        *//**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         *//*
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }*/
}
