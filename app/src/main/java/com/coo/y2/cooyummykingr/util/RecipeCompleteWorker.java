package com.coo.y2.cooyummykingr.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.coo.y2.cooyummykingr.entity.Recipe;
import com.coo.y2.cooyummykingr.entity.RecipeDesign;
import com.coo.y2.cooyummykingr.entity.User;
import com.coo.y2.cooyummykingr.network.HttpUtil;
import com.coo.y2.cooyummykingr.network.URL;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Y2 on 2015-06-05.
 */
public class RecipeCompleteWorker {
    private Context context;
    private RecipeDesign sRecipe;
    private int stepCount;
    int imageCount;
    int progress = 0;

    public RecipeCompleteWorker(Context context) {
        this.context = context;
        sRecipe = RecipeDesign.getDesign();
        stepCount = sRecipe.getAllImagePath().size();
        imageCount = sRecipe.getTotalImageCount();
    }

    //[TODO] make login feature
    public void execute() {
        RequestParams params = new RequestParams();
        params.put(User.USER_ID, String.valueOf(1));//
        params.put(User.USER_TOKEN, String.valueOf("change_later"));
        params.put(Recipe.RECIPE_TITLE, sRecipe.title);
        params.put(Recipe.RECIPE_INST, TextUtils.join(Recipe.RECIPE_SEPARATOR, sRecipe.instructions));
        params.put(Recipe.RECIPE_MAINIMG, sRecipe.mainImageIndex);
        params.put(Recipe.RECIPE_COOKINGTIME, sRecipe.cookingTime);
        params.put(Recipe.RECIPE_THEME, sRecipe.theme);
        params.put(Recipe.RECIPE_INGREDIENTS, sRecipe.ingredients);
        params.put(Recipe.RECIPE_SOURCES, sRecipe.sources);

        ArrayList<String> imagePaths = sRecipe.getAllImagePath();

        for (int i = 0; i < stepCount; i++) {
            String path = imagePaths.get(i);

            if (path.contains("||")) { // If edited image exist - send both file
                String[] paths = path.split("\\|\\|", -1);

                try {
                    params.put(String.valueOf(i), new File(paths[1]));
//                    params.put(i + Recipe.RECIPE_ORIGINAL_IMG, new File(paths[0]));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else { // If only original image exist
                try {
                    params.put(String.valueOf(i), new File(path));
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        requestPost(params);
    }

//    private class SharpenTask extends AsyncTask<Void, Void, Void> {
//        private File file;
//        private RequestParams params;
//
//        public SharpenTask (File file, RequestParams params, ) {
//            this.file = file;
//            this.params = params;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            GPUImage gpuImage = new GPUImage(context);
//            GPUImageSharpenFilter filter = new GPUImageSharpenFilter();
//            filter.setSharpness(2.0f);
//            gpuImage.setImage(file);
//            gpuImage.setFilter(filter);
//            params.put()
//
//            return null;
//        }
//    }


//    private class ImageLoadingListener extends SimpleImageLoadingListener {
//        private int index;
//        private RequestParams params;
//        private boolean isOriginal;
//
//        public ImageLoadingListener (int index, RequestParams params, boolean isOriginal) {
//            this.index = index;
//            this.params = params;
//            this.isOriginal = isOriginal;
//        }
//
//        @Override
//        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            super.onLoadingComplete(imageUri, view, loadedImage);
//            InputStream in;
//            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//            loadedImage.compress(Bitmap.CompressFormat.PNG, 100, byteOut);
//            in = new ByteArrayInputStream(byteOut.toByteArray());
//
//            if (!isOriginal) {
//                params.put(String.valueOf(index), in, index + ".png");
//            } else {
//                params.put(Recipe.RECIPE_ORIGINAL_IMG + index, in, "original_" + index + ".png");
//            }
//            if (++progress == imageCount) {
//                requestPost(params);
//            }
//        }
//    }

    private void requestPost(RequestParams params) {

        HttpUtil.post(URL.CREATE_RECIPES, null, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int result = 0;
                String msg = "";
                Log.i("CYMK", "error messages : " + response.opt("error_msg"));
                try {
                    result = response.getInt("result");
                    msg = response.optString("error_msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (statusCode == 200) {
                    if (result == 1) {
                        Toast.makeText(context, "레시피를 성공적으로 완성하였습니다", Toast.LENGTH_SHORT).show();
                    } else if (result == 0) {
                        new AlertDialog.Builder(context)
                                .setMessage("레시피 완성에 문제가 발생하였습니다\r레시피를 보관합니다\r" + msg)
                                .show();
                    }
                }

            }
        });
    }
}
