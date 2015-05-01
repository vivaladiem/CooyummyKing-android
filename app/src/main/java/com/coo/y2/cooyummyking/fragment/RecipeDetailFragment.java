package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.entity.User;
import com.coo.y2.cooyummyking.network.HttpUtil;
import com.coo.y2.cooyummyking.network.URL;
import com.coo.y2.cooyummyking.view.CircleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Y2 on 2015-04-30.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String EXTRA_RECIPEID = "com.coo.y2.cooyummyking.recipeId";
    public static final String EXTRA_USERID = "com.coo.y2.cooyummyking.userId";
    public static final String EXTRA_MAIN_IMAGE_NUM = "com.coo.y2.cooyummyking.mainImageNum";
    public static final String EXTRA_TITLE = "com.coo.y2.cooyummyking.title";

    private Recipe recipe;

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

    @Override
    public void onResume() {
        super.onResume();

    }

    private void executeGetAndDisplayRecipe(final View v) {
        String url = String.format(URL.GET_RECIPE, getArguments().getInt(EXTRA_RECIPEID), 1);
        HttpUtil.get(url, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Bundle args = getArguments();
                int id = args.getInt(EXTRA_RECIPEID);
                int mainImageNum = args.getInt(EXTRA_MAIN_IMAGE_NUM);
                String title = args.getString(EXTRA_TITLE);

                JSONObject recipeData;
                try {
                    recipeData = response.getJSONObject("recipe");


                    recipeData.put(Recipe.RECIPE_ID, id);
                    recipeData.put(Recipe.RECIPE_MAINIMG, mainImageNum);
                    recipeData.put(Recipe.RECIPE_TITLE, title);
                    buildRecipe(recipeData);
                    initWriterInfo(v);
                    initAdapterAndDisplayRecipe(v);
                } catch(Exception e) {
                    e.printStackTrace();
                    //TODO 오류시 메인으로 or 재로딩 등 처리해야
                }
            }

            private void buildRecipe(JSONObject data) {
                recipe = Recipe.build(data);
            }
            private void initWriterInfo(View v) {
                ((TextView) v.findViewById(R.id.detail_writer_nickname)).setText(recipe.userName); // User.USERNAME 등 상수로 변경
                CircleImageView ivProfileImage = (CircleImageView) v.findViewById(R.id.detail_writer_thumb);
                ivProfileImage.setBorderColor(Color.TRANSPARENT);
                ivProfileImage.setBorderWidth(0);
                ImageLoader.getInstance().displayImage(recipe.getWriterProfileImageUrl(recipe.userId), ivProfileImage);

            }
            private void initAdapterAndDisplayRecipe(View v) {
                ListView lv = (ListView) v.findViewById(R.id.detail_recipe_instruction_listview);
                RecipeDetailInstructionAdapter adapter =
                        new RecipeDetailInstructionAdapter(recipe.instructions);
                lv.setAdapter(adapter);
            }

            // 커스텀 ArrayAdapter를 정의합니다.
            class RecipeDetailInstructionAdapter extends ArrayAdapter<String> {
                private final int TYPE_FIRST = 0;
                private final int TYPE_CONTENT = 1;
                private final int TYPE_END = 2;
                private int count;

                public RecipeDetailInstructionAdapter(String[] instructions) {
                    super(getActivity(), 0, instructions);
                    count = instructions.length;
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
                            case TYPE_FIRST:
                                convertView = getActivity().getLayoutInflater()
                                        .inflate(R.layout.listview_recipe_instruction_first, null);

                                ImageView ivMainImage = (ImageView) convertView.findViewById(R.id.detail_recipe_main_image);
                                ImageLoader.getInstance().displayImage(recipe.getImageUrl(recipe.mainImageNum), ivMainImage); // TODO 캐시를 통해 본문에서 이 이미지를 다시 다운로드하지 않게 해야함.

                                TextView tvTitle = (TextView) convertView.findViewById(R.id.detail_recipe_title);
                                tvTitle.setText(recipe.title);
                                break;
                            case TYPE_END:
                                convertView = getActivity().getLayoutInflater()
                                        .inflate(R.layout.listview_recipe_instruction, null);
                                View bottom = ((ViewStub) convertView.findViewById(R.id.detail_recipe_instruction_end_stub)).inflate(); // 이게 ViewStub stub = ~; bottom = stub.inflate()보다 낫겠지?
                                ((ImageView)bottom.findViewById(R.id.detail_recipe_instruction_end_like)).setImageDrawable(getResources().getDrawable(R.drawable.detail_info_btn_like_on));
                                ((TextView)bottom.findViewById(R.id.detail_recipe_instruction_end_like_num)).setText(recipe.likeCount + "명");
                                ((ImageView)bottom.findViewById(R.id.detail_recipe_instruction_end_scrap)).setImageDrawable(getResources().getDrawable(R.drawable.detail_info_btn_scrab_on));
                                ((TextView)bottom.findViewById(R.id.detail_recipe_instruction_end_scrap_num)).setText(recipe.scrapCount + "명");
                                break;
                            default:
                                convertView = getActivity().getLayoutInflater()
                                        .inflate(R.layout.listview_recipe_instruction, null);
                        }
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    String instruction = getItem(position);
                    final ImageView instImageView = (ImageView) convertView.findViewById(R.id.detail_recipe_instruction_iv);
                    String imageUrl = String.format(URL.getBaseUrl() + URL.GET_IMAGE_URL_BASE, recipe.id) + (position + 1);
                    // Main Image와 같은 사진일 땐 캐시로 하거나 앞에서 받은걸 다시 쓰거나 해야.
                    ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            instImageView.setImageBitmap(loadedImage);
                            instImageView.setVisibility(View.VISIBLE);
                        }
                    });

                    holder.instTextView = (TextView) convertView.findViewById(R.id.detail_recipe_instruction_tv);
                    holder.instTextView.setText(instruction);
                    return convertView;
                }

            }
        });

    }
    public static class ViewHolder {
//        public ImageView instImageView;
        public TextView instTextView;
    }
}
