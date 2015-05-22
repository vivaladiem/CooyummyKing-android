package com.coo.y2.cooyummyking.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
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
import com.coo.y2.cooyummyking.activity.MainActivity;
import com.coo.y2.cooyummyking.entity.Recipe;
import com.coo.y2.cooyummyking.network.HttpUtil;
import com.coo.y2.cooyummyking.network.URL;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Y2 on 2015-04-21.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private static final String HIT_RECIPE_LENGTH = "hrl";
    private static final String NORMAL_RECIPE_LENGTH = "nrl";
    public static final String SCREEN_WIDTH_DP = "swd";

    private final int RECIPE_COUNT = 12;
    private ImageView[] mIvRecipeImages = new ImageView[RECIPE_COUNT];
    private ArrayList<Recipe> mRecipes = new ArrayList<>();

    DisplayImageOptions mImageLoaderOptions = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();

    private boolean isNew = true; // BackStack에서 돌아왔을 때인지 나타내는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.sIvBtnList.setSelected(true);
        MainActivity.sIvBtnTool.setSelected(false);
        MainActivity.sIvBtnMypage.setSelected(false);

        View v = inflater.inflate(R.layout.fragment_main_recipe_list, container, false);
        initResources(v);
        initEvents();
        executeGetRecipes(isNew); // is New => will reload | BackStack에서 돌아왔을 땐 새로고침 안함.
        setHasOptionsMenu(true);
        return v;
    }

    // TODO 불가능하겠지만 스크린이 변해서 화면이 이상해질 경우 이 설정만 지워서 다시 맞추게끔 하면 좋음
    // 아님 GridLayout으로 (GridView아님) 바꾸던가. 이게 더 효율적이긴하겠지.
    private void initResources(View view) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int recipeSideLength = prefs.getInt(NORMAL_RECIPE_LENGTH, 0);
        int hitRecipeSideLength = prefs.getInt(HIT_RECIPE_LENGTH, 0);

        if (recipeSideLength == 0 || hitRecipeSideLength == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidthPx = dm.widthPixels;
            float dens = dm.density;
            int screenWidthDp = Math.round((screenWidthPx - 0.5f) / dens);
            editor.putInt(SCREEN_WIDTH_DP, screenWidthDp);

            int recipeWidthDp = (screenWidthDp - (20 + 8 + 8 + 20)) / 3; // sceenWidthDp - (layout padding left +
            int hitRecipeWidthDp = recipeWidthDp * 2 + 8;

            recipeSideLength = (int) (recipeWidthDp * dens + 0.5f);
            hitRecipeSideLength = (int) (hitRecipeWidthDp * dens + 0.5f);
            editor.putInt(NORMAL_RECIPE_LENGTH, recipeSideLength);
            editor.putInt(HIT_RECIPE_LENGTH, hitRecipeSideLength);
            editor.apply();
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
                    Recipe recipe;
                    // TODO 나중엔 오프라인에선 저장(스크랩)해놓은 레시피를 볼 수 있게 하자
                    try {
                        recipe = mRecipes.get((int) v.getTag());
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(), "레시피를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Fragment fragment = new RecipeDetailFragment(mImageLoaderOptions);
                    Fragment fragment = RecipeDetailFragment.newInstance(mImageLoaderOptions);
                    Bundle data = new Bundle();
                    data.putInt(RecipeDetailFragment.EXTRA_RECIPEID, recipe.id);
                    data.putInt(RecipeDetailFragment.EXTRA_MAIN_IMAGE_NUM, recipe.mainImageNum);
                    data.putString(RecipeDetailFragment.EXTRA_TITLE, recipe.title);
                    fragment.setArguments(data);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // 기존 백스택을 비운다
//                    HttpUtil.cancle(); // 어차피 onStop에서 하니까 여기서 할 필요 없다.
//                    ImageLoader.getInstance().stop();
                    fm.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    private void executeGetRecipes(boolean willReload) {
        if (!isInternetAvailable(getActivity())) return; // Toast나 Dialog 등 처리
        if (!willReload) { loadRecipeImages(); return; }

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
                    mRecipes.add(Recipe.loadRecipe(recipes.optJSONObject(i)));
                }
                ImageLoader.getInstance().clearMemoryCache(); // 캐시는 popBackStack에서 쓰기 위해 쓰는것이니 Refresh에선 바로 비워줌
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
                returnBitmapMemory(iv);
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
                    ImageLoader.getInstance().displayImage(recipe.getImageUrl(recipe.mainImageNum), mIvRecipeImages[i], mImageLoaderOptions);
                } else {
                    // 혹시라도 사진이 하나도 없을 때.
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ImageLoader.getInstance().stop();
        HttpUtil.cancle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        for (ImageView iv : mIvRecipeImages) {
            returnBitmapMemory(iv);
            iv.setOnClickListener(null);
        }
        isNew = false;
//        mIvRecipeImages = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        TextView tv = (TextView) getActivity().findViewById(R.id.toolbar_title);
        tv.setText(R.string.toolbar_title_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                executeGetRecipes(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ------------- Util Methods ------------ //
    private int getResourceId(String id) {
        return getResources().getIdentifier(id, "id", this.getActivity().getPackageName());
    }

    private void returnBitmapMemory(ImageView v) {
        Drawable drawable;
        if ((drawable = v.getDrawable()) != null) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            v.setImageBitmap(null);
            drawable.setCallback(null); // callback이 남아있어서 비트맵이 반환되지 않는다는 말도 있는데 항상 그런진 모르겠다.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                // 허니콤부터는 비트맵 참조만 없어져도 메모리가 반환됐는데 그 이전에는 recycle 해줘야함.
                try {
                    bm.recycle();
                } catch (Exception e) { }
            }
        }
    }

    public static boolean isInternetAvailable(Context context) {
        boolean isInternetAvailable = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && (networkInfo.isConnected())) {
                isInternetAvailable  = true;
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }

        return isInternetAvailable;
    }
}
