package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.listener.OnBackPressedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Y2 on 2015-05-30.
 */
public class ToolDetailEditorPhotoFragment extends Fragment implements View.OnTouchListener, OnBackPressedListener{
    private ImageView mImageView;
    private Bitmap mOriginalImage;
    private Bitmap mFilteredImage;
    private int mCurrentItemIndex;

    private Menu menu;

    public static ToolDetailEditorPhotoFragment newInstance(int currentItemIndex) {
        ToolDetailEditorPhotoFragment fragment = new ToolDetailEditorPhotoFragment();
        fragment.mCurrentItemIndex = currentItemIndex;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mImageView.setLayoutParams(params);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setClickable(true);
        mImageView.setOnTouchListener(this);

        // Init initial image
        ImageSize size = new ImageSize(640, 640);
        ImageLoader.getInstance().loadImage("file://" + Recipe.getScheme().getImagePath(mCurrentItemIndex), size, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mOriginalImage = loadedImage;
                mImageView.setImageBitmap(loadedImage);
            }
        });

        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).setOnFragmentBackPressedListener(this);
        return mImageView;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public Bitmap getImage() {
        return mFilteredImage == null ? mOriginalImage : mFilteredImage;
    }

    public void setFilterImage(Bitmap image) {
        if ((mFilteredImage = image) == null) {
            mImageView.setImageBitmap(mOriginalImage);
        } else {
            mImageView.setImageBitmap(image);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mImageView.setImageBitmap(mOriginalImage);
                break;
            case MotionEvent.ACTION_UP:
                if (mFilteredImage == null)
                    break;
                mImageView.setImageBitmap(mFilteredImage);
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        menu.findItem(R.id.toolbar_tool_detail_editor_thumbnail).setVisible(false);
        inflater.inflate(R.menu.menu_tool_detail_editor_photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_tool_detail_editor_photo_complete) {
            ((ToolDetailEditorFragment)getParentFragment()).onPhotoEditFinish(true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        Drawable drawable;
        if ((drawable = mImageView.getDrawable()) != null) {
            drawable.setCallback(null);
            mImageView.setImageBitmap(null);
        }
        mImageView = null;
        mOriginalImage = null;
        mFilteredImage = null;

        menu.findItem(R.id.toolbar_tool_detail_editor_thumbnail).setVisible(true);
        ((MainActivity)getActivity()).setOnFragmentBackPressedListener(null);
        super.onDestroyView();
    }

    @Override
    public void onBackPressed() {
        ((ToolDetailEditorFragment)getParentFragment()).onPhotoEditFinish(false);
    }
}
