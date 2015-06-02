package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.network.HttpUtil;
import com.coo.y2.cooyummyking.network.URL;
import com.coo.y2.cooyummyking.widget.CircleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-04-30.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String EXTRA_RECIPEID = "com.coo.y2.cooyummyking.recipeId";
    public static final String EXTRA_USERID = "com.coo.y2.cooyummyking.userId";
    public static final String EXTRA_MAIN_IMAGE_INDEX = "com.coo.y2.cooyummyking.mainImageIndex";
    public static final String EXTRA_TITLE = "com.coo.y2.cooyummyking.title";

    private Recipe mRecipe;
    private DisplayImageOptions mOptions;

    public static RecipeDetailFragment newInstance(DisplayImageOptions options) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.mOptions = options;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        executeGetAndDisplayRecipe(v);
        return v;
    }

    // Inner Class를 너무 많이 쓴것 같다...
    private void executeGetAndDisplayRecipe(final View v) {
        String url = String.format(URL.GET_RECIPE, getArguments().getInt(EXTRA_RECIPEID), 1);
        HttpUtil.get(url, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                // TBD 프로그레스바 COOYUMMYKING 글씨 등
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // 가지고있는 레시피 정보를 할당합니다.
                Bundle args = getArguments();
                int id = args.getInt(EXTRA_RECIPEID);
                int mainImageNum = args.getInt(EXTRA_MAIN_IMAGE_INDEX);
                String title = args.getString(EXTRA_TITLE);

                // 네트워크로 받은 레시피 정보를 할당합니다.
                JSONObject recipeData;
                try {
                    recipeData = response.getJSONObject("recipe");

                    recipeData.put(Recipe.RECIPE_ID, id);
                    recipeData.put(Recipe.RECIPE_MAINIMG, mainImageNum);
                    recipeData.put(Recipe.RECIPE_TITLE, title);
                    buildRecipe(recipeData);
                    initWriterInfo(v);
                    initAdapterAndDisplayRecipe(v);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO 오류시 메인으로 or 재로딩 등 처리해야
                }
            }

            private void buildRecipe(JSONObject data) {
                mRecipe = Recipe.loadRecipe(data);
            }

            private void initWriterInfo(View v) {
                ((TextView) v.findViewById(R.id.detail_writer_nickname)).setText(mRecipe.userName); // TODO User.USERNAME 등 상수로 변경
                CircleImageView ivProfileImage = (CircleImageView) v.findViewById(R.id.detail_writer_thumb);
                ivProfileImage.setBorderColor(Color.TRANSPARENT);
                ivProfileImage.setBorderWidth(0);
                ImageLoader.getInstance().displayImage(mRecipe.getWriterProfileImageUrl(mRecipe.userId), ivProfileImage, mOptions);
                ((ImageView) v.findViewById(R.id.detail_writer_level)).setImageLevel(4); // set user's level - 1
            }

            private void initAdapterAndDisplayRecipe(View v) {
                ListView lv = (ListView) v.findViewById(R.id.detail_recipe_instruction_listview);
                RecipeDetailAdapter adapter =
                        new RecipeDetailAdapter(mRecipe.instructions);
                lv.setAdapter(adapter);
            }


        });

    }
    public class ViewHolder {
        public ImageView instImageView;
        public TextView instTextView;
    }

    // ListView에서 사용할 Adapter를 정의합니다.
    class RecipeDetailAdapter extends ArrayAdapter<String> {
        private final int TYPE_FIRST = 0;
        private final int TYPE_CONTENT = 1;
        private final int TYPE_END = 2;
        private int count;

        public RecipeDetailAdapter(ArrayList<String> instructions) {
            super(getActivity(), 0, instructions);
            count = instructions.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return TYPE_FIRST;
            if (position + 1 == count) return TYPE_END;
            return TYPE_CONTENT;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_END + 1;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                int type = getItemViewType(position);
                switch(type) {
                    case TYPE_FIRST: // 레시피 개요가 포함된 페이지를 인플레이트합니다.
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.listview_recipe_instruction_first, parent, false);

                        String url = mRecipe.getImageUrl(mRecipe.mainImageIndex);
                        ImageView ivMainImage = (ImageView) convertView.findViewById(R.id.detail_recipe_main_image);
                        ImageLoader.getInstance().displayImage(url, ivMainImage, mOptions);

                        TextView tvTitle = (TextView) convertView.findViewById(R.id.detail_recipe_title);
                        tvTitle.setText(mRecipe.title);
                        break;
                    case TYPE_END: // 레시피 좋아요, 스크랩 정보가 포함된 페이지를 인플레이트합니다.(같은 레이아웃, viewstub 사용)
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.listview_recipe_instruction, parent, false);
                        View bottom = ((ViewStub) convertView.findViewById(R.id.detail_recipe_instruction_end_stub)).inflate();
                        ((TextView)bottom.findViewById(R.id.detail_recipe_instruction_end_like)).setText(mRecipe.likeCount + "명");
                        ((TextView)bottom.findViewById(R.id.detail_recipe_instruction_end_scrap)).setText(mRecipe.scrapCount + "명");
                        break;
                    default: // 내용을 인플레이트합니다.
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.listview_recipe_instruction, parent, false);
                }

                holder.instImageView = (ImageView) convertView.findViewById(R.id.detail_recipe_instruction_iv);
                holder.instTextView = (TextView) convertView.findViewById(R.id.detail_recipe_instruction_tv);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String instruction = getItem(position);
            String imageUrl = mRecipe.getImageUrl(position);

            ImageLoader.getInstance().displayImage(imageUrl, holder.instImageView, mOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    view.setVisibility(View.VISIBLE);
                }
            });


            holder.instTextView.setText(instruction);
            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recursiveRecycle(getView());
    }

    private void recursiveRecycle(View root) {
        if (root == null)
            return;
        if (root.getBackground() != null) {
            root.getBackground().setCallback(null);
            root.setBackground(null);
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursiveRecycle(group.getChildAt(i));
            }
            if (!(group instanceof AdapterView)) {
                group.removeAllViews();
            } else {
                ((AdapterView)group).setAdapter(null);
            }
        }
        if (root instanceof ImageView) {
            Drawable drawable;
            ImageView iv = (ImageView) root;
            if ((drawable = iv.getDrawable()) == null) return;
            drawable.setCallback(null);
            iv.setImageDrawable(null);
            iv = null;
        }
        root = null;
    }
}
