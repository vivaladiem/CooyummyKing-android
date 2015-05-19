package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-13.
 */
public class GalleryCursorAdapter extends CursorRecyclerViewAdapter<GalleryCursorAdapter.ViewHolder> {
    private Context mContext;

    private final DisplayImageOptions mOptions =
            new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .cacheOnDisk(true)
                    .build();
    int mImageSideLength; // As UIL doesn't know exact size when the size is match_parent or wrap_content, manually set the value.

    ArrayList<Integer> mSelectedItemPosition = new ArrayList<>();
    ArrayList<String> mSelectedItemUrl = new ArrayList<>();

    int mPosition;

    int mDataColumnIndex;
    String mUrl;

    GalleryOnClickListener mGalleryOnClickListener = new GalleryOnClickListener();

    public GalleryCursorAdapter(Context context, Cursor cursor, int imageSideLength) {
        super(context, cursor);
        mContext = context;
        mImageSideLength = imageSideLength;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewGroup mViewGroup;
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mViewGroup = (ViewGroup) v;
            mImageView = (ImageView) v.findViewById(R.id.gallery_item_image);
            mTextView = (TextView) v.findViewById(R.id.gallery_item_label);
        }
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);
        ViewHolder holder = new ViewHolder(v);

        // 뷰홀더에 static으로 접근할 수 없는 변수를 사용하는 처리를 합니다.
        holder.mViewGroup.setOnClickListener(mGalleryOnClickListener);
        ViewGroup.LayoutParams lp = holder.mImageView.getLayoutParams();
        lp.width = mImageSideLength;
        lp.height = mImageSideLength;
        return holder;
    }


    // UI Related things SHOULD be handled only in here.
    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        mPosition = cursor.getPosition();
        if (holder.mViewGroup.getTag() != mPosition) { // Prevent repeat on notify from onClick
//            mDataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            mDataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            mUrl = cursor.getString(mDataColumnIndex);

            ImageLoader.getInstance().displayImage("file://" + mUrl, holder.mImageView, mOptions);

            holder.mViewGroup.setTag(mPosition);
        }

        // ------------------------ Set Label After NotifyDataSetChanged is Called ------------------------- //
        int index;
        if ((index = mSelectedItemPosition.indexOf(mPosition)) != -1) {
            holder.mTextView.setVisibility(View.VISIBLE);
            holder.mTextView.setText(String.valueOf(index + 1));
        } else {
            holder.mTextView.setVisibility(View.GONE);
        }
    }

    // TODO 나중엔 설정란이 있어서 지정순서대로 or 무조건 먼저 촬영한 사진 순으로 선택되게 해도 좋겠다. 라벨 디자인도 다르게 해서 미세재미
    // 사실 only 먼저촬영한 순으로만 되게 하는것도 좋겠지만 일단은 처음에 생각한 아이디어대로 해보자..
    private final class GalleryOnClickListener implements View.OnClickListener {
        ViewGroup vg;
        TextView tv;
        int position;

        @Override
        public void onClick(View view) {
            position = (int) view.getTag();
            vg = (ViewGroup) view;
            tv = (TextView) vg.getChildAt(1);

            int index;
            if ((index = mSelectedItemPosition.indexOf(position)) != -1) { // When the item is already selected
                mSelectedItemPosition.remove(index);
                mSelectedItemUrl.remove(index);

                notifyItemChanged(position); // 삭제된 아이템도 notify 해줘야.

            } else {
                mSelectedItemPosition.add(position);
                getCursor().moveToPosition(position);
                mSelectedItemUrl.add(getCursor().getString(mDataColumnIndex));
            }

            for (int changedItemPosition : mSelectedItemPosition) { //선택된 아이템들에 대한 notify
                notifyItemChanged(changedItemPosition);
            }

        }
    }

    public ArrayList<String> getSelectedItemUrl() {
        return mSelectedItemUrl;
    }
}