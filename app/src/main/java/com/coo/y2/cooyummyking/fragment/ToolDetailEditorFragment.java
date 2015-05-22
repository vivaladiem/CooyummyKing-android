package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Y2 on 2015-05-18.
 */
public class ToolDetailEditorFragment extends Fragment {
    public static final int INTENT_REQUESTCODE = 0;

    private ViewPager mViewPager;
    private View mBottomBar;
    private Animation mAnimSlideIn;
    private Animation mAnimFadeIn;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tool_detail_editor, container, false);
        initResources(v);
        initAnimation(v);
        return v;
    }

    private void initResources(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.tool_detail_editor_viewPager);
        mViewPager.setPageMargin(20);
        v.setBackground(new BitmapDrawable(getResources(), ToolFragment.screenImage));
        FragmentManager fm = getChildFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return ToolDetailEditorPageFragment.newInstance(position, options);
            }

            @Override
            public int getCount() {
                return Recipe.imagePaths.size();
            }
        });

        int position = getArguments().getInt("position");
        mViewPager.setCurrentItem(position);
    }

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

        mBottomBar = v.findViewById(R.id.tool_detail_editor_bottombar);
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
        super.onDestroyView();

    }

    //onBackPressed or Toolbar 왼쪽 썸네일 버튼 누르면 데이터 Intent로 보내면서 다시 ToolFragment로 가기
    // Toolbar 오른쪽 완료버튼 누르면 바로 레시피 작성 완성 미리보기 Fragment로 가기
}
