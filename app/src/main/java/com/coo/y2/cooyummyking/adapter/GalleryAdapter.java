package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coo.y2.cooyummyking.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-13.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    final Context mContext;
    final DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    ArrayList<String> mImageUrls = new ArrayList<>();
    ArrayList<Integer> mSelected; // 선택한 이미지의 position
    ArrayList<TextView> mLabels; // 선택여부 및 순서 라벨 텍스트뷰

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewGroup mViewGroup;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mViewGroup = (ViewGroup) v;
            mImageView = (ImageView) v.findViewById(R.id.gallery_item_image);
        }
    }

    public GalleryAdapter(Context context, ArrayList<String> imageUrls) {
        mContext = context;
        if (imageUrls != null) mImageUrls = imageUrls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader.getInstance().displayImage("file://" + mImageUrls.get(position), holder.mImageView, mOptions);
        holder.mImageView.setTag(position);
        holder.mImageView.setOnClickListener(new GalleryOnClickListener(holder));
    }

    private final class GalleryOnClickListener implements View.OnClickListener {
        ViewHolder holder;
        int position;

        GalleryOnClickListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            position = (int) view.getTag();

            int index;

            if ((index = mSelected.indexOf(position)) != -1) { // When the item is already selected

                holder.mViewGroup.removeViewAt(1); // 확실하게 하려면 removeView(holder.get(index));
                mLabels.remove(index);

                int count = mLabels.size();
                for (int i = index; i <= count; i++) {
                    mLabels.get(i).setText(String.valueOf(i + 1));
                }

                return;
            }


            mSelected.add(position);

            // set label
            TextView label = (TextView) LayoutInflater.from(mContext).inflate(R.layout.gallery_item_label, holder.mViewGroup, true);
            mLabels.add(label);
            label.setText(mLabels.size());
        }
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public ArrayList<Integer> getSelectedItems() {
        return mSelected;
    }

    public void addItem(String url) {
        mImageUrls.add(url);
        notifyItemInserted(getItemCount());
    }
}
