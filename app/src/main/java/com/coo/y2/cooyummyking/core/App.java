package com.coo.y2.cooyummyking.core;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Collection;

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
        // 여기서 Options 설정을 해도 각 사용처에서 추가옵션을 넣으려면 아래 설정 전부 다시 넣어줘야함.
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(13) // 1/8 of total memory. if set this, automatically use LruMemoryCache.
//                .diskCache(new LimitedAgeDiscCache(cacheDir, 7 * 24 * 60 * 60)) // 7 days // I'm not sure whether it's proper or not
                .diskCache(new UnlimitedDiscCache(cacheDir))
//                .diskCacheExtraOptions(600, 600, null) // 이게 있어야만 리사이즈된 이미지를 캐시에 저장하는데, 그 과정이 캐시 안쓰는것보다도 훨씬 느림..!? 안쓰는게 나음.. IO 과부하때문인지 발열도 심함.
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }


    //TODO OutOfMemory 몇 회 이상 발생하면 자동으로 조치 취하기
    // ViewPager 한 번에 로드하는 페이지 갯수 줄이기
    // image 사이즈 확실히 조절
    // ImageLoader 최적화(Document 참조)


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

    public void logCacheInMemory() {
        MemoryCache cache = ImageLoader.getInstance().getMemoryCache();
        Collection<String> keys = cache.keys();
        Log.i("CYMK", "// ----------------- memory cache ---------------- //");
        Log.i("CYMK", "Count: " + keys.size());
        for (String key : keys) {
            Log.i("CYMK", key);
        }
        Log.i("CYMK", "// ------------------------------------------------ //");
    }
}