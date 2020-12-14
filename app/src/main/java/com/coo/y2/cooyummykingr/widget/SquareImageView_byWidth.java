package com.coo.y2.cooyummykingr.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Y2 on 2015-04-30.
 */
public class SquareImageView_byWidth extends androidx.appcompat.widget.AppCompatImageView{
    public SquareImageView_byWidth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareImageView_byWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView_byWidth(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w, w);
        //int w = MeasureSpec.getSize(1073741830); // 오.. 맞네. MeasureSpec이란게 위치값 + pixel 사이즈네. EXACTLY가 1073741824니까 size가 6이면 1073741830임...
    }
}
