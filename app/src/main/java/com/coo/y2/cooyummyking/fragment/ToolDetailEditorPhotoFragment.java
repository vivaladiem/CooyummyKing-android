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
import com.coo.y2.cooyummyking.entity.RecipeDesign;
import com.coo.y2.cooyummyking.listener.OnBackPressedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Y2 on 2015-05-30.
 */
public class ToolDetailEditorPhotoFragment extends Fragment implements View.OnTouchListener, OnBackPressedListener{
    private ImageView mImageView;
    private Bitmap mOriginalImage; // original, filteredimage 주고받는게 복잡하여 recycle 또는 메모리 유출에 관련된 문제가 발생할 수 있음.
    private Bitmap mFilteredImage; // 문제가능성은 다 잡긴 한것같은데, 성능 손해 안보는 선에서 디자인패턴 적용해 구조 정리하는것도 좋음.
    private int mCurrentItemIndex;

    private Menu menu;

    public static ToolDetailEditorPhotoFragment newInstance(int currentItemIndex, Bitmap image) {
        ToolDetailEditorPhotoFragment fragment = new ToolDetailEditorPhotoFragment();
        fragment.mOriginalImage = image;
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
        mImageView.setImageBitmap(mOriginalImage); // Set previous image for smooth UI


        // If save process hasn't finished - use last edited one without load new.
        if (!((ToolDetailEditorFragment)getParentFragment()).isInSaveProcess) {

            // Set bigger image - could be used for save
            ImageSize size = new ImageSize(640, 640);
            ImageLoader.getInstance().loadImage("file://" + RecipeDesign.getDesign().getImagePath(mCurrentItemIndex), size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mOriginalImage = loadedImage;
                    mImageView.setImageBitmap(loadedImage);
                }
            });
        }

        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).setOnFragmentBackPressedListener(this);
        return mImageView;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public Bitmap getFilterImage() {
        return mFilteredImage;
    }

    public void setFilterImage(Bitmap image) {
        if ((mFilteredImage = image) == null) {
            mImageView.setImageBitmap(mOriginalImage);
        } else {
            mImageView.setImageBitmap(image);
        }
    }

    /**
     * Should use when save process has finish.
     * For fluent UI, at here use previous image until save process got done. so if use that after save - at there, previous one got recycled -
     * it will be hit by trying to use recycled bitmap error.
     * So this method should be used after save process done and only that case.
     * @param image Image file that last edited and got saved. it's okay to be a small image as it would never be saved.
     */
    public void setOriginalImage(Bitmap image) {
        this.mOriginalImage = image;
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
            ((ToolDetailEditorFragment)getParentFragment()).onFinishPhotoEdit(true);
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
        mImageView.setOnTouchListener(null);
        mImageView = null;
        mOriginalImage = null;
        mFilteredImage = null;

        menu.findItem(R.id.toolbar_tool_detail_editor_thumbnail).setVisible(true);
        ((MainActivity)getActivity()).setOnFragmentBackPressedListener(null);
        super.onDestroyView();
    }

    @Override
    public void onBackPressed() {
        ((ToolDetailEditorFragment)getParentFragment()).onFinishPhotoEdit(false);
    }
}
