package com.coo.y2.cooyummyking.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.coo.y2.cooyummykingr.R;

/**
 * Created by Y2 on 2015-05-23.
 * Canvas 회전식이라서 메모리 비효율적
 */
public class MyDialView extends View implements GestureDetector.OnGestureListener {
    private static Bitmap bimmap;
    private static Paint paint;
    private static Rect bounds;
    private int totalNicks = 100;
    private int currentNick = 0;
    private GestureDetector gestureDetector;
    private float dragStartDeg = Float.NaN;
    float dialerWidth = 0,dialerHeight = 0;

    private static Paint createDefaultPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        return paint;
    }
    private float xyToDegrees(float x, float y) {
        float distanceFromCenter = PointF.length((x - 0.5f), (y - 0.5f));
        if (distanceFromCenter < 0.1f
                || distanceFromCenter > 0.5f) { // ignore center and out of bounds events
            return Float.NaN;
        } else {
            return (float) Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
        }
    }
    public final float getRotationInDegrees() {
        return (360.0f / totalNicks) * currentNick;
    }

    public final void rotate(int nicks) {
        currentNick = (currentNick + nicks);
        if (currentNick >= totalNicks) {
            currentNick %= totalNicks;
        } else if (currentNick < 0) {
            currentNick = (totalNicks + currentNick);
        }
        if((currentNick > 80 || currentNick < 20)){
            invalidate();
        }
    }
    public MyDialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bimmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.jog_point);
        paint = createDefaultPaint();
        gestureDetector = new GestureDetector(getContext(), this);
        dialerWidth = bimmap.getWidth() /2.0f;
        dialerHeight = bimmap.getHeight() / 2.0f;
        bounds = new Rect();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(bounds);
        canvas.save();
        //{
        canvas.translate(bounds.left, bounds.top);

        float rotation = getRotationInDegrees();
        canvas.rotate(rotation, dialerWidth, dialerHeight);
        canvas.drawBitmap(bimmap, 0,0,null);
        //canvas.rotate(- rotation, dialerWidth, dialerHeight);
        //}
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
    //Gesture detector methods
    @Override
    public boolean onDown(MotionEvent e) {
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());

        dragStartDeg = xyToDegrees(x, y);
        //Log.d("deg = " , ""+dragStartDeg);
        if (! Float.isNaN(dragStartDeg)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (! Float.isNaN(dragStartDeg)) {
            float currentDeg = xyToDegrees(e2.getX() / getWidth(),
                    e2.getY() / getHeight());

            if (! Float.isNaN(currentDeg)) {
                float degPerNick = 360.0f / totalNicks;
                float deltaDeg = dragStartDeg - currentDeg;

                final int nicks = (int) (Math.signum(deltaDeg)
                        * Math.floor(Math.abs(deltaDeg) / degPerNick));

                if (nicks != 0) {
                    dragStartDeg = currentDeg;
                    rotate(nicks);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

}
