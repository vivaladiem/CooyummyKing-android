package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.coo.y2.cooyummyking.MyWrapableGridView;
import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.adapter.MyDynamicGridAdapter;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-04.
 * 쿠요미툴 (레시피 제작 툴)
 * 페이지가 길기때문에 메모리 관리가 중요.
 * 모두 한 액티비티에서 진행되기때문에 Context 참조가 계속되므로
 * 뷰와 리스너의 메모리를 직접 해체해줘야하는듯( 비트맵은 언제나 직접 해체해야.. )
 * 그렇지만 액티비티를 실행하는게 굉장히 무겁고, 코드도 중복되므로 잘만 관리하면 이게 나음
 * 메모리는 메모리 누수 검사기로(MAT 등)
 */
public class ToolFragment extends Fragment {
    private ArrayList<String> instructions = new ArrayList<>();
    private ArrayList<Bitmap> images = new ArrayList<>();

    private int mScrollDistance;
    private ScrollView mScrollView;

    private GstListener mGstListener = new GstListener();
    private GestureDetector mGstDetector = new GestureDetector(getActivity(), mGstListener);

    private Animation mOutAnim;
    private Animation mInAnim;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.setPadding(0, 0, 0, 0);
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_recipe_tool, container, false);
        initDummyData();
        initResources(mScrollView);
        initBottomTabAnimation();
        initScrollViewFlicker();
        return mScrollView;
    }

    private void initResources(View v) {

        // 뷰들의 크기를 정의하기 위한 상수들을 가져옵니다.
        // All units are pixels
        // 회전 안하니까 그냥 여기에서 다 해도..
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenHeightPixels = dm.heightPixels;
        int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height);
        int bottomTabHeight = getResources().getDimensionPixelSize(R.dimen.bottomtab_height);
        int linkHeight = getResources().getDimensionPixelSize(R.dimen.link_height);
        int padding = getResources().getDimensionPixelSize(R.dimen.tool_padding); // Upper Page Padding

        int upperPageHeight = screenHeightPixels - actionBarHeight - bottomTabHeight - padding; // Height For Upper Page
        mScrollDistance = (upperPageHeight - linkHeight * 2) / 2; // link와 '제작화면'이란 글씨 있는 탭 남겨놓고 움직일 거리의 1/2


        MyWrapableGridView mGridView = (MyWrapableGridView) v.findViewById(R.id.tool_making_sector);
        mGridView.setAdapter(new MyDynamicGridAdapter(getActivity()
                , getResources().getInteger(R.integer.dynamic_gridview_column_count)
                , instructions, images));

        View mToolUpperPage = v.findViewById(R.id.tool_upperpage);
        mToolUpperPage.getLayoutParams().height = upperPageHeight;
    }

    // onResume에서 했더니 화면은 빠르게 나타나는데 처음 켜지고 조작이 느려서 그냥 onCreateView에서 함..
    private void initBottomTabAnimation() {
        mOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom);
        mInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        mOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.bottomTab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.bottomTab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void initScrollViewFlicker() {
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mGstDetector.onTouchEvent(ev);
                return true;
            }
        });

        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                mGstListener.setTotalHeightAndPage(view.getMeasuredHeight()); // 처음에 mScrollView 그려질 때도 호출됨.
            }
        });
    }

    private final class GstListener extends GestureDetector.SimpleOnGestureListener {
        int selectedPage = 0;
        int totalHeight;
        int pageCount = 2; // 기본적으로 2개의 페이지 분량은 존재

        public final void setTotalHeightAndPage(int height) {
            totalHeight = height;
            pageCount = (int) Math.ceil((double) totalHeight / mScrollDistance);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 윗 페이지(upper page)는 2개의 페이지로 치고 한 번에 스크롤해 내린다.
            if (selectedPage == 0) {
                if (velocityY < 0) {
                    selectedPage += 2;
                    MainActivity.bottomTab.startAnimation(mOutAnim);

                }
            } else if (selectedPage == 2) {
                if (velocityY > 0) {
                    selectedPage -= 2;
                    MainActivity.bottomTab.startAnimation(mInAnim);
                } else {
                    selectedPage = Math.min(pageCount, selectedPage + 1);
                }
            } else {
                if (velocityY < 0) {
                    selectedPage = Math.min(pageCount, selectedPage + 1);
                } else {
                    selectedPage = Math.max(0, selectedPage - 1);
                }
            }

            mScrollView.smoothScrollTo(0, mScrollDistance * selectedPage);
//            return super.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

    }

    private void initEvents() {
        View btnAlbum = mScrollView.findViewById(R.id.tool_photo_album);
        View btnCamera = mScrollView.findViewById(R.id.tool_photo_camera);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        // 사실 getView().getParent()빼고는 별로 에러날 곳 없겠지만 그냥 편의+혹시몰라 한다.
        try {
            mGstDetector = null;
            mGstListener = null;
            mScrollView.setOnTouchListener(null); //mScrollView = null만 하면 안해도 되겠지?
            mScrollView = null;
            mOutAnim.setAnimationListener(null);
            mOutAnim = null;
            mInAnim.setAnimationListener(null);
            mInAnim = null;
            ((ViewGroup) getView().getParent()).setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.bottomtab_height));
//        returnMemory(getView());
            recursiveRecycle(getView());
        } catch(Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    /*
    private class ViewHolder {
        ViewGroup vg;
        View v;
    }

    private void returnMemory(View v) {
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);
        ViewHolder holder = new ViewHolder();

        while (!unvisited.isEmpty()) {
            holder.v = unvisited.remove(0);
            if (holder.v.getBackground() != null) {
                holder.v.getBackground().setCallback(null);
                holder.v.setBackground(null);
            }
            if (holder.v instanceof ImageView) {
                ((ImageView) holder.v).getDrawable().setCallback(null);
                ((ImageView) holder.v).setImageDrawable(null);
            }
            if (!(holder.v instanceof ViewGroup)) continue;
            holder.vg = (ViewGroup) holder.v;
            final int childCount = holder.vg.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(holder.vg.getChildAt(i));
        }
    }
    */

    // TODO destroy 후 메인으로 갈 때도 60kb가 추가되고, 다시 이 프래그먼트 시작할 때마다 300kb씩 쌓이는걸로 보아 아직도 무언가 반환되지 않는게 있다.
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
            if (!(group instanceof AdapterView)) {
                group.removeAllViews();
            } else {
                ((MyWrapableGridView) group).setAdapter(null); // 어떻게 어떤 AdapterView의 서브클래스이든 AdapterView 인 채로 setAdapter(null)을 할 수 있지?
            }
        }
        if (root instanceof ImageView) {
            ((ImageView)root).setImageDrawable(null);
        }
        root = null;
    }

    /*
    private void recursiveRecycle(List<WeakReference<View>> recycleList) {
        for (WeakReference<View> ref : recycleList) {
            recursiveRecycle(ref.get());
        }
    }
    */
}