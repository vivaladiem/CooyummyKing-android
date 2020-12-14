package com.coo.y2.cooyummyking;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Y2 on 2015-05-06.
 * Wrap_content가 가능한 DynamicGridView
 * https://github.com/askerov/DynamicGrid
 *
 */
// GridLayoutManager that can be layout_height = wrap_content.
public class MyWraperableGridLayoutManager extends GridLayoutManager {
    private int[] mMeasuredDimension = new int[2];


    public MyWraperableGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }



    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;

        // 각 row의 아이템의 높이가 일정할 때 성립. 아님 3개 값 따로 모아뒀다가 Math.max를 해야..
        int itemCount = getItemCount();
        int spanCount = getSpanCount();
        for (int i = 0; i < itemCount; i++) {
            // Only for Vertical Orientation.
            measureScrapChild1(recycler, i
                    , View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.AT_MOST)
                    , View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.AT_MOST)
                    , mMeasuredDimension);
            if ((i + 1) % spanCount == 1) {
                height = height + mMeasuredDimension[1];
            }
            if (i < spanCount) {
                width = width + mMeasuredDimension[0];
            }
        }

        height += getPaddingTop() + getPaddingBottom();

        switch(widthMode) {
            case View.MeasureSpec.AT_MOST:
                Log.i("CYMK", "Width AT_MOST");
                if (width < widthSize) break;
            case View.MeasureSpec.EXACTLY:
                Log.i("CYMK", "Width EXACTLY");
                width = widthSize;
            case View.MeasureSpec.UNSPECIFIED:
                Log.i("CYMK", "Width UNSPECIFIED");
        }

        switch(heightMode) {
            case View.MeasureSpec.AT_MOST:
                Log.i("CYMK", "Height AT_MOST");
                if (height < heightSize) break;
            case View.MeasureSpec.EXACTLY:
                Log.i("CYMK", "Height EXACTLY");
                height = heightSize;
            case View.MeasureSpec.UNSPECIFIED:
                Log.i("CYMK", "Height UNSPECIFIED");
        }

        setMeasuredDimension(width, height);

    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        // 아이템 크기를 정확히 주면 제대로 작동하지 않을까..
        // LayoutParams.height, widht에 screen에서 나눈 값 주.. 려면 그냥 쉽게 ToolFragment에서 하고 말지..
        View view = recycler.getViewForPosition(position);

        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();

        // For adding Item Decor Insets to view
        super.measureChildWithMargins(view, 0, 0);

        int viewWidth1 = view.getMeasuredWidth();
        int viewHeight1 = view.getMeasuredHeight();

        if (view != null) {
//            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            LayoutParams p = (LayoutParams) view.getLayoutParams();

            //
            int paddingLeft = getPaddingLeft();
            int addingRight = getPaddingRight();
            int decoLeft = getDecoratedLeft(view);
            int decoRight = getDecoratedRight(view);
            //

            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec
                    , getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view)
                    , p.width);

            //for debug
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int decoTop = getDecoratedTop(view);
            int decoBottom = getDecoratedBottom(view);
            //

            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec
                    , getPaddingTop() + getPaddingBottom() + getDecoratedTop(view) + getDecoratedBottom(view)
                    , p.height);

            view.measure(childWidthSpec, childHeightSpec);

            //
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();
            int decoWidth = getDecoratedMeasuredWidth(view);
            int decoHeight = getDecoratedMeasuredHeight(view);
            //

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.topMargin + p.bottomMargin;
            recycler.recycleView(view);
        }
    }

    private void measureScrapChild1(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        View view = recycler.getViewForPosition(position);

        if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec
                    , getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view)
                    , p.width);

            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec
                    , getPaddingTop() + getPaddingBottom() + getDecoratedTop(view) + getDecoratedBottom(view)
                    , p.height);

            view.measure(childWidthSpec, childHeightSpec);

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.topMargin + p.bottomMargin;
            recycler.recycleView(view);
        }
    }

}
