package com.coo.y2.cooyummyking.entity;

import com.coo.y2.cooyummyking.network.URL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Y2 on 2015-04-25.
 */
public class Recipe {
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_TITLE = "title";
    public static final String RECIPE_INST = "instruction";
    public static final String RECIPE_MAINIMG = "main_image_index";
    public static final String RECIPE_COOKINGTIME = "cooking_time";
    public static final String RECIPE_THEME = "theme";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_SOURCES = "sources";
    public static final String RECIPE_LIKE = "like_count";
    public static final String RECIPE_SCRAP = "scrap_count";
    public static final String CREATED_AT = "created_at";

    public static final String RECIPE_IMAGE_PATH = "image_path";


    // [Tuning] 안드로이드에선 게터 세터가 성능면에서 좋지 않기때문에 필터링이 필요없을땐 직접접근으로 한다.
    public int id;
    public int userId;
    public String userName;
    public String title;
    public ArrayList<String> instructions;
    public int mainImageIndex;
    public int cookingTime;
    public String theme;
    public String ingredients;
    public String sources;
    public int likeCount;
    public int scrapCount;
    public String createdAt;

    /**
     * Load recipe from network or scraped data
     * @param json Downloaded JSONObject data
     * @return Recipe instance
     */
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
        recipe.mainImageIndex = json.optInt(RECIPE_MAINIMG);
        recipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);
        recipe.theme = json.optString(RECIPE_THEME);
        recipe.ingredients = json.optString(RECIPE_INGREDIENTS);
        recipe.sources = json.optString(RECIPE_SOURCES);
        recipe.likeCount = json.optInt(RECIPE_LIKE);
        recipe.scrapCount = json.optInt(RECIPE_SCRAP);
        recipe.createdAt = json.optString(CREATED_AT);

        return recipe;
    }

    /**
     * Get server url of image
     * @param imageIndex image index
     * @return String url
     */
    public String getImageUrl(int imageIndex) {
        return String.format(URL.GET_IMAGE_URL, this.id, imageIndex);
    }
//    public String getImageUrl(int imageNum) {
//        return String.format(URL.GET_IMAGE_URL, this.id, imageNum);
//    }

    // 임시로 작성. User에 있어야.
    public String getWriterProfileImageUrl(int userId) {
        return URL.getBaseUrl() + "users/profile/" + userId;
    }

}
