package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.fragment.ToolFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-09.
 */
public class MyDynamicGridAdapter extends BaseDynamicGridAdapter implements AdapterView.OnItemClickListener {
    private final DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .cacheInMemory(true)
            .build();

    private int mMainImageNum;

    public MyDynamicGridAdapter(Context context, int columnCount) {
        super(context, columnCount);
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
        ImageLoader.getInstance().displayImage("file://" + ToolFragment.sImageUrls.get(position), holder.mIvRecipeImage, mOptions);
        holder.mTvRecipeText.setText(ToolFragment.sInstructions.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return ToolFragment.sInstructions.isEmpty() ? 0 : ToolFragment.sInstructions.size();
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

    // 디자인패턴도 잘 모르고 시간이 없어서 이렇게밖에 못했음.. 나중에 체계적으로 만들자.
    public void addItem(String instruction, String imageUrl) {
        ToolFragment.sInstructions.add(instruction);
        ToolFragment.sImageUrls.add(imageUrl);
        notifyDataSetChanged();
    }

    public void addBulkItem(@Nullable ArrayList<String> instructions, ArrayList<String> imageUrls) {
        if (instructions == null) {
            ArrayList<String> inst = new ArrayList<>();
            int count = imageUrls.size();
            for (int i = 0; i < count; i++) inst.add(null);
            instructions = inst;
        }
        ToolFragment.sInstructions.addAll(instructions);
        ToolFragment.sImageUrls.addAll(imageUrls);
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

    }
}
