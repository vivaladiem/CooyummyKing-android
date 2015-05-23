package com.coo.y2.cooyummyking.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Y2 on 2015-05-22.
 */
public class RoundDial extends RelativeLayout implements GestureDetector.OnGestureListener{
    public RoundDial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    private GestureDetector 	gestureDetector;
    private float 				mAngleDown , mAngleUp;
    private ImageView ivRotor;
    private Bitmap bmpRotorOn , bmpRotorOff;
    private boolean 			mState = false;
    private int					m_nWidth = 0, m_nHeight = 0;

    interface RoundKnobButtonListener {
        public void onStateChange(boolean newstate) ;
        public void onRotate(int percentage);
    }

    private RoundKnobButtonListener mListener;

    public void setListener(RoundKnobButtonListener l) {
        mListener = l;
    }

    public void setState(boolean state) {
        mState = state;
        ivRotor.setImageBitmap(state?bmpRotorOn:bmpRotorOff);
    }

    public RoundDial(Context context, int back, int rotoron, int rotoroff, final int w, final int h) {
        super(context);
        // we won't wait for our size to be calculated, we'll just store out fixed size
        m_nWidth = w;
        m_nHeight = h;
        // create stator
        ImageView ivBack = new ImageView(context);
        ivBack.setImageResource(back);
        RelativeLayout.LayoutParams lpIvBack = new RelativeLayout.LayoutParams(w, h);
        lpIvBack.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivBack, lpIvBack);
        // load rotor images
        Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotoron);
        Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(), rotoroff);
        float scaleWidth = ((float) w) / srcon.getWidth();
        float scaleHeight = ((float) h) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        bmpRotorOn = Bitmap.createBitmap(
                srcon, 0, 0,
                srcon.getWidth(),srcon.getHeight() , matrix , true);
        bmpRotorOff = Bitmap.createBitmap(
                srcoff, 0, 0,
                srcoff.getWidth(),srcoff.getHeight() , matrix , true);
        // create rotor
        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOn);
        RelativeLayout.LayoutParams lpIvKnob = new RelativeLayout.LayoutParams(w,h);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpIvKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivRotor, lpIvKnob);
        // set initial state
        setState(mState);
        // enable gesture detector
        gestureDetector = new GestureDetector(getContext(), this);
    }

    /**
     * math..
     * @param x
     * @param y
     * @return
     */
    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }


    @Override public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) return true;
        else return super.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent event) {
        float x = event.getX() / ((float) getWidth());
        float y = event.getY() / ((float) getHeight());
        mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());
        mAngleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        // if we click up the same place where we clicked down, it's just a button press
        if (! Float.isNaN(mAngleDown) && ! Float.isNaN(mAngleUp) && Math.abs(mAngleUp-mAngleDown) < 10) {
            setState(!mState);
            if (mListener != null) mListener.onStateChange(mState);
        }
        return true;
    }

    public void setRotorPosAngle(float deg) {

        if (deg >= 210 || deg <= 150) {
            if (deg > 180) deg = deg - 360;
            Matrix matrix=new Matrix();
            ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
            matrix.postRotate((float) deg, 210/2,210/2);//getWidth()/2, getHeight()/2);
            ivRotor.setImageMatrix(matrix);
        }
    }

    public void setRotorPercentage(int percentage) {
        int posDegree = percentage * 3 - 150;
        if (posDegree < 0) posDegree = 360 + posDegree;
        setRotorPosAngle(posDegree);
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());
        float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        if (! Float.isNaN(rotDegrees)) {
            // instead of getting 0-> 180, -180 0 , we go for 0 -> 360
            float posDegrees = rotDegrees;
            if (rotDegrees < 0) posDegrees = 360 + rotDegrees;

            // deny full rotation, start start and stop point, and get a linear scale
            if (posDegrees > 210 || posDegrees < 150) {
                // rotate our imageview
                setRotorPosAngle(posDegrees);
                // get a linear scale
                float scaleDegrees = rotDegrees + 150; // given the current parameters, we go from 0 to 300
                // get position percent
                int percent = (int) (Math.round(scaleDegrees / 3));
                if (mListener != null) mListener.onRotate(percent);
                return true; //consumed
            } else
                return false;
        } else
            return false; // not consumed
    }

    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) { return false; }

    public void onLongPress(MotionEvent e) {	}


}
