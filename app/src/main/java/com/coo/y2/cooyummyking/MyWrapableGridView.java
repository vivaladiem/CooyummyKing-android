package com.coo.y2.cooyummyking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.askerov.dynamicgrid.DynamicGridView;

/**
 * wrap_content를 가능하게 해주기 위한 확장.
 */
public class MyWrapableGridView extends DynamicGridView {
    public MyWrapableGridView(Context context) {
        super(context);
    }

    public MyWrapableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWrapableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
