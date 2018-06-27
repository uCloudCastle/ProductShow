package com.randal.aviana;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
    private BitmapUtils() {
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    public static Bitmap getBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(fis, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        try {
            AssetManager manager = context.getAssets();
            InputStream is = manager.open(fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getThumbnailsFromAssets(Context context, String fileName, int maxSize) {

        AssetManager manager = context.getAssets();
        InputStream is = null;
        try {
            is = manager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is == null) {
            return null;
        }

        // read bitmap size to options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);


        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        LogUtils.d("real height：" + realHeight + " real width:" + realWidth);

        options.inSampleSize = (int) ((realHeight > realWidth ? realHeight : realWidth) / maxSize) + 1;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {
            is = manager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        LogUtils.d("thumbnails height：" + h + " width:" + w);

        return bitmap;
    }
}
