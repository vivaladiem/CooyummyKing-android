package com.coo.y2.cooyummykingr.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.coo.y2.cooyummykingr.R;
import com.coo.y2.cooyummykingr.entity.RecipeDesign;
import com.coo.y2.cooyummykingr.util.ExhibitManager;
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


/**
 * Created by Y2 on 2015-05-18.
 * ToolFragment에서 Overview 아아템을 클릭했을 때 실행되는 프래그먼트
 */
public class ToolDetailEditorFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ViewPager mViewPager;
    private FragmentManager fm;
    private ViewPagerAdapter mAdapter;
    private ViewGroup mLowerView;
    private ViewGroup mBottomBar;
    private Animation mAnimSlideIn;
    private Animation mAnimFadeIn = null;
    private RecipeDesign sRecipe = RecipeDesign.getDesign();

    // ------------ Bottom bar ------------ //
    private View mBtnEditPhoto;
    private View mBtnFilter;
    private View mBtnSticker;
    private View mBtnSubtitle;
    private View mBtnText;
    private int currentBtnId; // 현재 선택되어있는 하단바 도구의 Id

    /// -------------- Util --------------- //
    private ToolDetailEditorPhotoFragment mPhotoFragment;

    private final String VIEW_IMAGE = "iv";
    private final String VIEW_TEXT = "ed";

    private Animation mAnimScaleUp;
    private Animation mAnimTrans;
    private Animation mAnimRTrans;
    private Animation mAnimFadeOut;


    // Use in ToolDetailEditorPageFragment
    private DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(200))
            .cacheInMemory(true)
            .build();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tool_detail_editor, container, false);
        initResources(view);
        initAnimation(view);
        initBottomBar();
        setHasOptionsMenu(true);
        return view;
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
            return sRecipe.getStepSize();
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


    /* BottomBar Item Click Listener */
    @Override
    public void onClick(View view) {
        if (currentBtnId == view.getId() && currentBtnId != R.id.tool_detail_bottombar_text) return;

        clearUtilView();

        currentBtnId = view.getId();

        switch(view.getId()) {
            case R.id.tool_detail_bottombar_photo_edit:
                break;

            case R.id.tool_detail_bottombar_filter:
                mBtnFilter.setSelected(true);

                showPhotoEditFragment();


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

    // Clear lower view and button selected state
    private void clearUtilView() {
        recursiveRecycle(mLowerView);
        int count = mBottomBar.getChildCount();
        for (int i = 0; i < count; i++) {
            mBottomBar.getChildAt(i).setSelected(false);
        }
    }

    private void showPhotoEditFragment() {
        if (fm.findFragmentById(R.id.tool_detail_editor_photo_container) != null) return;

        final int i = getCurrentItem();
        // TODO 아직 뷰가 완성이 안되서 image가 없을 때 해결해야.
        // 여기에서 이미지헬퍼 쓰면 Fragment 생성과정에서 저장이 완료되면 문제가 발생하므로 ToolDetailEditorPhotoFragment에서 한다.


        // Set animation
        View cardIv = getPageChildView(VIEW_IMAGE);
        View card = (View) getPageChildView(VIEW_IMAGE).getParent();

        if (mAnimScaleUp == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            float scale = (float) screenWidth / (float) card.getWidth();

            int padding = getResources().getDimensionPixelSize(R.dimen.tool_detail_item_padding);
            mAnimScaleUp = new ScaleAnimation(1, scale, 1, scale,
                    card.getWidth() / 2, cardIv.getHeight() / 2 + padding);
            mAnimScaleUp.setDuration(300);
            mAnimScaleUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Start fragment
                    Bitmap image = ((BitmapDrawable)((ImageView) getPageChildView(VIEW_IMAGE)).getDrawable()).getBitmap(); // 문제가능지점 1. view 미완성 3. 사진로드 아직 안됨// from : 1.load new,
                    mPhotoFragment = ToolDetailEditorPhotoFragment.newInstance(getCurrentItem(), image);
                    fm.beginTransaction()
                            .add(R.id.tool_detail_editor_photo_container, mPhotoFragment)
                            .commit();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        if (mAnimTrans == null) {
            int padding = getResources().getDimensionPixelSize(R.dimen.tool_detail_page_padding);
            mAnimTrans = new TranslateAnimation(1, -padding, 1, 1);
            mAnimTrans.setDuration(300);
        }

        if (mAnimRTrans == null) {
            int padding = getResources().getDimensionPixelSize(R.dimen.tool_detail_page_padding);
            mAnimRTrans = new TranslateAnimation(1, padding, 1, 1);
            mAnimRTrans.setDuration(300);
        }

        if (mAnimFadeOut == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 너무 느림 // 버전이 아니라 CPU 성능 등을 기준으로 할 순 없나..
            mAnimFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
            getPageChildView(VIEW_TEXT).startAnimation(mAnimFadeOut);
        } else
            getPageChildView(VIEW_TEXT).setVisibility(View.INVISIBLE);

        // Start animation
        Bitmap background = Bitmap.createBitmap(ToolFragment.screenImage,
                0, (ToolFragment.screenImage.getHeight() - (mLowerView.getHeight() + getResources().getDimensionPixelSize(R.dimen.bottombar_height))/2),
                ToolFragment.screenImage.getWidth(), mLowerView.getHeight()/2);
        mLowerView.setBackground(new BitmapDrawable(getResources(), background));

        card.startAnimation(mAnimScaleUp);
        if (i > 0) ((View)getPageChildView(VIEW_IMAGE, i - 1).getParent()).startAnimation(mAnimTrans);
        if (i < sRecipe.getStepSize() - 1) ((View)getPageChildView(VIEW_IMAGE, i + 1).getParent()).startAnimation(mAnimRTrans);
    }


//    @Override
//    public void onFilterSelected(final GPUImageFilter filter) {
//
//        // on no filter is selected
//        if (filter == null) {
//            mPhotoFragment.setFilterImage(null);
//            return;
//        }
//
//        // on filter is selected
//        String imagePath = "file://" + sRecipe.getImagePath(getCurrentItem());
//
//        // 여기에서 매번 불필요하게 새로 로딩하니 느려진다.. 캐시에 저장하면 메모리가 조금 아깝고, 확인을 하자니 애매한데.
//        // 확인법? Tool~PhotoFragment에서 이미지 가져와서 size w, h 다 640 넘는지 확인하는 임시변통 - 이미지가 원래 작으면 안됨...
//        // 일단은 캐시적용
//        ImageLoader.getInstance().loadImage(imagePath, imageOptions, listener.getListener(imagePath, filter));
//    }
//
//
//    ArgumentImageLoadingListener listener = new ArgumentImageLoadingListener() {
//
//        @Override
//        public ArgumentImageLoadingListener getListener(String uriKey, Object... args) {
//            setProperty(uriKey, "filter", args[0]);
//            return this;
//        }
//
//        @Override
//        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            super.onLoadingComplete(imageUri, view, loadedImage);
//            new SetFilterTask((GPUImageFilter) removeProperty("filter")).execute(loadedImage);
//        }
//    };
//
//
//    private class SetFilterTask extends AsyncTask<Bitmap, Void, Void> {
//        GPUImageFilter filter;
//        GPUImage gpu = new GPUImage(getActivity()); // 전역변수로 두고 계속 쓰면 메모리유출 심함. deleteImage, setFilter(null)을 해도..
//
//        public SetFilterTask(GPUImageFilter filter) {
//            this.filter = filter;
//        }
//
//        @Override
//        protected Void doInBackground(Bitmap... bitmaps) {
//            Bitmap image = bitmaps[0];
//            gpu.setImage(image);
//            gpu.setFilter(filter);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mPhotoFragment.setFilterImage(gpu.getBitmapWithFilterApplied());//에러발생구역. 필터를 빨리 바꿀땐 괜찮은데 저장중에 변경을 어찌어찌 하다가 발생. gpu getBitmap~가 없어서 생긴듯?
//        }
//    }



    // TODO 필터 뿐 아니라 다른 변경에도 대응해야. ToolDetailEditorPhotoFragment에도 해당.
    public void onFinishPhotoEdit(boolean isApply) {
        clearUtilView();
        currentBtnId = 0;
        getPageChildView(VIEW_TEXT).setVisibility(View.VISIBLE);

        /* Finished by back button*/
        if (!isApply) {
            finishPhotoFragment();
            return;
        }

        /* Finished by complete button */
        // If complete with original image - just finish edit.
        Bitmap resultImage = mPhotoFragment.getFilterImage();
        if (resultImage == null) {
            finishPhotoFragment();
            return;
        }

        // TODO 아무래도 resultImage가 recycle 되어 생기는 오류같은데, 왜 그렇게 되지.. 순서상 불가능하지않나 / 일단 resultImage(==아래의 image)에서 에러나는건 맞음.
        if(resultImage.isRecycled()) {
            Log.i("CYMK", "resultImage is recycled!");
        }
        // If complete with filtered image - save file, add path to recipe design
        ((ImageView)getPageChildView(VIEW_IMAGE)).setImageBitmap(resultImage); //resultImage == mFilteredImage; // do for UI
        finishPhotoFragment();

        new SaveEditedImageTask(resultImage, getCurrentItem()).execute();
    }



    private void finishPhotoFragment() {
        getChildFragmentManager().beginTransaction().remove(mPhotoFragment).commit();
    }


    private final class SaveEditedImageTask extends AsyncTask<Void, Void, Boolean> {
        private int index;
        private Bitmap image; // == mFilteredImage. should recycle
        private String filePath;
        private File file;

        public SaveEditedImageTask(Bitmap image, int index) {
            this.image = image;
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ExhibitManager.getExhibit(index).startExhibit(image);
            sRecipe.isChanged = true; // TODO notify에 이어 또 남은 문제는 사진편집만 했을 때 isChanged 바꾸는 타이밍 문제인데.. 먼저 하자니 실패되면 저장할게 없는데..
            // 그래도 작동 자체를 따지자면 앞에서 해두는게 맞긴 하다. 똑같은걸 저장해도 문제가 되진 않으니.

            filePath = sRecipe.getImagePath(index, RecipeDesign.IMAGE_EDITED);
            if (filePath == null) filePath = sRecipe.getFilesDir(getActivity()) + File.separator + UUID.randomUUID() + ".jpg";
            file = new File(filePath);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OutputStream out = null;
            boolean success = false;
            try {
//                Log.i("CYMK", "create new file : " + file.createNewFile()); // 없어도 됨. dir만 존재하면.
                out = new FileOutputStream(file);
                success = image.compress(Bitmap.CompressFormat.JPEG, 75, out); // TODO recycle 에러 발생
                if (success) {
                    sRecipe.addEditedImage(index, file.getAbsolutePath()); // 임시 데이터에 포함되야하므로 여기에서 처리한다.
                    Log.i("CYMK", "Edited image of " + index + " save success : " + file.getAbsolutePath());
                }
                ExhibitManager.getExhibit(index).notifyCloseTime();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (!success) {
                String msg = getResources().getString(R.string.err_fail_edit_image);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }

            // 기존에 작업한 이미지를 반환하고 새로 가져와 할당합니다. - only for display. not for save
            String path = sRecipe.getImagePath(index);
            ImageSize size = new ImageSize(480, 480); // 저장용 이미지가 아니므로 작은걸 불러온다.
            ImageLoader.getInstance().loadImage("file://" + path, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);

                    ImageView pageView = (ImageView) getPageChildView(VIEW_IMAGE, index);
                    if (pageView != null) pageView.setImageBitmap(loadedImage);

                    if (index == getCurrentItem()) mPhotoFragment.setOriginalImage(loadedImage); // 이걸 실행하기 전에는 recycle하면안됨.
                    ExhibitManager.getExhibit(index).finishExhibit();
                }
            });

        }
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

    private View getPageChildView(String viewType, int index) {
        return mViewPager.findViewWithTag(viewType + index);
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
        mAnimFadeIn = null;

        recursiveRecycle(view);
        mViewPager.setAdapter(null);
        mAdapter = null;
        view = null;
        super.onDestroyView();
    }

    // 하나로 관리하면서 필요에 따라 추가기능을 넣으려면 어떻게 하지? 빌더를 쓰자니 너무 거창하고.. 사실상 맨 밑 리스너부분 빼고는 다 공통사항 -> 리스너는 따로 해주든가,
    @SuppressWarnings("unchecked")
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
                ((AdapterView)group).setAdapter(null);
            }
        }
        if (root instanceof ImageView) {
            Drawable drawable;
            ImageView iv = (ImageView) root;
            if ((drawable = iv.getDrawable()) == null) return;
            drawable.setCallback(null);
            iv.setImageDrawable(null);
//            iv = null;
//            drawable = null;
        }
        root.setOnTouchListener(null);
        root.setOnClickListener(null);
//        root = null;
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
