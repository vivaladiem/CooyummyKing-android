package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coo.y2.cooyummyking.MyWraperableGridLayoutManager;
import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.adapter.MyRecyclerAdapter;
import com.coo.y2.cooyummyking.view.VerticalViewPager;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-04.
 */
public class ToolFragment extends Fragment {

    ArrayList<View> mIvs = new ArrayList<>();

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
        initViewPager(v);
        return v;
    }

    private void initViewPager(View v) {
//        fr.castorflex.android.verticalviewpager.VerticalViewPager mViewPager = (fr.castorflex.android.verticalviewpager.VerticalViewPager) v.findViewById(R.id.tool_viewpager);
        VerticalViewPager mViewPager = (VerticalViewPager) v.findViewById(R.id.tool_viewpager);
        mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
//        mViewPager.setPageMargin(0);
        PagerAdapter mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ToolPageFragment.create(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*
        for (View v : mIvs) {
            v.getBackground().setCallback(null);
            v.setBackground(null);
        }
        */
    }
}
