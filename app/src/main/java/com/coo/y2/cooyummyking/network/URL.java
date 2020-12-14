package com.coo.y2.cooyummyking.network;

import com.coo.y2.cooyummyking.core.App;

/**
 * Created by Y2 on 2015-04-25.
 */
public class URL {
    public static final String BASE_URL = "http://ec2-3-139-236-152.us-east-2.compute.amazonaws.com:52273/";
    public static final String BASE_URL_DEVELOPMENT = "http://ec2-3-139-236-152.us-east-2.compute.amazonaws.com:52273/";
//    public static final String BASE_URL_DEVELOPMENT = "http://192.168.10.101:3000/";
    public static final String getBaseUrl() {
        return App.SERVER_TARGET == App.SERVER_TEST ? BASE_URL_DEVELOPMENT : BASE_URL;
    }

    public static final String GET_RECIPES = getBaseUrl() + "recipes/list";
    public static final String CREATE_RECIPES = getBaseUrl() + "recipes";
    public static final String GET_IMAGE_URL = getBaseUrl() + "recipes/%s/images/%s";
    public static final String GET_RECIPE = getBaseUrl() + "recipes/%s/user/%s";
}
