package com.coo.y2.cooyummykingr.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;

/**
 * Created by Y2 on 2015-06-06.
 * ImageLoadingListener that possible to pass arguments.
 *
 * For pass arguments, use getListener method, not instance itself.
 */
public abstract class ArgumentImageLoadingListener implements ImageLoadingListener {

    private HashMap<String, Object> variables = new HashMap<>();
    private String key;

    @Override
    public void onLoadingStarted(String imageUri, View view) {
//        onLoadingStarted(imageUri, view, variables);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//        onLoadingFailed(imageUri, view, failReason, variables);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//        onLoadingComplete(imageUri, view, loadedImage, variables);
        setKey(imageUri);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
//        onLoadingCancelled(imageUri, view, variables);
    }

//    public void onLoadingStarted(String imageUri, View view, HashMap<String, Object> variables) {
//        // Empty implementation
//
//    }
//
//    public void onLoadingFailed(String imageUri, View view, FailReason failReason, HashMap<String, Object> variables) {
//        // Empty implementation
//    }
//
//    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage, HashMap<String, Object> variables) {
//        // Empty implementation
//    }
//
//    public void onLoadingCancelled(String imageUri, View view, HashMap<String, Object> variables) {
//        // Empty implementation
//    }

    /**
     * Receive arguments and return listener instance.
     * Must set properties at here.
     * @param uriKey uri of imageloader request. It makes sure about arguments
     * @param args arguments
     * @return listener instance
     */
//    public abstract ArgumentImageLoadingListener getListener(final Object... args);
    public abstract ArgumentImageLoadingListener getListener(String uriKey, final Object... args);

    /**
     * Set property. use in getListener
     * @param field field name
     * @param value field value
     */
    public void setProperty(String uriAsKey, String field, Object value) {
        variables.put(uriAsKey + field, value);
    }

    /**
     * Remove and return the property. use in onLoading~ methods.
     * It's better to use removeProperty(String field) with call super.onLoading~ method.
     * @param uriAsKey put imageUri as key
     * @param field field name
     * @return Removed field value as Object
     */
    public Object removeProperty(String uriAsKey, String field) {
        return variables.remove(uriAsKey + field);
    }

    public Object removeProperty(String field) {
        return variables.remove(this.key + field);
    }

    private synchronized void setKey(String key) {
        this.key= key;
    }
}
