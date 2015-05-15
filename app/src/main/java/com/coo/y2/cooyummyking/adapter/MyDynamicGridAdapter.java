package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-09.
 */
public class MyDynamicGridAdapter extends BaseDynamicGridAdapter {
    private final DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .build();

    private ArrayList<String> mInstructions = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private int mMainImageNum;

    public MyDynamicGridAdapter(Context context, int columnCount, ArrayList<String> instructions, ArrayList<String> imageUrls) {
        super(context, columnCount);
        /*
        Iterator<String> itInst = instructions.iterator();
        while(itInst.hasNext()) {
            this.mInstructions.add(itInst.next());
        }
        Iterator<Bitmap> itImg = images.iterator();
        while(itImg.hasNext()) {
            this.mImages.add(itImg.next());
        }
        */
        mInstructions = instructions;
        mImageUrls = imageUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tool_lowerpage_overview_content, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvTagNum.setText(String.valueOf(position + 1));
        ImageLoader.getInstance().displayImage("file://" + mImageUrls.get(position), holder.mIvRecipeImage, mOptions);
        holder.mTvRecipeText.setText(mInstructions.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return mInstructions.isEmpty() ? 0 : mInstructions.size();
    }

    private class ViewHolder {
        public TextView mTvTagNum;
        public ImageView mIvRecipeImage;
        public TextView mTvRecipeText;

        ViewHolder (View v) {
            mTvTagNum = (TextView)v.findViewById(R.id.tool_making_tag_num);
            mIvRecipeImage = (ImageView) v.findViewById(R.id.tool_making_image);
            mTvRecipeText = (TextView) v.findViewById(R.id.tool_making_text);
        }
    }

    public void addItem(String instruction, String imageUrl) {
        mInstructions.add(instruction);
        mImageUrls.add(imageUrl);
        notifyDataSetChanged();
    }

    public void addBulkItem(ArrayList<String> instructions, ArrayList<String> imageUrls) {
        mInstructions.addAll(instructions);
        mImageUrls.addAll(imageUrls);
        notifyDataSetChanged();
    }

}
