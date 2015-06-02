package com.coo.y2.cooyummyking.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.activity.GalleryActivity;
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.entity.RecipeDesign;
import com.coo.y2.cooyummyking.util.RecipeSerializer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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

    // ----- Variables for Recipe data ----------- //
    private RecipeDesign mRecipe;

    //------ Variables for view --------------//
    private int widthPx;
    private ScrollView mScrollView;
    private GridLayout mGridLayout;
    private int itemWidth;

    private EditText mTxtTitle;
    private EditText mTxtTime;
    private EditText mTxtTheme;
    private EditText mTxtIngredient;
    private EditText mTxtSource;
    private View mTimeEditArea;
    private InputMethodManager imm;
    private boolean isFinish = false; // 나의 멍청함을 탓하라...달리 해결책을 모르겠다..// 뒤로가기로 종료 할 때 EditText의ImeOption이 수행되서 Jogdialog 실행되며 에러나는 현상 막기 위한 변수
    public boolean isDialogFromTouch = false; // If so, don't move focus to next EditText

    private final DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .cacheInMemory(true)
            .build();

    //------ Variables for Scroll -------//
    private ViewGroup mContainer; // For Reset Padding
    private int mScrollDistance;
    private GstListener mGstListener;
    private GestureDetector mGstDetector;
    private int selectedPage = 0;
    private int totalHeight;
    private int pageCount = 0;
    private Vibrator mVibrator;

    //------- Variables for BottomBar Animation ---//
    private Animation mOutAnim;
    private Animation mInAnim;

    //------- Variables for Intent - Album, Camera -----------------//
    public static final int INTENT_FOR_ALBUM = 0;
    private View mBtnAlbum;
    private View mBtnCamera;

    //------- Variables for DetailEditor ----------//
    public static Bitmap screenImage; // Detail Editor에서 Background로 사용할 스크린캡쳐된 이미지


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // onBackStack에서 돌아올 때 반복되지 말아야 하는 것들의 처리를 합니다.

        initRecipeScheme();
    }

    private void initRecipeScheme() {
        if (RecipeDesign.isInited()) {
            mRecipe = RecipeDesign.getDesign();
            return;
        }

        RecipeSerializer serializer = new RecipeSerializer(getActivity());
        mRecipe = RecipeDesign.loadTempDesign(serializer);
        if (mRecipe == null) {
            mRecipe = RecipeDesign.getDesign();
            Log.i("CYMK", "새로운 레시피 작성");
        } else {
            Log.i("CYMK", "임시저장된 레시피 로드됨");
        }
        serializer = null;
    }

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
        initGridLayout(mScrollView);
        initBottomTabAnimation();
        initScrollViewFlicker();
        initEvents();
        return mScrollView;
    }

    private void initResources(View v) {
        // Variables for determine view sizes. All units are pixels
        DisplayMetrics dm = getResources().getDisplayMetrics();
        widthPx = dm.widthPixels;
        int screenHeightPixels = dm.heightPixels;
        int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        int bottomTabHeight = getResources().getDimensionPixelSize(R.dimen.bottombar_height);
        int linkHeight = getResources().getDimensionPixelSize(R.dimen.link_height);
        int padding = getResources().getDimensionPixelSize(R.dimen.tool_padding); // Upper Page Padding

        int upperPageHeight = screenHeightPixels - actionBarHeight - bottomTabHeight - padding; // Height For Upper Page
        mScrollDistance = (upperPageHeight - linkHeight * 2) / 2; // link와 제작화면 탭 남겨놓고 움직일 거리의 1/2

        View mToolUpperPage = v.findViewById(R.id.tool_upperpage);
        mToolUpperPage.getLayoutParams().height = upperPageHeight;

        //------------- recyclerView -------------------- //
//        mRecyclerView = (RecyclerView) v.findViewById(R.id.tool_making_sector);
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
//        MyWraperableGridLayoutManager layoutManager = new MyWraperableGridLayoutManager(getActivity(), 3);
//        mAdapter = new MyRecyclerAdapter(getActivity(), mRecyclerView);
//
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setAdapter(mAdapter);
        //------------------------------------------------//

        mTxtTitle = (EditText) v.findViewById(R.id.tool_info_title);
        mTxtTime = (EditText) v.findViewById(R.id.tool_info_time);
        mTxtTheme = (EditText) v.findViewById(R.id.tool_info_theme);
        mTxtIngredient = (EditText) v.findViewById(R.id.tool_info_ingredient);
        mTxtSource = (EditText) v.findViewById(R.id.tool_info_source);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); // Variable for handle keyboard

        // * ImeOptions on Multiple lines EditText only works when "singleLine = true & HorizontallyScrolling = false" -> horizon~ setting should be set on code!
        // (to make multiple lines show, set maxLines)

        mTxtTitle.setOnFocusChangeListener(focusChangeListener);

        mTxtTime.setOnFocusChangeListener(focusChangeListener);
        mTxtTime.setTag("time");

        mTxtTheme.setOnFocusChangeListener(focusChangeListener);

        mTxtIngredient.setOnFocusChangeListener(focusChangeListener);

        mTxtSource.setOnFocusChangeListener(focusChangeListener);

        mTimeEditArea = v.findViewById(R.id.tool_info_time_area);

        mTxtTitle.setText(mRecipe.title);
        mTxtTime.setText(timeToText(mRecipe.cookingTime));
        mTxtTheme.setText(mRecipe.theme);
        mTxtIngredient.setText(mRecipe.ingredients);
        mTxtSource.setText(mRecipe.sources);

    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            final EditText v = (EditText) view;
            if (hasFocus) {
                v.setMaxLines(10);

                v.postDelayed(new Runnable() { // post를 하지 않으면 키보드가 열리지 않음.
                    @Override
                    public void run() { // setHorizon~을 하면 키보드가 안뜨므로 이렇게 해서 키보드가 띄운다.
                        v.setHorizontallyScrolling(false);
                    }
                }, 100);
                // 커지고 작아지는 애니메이션 설정하면 더 좋긴 하겠다. TBD
                switch(String.valueOf(view.getTag())) {
                    case "time":
                        openTimerDialog();
                        break;
                    // TBD
                    default:
                }
            } else {
                v.setHorizontallyScrolling(true);
                v.setSelection(0); // 첫 줄이 보이도록 스크롤을 맨 위로 // 여러줄일 때 약간 내려간 듯 보이는 문제점.. - setHorizontallyScrolling 동적으로 조절해서 해결 -> 키보드 한번에 안뜨는 문제점.... -> post로 해결!
                v.setMaxLines(1);
            }
        }
    };


    private void initGridLayout(View v) {
        final int columnCount = 3;

        //멍청한 그리드레이아웃.. weight나 column strech같은게 없다.. 크기 작으면서 wrap_content되는게 아니면 일일이 크기 정해줘야..
        // 그래도 크기 정해주니까 드디어 제대로 작동한다. 리사이클러뷰, 그리드뷰 어떤 방식으로도 골치아픈 일이 계속 생겼는데
        Resources res = getResources();
        int itemMarginSide = res.getDimensionPixelSize(R.dimen.tool_overview_item_margin_side);
        int padding = res.getDimensionPixelSize(R.dimen.tool_padding);
        itemWidth = (widthPx - itemMarginSide * 2 * columnCount - padding * 2) / 3;

        mGridLayout = (android.support.v7.widget.GridLayout) v.findViewById(R.id.tool_making_sector);
        mGridLayout.setColumnCount(columnCount);

        int count = mRecipe.instructions.size();
        int rowCount = count / columnCount + 1;
        mGridLayout.setRowCount(rowCount);
        for (int i = 0; i < count; i++) {
//            int column = i % columnCount;
//            int row = i / columnCount;
//            columnSpec = android.support.v7.widget.GridLayout.spec(column, 1);
//            rowSpec = android.support.v7.widget.GridLayout.spec(row, 1);
//            lp = new android.support.v7.widget.GridLayout.LayoutParams(rowSpec, columnSpec);
//            lp.columnSpec = android.support.v7.widget.GridLayout.spec(column, 1);
//            lp.rowSpec = android.support.v7.widget.GridLayout.spec(row, 1);


//            View view = getLayoutInflater(null).inflate(R.layout.tool_lowerpage_overview_content, mGridLayout, false);
////            view.setLayoutParams(lp);
//            view.getLayoutParams().width = itemWidth;
//
//            mTvTagNum = (TextView) view.findViewById(R.id.tool_making_tag_num);
//            mIvRecipeImage = (ImageView) view.findViewById(R.id.tool_making_image);
//            mTvRecipeText = (TextView) view.findViewById(R.id.tool_making_text);
//            mTagMain = view.findViewById(R.id.tool_making_tag_main);
//
//            mTvTagNum.setText(String.valueOf(i + 1));
//            ImageLoader.getInstance().displayImage("file://" + Recipe.imagePaths.get(i), mIvRecipeImage, mOptions);
//            mTvRecipeText.setText(mRecipe.instructions.get(i));
//
//            if (mRecipe.mainImageIndex == i + 1) {
//                mTagMain.setVisibility(View.VISIBLE);
//            } else {
//                mTagMain.setVisibility(View.GONE);
//            }
//
//            mGridLayout.addView(view);
            addItem(i);
        }
    }

    private void addItem(int i) {
        View view = getLayoutInflater(null).inflate(R.layout.tool_lowerpage_overview_content, mGridLayout, false);
        view.getLayoutParams().width = itemWidth;

        TextView mTvTagNum = (TextView) view.findViewById(R.id.tool_making_tag_num);
        ImageView mIvRecipeImage = (ImageView) view.findViewById(R.id.tool_making_image);
        TextView mTvRecipeText = (TextView) view.findViewById(R.id.tool_making_text);
        View mTagMain = view.findViewById(R.id.tool_making_tag_main);

        mTvTagNum.setText(String.valueOf(i + 1));
        ImageLoader.getInstance().displayImage("file://" + mRecipe.getImagePath(i), mIvRecipeImage, mOptions);
        mTvRecipeText.setText(mRecipe.instructions.get(i));

        if (mRecipe.mainImageIndex == i)
            mTagMain.setVisibility(View.VISIBLE);

        view.setOnClickListener(gridItemClickListener);
        mGridLayout.addView(view);
    }


    private void initEvents() {
        mBtnAlbum = mScrollView.findViewById(R.id.tool_photo_album);
//        mBtnCamera = mScrollView.findViewById(R.id.tool_photo_camera);
        mBtnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivityForResult(intent, INTENT_FOR_ALBUM);
            }
        });


        mTimeEditArea.setOnClickListener(timerClickListener);


        // GridLayout Item의 리스너는 addItem()에서 등록한다
    }

    View.OnClickListener timerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openTimerDialog();
            isDialogFromTouch = true;
        }
    };

    View.OnClickListener gridItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Take ScreenShot for DetailEditorFragment's background
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

            // Open Detail Editor Fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment fragment = new ToolDetailEditorFragment();
            Bundle args = new Bundle();
            args.putInt("position", mGridLayout.indexOfChild(view));
            fragment.setArguments(args);

            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    };

    private void openTimerDialog() {
        if (isFinish) return;
        FragmentManager fm = getChildFragmentManager();
        JogDialogFragment jogFragment = JogDialogFragment.newInstance(mRecipe.cookingTime);
        jogFragment.show(fm, "dial_timer");
    }

    public void onTimerDialogClose(int time) {

        mTxtTime.setText(timeToText(time));
        mRecipe.cookingTime = time;

        if (isDialogFromTouch) {
            // 다이얼로그가 터치로 실행되었을 땐 연속작성을 하지 않는다.
            isDialogFromTouch = false;
            return;
        }

        mTxtTheme.requestFocus(); // txtTheme에 포커스 활성화
        imm.showSoftInput(mTxtTheme, InputMethodManager.SHOW_FORCED); // 키보드를 보여줌.
    }

    private String timeToText(int time) {
        StringBuilder txt = new StringBuilder();
        Resources resources = getResources();
        int h = time / 60;
        int m = time % 60;
        if (h != 0) txt.append(h).append(resources.getString(R.string.tool_info_time_hour)).append(" ");
        if (m != 0) txt.append(m).append(resources.getString(R.string.tool_info_time_minute));
        return txt.toString();
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
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mGstDetector.onTouchEvent(ev);
                return true;
            }
        });

        // TODO [Tuning] 이렇게 하면 여러 아이템이 추가될 땐 같은게 계속 불러지는 문제점.
        // 수동으로 setTotalHeightAndPage 콜 해야 효율적일 듯. 대신 코드는 지저분해지겠다.. 나중에.
        mGridLayout.addOnLayoutChangeListener(layoutChangeListener);
    }

    View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            if (view.getHeight() == totalHeight) return;

            // gridView의 padding으로 인해 초기에 pageCount가 1이 되는것을 방지함.
            if (((ViewGroup) view).getChildCount() != 0)
                mGstListener.setTotalHeightAndPage(view.getHeight());
        }
    };

    private final class GstListener extends GestureDetector.SimpleOnGestureListener {
        //        long[] vibPattern = {5, 10, 20, 10, 20, 15, 15, 15, 10, 15};
//        long[] vibPattern = {20, 1, 80, 1, 70, 1, 60, 1, 70, 2, 80}; // {대기시간, 지속시간, 대기시간, 지속시간, ..., 대기시간(?)}
        long[] vibPattern = {10, 10, 20, 10, 10, 10, 10, 10, 20}; // 이상하게 진동지속시간 1보다 10이 더 약하다..

        public final void setTotalHeightAndPage(int height) {
            totalHeight = height;
            pageCount = (int) Math.ceil((double) totalHeight / mScrollDistance); // 미세하게 남은 부분을 없애기 위해 0.95를 곱한다.
            if (pageCount == 1) pageCount = 2; // 조잡하다 조잡해... 아오...
        }

        // 세게 스크롤하면 Overview의 첫페이지 / 마지막 페이지로 바로가기
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
                    selectedPage = velocityY < -10000 ? pageCount : Math.min(pageCount, selectedPage + 1);
                    if (velocityY < -10000) mVibrator.vibrate(vibPattern, -1);
                }
            } else if (selectedPage == pageCount) {
                if (velocityY > 0) {
                    selectedPage = velocityY > 10000 ? 2 : selectedPage - 1;
                    if (velocityY > 10000) mVibrator.vibrate(vibPattern, -1);
                } else { return true; }
            } else {
                if (velocityY < 0) {
                    selectedPage = velocityY < -10000 ? pageCount : Math.min(pageCount, selectedPage + 1);
                    if (velocityY < -10000) mVibrator.vibrate(vibPattern, -1);
                } else {
                    selectedPage = velocityY > 10000 ? 2 : Math.max(2, selectedPage - 1);
                    if (velocityY > 10000) mVibrator.vibrate(vibPattern, -1);
                }
            }

            mScrollView.smoothScrollTo(0, mScrollDistance * selectedPage);
//            return super.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

    }


    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case INTENT_FOR_ALBUM:
                if (resultCode != Activity.RESULT_OK) return;
                // [Tuning] 반드시 Clone을 해야 참조가 끊겨 GalleryActivity가 종료됨.
                ArrayList<String> imageUrls = (ArrayList<String>) data.getStringArrayListExtra(GalleryActivity.EXTRA_SELECTED_ITEMS).clone();
//                Recipe.localImagePaths.addAll(imageUrls);
                int count = imageUrls.size();
                for (int i = 0; i < count; i++) {
                    mRecipe.instructions.add("");
                    mRecipe.imagePaths.add(imageUrls.get(i));
                    addItem(mRecipe.getStepSize() - 1);
                }

                // 메인이미지 태그를 새로고침
                if (!mRecipe.isMainImgManuallySet) {
                    mGridLayout.getChildAt(mRecipe.mainImageIndex).findViewById(R.id.tool_making_tag_main).setVisibility(View.GONE);
                    mRecipe.mainImageIndex = mRecipe.getStepSize() - 1;
                    mGridLayout.getChildAt(mRecipe.mainImageIndex).findViewById(R.id.tool_making_tag_main).setVisibility(View.VISIBLE);
                }

                mRecipe.isChanged = true;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Recipe Data는 정보를 입력하는 각 프래그먼트의 onPause에서 Recipe 싱글톤에 할당하고,
        // MainActivity의 OnPause에서 파일에 저장한다.
        String txt;
        if (!(txt = mTxtTitle.getText().toString()).equals(mRecipe.title)) {
            mRecipe.title = txt;
            mRecipe.isChanged = true;
        }

        // TODO 언어따라 다르니 코드로 배정 - 적어도 테마, 주재료 등 값 정해져있는 건 자동번역이 가능하게 됨
        if (!(txt = mTxtTheme.getText().toString()).equals(mRecipe.theme)) {
            //getText()가 아니라 다이얼로그 닫길 때 선택한 것으로, 닫길 때 할당되어야.(요리시간처럼) 그러니 나중엔 이 코드는 필요 없게됨.
            mRecipe.theme = txt;
            mRecipe.isChanged = true;
        }

        if (!(txt = mTxtIngredient.getText().toString()).equals(mRecipe.ingredients)) {
            mRecipe.ingredients = txt;
            mRecipe.isChanged = true;
        }

        if (!(txt = mTxtSource.getText().toString()).equals(mRecipe.sources)) {
            mRecipe.sources = txt;
            mRecipe.isChanged = true;
        }

    }

    @Override
    public void onDestroyView () {
        mGstDetector = null;
        mGstListener = null;
        mVibrator = null;
//        mScrollView.setOnTouchListener(null); //mScrollView = null만 하면 안해도 되겠지?
        mOutAnim.setAnimationListener(null);
        mOutAnim = null;
        mInAnim.setAnimationListener(null);
        mInAnim = null;
        isFinish = true;

        // Recipe Info - EditTexts
//        mTxtTitle.setOnKeyListener(null);
//        mTxtTitle.setOnFocusChangeListener(null);
//        mTxtTime.setOnFocusChangeListener(null);
//        mTxtTheme.setOnFocusChangeListener(null);
//        mTxtIngredient.setOnFocusChangeListener(null);
//        mTxtSource.setOnFocusChangeListener(null);
//        mTimeEditArea.setOnClickListener(null);

//        mBtnAlbum.setOnClickListener(null);

        mGridLayout.removeOnLayoutChangeListener(layoutChangeListener);

        recursiveRecycle(getView());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // DetailEditor로 갈 때는 작동 되지 않아야 하는 것들 여기에서 처리
        screenImage = null;

        ImageLoader.getInstance().clearMemoryCache();

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
                ((AdapterView)group).setAdapter(null); // AdapterView doesn't exist here.
            }
        }
        if (root instanceof ImageView) {
            Drawable drawable;
            ImageView iv = (ImageView) root;
            if ((drawable = iv.getDrawable()) == null) return;
            drawable.setCallback(null);
            iv.setImageDrawable(null);
            iv = null;
            drawable = null;
        }
        root.setOnFocusChangeListener(null);
        root.setOnTouchListener(null);
        root.setOnClickListener(null);
        root = null;
    }

//    private void recursiveRecycle(List<WeakReference<View>> recycleList) {
//        for (WeakReference<View> ref : recycleList) {
//            recursiveRecycle(ref.get());
//        }
//    }
}