package com.coo.y2.cooyummyking.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import com.coo.y2.cooyummyking.activity.GalleryActivity;
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.adapter.MyDynamicGridAdapter;

import java.util.ArrayList;
import java.util.Iterator;

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
    private ArrayList<String> mSavedInstructions = new ArrayList<>();
    private ArrayList<String> mSavedImageUrls = new ArrayList<>();

    private int mScrollDistance;
    private ScrollView mScrollView;
    private MyWrapableGridView mGridView;

    private MyDynamicGridAdapter mAdapter;

    private GstListener mGstListener = new GstListener();
    private GestureDetector mGstDetector = new GestureDetector(getActivity(), mGstListener);

    private Animation mOutAnim;
    private Animation mInAnim;

    public static final int INTENT_FOR_ALBUM = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.setPadding(0, 0, 0, 0);
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_recipe_tool, container, false);
//        initDummyData();
        initResources(mScrollView);
        loadSavedData();
        initBottomTabAnimation();
        initScrollViewFlicker();
        initEvents();
        return mScrollView;
    }

    private void initResources(View v) {
        // 뷰들의 크기를 정의하기 위한 상수들을 가져옵니다.
        // All units are pixels
        // 회전 안하니까 그냥 여기에서 다 해도..
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenHeightPixels = dm.heightPixels;
        int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        int bottomTabHeight = getResources().getDimensionPixelSize(R.dimen.bottombar_height);
        int linkHeight = getResources().getDimensionPixelSize(R.dimen.link_height);
        int padding = getResources().getDimensionPixelSize(R.dimen.tool_padding); // Upper Page Padding

        int upperPageHeight = screenHeightPixels - actionBarHeight - bottomTabHeight - padding; // Height For Upper Page
        mScrollDistance = (upperPageHeight - linkHeight * 2) / 2; // link와 '제작화면'이란 글씨 있는 탭 남겨놓고 움직일 거리의 1/2


        View mToolUpperPage = v.findViewById(R.id.tool_upperpage);
        mToolUpperPage.getLayoutParams().height = upperPageHeight;


        mGridView = (MyWrapableGridView) v.findViewById(R.id.tool_making_sector);
        mAdapter = new MyDynamicGridAdapter(getActivity(), getResources().getInteger(R.integer.dynamic_gridview_column_count), mSavedInstructions, mSavedImageUrls); // null자리에 원래 mSaved~ 가 들어가있어야.
        mGridView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            mGridView.setClipToOutline(false);

        //------------- recyclerView -------------------- //
//        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.tool_making_sector);
//        MyWraperableGridLayoutManager layoutManager = new MyWraperableGridLayoutManager(getActivity(), 3);
//        MyRecyclerAdapter adapter = new MyRecyclerAdapter(mSavedInstructions, mSavedImageUrls);
//
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
        //------------------------------------------------//
    }

    private void loadSavedData() {
        // 저장되었던 정보들을 처리합니다.
        // view 굳이 멤버로 만들 필요 없으니 initResources에서 처리해야 할지도..
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

    // 어째서인지 스크롤 할 때마다 10kb정도씩 소모된다? 메인화면의 일반 스크롤뷰에서도 역시..
    private void initScrollViewFlicker() {
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mGstDetector.onTouchEvent(ev);
                return true;
            }
        });

        mGridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                mGstListener.setTotalHeightAndPage(view.getMeasuredHeight()); // 처음에 mScrollView 그려질 때도 호출됨.
                if (((ViewGroup) view).getChildCount() != 0)
                    mGstListener.setTotalHeightAndPage(view.getHeight());
            }
        });
    }

    private final class GstListener extends GestureDetector.SimpleOnGestureListener {
        int selectedPage = 0;
        int totalHeight;
        int pageCount = 0;

        public final void setTotalHeightAndPage(int height) {
            totalHeight = height;
            pageCount = (int) Math.ceil((double) totalHeight / mScrollDistance);
            if (pageCount == 1) pageCount = 2; // 조잡하다 조잡해... 아오...
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (pageCount == 0) return false;
            // 윗 페이지(upper page)는 2개의 페이지로 치며 한 번에 스크롤해 내린다.
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
                // TODO 입력한 내용 저장 안되나? 그럼 저장해놓는 과정 필요
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivityForResult(intent, INTENT_FOR_ALBUM);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case INTENT_FOR_ALBUM:
                if (resultCode != Activity.RESULT_OK) return;
                // clone 안하면 참조값복사이므로 GalleryActivity가 남아있을지도 몰라 clone을 할라했는데 type unChecked warn 같은 번거로운게 뜨네..
//                ArrayList<String> imageUrls = (ArrayList<String>) data.getStringArrayListExtra(GalleryActivity.EXTRA_SELECTED_ITEMS).clone();
                ArrayList<String> imageUrls = data.getStringArrayListExtra(GalleryActivity.EXTRA_SELECTED_ITEMS);

                // addBulkItem을 하는것이 효율적이나 imageUrls와 같은 사이즈의 (현재로선 null로 차있는) instructions가 필요해서 귀찮아서 나중에..
                Iterator<String> it = imageUrls.iterator();
                while (it.hasNext()) {
                    mAdapter.addItem(null, it.next());
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            ((ViewGroup) getView().getParent()).setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.bottombar_height));
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
    // TODO GridView의 Children의 이미지들도 비워줘야.
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