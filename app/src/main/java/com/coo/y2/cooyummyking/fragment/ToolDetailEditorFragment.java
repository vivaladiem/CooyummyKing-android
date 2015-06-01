package com.coo.y2.cooyummyking.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.filterUtil.GPUImageFilterTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Y2 on 2015-05-18.
 */
public class ToolDetailEditorFragment extends Fragment implements View.OnClickListener, GPUImageFilterTools.OnGpuImageFilterChosenListener{
    public static final int INTENT_REQUESTCODE = 0;

    private ViewPager mViewPager;
    private FragmentManager fm;
    private ViewPagerAdapter mAdapter;
    private ViewGroup mLowerView;
    private ViewGroup mBottomBar;
    private Animation mAnimSlideIn;
    private Animation mAnimFadeIn;


    // ------------ Bottom bar ------------ //
    private View mBtnEditPhoto;
    private View mBtnFilter;
    private View mBtnSticker;
    private View mBtnSubtitle;
    private View mBtnText;

    private int currentBtnId; // 현재 선택되어있는 하단바 도구의 Id
    private ToolDetailEditorPhotoFragment mPhotoFragment;
    private GPUImageFilter mCurrentFilter = null; // 현재 선택된 필터

    private final String VIEW_IMAGE = "iv";
    private final String VIEW_TEXT = "ed";


    // Use in ToolDetailEditorPageFragment
    private DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(200))
            .build();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tool_detail_editor, container, false);
        initResources(v);
        initAnimation(v);
        initBottomBar();
        setHasOptionsMenu(true);
        return v;
    }
    private void initResources(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.tool_detail_editor_viewPager);
        mViewPager.setPageMargin(20);
        v.setBackground(new BitmapDrawable(getResources(), ToolFragment.screenImage));
        fm = getChildFragmentManager();
        mAdapter = new ViewPagerAdapter(fm);
        mViewPager.setAdapter(mAdapter);

        int position = getArguments().getInt("position");
        mViewPager.setCurrentItem(position);

        mLowerView = (ViewGroup) v.findViewById(R.id.tool_detail_editor_util_container);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ToolDetailEditorPageFragment.newInstance(position, imageOptions);
        }

        @Override
        public int getCount() {
            return Recipe.getStepSize();
        }
    }

//    FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(fm) {
//
//        @Override
//        public Fragment getItem(int position) {
//            return ToolDetailEditorPageFragment.newInstance(position, imageOptions);
//        }
//
//        @Override
//        public int getCount() {
//            return Recipe.getStepSize();
//        }
//    };

    private void initAnimation(View v) {
        mAnimFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in);
        mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mViewPager.setBackgroundColor(Color.argb(150, 0, 0, 0));
            }

            @Override
            public void onAnimationEnd(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mViewPager.startAnimation(mAnimFadeIn);

        mBottomBar = (ViewGroup) v.findViewById(R.id.tool_detail_editor_bottombar);
        mAnimSlideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        mAnimSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mBottomBar.startAnimation(mAnimSlideIn);
    }

    private void initBottomBar() {
        // Each Listener need to clear previously added view.
//        View btnEditPhoto = mBottomBar.getChildAt(0); // add view. set onclick listener.
//        View btnFilter = mBottomBar.getChildAt(2);
//        View btnSticker = mBottomBar.getChildAt(4);
//        View btnSubtitle = mBottomBar.getChildAt(6);
//        View btnText = mBottomBar.getChildAt(8); // 그냥 되돌리기 기능을 넣는게 어떤가 싶음. 본문쓰기는 직접 터치하면 되니까. 되돌리기->사진 or 텍스트 되돌리기 버튼.

        mBtnEditPhoto = mBottomBar.findViewById(R.id.tool_detail_bottombar_photo_edit); // add view. set onclick listener.
        mBtnFilter = mBottomBar.findViewById(R.id.tool_detail_bottombar_filter);
        mBtnSticker = mBottomBar.findViewById(R.id.tool_detail_bottombar_sticker);
        mBtnSubtitle = mBottomBar.findViewById(R.id.tool_detail_bottombar_subtitle);
        mBtnText = mBottomBar.findViewById(R.id.tool_detail_bottombar_text);

        mBtnEditPhoto.setOnClickListener(this);
        mBtnFilter.setOnClickListener(this);
        mBtnSticker.setOnClickListener(this);
        mBtnSubtitle.setOnClickListener(this);
        mBtnText.setOnClickListener(this);
    }


    // BottomBar Item Click Listener
    @Override
    public void onClick(View view) {
        if (currentBtnId == view.getId()) return;

        removeLowerView();

        view.requestFocus();
        currentBtnId = view.getId();

        switch(view.getId()) {
            case R.id.tool_detail_bottombar_photo_edit:
                break;

            case R.id.tool_detail_bottombar_filter:
//                Fragment fragment = fm.findFragmentById(containerResId);
//                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
//                if (fragment == null) {
////                    ft.add(containerResId, );
//                } else {
////                    ft.replace(containerResId, );
//                }
//                // remove에 null을 넣어도 되나? 그러면은 remove(fragment).add()를 하는게 더 깔끔할텐데.
////                ft.commit();

//                final GPUImage gpuImage = new GPUImage(getActivity());
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Drawable d = ((ImageView) getPageChildView(VIEW_IMAGE)).getDrawable();
//                        Bitmap bmp = ((BitmapDrawable)d).getBitmap();
//                        gpuImage.setFilteredImage(bmp);
//                        gpuImage.setFilter(new IFLomoFilter(getActivity()));
//                        ((ImageView) getPageChildView(VIEW_IMAGE)).setImageBitmap(gpuImage.getBitmapWithFilterApplied());
//                        bmp.recycle();
//                    }
//                }).run();
                mBtnFilter.setSelected(true);

                showPhotoEditFragment();

                GPUImageFilterTools filterTools = new GPUImageFilterTools(getActivity());
                mLowerView.addView(filterTools.makeFilterLayout(this, mLowerView));

                break;

            case R.id.tool_detail_bottombar_sticker: // 새 뷰페이저 프래그먼트 실행
                break;

            case R.id.tool_detail_bottombar_subtitle:
                break;

            case R.id.tool_detail_bottombar_text:
                try {
                    View ed = getPageChildView(VIEW_TEXT);
                    ed.requestFocus();
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(ed, InputMethodManager.SHOW_FORCED);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void removeLowerView() {
        recursiveRecycle(mLowerView);
        int count = mBottomBar.getChildCount();
        for (int i = 0; i < count; i++) {
            mBottomBar.getChildAt(i).setSelected(false);
        }
    }

    private void showPhotoEditFragment() {
        if (fm.findFragmentById(R.id.tool_detail_editor_photo_container) != null) return;

        mPhotoFragment = ToolDetailEditorPhotoFragment.newInstance(getCurrentItem());
        fm.beginTransaction()
                .add(R.id.tool_detail_editor_photo_container, mPhotoFragment)
                .commit();
    }


    @Override
    public void onFilterChosen(final GPUImageFilter filter) {
        if (mCurrentFilter == filter) return;
        String imagePath = Recipe.getScheme().getImagePath(getCurrentItem());

        // On Reset Chosen
        if (filter == null) {
            mCurrentFilter = null;
            mPhotoFragment.setFilterImage(null);
            return;
        }

        // On Filter Chosen
        ImageSize size = new ImageSize(640, 640);

        ImageLoader.getInstance().loadImage("file://" + imagePath, size, imageOptions, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                new SetFilterTask(filter).execute(loadedImage);
            }
        });
        mCurrentFilter = filter;
    }

    private class SetFilterTask extends AsyncTask<Bitmap, Void, Void> {
        GPUImageFilter filter;
        GPUImage gpu;

        public SetFilterTask(GPUImageFilter filter) {
            this.filter = filter;
            this.gpu = new GPUImage(getActivity());
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap image = bitmaps[0];
            gpu.setImage(image);
            gpu.setFilter(filter);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPhotoFragment.setFilterImage(gpu.getBitmapWithFilterApplied());
        }
    }

    // TODO 필터 뿐 아니라 다른 변경에도 대응해야. ToolDetailEditorPhotoFragment에도 해당.
    public void onPhotoEditFinish(boolean isApply) {
        removeLowerView();
        currentBtnId = 0;

        // Finished by back button
        if (!isApply) {
            getChildFragmentManager().beginTransaction().remove(mPhotoFragment).commit();
            return;
        }

        // Finished by complete button
        Bitmap resultImage = mPhotoFragment.getImage();

        String fileName = UUID.randomUUID().toString() + ".png";
        String dir = getActivity().getFilesDir().toString();
        File file = new File(dir, fileName);
        OutputStream out = null;
        boolean success = false;
        try {
            out = new FileOutputStream(file);
            success = resultImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        if (success) {
            Recipe.getScheme().setEditedPhotoPath(getCurrentItem(), file.getAbsolutePath());
        }

        getChildFragmentManager().beginTransaction().remove(mPhotoFragment).commit();

    }


//    private int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int width = options.outWidth;
//        final int height = options.outHeight;
//        int inSampleSize = 1;
//
//        if (width > reqWidth || height > reqHeight) {
//            final int halfWidth = width / 2;
//            final int halfHeight = height / 2;
//
//            while(halfWidth / inSampleSize > reqWidth && halfHeight / inSampleSize > reqHeight) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }
//
//    private Bitmap decodeBitmapFromUri(String url, int reqWidth, int reqHeight) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(url, options);
//        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(url, options);
//
//
//    }
    
    private int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    private View getPageChildView(String viewType) {
        return mViewPager.findViewWithTag(viewType + getCurrentItem());
    }



    @Override
    public void onDestroyView() {
//        Animation animSlideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
//        animSlideOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mBottomBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//
//        mBottomBar.startAnimation(animSlideOut);
        mAnimSlideIn.setAnimationListener(null);
        mAnimSlideIn = null;
//        animSlideOut.setAnimationListener(null);
//        animSlideOut = null;
        mAnimFadeIn.setAnimationListener(null);
        mAnimFadeIn = null;

        try {getView().getBackground().setCallback(null);} catch(NullPointerException e) { e.printStackTrace(); }
        getView().setBackground(null);
        mViewPager.setAdapter(null);
        mAdapter = null;
        super.onDestroyView();

    }


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
        root.setOnTouchListener(null);
        root.setOnClickListener(null);
        root = null;
    }



    //onBackPressed or Toolbar 왼쪽 썸네일 버튼 누르면 데이터 Intent로 보내면서 다시 ToolFragment로 가기
    // Toolbar 오른쪽 완료버튼 누르면 바로 레시피 작성 완성 미리보기 Fragment로 가기


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tool_detail_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.toolbar_tool_detail_editor_thumbnail:
                getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
