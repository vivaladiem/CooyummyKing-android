package com.coo.y2.cooyummyking.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.adapter.GalleryCursorAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-13.
 * ToolFragment에서 앨범에서 사진 가져오기를 할 때 불려지는 Activity.
 * 프래그먼트 없이 액티비티에서 모두 처리한다.
 * TODO 앨범별로 고를 수 있으면 더 좋겠음.(인스타그램의 경우 앨범없이 쭉 나오긴 한다)
 */
public class GalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView mRecyclerView;
    GalleryCursorAdapter mAdapter;
    Cursor mCursor;

    public static final String EXTRA_SELECTED_ITEMS = "com.coo.y2.cooyummyking.R.activity.GalleryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        initToolbar();
        initRecyclerView();

        getSupportLoaderManager().initLoader(1, null, this);
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.gallery_gridview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int imageSideLength = dm.widthPixels / 3;

        mAdapter = new GalleryCursorAdapter(this, null, imageSideLength);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_gallery); // 프래그먼트가 있다면 프래그먼트의 onCreateOptionsMenu 에서도 인플레이트 할 수 있음. 액티비티에서도 아마 되겠지.
//        toolbar.setOnMenuItemClickListener(new ToolbarMenuListener());
        setSupportActionBar(toolbar);
        try {getSupportActionBar().setDisplayShowTitleEnabled(false);} catch(Exception e) {e.printStackTrace();}
    }

    // ----------------------------------------- Loader Setting ----------------------------------------- //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
//        final String[] columns = {MediaStore.Images.Thumbnails.DATA
//                , MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails._ID};
//        final String where = MediaStore.Images.Thumbnails.KIND + "=" + MediaStore.Images.Thumbnails.MINI_KIND;

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        mAdapter.swapCursor(data);
        mCursor = data;
    }

    // ---------------------------------------- Menu Setting ---------------------------------------------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.gallery));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.toolbar_gallery_complete:
                ArrayList<String> selectedItemUrls = mAdapter.getSelectedItemPaths();
                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra(EXTRA_SELECTED_ITEMS, selectedItemUrls);
                setResult(RESULT_OK, returnIntent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // 뷰홀더때문인지 가끔 getChildAt(0)에서 Unable to destroy activity : NullPointerException 일어남.
        try {
            ViewGroup vg;
            ImageView iv;
            int count = mRecyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                vg = (ViewGroup) mRecyclerView.getChildAt(i);
                if (!(vg.getChildAt(0) instanceof ImageView)) return;
                iv = (ImageView) vg.getChildAt(0);
                iv.getDrawable().setCallback(null);
                iv.setImageBitmap(null);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        mCursor.close();
        ImageLoader.getInstance().clearMemoryCache();
        super.onDestroy();

        // 리스너는 Activity에 귀속되므로 어차피 끊길테니 놔둬도 됨.
        // 다만 Bitmap은 언제나 안끊김ㅁㅁㅁㅁ.. 그러니 비트맵만 정리하자.
        // 아 놔 또 정리 안되는거같다 왜 메모리가 그대로지
    }
}
