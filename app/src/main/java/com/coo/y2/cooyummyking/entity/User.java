package com.coo.y2.cooyummyking.entity;

import com.coo.y2.cooyummyking.network.URL;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Y2 on 2015-04-25.
 */
public class User implements Serializable{
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "username"; //user_name으로 하는게 일관성있긴 하겠네..
    public static final String USER_EMAIL = "email";
    public static final String USER_PROFILETEXT = "profile_text";
    public static final String USER_POINT = "point";
    public static final String USER_LEVEL = "level";
    public static final String USER_RECIPECOUNT = "recipe_count";
    public static final String FOLLOWING_COUNT = "following_count";
    public static final String FOLLOWER_COUNT = "follower_count";

    public int id;
    public String name;
    public String email;
    public String profileText;
    public int point;
    public int level;
    public int recipeCount;
    public int followingCount;
    public int followerCount;

    public static User build(JSONObject json) {
        if (json == null) {
            return null;
        }
        User user = new User();
        user.id = json.optInt(USER_ID);
        user.name = json.optString(USER_NAME);
        user.email = json.optString(USER_EMAIL);
        user.profileText = json.optString(USER_PROFILETEXT);
        user.point = json.optInt(USER_POINT);
        user.level = json.optInt(USER_LEVEL);
        user.recipeCount = json.optInt(USER_RECIPECOUNT);
        user.followingCount = json.optInt(FOLLOWING_COUNT);
        user.followerCount = json.optInt(FOLLOWER_COUNT);

        return user;
    }
    public String getImageUrl() {
        return new StringBuilder().append(URL.getBaseUrl()).append("/users/profile/").append(this.id).toString();
    }
}
