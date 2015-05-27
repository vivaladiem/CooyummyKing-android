package com.coo.y2.cooyummyking.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.fragment.MainFragment;
import com.coo.y2.cooyummyking.fragment.ToolFragment;
import com.coo.y2.cooyummyking.util.RecipeSerializer;


public class MainActivity extends AppCompatActivity {
    //    private Toolbar mToolbar; // 툴바는 액션바를 대체하는 신규기능 / To local
    public static View sBottomBar;
    public static ImageView sIvBtnList;
    public static ImageView sIvBtnTool;
    public static ImageView sIvBtnMypage;
    private DrawerLayout mDrawerLayout;
    //    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
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

    /*
        private void addDrawerItems() {
            mDrawerList = (ListView) findViewById(R.id.ham_menu);
            int[] arrays = {R.drawable.ham_btn_notify_off, R.drawable.ham_btn_battle}
            mAdapter = new ArrayAdapter<> (this, android.R.layout.simple_gallery_item, arrays);
            mDrawerList.setAdapter(mAdapter);
        }
    */

    @Override
    protected void onPause() {
        super.onPause();

        // Temp Recipe Save
        // ToolFragment가 이 Activity 위에 있으므로 생명주기상 여기에서 작성중인 레시피 저장을 한다.

        if (!Recipe.isChanged) return; // if recipe has no change(include didn't start making one)
        RecipeSerializer serializer = new RecipeSerializer(this);
        try {
            serializer.saveTempData(Recipe.getScheme());
            Recipe.isChanged = false; // code for onPause is called by starting other Activity
            Log.i("CYMK", "Temp recipe data saved");
        } catch(Exception e) {
            Log.i("CYMK", "Failed to save recipe temp data");
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
    long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
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
}
