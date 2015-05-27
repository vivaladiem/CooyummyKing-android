package com.coo.y2.cooyummyking.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-05-05.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private Recipe mRecipe = Recipe.getScheme();
    private final DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .cacheInMemory(true)
            .build();

    private Context mContext;
    private RecyclerView mParent;
    private DisplayMetrics mDisplayMetrics;
    private int screenWidthPixels;
    private int itemWidth;
    private int itemHeight;
    private int itemMarginSide;
    private int itemMarginHeight;
    private int parentPadding;
    private int parentPaddingBottom;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvTagNum;
        public ImageView mIvRecipeImage;
        public TextView mTvRecipeText;
        public View mTagMain;

        public ViewHolder(View v, boolean isHeader) {
            super(v);
            if (!isHeader) {
                this.mTvTagNum = (TextView) v.findViewById(R.id.tool_making_tag_num);
                this.mIvRecipeImage = (ImageView) v.findViewById(R.id.tool_making_image);
                this.mTvRecipeText = (TextView) v.findViewById(R.id.tool_making_text);
                this.mTagMain = v.findViewById(R.id.tool_making_tag_main);
            }
        }
    }

    public MyRecyclerAdapter(Context context, RecyclerView parent) {
        mContext = context;
        mParent = parent;
        Resources res = mContext.getResources();
        mDisplayMetrics = res.getDisplayMetrics();
        this.screenWidthPixels = mDisplayMetrics.widthPixels;

        int itemInnerPadding = res.getDimensionPixelSize(R.dimen.tool_overview_item_padding);
        itemMarginHeight = res.getDimensionPixelSize(R.dimen.tool_overview_item_margin_top_bottom);
        itemMarginSide = res.getDimensionPixelSize(R.dimen.tool_overview_item_margin_side);


        parentPadding = res.getDimensionPixelSize(R.dimen.tool_padding); // padding top, left, right
        parentPaddingBottom = res.getDimensionPixelSize(R.dimen.tool_padding_bottom);

        itemWidth = (screenWidthPixels - itemMarginSide * 2 - parentPadding * 2) / 3; // == imageViewHeight + padding

        int itemTextHeight = res.getDimensionPixelSize(R.dimen.tool_overview_item_text_height);

        itemHeight = itemWidth + itemTextHeight + itemInnerPadding;

    }

    // Create layout and initiate ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_lowerpage_overview_content, parent, false);

        int row = getItemCount() / 3 + 1;
        int height = (itemHeight + itemMarginHeight * 2) * row;

        parent.getLayoutParams().height = height + parentPadding + parentPaddingBottom;
        parent.invalidate();
        return new ViewHolder(v, false);
//        return new ViewHolder(v);
    }

    // Handle View contents
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTvTagNum.setText(String.valueOf(position + 1));
        ImageLoader.getInstance().displayImage("file://" + Recipe.imagePaths.get(position), holder.mIvRecipeImage, mOptions);
        holder.mTvRecipeText.setText(mRecipe.instructions.get(position));

        if (mRecipe.mainImageIndex == position + 1) {
            holder.mTagMain.setVisibility(View.VISIBLE);
        } else {
            holder.mTagMain.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipe.instructions.size();
    }

    // ------------- Listener Methods ------------- //
    // called at ToolFragment in Listener.

    /**
     * Add new Item at last or specific position
     * @param position : if -1, add at end. if positive integer, add at the position.
     * @param instruction:
     * @param imageUrl:
     */
    public void addItem(int position, String instruction, String imageUrl) {
        if (position == -1) {
            mRecipe.instructions.add(instruction);
            Recipe.imagePaths.add(imageUrl);
        } else {
            mRecipe.instructions.add(position, instruction);
            Recipe.imagePaths.add(position, imageUrl);
        }
        notifyItemInserted(position);
    }

    public void addBulkItem(@Nullable ArrayList<String> instructions, ArrayList<String> imagePaths) {
        int startPosition = getItemCount();
        int count = imagePaths.size();

        if (instructions == null) {
            ArrayList<String> inst = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                inst.add(""); // ""을 null로 취급하는 기기가 있을까 걱정됨... / null을 넣으면 'null'이라 찍혀나와서 쓰면 안됨
            }
            instructions = inst;
        }
        mRecipe.instructions.addAll(instructions);
        Recipe.imagePaths.addAll(imagePaths);
        mRecipe.mainImageIndex = Recipe.imagePaths.size();
        Recipe.isChanged = true;


        notifyItemRangeInserted(startPosition, count);
    }

    public void removeItem(int position) {
        mRecipe.instructions.remove(position);
        Recipe.imagePaths.remove(position);
        notifyItemRemoved(position);
    }

    public void changePosition(int before, int after) {
        mRecipe.instructions.add(after, mRecipe.instructions.remove(before));
        Recipe.imagePaths.add(after, Recipe.imagePaths.remove(before));
        notifyItemMoved(before, after);
    }

    public void setInstruction(int position, String instruction) {
        mRecipe.instructions.set(position, instruction);
        notifyItemChanged(position);
    }

    public void setImage(int position, String imageUrl) {
        Recipe.imagePaths.set(position, imageUrl);
        notifyItemChanged(position);
    }

}