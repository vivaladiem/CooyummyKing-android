package com.coo.y2.cooyummykingr.fragment;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.coo.y2.cooyummykingr.R;

import java.io.IOException;

/**
 * Created by Y2 on 2015-05-23.
 */
public class JogDialogFragment extends DialogFragment {
    private TextView tvTimeDisplay;
    private View v;

    // Jog dial
    private Bitmap imageOriginal, imageScaled;
    private Matrix matrix;
    private ImageView dialer;
    private int dialerWidth, dialerHeight;
    private float rotationDegrees;
    private final int timeMax = 120 + 1;

    // Cooking time
    private int tickNumber = 0;
    private int preTickNumber;
    private int mCookingTime;
    private StringBuilder mTimeTxt = new StringBuilder();
    private Resources resources;

    // Variables for play tick sound
    private AssetFileDescriptor mDescriptor;
    private SoundPool mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private int soundID;

    public static JogDialogFragment newInstance(int time) {
        JogDialogFragment fragment = new JogDialogFragment();
        Bundle args = new Bundle();
        args.putInt("time", time);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Init SoundPool
        try {
            mDescriptor = getActivity().getAssets().openFd("Effect_Tick.ogg");
        } catch(IOException e) {
            e.printStackTrace();
        }

        soundID = mSoundPool.load(mDescriptor, 1);


        // Init View
        v = inflater.inflate(R.layout.tool_timer_dialog, container, false);
        tvTimeDisplay = (TextView) v.findViewById(R.id.tool_timer_display);
        resources = getResources();

        // Load and apply previously set time. if no past value, get 0
        int savedTick = getArguments().getInt("time");
        final float savedDegree = (float) savedTick * 360f / (float) timeMax;
        mCookingTime = savedTick; // 설정값 있을 때 아무것도 건들지 않고 닫았을 경우를 위함
        tickNumber = savedTick;
        tvTimeDisplay.setText(timeToText());


        // Init Jog Dial
        if (imageOriginal == null)
            imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.jog_point);
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset(); // not necessary
        }

        dialer = (ImageView) v.findViewById(R.id.tool_timer_jog);
        dialer.setSoundEffectsEnabled(true);
        dialer.setOnTouchListener(new DialOnTouchListener());
        dialer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (dialerHeight == 0 || dialerWidth == 0) {
                    dialerHeight = dialer.getHeight();
                    dialerWidth = dialer.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    resize.postScale(
                            (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getWidth(),
                            (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);

                    // translate to the image view's center
                    float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                    float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                    matrix.postTranslate(translateX, translateY);

                    dialer.setImageBitmap(imageScaled);
                    dialer.setImageMatrix(matrix);
//                    Log.e("Rotation degree :" + rotationDegrees, String.valueOf(tickNumber));

                    // Rotate dial to previously set time
                    rotateDialer(savedDegree);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO 크기 화면크기 상관 없게끔 정확하게 정하기.
        getDialog().getWindow().setLayout(640, 860);
    }


    // -------------------------------------------------------- //
    //                      Tick Listener                       //
    // -------------------------------------------------------- //
    public void onTick() {
        if (tickNumber % 5 != 0) return;

        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS); // vibrator는 세기가 너무 강해서 어색함. 햅틱이 적절
//        v.playSoundEffect(SoundEffectConstants.NAVIGATION_UP); // 기기설정에서 꺼져있으면 불가능
//        mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.5f); // 이상하게 안먹힘
        mSoundPool.play(soundID, 0.5f, 0.5f, 1, 0, 1f);

        tvTimeDisplay.setText(timeToText());
        mCookingTime = tickNumber;
    }

    private synchronized String timeToText() {
        int h = tickNumber / 60;
        int m = tickNumber % 60;

        if (h != 0) mTimeTxt.append(h).append(resources.getString(R.string.tool_info_time_hour)).append(" ");
        if (m != 0) mTimeTxt.append(m).append(resources.getString(R.string.tool_info_time_minute));

        String txt = mTimeTxt.toString();
        mTimeTxt.delete(0, mTimeTxt.length()); // 동시접근에서 문제가 생길 수 있을듯.. 아직 내용이 남아있는데 다시 호출시 내용의 뒤섞임. 실제 발생은 아직 안했지만

        return txt;
    }

    // -------------------------------------------------------- //
    //               Init Jog Dial Function                     //
    // -------------------------------------------------------- //

//    private void rotateDialer(float degrees) {
//        // if(!rotationDone) {
//
//        this.rotationDegrees += degrees;
//        this.rotationDegrees = this.rotationDegrees % 360;
//
//        tickNumber = (int)this.rotationDegrees * timeMax / 360;
//        // It could be negative
//        if (tickNumber < 0) tickNumber = timeMax + tickNumber;
//
//
//        //this.rotationDegrees  = Math.abs(rotationDegrees);
//        this.tickNumber = Math.abs(tickNumber);
//
//        Log.e("Rotation degree :" + rotationDegrees, String.valueOf(tickNumber));
//        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
//        dialer.setImageMatrix(matrix);
//
//
//        // }
//    }


    // 회전수 반영 테스트
    private void rotateDialer(float degrees) {
        // if(rotationDone) return;

        preTickNumber = tickNumber;

        this.rotationDegrees += degrees;
//        this.rotationDegrees = this.rotationDegrees % 360;
//        Log.i("CYMK", "rotation Degree: " + (int) this.rotationDegrees);

        // 0도의 밑으로는 갈 수 없게 합니다.
        if (this.rotationDegrees < 0) {
            this.rotationDegrees -= degrees;
            return;
        }


        tickNumber = (int)this.rotationDegrees * timeMax / 360;

        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        dialer.setImageMatrix(matrix);

        if (preTickNumber != tickNumber) onTick();
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    private double getAngle(double xTouch, double yTouch) {

        double delta_x = xTouch - (dialerWidth) /2;
        double delta_y = (dialerHeight) /2 - yTouch;
        double radians = Math.atan2(delta_y, delta_x);

        double degree = Math.toDegrees(radians);
        if (degree < 0) degree += 360.0d;
//        return Math.toDegrees(radians);
        return degree;
    }

    private class DialOnTouchListener implements View.OnTouchListener {
        private double startAngle;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    // reset the touched quadrants
                    /*for (int i = 0; i < quadrantTouched.length; i++) {
                        quadrantTouched[i] = false;
                    }*/

                    //allowRotating = false;

                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngle(event.getX(), event.getY());
                    //if(currentAngle < 130 || currentAngle < 110){
//                    Log.e("Start angle :"+startAngle, "Current angle:"+currentAngle);

                    // 360도에서 0도로 갈 경우에 대응하는 코드.
                    // 나의 멍청함을 나타내는 코드
                    // 회전수를 쌓아가는데 한바퀴 돌면 -360도를 해버려서 회전수가 쌓이지 않는 문제를 해결.
                    // 자꾸 이따구로 할거야?ㅠ
                    double delta_angle = startAngle - currentAngle;
                    if (delta_angle < -180) delta_angle += 360.0d;
                    if (delta_angle > 180) delta_angle -= 360.0d;
//                    rotateDialer((float) (startAngle - currentAngle));
                    rotateDialer((float) delta_angle);
                    //}

//                    Log.e("MOVE start Degree:"+startAngle, "Current Degree :"+currentAngle);
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:
                    //allowRotating = true;
                    break;
            }

            // set the touched quadrant to true
            //quadrantTouched[getQuadrant(event.getX() - (dialerWidth / 2), dialerHeight - event.getY() - (dialerHeight / 2))] = true;

            //detector.onTouchEvent(event);

            return true;
        }
    }

    // -------------------------------------------------------- //
    //                       Return Memory                      //
    // -------------------------------------------------------- //

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ToolFragment)getParentFragment()).onTimerDialogClose(mCookingTime);
        recursiveRecycle(v);

        mSoundPool.release();
        try {
            mDescriptor.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        mDescriptor = null;
        mSoundPool = null;
        mTimeTxt = null;
        v = null;
        imageOriginal.recycle();
        imageScaled.recycle();
        imageOriginal = null;
        imageScaled = null;
    }

    private void recursiveRecycle(View root) {
        if (root == null)
            return;
        if (root.getBackground() != null) {
            root.getBackground().setCallback(null);
            root.setBackground(null);
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursiveRecycle(group.getChildAt(i));
            }
            group.removeAllViews();
        }
        if (root instanceof ImageView) {
            Drawable drawable;
            ImageView iv = (ImageView) root;
            if ((drawable = iv.getDrawable()) == null) return;
            drawable.setCallback(null);
            iv.setImageDrawable(null);
            iv = null;
        }
        root = null;
    }
}
