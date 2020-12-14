package com.coo.y2.cooyummyking.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.coo.y2.cooyummyking.fragment.MainFragment;
import com.coo.y2.cooyummyking.fragment.ToolFragment;
import com.coo.y2.cooyummyking.listener.OnBackPressedListener;
import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.RecipeDesign;
import com.coo.y2.cooyummyking.util.ExhibitManager;
import com.coo.y2.cooyummyking.util.RecipeSerializer;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static View sBottomBar;
    public static ImageView sIvBtnList;
    public static ImageView sIvBtnTool;
    public static ImageView sIvBtnMypage;
    private DrawerLayout mDrawerLayout;
        private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private OnBackPressedListener onFragmentBackPressedListener;
//    private Typeface mTypeface;

//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//        if (mTypeface == null) {
//        mTypeface = Typeface.createFromAsset(getAssets(), "nanum.otf.mp3");
//        }
//        ViewGroup root = (ViewGroup) findViewById(R.id.activity_main_layout);
//        setAppFont(root);
//    }
//    private void setAppFont(ViewGroup root) {
//        ViewHolderForApplyFont holder = new ViewHolderForApplyFont();
//        int count = root.getChildCount();
//        for (int i = 0; i < count; i++) {
//            holder.v = root.getChildAt(i);
//            if (holder.v instanceof TextView) {
//                ((TextView)holder.v).setTypeface(mTypeface);
//            } else if (holder.v instanceof ViewGroup) {
//                setAppFont((ViewGroup) holder.v);
//            }
//        }
//    }
//    private class ViewHolderForApplyFont {
//        View v;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initFragment();
        initToolbar();
        initBottomTab();
//        addDrawerItems();

//        filters = new FilterList();

//        filters.addFilter("filter_1977", FilterType.I_1977);
//        filters.addFilter("filter_amaro", FilterType.I_AMARO);
//        filters.addFilter("filter_brannan", FilterType.I_BRANNAN);
//        filters.addFilter("filter_earlybird", FilterType.I_EARLYBIRD);
//        filters.addFilter("filter_hefe", FilterType.I_HEFE);
//        filters.addFilter("filter_hudson", FilterType.I_HUDSON);
//        filters.addFilter("filter_inkwell", FilterType.I_INKWELL);
//        filters.addFilter("filter_lomo", FilterType.I_LOMO);
//        filters.addFilter("filter_lord_kelvin", FilterType.I_LORDKELVIN);
//        filters.addFilter("filter_nashville", FilterType.I_NASHVILLE);
//        filters.addFilter("filter_rise", FilterType.I_RISE);
//        filters.addFilter("filter_sierra", FilterType.I_SIERRA);
//        filters.addFilter("filter_sutro", FilterType.I_SUTRO);
//        filters.addFilter("filter_toaster", FilterType.I_TOASTER);
//        filters.addFilter("filter_valencia", FilterType.I_VALENCIA);
//        filters.addFilter("filter_walden", FilterType.I_WALDEN);
//        filters.addFilter("filter_xproll", FilterType.I_XPROII);
//        filters.addFilter("filter_contrast", FilterType.CONTRAST);
//        filters.addFilter("filter_sepia", FilterType.SEPIA);
//        filters.addFilter("filter_vignette", FilterType.VIGNETTE);
//        filters.addFilter("filter_tone_curve", FilterType.TONE_CURVE);
//        filters.addFilter("filter_amatorka", FilterType.LOOKUP_AMATORKA);
//
//        gpu = new GPUImage(this);
//        new AsyncFilterTask().execute();
//        Log.i("CYMK", "file dir : " + getFilesDir());
//        Log.i("CYMK", "exeternal files dir : " + getExternalFilesDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.coos/"));
    }

//    FilterList filters;
//    GPUImage gpu;
//
//    private class AsyncFilterTask extends AsyncTask<Void, Integer, Void> {
//        int count = filters.names.size();
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            for (int i = 0; i < count; i++) {
//                gpu.setImage(((BitmapDrawable) getResources().getDrawable(R.drawable.icecream6)).getBitmap());
//                publishProgress(i);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            int i = values[0];
//            if (i < count)
//            gpu.setFilter(createFilterForType(MainActivity.this, filters.filters.get(i)));
//            Bitmap bmp = gpu.getBitmapWithFilterApplied();
//            String filterName = filters.names.get(i);
//            Log.i("CYMK", filterName + " image saved " + saveBitmap(filters.names.get(i), bmp));
//        }
//    }
//
//    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.Coos/";
//    private boolean saveBitmap(String fileName, Bitmap bitmap) {
//        File file = new File(StorageUtils.getCacheDirectory(this).getAbsolutePath() + "/" + fileName + ".png");
//        OutputStream out;
//        boolean isSuccess = false;
//        try {
//            file.createNewFile();
//            out = new FileOutputStream(file);
//            isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//        } catch(IOException e) {
//            e.printStackTrace();
//        } finally {
//        }
//        return isSuccess;
//    }
//
//    private static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
//        switch (type) {
////            case CONTRAST:
////                return new GPUImageContrastFilter(2.0f);
////            case GAMMA:
////                return new GPUImageGammaFilter(2.0f);
////            case INVERT:
////                return new GPUImageColorInvertFilter();
////            case PIXELATION:
////                return new GPUImagePixelationFilter();
////            case HUE:
////                return new GPUImageHueFilter(90.0f);
////            case BRIGHTNESS:
////                return new GPUImageBrightnessFilter(1.5f);
////            case GRAYSCALE:
////                return new GPUImageGrayscaleFilter();
////            case SEPIA:
////                return new GPUImageSepiaFilter();
////            case SHARPEN:
////                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
////                sharpness.setSharpness(2.0f);
////                return sharpness;
////            case SOBEL_EDGE_DETECTION:
////                return new GPUImageSobelEdgeDetection();
////            case THREE_X_THREE_CONVOLUTION:
////                GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
////                convolution.setConvolutionKernel(new float[] {
////                        -1.0f, 0.0f, 1.0f,
////                        -2.0f, 0.0f, 2.0f,
////                        -1.0f, 0.0f, 1.0f
////                });
////                return convolution;
////            case EMBOSS:
////                return new GPUImageEmbossFilter();
////            case POSTERIZE:
////                return new GPUImagePosterizeFilter();
////            case FILTER_GROUP:
////                List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
////                filters.add(new GPUImageContrastFilter());
////                filters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
////                filters.add(new GPUImageGrayscaleFilter());
////                return new GPUImageFilterGroup(filters);
////            case SATURATION:
////                return new GPUImageSaturationFilter(1.0f);
////            case EXPOSURE:
////                return new GPUImageExposureFilter(0.0f);
////            case HIGHLIGHT_SHADOW:
////                return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
////            case MONOCHROME:
////                return new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f});
////            case OPACITY:
////                return new GPUImageOpacityFilter(1.0f);
////            case RGB:
////                return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
////            case WHITE_BALANCE:
////                return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
////            case VIGNETTE:
////                PointF centerPoint = new PointF();
////                centerPoint.x = 0.5f;
////                centerPoint.y = 0.5f;
////                return new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
////            case TONE_CURVE:
////                GPUImageToneCurveFilter toneCurveFilter = new GPUImageToneCurveFilter();
////                toneCurveFilter.setFromCurveFileInputStream(
////                        context.getResources().openRawResource(R.raw.tone_cuver_sample));
////                return toneCurveFilter;
////
////            case LOOKUP_AMATORKA:
////                GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
////                amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_amatorka));
////                return amatorka;
////
////            case I_1977:
////                return new IF1977Filter(context);
////            case I_AMARO:
////                return new IFAmaroFilter(context);
////            case I_BRANNAN:
////                return new IFBrannanFilter(context);
////            case I_EARLYBIRD:
////                return new IFEarlybirdFilter(context);
////            case I_HEFE:
////                return new IFHefeFilter(context);
////            case I_HUDSON:
////                return new IFHudsonFilter(context);
////            case I_INKWELL:
////                return new IFInkwellFilter(context);
////            case I_LOMO:
////                return new IFLomoFilter(context);
////            case I_LORDKELVIN:
////                return new IFLordKelvinFilter(context);
//            case I_NASHVILLE:
//                return new IFNashvilleFilter(context);
//            case I_RISE:
//                return new IFRiseFilter(context);
////            case I_SIERRA:
////                return new IFSierraFilter(context);
////            case I_TOASTER:
////                return new IFToasterFilter(context);
////            case I_VALENCIA:
////                return new IFValenciaFilter(context);
////            case I_WALDEN:
////                return new IFWaldenFilter(context);
////            case I_XPROII:
////                return new IFXprollFilter(context);
//
//
//            default:
//                throw new IllegalStateException("No filter of that type!");
//        }
//
//    }
//
//    private enum FilterType {
//        CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
//        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, TONE_CURVE, LOOKUP_AMATORKA,
//        I_1977, I_AMARO, I_BRANNAN, I_EARLYBIRD, I_HEFE, I_HUDSON, I_INKWELL, I_LOMO, I_LORDKELVIN, I_NASHVILLE, I_RISE, I_SIERRA, I_SUTRO,
//        I_TOASTER, I_VALENCIA, I_WALDEN, I_XPROII
//    }
//
//    private static class FilterList {
//        public List<String> names = new LinkedList<String>();
//        public List<FilterType> filters = new LinkedList<FilterType>();
//
//        public void addFilter(final String name, final FilterType filter) {
//            names.add(name);
//            filters.add(filter);
//        }
//    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new MainFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.ham_open, R.string.ham_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e) { e.printStackTrace(); }
    }

    // Selected 상태는 각 Fragment에서 처리하기로.(여기서는 PopBackStack할 때 처리가 곤란)
    private void initBottomTab() {
        sBottomBar = findViewById(R.id.bottombar);
        sIvBtnList = (ImageView) findViewById(R.id.bottombar_list);
        sIvBtnTool = (ImageView) findViewById(R.id.bottombar_tool);
        sIvBtnMypage = (ImageView) findViewById(R.id.bottombar_mypage);

        // TODO 버튼 사이 뷰 인것같은데, 간혹 버튼사이 밑으로 끝 쪽 터치되면 아래로 이벤트 전달되는 문제 발생.
        sIvBtnList.setSelected(true);
        sIvBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
                Fragment fragment = new MainFragment();
                replaceFragment(fragment, false);
            }
        });
        sIvBtnTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
                Fragment fragment = new ToolFragment();
                replaceFragment(fragment, sIvBtnList.isSelected());
            }
        });
        sIvBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
            }
        });
    }

    private void replaceFragment (Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fm.beginTransaction();
        if (addToBackStack) transaction.addToBackStack(null);
        transaction
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

//        private void addDrawerItems() {
//            mDrawerList = (ListView) findViewById(R.id.ham_menu);
//            int[] arrays = {R.drawable.ham_btn_notify_off, R.drawable.ham_btn_battle}
//            mAdapter = new ArrayAdapter<> (this, android.R.layout.simple_gallery_item, arrays);
//            mDrawerList.setAdapter(mAdapter);
//        }

    @Override
    protected void onPause() {
        super.onPause();

        // Save temp recipe
        // ToolFragment가 이 Activity 위에 있으므로 생명주기상 여기에서 작성중인 레시피 저장을 한다.
        RecipeDesign recipe = RecipeDesign.getDesign();
        if (!recipe.isChanged) return; // if recipe hasn't changed(include didn't start making one)
        RecipeSerializer serializer = new RecipeSerializer(this);
        waitExhibit();
        try {
            serializer.saveTempData(recipe);
            recipe.isChanged = false; // code needed when starting other Activity
            Log.i("CYMK", "Temp recipe data is saved");
        } catch(Exception e) {
            Log.i("CYMK", "Failed to save recipe temp data");
        }
    }

    // 이미지 편집 중 저장이 끝나지 않은 것이 존재한다면 완료시까지 기다립니다.
    private void waitExhibit() {
        ArrayList<ExhibitManager.Exhibit> exhibits = ExhibitManager.getAllExhibit();
        if (exhibits == null) return;
        for (ExhibitManager.Exhibit exhibit : exhibits) {
            exhibit.waitUntilClose();
        }
    }

    // ------------------------ Set Toolbar and Menu --------------------------------- //
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        mToolbar.setNavigationIcon(R.drawable.toolbar_btn_hammenu); // 일반 정적 아이콘을 toggle로 사용하기. 애니메이션이 없어져서 drawerArrayToggle의 크기를 커스텀해서 사용중.(styles.xml)
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private static int backPressedCount = 0;
    private long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        if (onFragmentBackPressedListener != null) {
            onFragmentBackPressedListener.onBackPressed();
            return;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        // 두번 눌러 종료하기 //
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = 0;
            backPressedCount = 0;
        }

        if (backPressedCount == 0) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.close_toast, Toast.LENGTH_SHORT).show();
            backPressedCount++;
            return;
        }

        super.onBackPressed();
    }

    public void setOnFragmentBackPressedListener(OnBackPressedListener listener) {
        this.onFragmentBackPressedListener = listener;
    }
}
