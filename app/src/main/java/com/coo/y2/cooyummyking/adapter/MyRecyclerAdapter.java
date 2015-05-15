package com.coo.y2.cooyummyking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Y2 on 2015-05-05.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private ArrayList<String> mInstructions = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private int mMainImageNum;


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
    public MyRecyclerAdapter(ArrayList<String> savedInstructions, ArrayList<String> savedImageUrls) {
        // TODO Iterator 방식이 좋은건가? 그냥 = 으로 할당하면 안되나? 초기화 한 이후로로 다시 해도 될것같은데.
        Iterator<String> itInst = savedInstructions.iterator();
        while(itInst.hasNext()) {
            this.mInstructions.add(itInst.next());
        }
        Iterator<String> itImg = savedImageUrls.iterator();
        while(itImg.hasNext()) {
            this.mImageUrls.add(itImg.next());
        }
    }

    // Create layout and initiate ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_lowerpage_overview_content, parent, false);
        return new ViewHolder(v, false);
//        return new ViewHolder(v);
    }

    // Handle View contents
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTvTagNum.setText(String.valueOf(position + 1));
        ImageLoader.getInstance().displayImage(mImageUrls.get(position), holder.mIvRecipeImage);
        holder.mTvRecipeText.setText(mInstructions.get(position));
    }

    @Override
    public int getItemCount() {
        return mInstructions.size();
    }


    // ------------- Listener Methods ------------- //
    // called at ToolFragment in Listener.

    public void addItem(int position, String instruction, String imageUrl) {
        mInstructions.add(position, instruction);
        mImageUrls.add(position, imageUrl);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mInstructions.remove(position);
        mImageUrls.remove(position);
        notifyItemRemoved(position);
    }

    public void changePosition(int before, int after) {
        mInstructions.add(after, mInstructions.remove(before));
        mImageUrls.add(after, mImageUrls.remove(before));
        notifyItemMoved(before, after);
    }

    public void setInstruction(int position, String instruction) {
        mInstructions.set(position, instruction);
        notifyItemChanged(position);
    }

    public void setImage(int position, String imageUrl) {
        mImageUrls.set(position, imageUrl);
        notifyItemChanged(position);
    }

}