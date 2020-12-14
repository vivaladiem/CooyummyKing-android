package com.coo.y2.cooyummykingr.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coo.y2.cooyummykingr.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-13.
 */
public class GalleryCursorAdapter extends CursorRecyclerViewAdapter<GalleryCursorAdapter.ViewHolder> {
    private final Context mContext;

    private final DisplayImageOptions mOptions;

    private int mImageSideLength; // As UIL doesn't know exact size when the size is match_parent or wrap_content, manually set the value.

    private ArrayList<Integer> mSelectedItemPosition = new ArrayList<>();
    private ArrayList<String> mSelectedItemPaths = new ArrayList<>();

    private Integer mPosition;
    private int mDataColumnIndex;
    private String mUrl;

    private GalleryOnClickListener mGalleryOnClickListener = new GalleryOnClickListener();

    public GalleryCursorAdapter(Context context, Cursor cursor, int imageSideLength) {
        super(context, cursor);
        mContext = context;
        mImageSideLength = imageSideLength;
        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(android.R.color.black)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                        // EXACTLY로 설정하니까 이걸 할 필요가 별로 없다. 어차피 320정도 크기로 가져오는데 크기는 조금 더 커도 속도는 좀 더 빠른듯.
//                .preProcessor(new BitmapProcessor() {
//                    @Override
//                    public Bitmap process(Bitmap bitmap) {
//                        return ThumbnailUtils.extractThumbnail(bitmap, mImageSideLength, mImageSideLength);
//                    }
//                })
                .build();
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


    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        mPosition = cursor.getPosition();
        if (!mPosition.equals(holder.mViewGroup.getTag())) { // Prevent repeat on notify from onClick
//        if (holder.mViewGroup.getTag() != mPosition) { // Prevent repeat on notify from onClick / 어떻게된건지 127까지만 성립함.. 그후로는 무조건 false
            mDataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            mUrl = cursor.getString(mDataColumnIndex);

            // 썸네일(MediaStore.Images.Thumbnail)은 Exif가 날라가서 방향이 제멋대로인 경우가 많으며, 가끔 파일이 없는 경우도 있어서 곤란.
            // 그렇지만 썸네일을 쓰면 원본파일보다 속도가 비교할 수 없이 빠름.
            // 원본을 사용하는 방법 중 ThumbnailUtils보다 EXACTLY로 줄여서 가져오는게 더 나은데, 그래도 속도가 느림.
            // 그러니까 원 사진을 크기를 줄여서 불러와서 캐시 등에 저장을 해놓고 다음부턴 그 파일을 사용하는 식으로 하면 좋을듯.
            // 캐시파일명은 _ID와 똑같이 하면 됨. 그래서 id를 먼저 뽑아와서 캐시를 찾아보고 없으면 data컬럼 뽑아와서 진행하면 됨.
            // 커서는 똑같이 적용, Thumbnail을 찾아봄. 있으면 백그라운드쓰레드로 적용, 없으면 생성 후 적용.
            // 그러다보니 여기에선 UIL은 안쓰일 것.
            // 일단은 그냥 쓰고 나중에.. 지금은 매번 새로 resize하는거니까 비효율적이긴 한데 다른기능 개발이 시급함..
            // TODO UIL쓰지말고, 1. (Thread로) ThumbnailUtils로 썸네일로 만들어서 2. 디스크캐시에 저장 3. Thread로 ImageView에 나타내기. + Thread Pool 사용.
            //
            // * 불러온 이미지를 직접 디스크캐시에 넣으려고 했더니
            // 어디가 문제인지 모르겠지만 recycle 된 이미지를 사용한다는 에러가 난다.. 이미 뷰에 들어가서 상관이 없을텐데도..
            // 나중에 분석해봐서 해결되면 디스크캐시에 넣어 쓰면 됨.
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


    // 나중엔 설정란이 있어서 지정순서대로 or 무조건 먼저 촬영한 사진 순으로 선택되게 해도 좋겠다. 라벨 디자인도 다르게 해서 미세재미
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
                mSelectedItemPaths.remove(index);

                notifyItemChanged(position); // 삭제된 아이템도 notify 해줘야.

            } else {
                mSelectedItemPosition.add(position);
                getCursor().moveToPosition(position);
                mSelectedItemPaths.add(getCursor().getString(mDataColumnIndex));
            }

            for (int changedItemPosition : mSelectedItemPosition) { //선택된 아이템들에 대한 notify
                notifyItemChanged(changedItemPosition);
            }

        }
    }

    public ArrayList<String> getSelectedItemPaths() {
        return mSelectedItemPaths;
    }
}