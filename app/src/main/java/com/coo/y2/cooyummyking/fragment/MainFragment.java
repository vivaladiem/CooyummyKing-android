package com.coo.y2.cooyummyking.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coo.y2.cooyummyking.R;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.network.HttpUtil;
import com.coo.y2.cooyummyking.network.URL;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-04-21.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    // [Tuning] 실제 서비스할 때는 아래와 같이 한 메서드에서만 사용하는 변수들은 그 메서드에서 선언할것.
    private final String HIT_RECIPE_LENGTH = "hrl";
    private final String NORMAL_RECIPE_LENGTH = "nrl";
    private final int RECIPE_COUNT = 12;
    private ImageView[] mIvRecipeImages = new ImageView[RECIPE_COUNT];
    private ArrayList<Recipe> mRecipes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_recipe_list, container, false);
        initResources(v);
        initEvents();
        setHasOptionsMenu(true);
        return v;
    }

    private int getResourceId(String id) {
        return getResources().getIdentifier(id, "id", this.getActivity().getPackageName());
    }

    private void initResources(View view) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int recipeSideLength;
        int hitRecipeSideLength;
        recipeSideLength = prefs.getInt(NORMAL_RECIPE_LENGTH, 0);
        hitRecipeSideLength = prefs.getInt(HIT_RECIPE_LENGTH, 0);

        if (recipeSideLength == 0 || hitRecipeSideLength == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidthPx = dm.widthPixels;
            float dens = dm.density;
            int screenWidthDp = Math.round((screenWidthPx - 0.5f) / dens);

            int recipeWidthDp = (screenWidthDp - (20 + 8 + 8 + 20)) / 3;
            int hitRecipeWidthDp = recipeWidthDp * 2 + 8;

            recipeSideLength = (int) (recipeWidthDp * dens + 0.5f);
            hitRecipeSideLength = (int) (hitRecipeWidthDp * dens + 0.5f);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(NORMAL_RECIPE_LENGTH, recipeSideLength);
            editor.putInt(HIT_RECIPE_LENGTH, hitRecipeSideLength);
            editor.commit();
        }

        for (int i = 0; i < RECIPE_COUNT; i++) {
            mIvRecipeImages[i] = (ImageView) view.findViewById(getResourceId("iv_recipe_" + i));
            if (i == 0 || i == 1) {
                mIvRecipeImages[i].getLayoutParams().height = hitRecipeSideLength;
                mIvRecipeImages[i].getLayoutParams().width = hitRecipeSideLength;
            } else {
                mIvRecipeImages[i].getLayoutParams().height = recipeSideLength;
                mIvRecipeImages[i].getLayoutParams().width = recipeSideLength;
            }
        }
    }

    private void initEvents() {
        for (int i = 0; i < RECIPE_COUNT; i++) {
            mIvRecipeImages[i].setTag(i);
            mIvRecipeImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe recipe = mRecipes.get((int) v.getTag());
                    if (recipe == null) {
                        Toast.makeText(getActivity(), "레시피를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Fragment fragment = new RecipeDetailFragment();
                    Bundle data = new Bundle();
                    data.putInt(RecipeDetailFragment.EXTRA_RECIPEID, recipe.id);
//                    data.putInt(RecipeDetailFragment.EXTRA_USERID, 본인아이디);
                    data.putInt(RecipeDetailFragment.EXTRA_MAIN_IMAGE_NUM, recipe.mainImageNum);
                    data.putString(RecipeDetailFragment.EXTRA_TITLE, recipe.title);
                    fragment.setArguments(data);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // 기존 백스택을 비운다. 근데 왜 하나는 남는거지..
                    HttpUtil.cancle();
                    fm.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        executeGetRecipes();
    }

    private void executeGetRecipes() {
        HttpUtil.get(URL.GET_RECIPES, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                clearAndBuildRecipes(response.optJSONArray("recipes"));
                loadRecipeImages();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                HttpUtil.cancle();
            }

            private void clearAndBuildRecipes(JSONArray recipes) {
                mRecipes.clear();
                int count = recipes.length(); // [Tuning] 반복문에선 이런식으로 값을 상수에 넣어놓고 사용해야 빠름.
                for (int i = 0; i < count; i++) {
                    mRecipes.add(Recipe.build(recipes.optJSONObject(i)));
                }
            }
        });
    }
    private void loadRecipeImages() {
        new AttachImageTask().execute();
    }
    private class AttachImageTask extends AsyncTask<Void, Integer, Void> {
        private int size = mRecipes.size();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for (ImageView iv : mIvRecipeImages) {
//                ((BitmapDrawable) iv.getDrawable()).getBitmap().recycle(); // 3.0 버전 이하에선 recycle을 해줘야만 함.
                iv.setImageDrawable(null); // 3.0 이상에선 이것만 해줘도 자동으로 비트맵 메모리 반환됨
                if (iv.getDrawable() != null) iv.getDrawable().setCallback(null);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < size; i++) {
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0];
            if (i < size) {
                Recipe recipe = mRecipes.get(i);
                if (recipe.mainImageNum != 0) {
                    ImageLoader.getInstance().displayImage(recipe.getImageUrl(recipe.mainImageNum), mIvRecipeImages[i]);
                } else {
                    // 혹시라도 사진이 하나도 없을 때.
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        TextView tv = (TextView) getActivity().findViewById(R.id.toolbar_title);
        tv.setText(R.string.title_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                executeGetRecipes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
