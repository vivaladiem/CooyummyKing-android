package com.coo.y2.cooyummyking.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.activity.GalleryActivity;
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.adapter.MyDynamicGridAdapter;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.widget.MyWrapableGridView;

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
    //------ Variables for load previously uncompleted recipe data ----//
//    private ArrayList<String> mSavedInstructions = new ArrayList<>();
//    private ArrayList<String> mSavedImageUrls = new ArrayList<>();

    // ----- Variables for Recipe data. Accessed by all tool related classes ---- //
    private Recipe mRecipe = Recipe.getScheme();

    //------ Variables for view --------------//
    private ScrollView mScrollView;
    private MyWrapableGridView mGridView;
    private MyDynamicGridAdapter mAdapter;

    //------ Variables for BottomBar Scroll -------//
    private ViewGroup mContainer; // For Reset Padding
    private int mScrollDistance;
    private GstListener mGstListener;
    private GestureDetector mGstDetector;
    int selectedPage = 0;
    int totalHeight;
    int pageCount = 0;

    //------- Variables for BottomBar Animation ---//
    private Animation mOutAnim;
    private Animation mInAnim;

    //------- Variables for Intent - Album, Camera -----------------//
    public static final int INTENT_FOR_ALBUM = 0;
    View mBtnAlbum;
    View mBtnCamera;

    //------- Variables for DetailEditor ----------//
    public static Bitmap screenImage; // Detail Editor에서 Background로 사용할 스크린캡쳐된 이미지


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.setPadding(0, 0, 0, 0);
        MainActivity.sIvBtnList.setSelected(false);
        MainActivity.sIvBtnTool.setSelected(true);
        MainActivity.sIvBtnMypage.setSelected(false);
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_recipe_tool, container, false);
        mContainer = container;

        initResources(mScrollView);
        loadSavedData();
        initBottomTabAnimation();
        initScrollViewFlicker();
        initEvents();
        return mScrollView;
    }

    private void initResources(View v) {
        // Variables for determine view sizes. All units are pixels
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenHeightPixels = dm.heightPixels;
        int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        int bottomTabHeight = getResources().getDimensionPixelSize(R.dimen.bottombar_height);
        int linkHeight = getResources().getDimensionPixelSize(R.dimen.link_height);
        int padding = getResources().getDimensionPixelSize(R.dimen.tool_padding); // Upper Page Padding

        int upperPageHeight = screenHeightPixels - actionBarHeight - bottomTabHeight - padding; // Height For Upper Page
        mScrollDistance = (upperPageHeight - linkHeight * 2) / 2; // link와 제작화면 탭 남겨놓고 움직일 거리의 1/2

        View mToolUpperPage = v.findViewById(R.id.tool_upperpage);
        mToolUpperPage.getLayoutParams().height = upperPageHeight;

        mGridView = (MyWrapableGridView) v.findViewById(R.id.tool_making_sector);
        mAdapter = new MyDynamicGridAdapter(getActivity(), getResources().getInteger(R.integer.dynamic_gridview_column_count));
        mGridView.setAdapter(mAdapter);

        //------------- recyclerView -------------------- //
//        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.tool_making_sector);
//        MyWraperableGridLayoutManager layoutManager = new MyWraperableGridLayoutManager(getActivity(), 3);
//        MyRecyclerAdapter adapter = new MyRecyclerAdapter(mSavedInstructions, mSavedImageUrls);
//
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
        //------------------------------------------------//

        EditText txtTitle = (EditText) v.findViewById(R.id.tool_info_title);
        EditText txtTime = (EditText) v.findViewById(R.id.tool_info_time);
        EditText txtTheme = (EditText) v.findViewById(R.id.tool_info_theme);
        EditText txtIngredient = (EditText) v.findViewById(R.id.tool_info_ingredient);
        EditText txtSource = (EditText) v.findViewById(R.id.tool_info_source);

        txtTitle.setOnFocusChangeListener(focusChangeListener);
        txtTime.setOnFocusChangeListener(focusChangeListener);
        txtTheme.setOnFocusChangeListener(focusChangeListener);
        txtIngredient.setOnFocusChangeListener(focusChangeListener);
        txtSource.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                ((EditText) view).setSingleLine(false);
            } else {
                ((EditText) view).setSingleLine(true);
            }
        }
    };

    private void loadSavedData() {
        // 저장되었던 정보들을 처리합니다.
        // view 굳이 멤버로 만들 필요 없으니 initResources에서 처리해야 할지도..
    }

    private void initBottomTabAnimation() {
        mOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom);
        mInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        mOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.sBottomBar.setVisibility(View.GONE);
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
                MainActivity.sBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    // 어째서인지 스크롤 할 때마다 10kb정도씩 소모된다? 메인화면의 일반 스크롤뷰에서도 역시..
    private void initScrollViewFlicker() {
        mGstListener = new GstListener();
        mGstDetector = new GestureDetector(getActivity(), mGstListener);

        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mGstDetector.onTouchEvent(ev);
                return true;
            }
        });
//
//        mGridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                // GridView의 최소한의 높이로 인해 page = 1이 되어 곤란하므로 불러온 사진이 없을 땐 이렇게 처리함.
//                if (((ViewGroup) view).getChildCount() != 0)
//                    mGstListener.setTotalHeightAndPage(view.getHeight());
//            }
//        });

        mGridView.addOnLayoutChangeListener(layoutChangeListener);
    }

    View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            // GridView의 최소한의 높이로 인해 page = 1이 되어 곤란하므로 불러온 사진이 없을 땐 이렇게 처리함.
            if (((ViewGroup) view).getChildCount() != 0)
                mGstListener.setTotalHeightAndPage(view.getHeight());

        }
    };

    private final class GstListener extends GestureDetector.SimpleOnGestureListener {
//        int selectedPage = 0;
//        int totalHeight;
//        int pageCount = 0;

        public final void setTotalHeightAndPage(int height) {
            totalHeight = height;
            pageCount = (int) Math.ceil((double) totalHeight / mScrollDistance); // 미세하게 남은 부분을 없애기 위해 0.95를 곱한다.
            if (pageCount == 1) pageCount = 2; // 조잡하다 조잡해... 아오...
        }

        // TODO OverView 부분에선 한 줄 높이만큼 스크롤되게 하는것도 괜찮을듯.
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (pageCount == 0) return false;
            // 윗 페이지(upper page)는 2개의 페이지로 치며 한 번에 스크롤해 내린다.
            if (selectedPage == 0) {
                if (velocityY < 0) {
                    selectedPage += 2;
                    MainActivity.sBottomBar.startAnimation(mOutAnim);
                }
            } else if (selectedPage == 2) {
                if (velocityY > 0) {
                    selectedPage -= 2;
                    MainActivity.sBottomBar.startAnimation(mInAnim);
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
        mBtnAlbum = mScrollView.findViewById(R.id.tool_photo_album);
        mBtnCamera = mScrollView.findViewById(R.id.tool_photo_camera);
        mBtnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 입력한 내용 저장 안되나? 그럼 저장해놓는 과정 필요
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivityForResult(intent, INTENT_FOR_ALBUM);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Detail Editor DialogFragment 실행(메모리때문에 어떻게 해야할지..)
                // DialogFragment or 스크린샷찍어 배경으로 쓰는 새로운 Activity or ReplaceFragment

                int width = mContainer.getMeasuredWidth();
                int height = mContainer.getMeasuredHeight();
                float scaleFactor = 2;

                screenImage = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(screenImage);
                canvas.scale(1 / scaleFactor, 1 / scaleFactor);


                mScrollView.setDrawingCacheEnabled(true);
                canvas.drawBitmap(mScrollView.getDrawingCache(), 0, 0, null);
                mScrollView.setDrawingCacheEnabled(false);
                canvas.scale(scaleFactor, scaleFactor);


                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = new ToolDetailEditorFragment();
                Bundle args = new Bundle();
                args.putInt("position", position);
                fragment.setArguments(args);
                // 현재 프래그먼트를 첫번째 인자로 넣어야 하는데 OnCLickListener 안이라 this로 못가져와서 findFragmentById로 함.
                fragment.setTargetFragment(fm.findFragmentById(R.id.fragmentContainer), ToolDetailEditorFragment.INTENT_REQUESTCODE);

                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case INTENT_FOR_ALBUM:
                if (resultCode != Activity.RESULT_OK) return;
                // 반드시 Clone을 해야 참조가 끊겨 GalleryActivity가 종료됨.
                ArrayList<String> imageUrls = (ArrayList<String>) data.getStringArrayListExtra(GalleryActivity.EXTRA_SELECTED_ITEMS).clone();
                mAdapter.addBulkItem(null, imageUrls);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView () {
        mGstDetector = null;
        mGstListener = null;
        mScrollView.setOnTouchListener(null); //mScrollView = null만 하면 안해도 되겠지?
        mScrollView = null;
        mOutAnim.setAnimationListener(null);
        mOutAnim = null;
        mInAnim.setAnimationListener(null);
        mInAnim = null;
        mGridView.setOnItemClickListener(null);
        mGridView.removeOnLayoutChangeListener(layoutChangeListener);
//        mGridView.setAdapter(null);
        mGridView = null;
        mAdapter = null;
        mBtnAlbum.setOnClickListener(null);
        mBtnAlbum = null;

        recursiveRecycle(getView());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // DetailEditor로 갈 때는 작동 되지 않도록 여기에서 처리
        screenImage = null;

        // 저장 후 참조끊기. 임시저장기능 추가해야.
        // 완전 허술한 구조...길이 같지 않으면 오류나는.. 1차원으로 나눴기때문에 어쩔 수 없긴 하지만, 보강해야
        int count = mRecipe.instructions.size();
        for (int i = count - 1; i >= 0; i--) { // 아니 대체 왜 --i가 먼저 --되고 진행하는게 아닌거야..
            mRecipe.instructions.remove(i);
            Recipe.imagePaths.remove(i);
        }

//        ImageLoader.getInstance().clearMemoryCache();

        MainActivity.sBottomBar.setVisibility(View.VISIBLE);
        mContainer.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.bottombar_height));
        super.onDestroy();
    }


//        private class ViewHolder {
//            ViewGroup vg;
//            View v;
//        }
//
//        private void returnMemory(View v) {
//            List<View> unvisited = new ArrayList<View>();
//            unvisited.add(v);
//            ViewHolder holder = new ViewHolder();
//
//            while (!unvisited.isEmpty()) {
//                holder.v = unvisited.remove(0);
//                if (holder.v.getBackground() != null) {
//                    holder.v.getBackground().setCallback(null);
//                    holder.v.setBackground(null);
//                }
//                if (holder.v instanceof ImageView) {
//                    ((ImageView) holder.v).getDrawable().setCallback(null);
//                    ((ImageView) holder.v).setImageDrawable(null);
//                }
//                if (!(holder.v instanceof ViewGroup)) continue;
//                holder.vg = (ViewGroup) holder.v;
//                final int childCount = holder.vg.getChildCount();
//                for (int i=0; i<childCount; i++) unvisited.add(holder.vg.getChildAt(i));
//            }
//        }

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
                count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    recursiveRecycle(group.getChildAt(i));
                }
                ((AdapterView)group).setAdapter(null); // 어떻게 어떤 AdapterView의 서브클래스이든 AdapterView 인 채로 setAdapter(null)을 할 수 있지?
            }
        }
        if (root instanceof ImageView) {
            ((ImageView)root).setImageDrawable(null);
        }
//        root = null;
    }

//    private void recursiveRecycle(List<WeakReference<View>> recycleList) {
//        for (WeakReference<View> ref : recycleList) {
//            recursiveRecycle(ref.get());
//        }
//    }
}