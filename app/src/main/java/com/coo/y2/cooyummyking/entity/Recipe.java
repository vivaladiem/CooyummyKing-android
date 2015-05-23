package com.coo.y2.cooyummyking.entity;

import android.text.TextUtils;

import com.coo.y2.cooyummyking.network.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Y2 on 2015-04-25.
 */
public class Recipe implements Serializable{
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_TITLE = "title";
    public static final String RECIPE_INST = "instruction";
    public static final String RECIPE_MAINIMG = "main_image_num";
    public static final String RECIPE_COOKINGTIME = "cooking_time";
    public static final String RECIPE_THEME = "theme";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_SOURCES = "sources";
    public static final String RECIPE_LIKE = "like_count";
    public static final String RECIPE_SCRAP = "scrap_count";
    public static final String CREATED_AT = "created_at";

    // [Tuning] 안드로이드에선 게터 세터가 성능면에서 좋지 않기때문에 필터링이 필요없을땐 직접접근으로 한다.
    public int id;
    public int userId;
    public String userName;
    public String title;
    public ArrayList<String> instructions;
    public int mainImageNum;
    public int cookingTime;
    public String theme;
    public String ingredients;
    public String sources;
    public int likeCount;
    public int scrapCount;
    public String createdAt;

    // ------------------------- Related to tool -------------------------- //
    private static Recipe recipe;
    public static ArrayList<String> imagePaths = new ArrayList<>();

    public static Recipe getScheme() {
        if (recipe == null) {
            recipe = new Recipe();

            // 초기화 필요한 변수들 초기화
            recipe.instructions = new ArrayList<>();
            recipe.cookingTime = 0;
        }
        return recipe;
    }
    public static Recipe loadSavedRecipe(JSONObject json) {
        if (json == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        recipe.title = json.optString(RECIPE_TITLE);
        recipe.instructions = new ArrayList<>(Arrays.asList(json.optString(RECIPE_INST).split("\\|\\|")));
        recipe.mainImageNum = json.optInt(RECIPE_MAINIMG);
        recipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);
        recipe.theme = json.optString(RECIPE_THEME);
        recipe.ingredients = json.optString(RECIPE_INGREDIENTS);
        recipe.sources = json.optString(RECIPE_SOURCES);
        return recipe;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(RECIPE_TITLE, title);
        json.put(RECIPE_INST, TextUtils.join("||", instructions));
        json.put(RECIPE_COOKINGTIME, cookingTime);
        json.put(RECIPE_THEME, theme);
        json.put(RECIPE_INGREDIENTS, ingredients);
        json.put(RECIPE_SOURCES, sources);
        return json;
    }

    public void saveTempData() {
    }
    // ----------------------------------------------------------------------- //


    public static Recipe loadRecipe(JSONObject json) {
        if (json == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        recipe.id = json.optInt(RECIPE_ID);
        recipe.userId = json.optInt(User.USER_ID);
        recipe.userName = json.optString(User.USER_NAME);
        recipe.title = json.optString(RECIPE_TITLE);
        recipe.instructions = new ArrayList<>(Arrays.asList(json.optString(RECIPE_INST).split("\\|\\|"))); // Split by || // asList가 참조값을 가지게 하는거라 어딘가 누수 생길지도
        recipe.mainImageNum = json.optInt(RECIPE_MAINIMG);
        recipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);
        recipe.theme = json.optString(RECIPE_THEME);
        recipe.ingredients = json.optString(RECIPE_INGREDIENTS);
        recipe.sources = json.optString(RECIPE_SOURCES);
        recipe.likeCount = json.optInt(RECIPE_LIKE);
        recipe.scrapCount = json.optInt(RECIPE_SCRAP);
        recipe.createdAt = json.optString(CREATED_AT);

        return recipe;
    }

    public String getImageUrl(int imageNum) {
//        return URL.getBaseUrl() + String.format(URL.GET_IMAGE_URL_BASE, this.id) + imageNum + ".jpg";
//        return URL.getBaseUrl() + "recipes/" + id + "/images/" + imageNum;
        //return String.format(mImageUrl, this.id, imageNum);
		return String.format(URL.GET_IMAGE_URL, this.id, imageNum);
    }

    // 임시로 작성. User에 있어야.
    public String getWriterProfileImageUrl(int userId) {
        return URL.getBaseUrl() + "users/profile/" + userId;
    }

}
