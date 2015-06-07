package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.coo.y2.cooyummyking.widget.SquareImageView_byWidth;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Y2 on 2015-05-30.
 */
public class ToolDetailEditorPhotoFragment extends Fragment implements View.OnTouchListener, OnBackPressedListener{
    private SquareImageView_byWidth mImageView;
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
        mImageView = new SquareImageView_byWidth(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mImageView.setBackgroundColor(Color.WHITE);
        mImageView.setLayoutParams(params);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setClickable(true);
        mImageView.setOnTouchListener(this);


//        if (ExhibitManager.getReplica(mCurrentItemIndex) == null) { // If save process is done.
//            // Set bigger image - could be used for save
////            ImageSize size = new ImageSize(640, 640);
//            ImageLoader.getInstance().loadImage("file://" + RecipeDesign.getDesign().getImagePath(mCurrentItemIndex), new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    super.onLoadingComplete(imageUri, view, loadedImage);
//                    mOriginalImage = loadedImage;
//                    mImageView.setImageBitmap(loadedImage);
//                }
//            });
//        } else {
//            mImageView.setImageBitmap(mOriginalImage); // Set last edited image for smooth UI
//        }
        // 저장중이 아니어도 쓸 수 있느냐 없느냐가 갈리므로 isRecycle을 사용해 처리한다.
        //      (처음부터 저장중인게 없어서 저장중이 아닌경우 or 저장이 끝나서 아닌경우)
        //      괜히 다른데선 쓸 일도 없는 '시작을 안했는지 체크하기' 기능보다 이렇게 하면 간단함.
        if (mOriginalImage.isRecycled()) {
            ImageLoader.getInstance().loadImage("file://" + RecipeDesign.getDesign().getImagePath(mCurrentItemIndex), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mOriginalImage = loadedImage;
                    mImageView.setImageBitmap(loadedImage);
                }
            });
        } else {
            mImageView.setImageBitmap(mOriginalImage);
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

    public synchronized void setFilterImage(Bitmap image) {
        if (mImageView == null) return;
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

//    Matrix matrix = new Matrix();
//    Point point = new Point();
//    Point currentPoint = new Point();
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mImageView.setImageBitmap(mOriginalImage);
//                point.set((int) motionEvent.getX(), (int) motionEvent.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (mFilteredImage == null)
                    break;
                mImageView.setImageBitmap(mFilteredImage);
                break;
            case MotionEvent.ACTION_MOVE:
//                currentPoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
//                matrix.postTranslate(currentPoint.x - point.x, currentPoint.y - point.y);
//                mImageView.setImageMatrix(matrix);
//                point.set(currentPoint.x, currentPoint.y);
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
