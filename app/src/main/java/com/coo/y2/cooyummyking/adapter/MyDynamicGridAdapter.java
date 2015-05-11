package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Y2 on 2015-05-09.
 */
public class MyDynamicGridAdapter extends BaseDynamicGridAdapter {
    private ArrayList<String> mInstructions;
    private ArrayList<Bitmap> mImages;
    private int mMainImageNum;

    public MyDynamicGridAdapter(Context context, int columnCount, ArrayList<String> instructions, ArrayList<Bitmap> images) {
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
        mImages = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tool_lowerpage_overview_content, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvTagNum.setText(String.valueOf(position + 1));
        holder.mIvRecipeImage.setImageBitmap(mImages.get(position));
        holder.mTvRecipeText.setText(mInstructions.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return mInstructions.size();
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
}
