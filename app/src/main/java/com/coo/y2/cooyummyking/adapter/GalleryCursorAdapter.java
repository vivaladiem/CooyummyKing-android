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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-13.
 */
public class GalleryCursorAdapter extends CursorRecyclerViewAdapter<GalleryCursorAdapter.ViewHolder> {
    private Context mContext;
    // TODO 디스크캐시가 원본 그대로 저장되서 너무 크다. denyMultipleSizeCache어쩌구를 하면 줄인것으로 저장되야하는데 안되네.. 해결해야.
    private final DisplayImageOptions mOptions =
            new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.detail_recipe_card_background)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .build();


    //    ArrayList<TextView> mLabels = new ArrayList<>(); // 선택여부 및 순서 라벨 텍스트뷰
    ArrayList<Integer> mSelectedItemPosition = new ArrayList<>(); // 선택한 이미지의 position
    ArrayList<String> mSelectedItemUrl = new ArrayList<>();

    int mPosition;
    int mDataColumnIndex;
    String mUrl;

    GalleryOnClickListener mGalleryOnClickListener = new GalleryOnClickListener();


    public GalleryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
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
        // Static으로 접근할 수 없는 변수를 사용하는 처리를 합니다.
        holder.mViewGroup.setOnClickListener(mGalleryOnClickListener);
        return holder;
    }

    // TODO 검은화면 안나오고 계속 이미지 이어지도록(미리 앞뒤페이지를 로딩한다거나) 하려면 어떻게 하지?
    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        mPosition = cursor.getPosition();
        if (holder.mViewGroup.getTag() != mPosition) { // notify에 대비한 조건절
            mDataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            mUrl = cursor.getString(mDataColumnIndex);
            ImageLoader.getInstance().displayImage("file://" + mUrl, holder.mImageView, mOptions);

//            holder.mViewGroup.setOnClickListener(mGalleryOnClickListener);
            holder.mViewGroup.setTag(mPosition);
        }

        // ------------------------ Set Label After NotifyDataSetChanged Called ------------------------- //
        // Seems little inefficient.. anyway first success. so hard to solve viewholder's confusing with other page's item.
        // TODO setSelected(LayoutParams) Could be better way. but I don't know how can I set order. maybe later.
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
                mSelectedItemUrl.remove(index); // 'add' is called in onBindViewHolder
//                mLabels.remove(index);
//                int count = mSelectedItemPosition.size();
//                for (int i = index; i < count; i++) {
//                    mLabels.get(i).setText(String.valueOf(i + 1));
//                }

                notifyItemChanged(position); // 삭제된 아이템도 notify 해줘야.

            } else {
                mSelectedItemPosition.add(position);
                getCursor().moveToPosition(position);
                mSelectedItemUrl.add(getCursor().getString(mDataColumnIndex));
//                mLabels.add(tv);
            }

            for (int changedItemPosition : mSelectedItemPosition) { //선택된 아이템들에 대한 notify
                notifyItemChanged(changedItemPosition);
            }
//            notifyDataSetChanged(); 메모리 엄청 잡아먹음. 절대 이런데 쓰면 안되겠다.

        }
    }

    public ArrayList<String> getSelectedItemUrl() {
        return mSelectedItemUrl;
    }


//     //뷰를 새로 추가하는 방식인데 아래방식이랑 이거랑 뭐가 더 효율적인지는 모르겠다..
//    private final class GalleryOnClickListener1 implements View.OnClickListener {
//        ViewHolder holder;
//        int position;
//
//        GalleryOnClickListener1(ViewHolder holder) {
//            this.holder = holder;
//        }
//
//        @Override
//        public void onClick(View view) {
//            position = (int) view.getTag();
//
//            int index;
//
//            if ((index = mSelectedItemPosition.indexOf(position)) != -1) { // When the item is already selected
//                /*
//                mSelectedItemPosition.remove(index);
//
//                ((ViewGroup)view.getParent()).removeViewAt(1); // 확실하게 하려면 removeView(holder.get(index));
//                mLabels.remove(index);
//
//                int count = mLabels.size();
//                for (int i = index; i < count; i++) {
//                    mLabels.get(i).setText(String.valueOf(i + 1));
//                }
//                */
//
//                holder.mTextView.setText(null);
//                holder.mTextView.setVisibility(View.GONE);
//                mSelectedItemPosition.remove(index);
//                mLabels.remove(index);
//                int count = mSelectedItemPosition.size();
//                for (int i = index; i < count; i++) {
//                    mLabels.get(i).setText(String.valueOf(i + 1));
//                }
//
//                return;
//            }
//
//
//            mSelectedItemPosition.add(position);
//
//            // set label
////            TextView label = (TextView) LayoutInflater.from(mContext).inflate(R.layout.gallery_item_label, (ViewGroup)view.getParent(), true).findViewById(R.id.gallery_item_label);
//
////            mLabels.add(label);
////            label.setText(String.valueOf(mLabels.size()));
//
//            holder.mTextView.setVisibility(View.VISIBLE);
//            holder.mTextView.setText(String.valueOf(mSelectedItemPosition.size()));
//
//            mLabels.add(holder.mTextView);
//
//        }
//    }
}