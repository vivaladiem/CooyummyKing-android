package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.adapter.MyRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-04.
 */
public class ToolFragment extends Fragment {
    private static final int SPAN_COUNT = 3;
    private ArrayList<String> instructions = new ArrayList<>();
    private ArrayList<Bitmap> images = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_tool, container, false);
        /*
        initRecyclerView(v);
        mIvs.add((View) v.findViewById(R.id.tool_background_1));
        mIvs.add((View) v.findViewById(R.id.tool_background_2));
        mIvs.add((View) v.findViewById(R.id.tool_background_link_1));
        mIvs.add((View) v.findViewById(R.id.tool_background_link_2));
        mIvs.add((View) v.findViewById(R.id.tool_making_sector));
        */
        initDummyData();
        initResources(v);
        return v;
    }

    private void initResources(View v) {

        // 뷰들의 크기를 정의하기 위한 상수들을 가져옵니다.
        // All units are pixels
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int screenHeightPixels = dm.heightPixels;
        final int screenWidthPixels = dm.widthPixels;
        final int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height);
        final int bottomTabHeight = getResources().getDimensionPixelSize(R.dimen.bottomtab_height);
        final int linkHeight = getResources().getDimensionPixelSize(R.dimen.link_height);
        final int padding = getResources().getDimensionPixelSize(R.dimen.tool_padding);

        final int layoutHeight = screenHeightPixels - actionBarHeight - bottomTabHeight - padding; // Height For Upper Page
        final int scrollDistance = layoutHeight - linkHeight * 2;

        // -------------- RecyclerView ------------ //
        // TODO 정확성 높일 필요.. dimen에 수치 넣어 쓰던가 LayoutManager를 extends해서 만들던가. 전자가 쉬움
        RecyclerView mRecycleView = (RecyclerView) v.findViewById(R.id.tool_making_sector);

        ViewGroup.LayoutParams params = mRecycleView.getLayoutParams();

        final int itemWidthApx = screenWidthPixels / SPAN_COUNT; // Approximate one grid item width
        final int itemHeightApx = itemWidthApx + 36 + 32 + 16; // Image side length (w == h) + textview height + margin top + margin bottom
        final int RecyclerViewHeightApx = itemHeightApx * (instructions.size() / SPAN_COUNT + 1) + 96 + 96; // view height * (rowcount) + paddingTop + paddingBottom

        params.height = RecyclerViewHeightApx;

        // -------------- UpperPage ---------------- //
        View mToolUpperPage = v.findViewById(R.id.tool_upperpage);
        mToolUpperPage.getLayoutParams().height = layoutHeight;


        // -------------- ScrollView ---------------- //
        final ScrollView mScrollView = (ScrollView) v.findViewById(R.id.tool_scrollview);
        // 뷰페이저는 비트맵 메모리 관리 더 어렵고, 더 느리고 스크롤 등 곤란한 점들이 있어서 스크롤바로 대체.
        // 뷰페이저와 같은 효과를 내기 위한 코드
        // page 2 부터는 scroll distance 줄여도 좋을듯.
        final GestureDetector mGd = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            int selectedPage = 0;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                /*
                if (selectedPage == 0) {
                    if (velocityY < 0) {
                        selectedPage += 1;
                    }
                } else if (selectedPage == 1) {
                    if (velocityY > 0) {
                        selectedPage -= 1;
                    }
                }
                */
                if (velocityY < 0) {
                    selectedPage = Math.min(3, selectedPage + 1);
                } else {
                    selectedPage = Math.max(0, selectedPage - 1);
                }
                mScrollView.smoothScrollTo(0, scrollDistance * selectedPage);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });


        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mGd.onTouchEvent(ev);
                return true;
            }
        });
        //------------------------------------------------------------------------------------------//


        final MyRecyclerAdapter mAdapter = new MyRecyclerAdapter(instructions, images);
        mRecycleView.setAdapter(mAdapter);

//        mRecycleView.setHasFixedSize(true);
//        MyWraperableGridLayoutManager mMyGridLayoutManager = new MyWraperableGridLayoutManager(getActivity(), SPAN_COUNT);
        final GridLayoutManager mMyGridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecycleView.setLayoutManager(mMyGridLayoutManager);
    }

    private void initDummyData() {
        instructions.add("맛있겠다");
        instructions.add("만들자");
        instructions.add("짠");
        instructions.add("감동ㅠ");
        instructions.add("맛있어");
        instructions.add("너무맛있어");
        instructions.add("어떡하지 널");
        instructions.add("아오 쓸데없는데서 어렵다");
        instructions.add("게다가 메모리가 왜이렇게 많이들지??!");
        instructions.add("말도안되.. 계속 더 잡아먹고있잖아.. 비트맵때문인가");
        instructions.add(2, "타다다다찹찹찹찹후루루룩포롱포롱후화화학빠바라람");

        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
        images.add(null);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        /*
        for (View v : mIvs) {
            v.getBackground().setCallback(null);
            v.setBackground(null);
        }
        */
    }
}