package com.coo.y2.cooyummyking.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Y2 on 2015-05-05.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private ArrayList<String> mInstructions = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private int mMainImageNum;

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_NORMAL = 1;

    public boolean isHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvTagNum;
        public ImageView mIvRecipeImage;
        public TextView mTvRecipeText;

        public ViewHolder(View v, boolean isHeader) {
            super(v);
            if (!isHeader) {
                this.mTvTagNum = (TextView) v.findViewById(R.id.tool_making_tag_num);
                this.mIvRecipeImage = (ImageView) v.findViewById(R.id.tool_making_image);
                this.mTvRecipeText = (TextView) v.findViewById(R.id.tool_making_text);
            }
        }
    }

    /*  Called when newly making one. */
    public MyRecyclerAdapter() { }

    /**
     *     When user terminate app or tool without complete a recipe,
     *     all information is saved and loaded nex time using this constructor.
     *
     *     Instructions and images array size should be same.
     */
    public MyRecyclerAdapter(ArrayList<String> instructions, ArrayList<Bitmap> images) {
        Iterator<String> itInst = instructions.iterator();
        while(itInst.hasNext()) {
            this.mInstructions.add(itInst.next());
        }
        Iterator<Bitmap> itImg = images.iterator();
        while(itImg.hasNext()) {
            this.mImages.add(itImg.next());
        }
    }

    // Create layout and initiate ViewHolder
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_lowerpage_overview_content, parent, false);
            return new ViewHolder(v, false);
//        return new ViewHolder(v);
    }

    // Handle View contents
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == VIEW_HEADER) {
            // setOnCLickListener or ScrollLinster for Scroll.
        }
        holder.mTvTagNum.setText(String.valueOf(position + 1));
        holder.mIvRecipeImage.setImageBitmap(mImages.get(position));

        holder.mTvRecipeText.setText(mInstructions.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_HEADER : VIEW_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mInstructions.size();
    }


    // ------------- Listener Methods ------------- //
    // called at ToolFragment in Listener.

    public void addItem(int position, String instruction, Bitmap image) {
        mInstructions.add(position, instruction);
        mImages.add(position, image);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mInstructions.remove(position);
        mImages.remove(position);
        notifyItemRemoved(position);
    }

    public void changePosition(int before, int after) {
        mInstructions.add(after, mInstructions.remove(before));
        mImages.add(after, mImages.remove(before));
        notifyItemMoved(before, after);
    }

    public void setInstruction(int position, String instruction) {
        mInstructions.set(position, instruction);
        notifyItemChanged(position);
    }

    public void setImage(int position, Bitmap image) {
        mImages.set(position, image);
        notifyItemChanged(position);
    }

}