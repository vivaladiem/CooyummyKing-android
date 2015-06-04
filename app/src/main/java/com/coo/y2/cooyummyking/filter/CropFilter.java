package com.coo.y2.cooyummyking.filter;

import android.graphics.Bitmap;

/**
 * Created by Y2 on 2015-06-03.
 */
public class CropFilter {
    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * Construct a CropFilter.
     */
    public CropFilter() {
        this(0, 0, 32, 32);
    }

    /**
     * Construct a CropFilter.
     * @param x the left edge of the crop rectangle
     * @param y the top edge of the crop rectangle
     * @param width the width of the crop rectangle
     * @param height the height of the crop rectangle
     */
    public CropFilter(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Set the left edge of the crop rectangle.
     * @param x the left edge of the crop rectangle
     * @see #getX
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the left edge of the crop rectangle.
     * @return the left edge of the crop rectangle
     * @see #setX
     */
    public int getX() {
        return x;
    }

    /**
     * Set the top edge of the crop rectangle.
     * @param y the top edge of the crop rectangle
     * @see #getY
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get the top edge of the crop rectangle.
     * @return the top edge of the crop rectangle
     * @see #setY
     */
    public int getY() {
        return y;
    }

    /**
     * Set the width of the crop rectangle.
     * @param width the width of the crop rectangle
     * @see #getWidth
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the width of the crop rectangle.
     * @return the width of the crop rectangle
     * @see #setWidth
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the height of the crop rectangle.
     * @param height the height of the crop rectangle
     * @see #getHeight
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the height of the crop rectangle.
     * @return the height of the crop rectangle
     * @see #setHeight
     */
    public int getHeight() {
        return height;
    }

    public int[] filter( int[] src ,int w, int h ) {
        int[] dst = new int[width * height];

        Bitmap srcBitmap = Bitmap.createBitmap(src, 0, w, w, h, Bitmap.Config.ARGB_8888);
        Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap, x, y, w - x, h - y);
        dstBitmap = Bitmap.createScaledBitmap(dstBitmap, width, height, false);

        dstBitmap.getPixels(dst, 0, width, 0, 0, width, height);
        return dst;
    }

    public String toString() {
        return "Distort/Crop";
    }
}
