package com.coo.y2.cooyummyking.entity;

import com.coo.y2.cooyummyking.network.URL;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Y2 on 2015-04-25.
 */
public class Recipe implements Serializable{
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_TITLE = "title";
    public static final String RECIPE_INST = "instruction";
    public static final String CREATED_AT = "created_at";
    public static final String RECIPE_MAINIMG = "main_image_num";
    public static final String RECIPE_LIKE = "like_count";
    public static final String RECIPE_SCRAP = "scrap_count";
    public static final String RECIPE_COOKINGTIME = "cooking_time";

    // [Tuning] 안드로이드에선 게터 세터가 성능면에서 좋지 않기때문에 필터링이 필요없을땐 직접접근으로 한다.
    public int id;
    public int userId;
    public String userName;
    public String title;
    public String[] instructions;
    public String createdAt;
    public int mainImageNum;
    public int likeCount;
    public int scrapCount;
    public int cookingTime;

    public static Recipe build(JSONObject json) {
        if (json == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        recipe.id = json.optInt(RECIPE_ID);
        recipe.userId = json.optInt(User.USER_ID);
        recipe.userName = json.optString(User.USER_NAME);
        recipe.title = json.optString(RECIPE_TITLE);
        recipe.instructions = json.optString(RECIPE_INST).split("\\|\\|");
        recipe.createdAt = json.optString(CREATED_AT);
        recipe.mainImageNum = json.optInt(RECIPE_MAINIMG);
        recipe.likeCount = json.optInt(RECIPE_LIKE);
        recipe.scrapCount = json.optInt(RECIPE_SCRAP);
        recipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);

        return recipe;
    }
    public String getImageUrl(int imageNum) {
        return URL.getBaseUrl() + String.format(URL.GET_IMAGE_URL_BASE, this.id) + imageNum + ".jpg";
//        return URL.getBaseUrl() + "recipes/" + id + "/images/" + imageNum;
    }
    // 임시로 작성. User에 있어야.
    public String getWriterProfileImageUrl(int userId) {
        return new StringBuilder(URL.getBaseUrl()).append("users/profile/").append(userId).toString();
    }

}
