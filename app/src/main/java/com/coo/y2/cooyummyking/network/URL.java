package com.coo.y2.cooyummyking.network;

import com.coo.y2.cooyummyking.core.App;

/**
 * Created by Y2 on 2015-04-25.
 */
public class URL {
    public static final String BASE_URL = "http://ec2-54-64-71-195.ap-northeast-1.compute.amazonaws.com:3000/";
    public static final String BASE_URL_DEVELOPMENT = "http://192.168.10.102:3000/";
    public static final String getBaseUrl() {
        if (App.SERVER_TARGET == App.SERVER_TEST) {
            return BASE_URL_DEVELOPMENT;
        } else {
            return BASE_URL;
        }
    }

    public static final String GET_RECIPES = "recipes/list";
    public static final String CREATE_RECIPES = "recipes";
    public static final String GET_IMAGE_URL_BASE = "recipes/%s/images/";
    public static final String GET_RECIPE = "recipes/%s/user/%s";
}
