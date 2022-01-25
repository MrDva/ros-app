package edu.czb.ros_app.ui.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.entities.widgets.BaseEntity;
import edu.czb.ros_app.utils.ViewUtil;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.1.2
 * @created on 18.10.19
 * @updated on 22.04.20
 * @modified by Nils Rottmann
 * @updated on 25.09.20
 * @modified by Nils Rottmann
 */
public class WidgetViewGroup extends ViewGroup {

    public static final String TAG = WidgetViewGroup.class.getSimpleName();
    public static final int TILES_X = 8;

    Paint crossPaint;
    Paint scaleShadowPaint;
    int tilesX;
    int tilesY;
    float tileWidth;
    List<BaseEntity> widgetList;
    boolean vizEditMode = false;
    boolean drawWidgetScaleShadow = false;


    public WidgetViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.widgetList = new ArrayList<>();

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                        R.styleable.WidgetViewGroup, 0, 0);

        int crossColor = a.getColor(R.styleable.WidgetViewGroup_crossColor,
                getResources().getColor(R.color.delete_red));

        a.recycle();

        float stroke = ViewUtil.dpToPx(getContext(), 1);

        crossPaint = new Paint();
        crossPaint.setColor(crossColor);
        crossPaint.setStrokeWidth(stroke);

        scaleShadowPaint = new Paint();
        scaleShadowPaint.setColor(getResources().getColor(R.color.colorPrimary));
        scaleShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleShadowPaint.setAlpha(100);

        this.setWillNotDraw(false);

    }


    private void calculateTiles() {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float height = getHeight() - getPaddingBottom() - getPaddingTop();

        if (width < height) { // Portrait
            tilesX = TILES_X;
            tileWidth = width / tilesX;
            tilesY = (int) (height / tileWidth);
        } else { // Landscape
            tilesY = TILES_X;
            tileWidth = height / tilesY;
            tilesX = (int) ( width / tileWidth);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            // this works because you set the dimensions of the ImageView to FILL_PARENT
            v.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Position all children within this layout.
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        calculateTiles();

        for (int i = 0; i < getChildCount(); i++) {
           /* positionChild(i);*/
        }
    }

    private void positionChild(int i) {
        final View child = getChildAt(i);

        // Check if view is visible
        if(child.getVisibility() == GONE)
            return;

        /*Position position = ((WidgetView) child).getPosition();

        // Y pos from bottom up
        int w = (int) (position.width * tileWidth);
        int h = (int) (position.height * tileWidth);
        int x = (int) (getPaddingLeft() + position.x * tileWidth);
        int y = (int) (getPaddingTop() + (tilesY - (position.height + position.y)) * tileWidth);

        // Place the child.
        child.layout(x, y, x + w, y + h);*/
    }

    @Override
    public void onDraw(Canvas canvas) {
        float startX = getPaddingLeft();
        float endX = getWidth() - this.getPaddingRight();
        float startY = getPaddingTop();
        float endY = getHeight() - this.getPaddingBottom();

        // Draw x's
        float lineLen = ViewUtil.dpToPx(getContext(), 5)/2;

        for(float drawY = startY; drawY <= endY; drawY += tileWidth){
            for(float drawX = startX; drawX <= endX; drawX += tileWidth){
                canvas.drawLine(drawX-lineLen, drawY, drawX+lineLen, drawY, crossPaint);
                canvas.drawLine(drawX, drawY-lineLen, drawX, drawY+lineLen, crossPaint);
            }
        }

        /*if (drawWidgetScaleShadow && widgetScaleShadowPosition != null) {
            int w = (int) (widgetScaleShadowPosition.width * tileWidth);
            int h = (int) (widgetScaleShadowPosition.height * tileWidth);
            int x = (int) (getPaddingLeft() + widgetScaleShadowPosition.x * tileWidth);
            int y = (int) (getPaddingTop() + (tilesY - (widgetScaleShadowPosition.height + widgetScaleShadowPosition.y)) * tileWidth);
            canvas.drawRect(x, y, x + w, y + h, scaleShadowPaint);
        }*/
    }



}