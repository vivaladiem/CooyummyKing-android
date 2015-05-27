package com.coo.y2.cooyummyking.core;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Y2 on 2015-04-25.
 */
public class App extends Application {
    public static final int SERVER_TEST = 0;
    public static final int SERVER_PRODUCTION = 1;
    public static final int SERVER_TARGET = SERVER_TEST;

    public static final String TAG = "CooYummyKing";


    @Override
    public void onCreate() {
        super.onCreate();
        // 여기서 Options 설정을 해도 각 사용처에서 추가옵션을 넣으려면 아래 설정 전부 다시 넣어줘야함. 그러니 그냥 각자에서 하는게 낫다
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .cacheInMemory(false)
//                .cacheOnDisk(false)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .build();
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(4 * 1024 * 1024)) // 4MB
                .memoryCacheSizePercentage(13)
//                .diskCache(new LimitedAgeDiscCache(cacheDir, 7 * 24 * 60 * 60)) // 7 days // I'm not sure whether it's proper or not
//                .defaultDisplayImageOptions(options)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);
    }
}