package com.coo.y2.cooyummyking.entity;

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

    private int id;
    private String name;
    private String email;
    private String profileText;
    private int point;
    private int level;
    private int recipeCount;
    private int followingCount;
    private int followerCount;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileText() {
        return profileText;
    }

    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRecipeCount() {
        return recipeCount;
    }

    public void setRecipeCount(int recipeCount) {
        this.recipeCount = recipeCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
}
