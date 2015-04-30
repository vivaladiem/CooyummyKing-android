package com.coo.y2.cooyummyking.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.fragment.MainFragment;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar; // 툴바는 액션바를 대체하는 신규기능
    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initFragment();
        initToolbar();
        initBottomTab();
//        addDrawerItems();
    }
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.ham_open, R.string.ham_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initBottomTab() {
        final ImageView mIvBtnList = (ImageView) findViewById(R.id.bottom_tab_list);
        final ImageView mIvBtnTool = (ImageView) findViewById(R.id.bottom_tab_tool);
        final ImageView mIvBtnMypage = (ImageView) findViewById(R.id.bottom_tab_mypage);
        mIvBtnList.setSelected(true);
        mIvBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
                v.setSelected(true);
                Fragment fragment = new MainFragment();
                replaceFragment(fragment);
                mIvBtnTool.setSelected(false);
                mIvBtnMypage.setSelected(false);
            }
        });
        mIvBtnTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
                v.setSelected(true);
                mIvBtnList.setSelected(false);
                mIvBtnMypage.setSelected(false);
            }
        });
        mIvBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) return;
                v.setSelected(true);
                mIvBtnList.setSelected(false);
                mIvBtnTool.setSelected(false);
            }
        });

    }

    private void replaceFragment (Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
    /*
        private void addDrawerItems() {
            mDrawerList = (ListView) findViewById(R.id.ham_menu);
            int[] arrays = {R.drawable.ham_btn_notify_off, R.drawable.ham_btn_battle}
            mAdapter = new ArrayAdapter<> (this, android.R.layout.simple_gallery_item, arrays);
            mDrawerList.setAdapter(mAdapter);
        }
    */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        mToolbar.setNavigationIcon(R.drawable.actionbar_btn_hammenu); // 일반 정적 아이콘을 toggle로 사용하기. 애니메이션이 없어져서 drawerArrayToggle을 커스텀해서 사용중.(styles.xml)
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
