package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.adapter.MyRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-08.
 */
public class ToolPageFragment extends Fragment {
    private int mPageNum;
    private static final int SPAN_COUNT = 3;

    public static ToolPageFragment create (int pageNum) {
        ToolPageFragment fragment = new ToolPageFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNum = getArguments().getInt("page");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mPageNum == 0) {
            return inflateUpperPage(inflater, container);
        } else {
            return inflateLowerPage(inflater, container);
        }
    }

    private ViewGroup inflateUpperPage(LayoutInflater inflater, ViewGroup container) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tool_upperpage_info, container, false);

        return rootView;
    }
    private ViewGroup inflateLowerPage(LayoutInflater inflater, ViewGroup container) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tool_lowerpage_overview, container, false);
        initRecyclerView(rootView);
        return rootView;
    }

    // --------------Use In onCreateView ---------------- //
    private void initRecyclerView(View v) {
        // Dummy data for test
        ArrayList<String> instructions = new ArrayList<>();
        ArrayList<Bitmap> images = new ArrayList<>();
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

        //
        // 뷰페이져로 하는것도 괜찮겠는데 화면 큰 기기에선 윗페이지가 꽉차지 않아서 곤란함..
        RecyclerView mRecycleView = (RecyclerView) v.findViewById(R.id.tool_making_sector);
        mRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        final MyRecyclerAdapter mAdapter = new MyRecyclerAdapter(instructions, images);
        mRecycleView.setAdapter(mAdapter);

//        mRecycleView.setHasFixedSize(true);
//        MyWraperableGridLayoutManager mMyGridLayoutManager = new MyWraperableGridLayoutManager(getActivity(), SPAN_COUNT);
        final GridLayoutManager mMyGridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecycleView.setLayoutManager(mMyGridLayoutManager);

        /*
        mMyGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isHeader(position) ? 1 : 1;
            }
        });
        */
    }

}
