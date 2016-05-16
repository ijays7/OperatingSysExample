package com.ijays.operatonsysexample.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.ijays.operatonsysexample.AppConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by ijaysdev on 16/5/13.
 */
public class Utils {
    /**
     * 获取当前进程的进程名
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context, int processId) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appInfo : activityManager.getRunningAppProcesses()) {
            if (appInfo.pid == processId) {
                return appInfo.processName;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 指定拍照路径
     *
     * @return
     */
    public static String generatePth() {
        File file = new File(AppConstants.FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return AppConstants.FILE_PATH + timeStamp + ".jpg";
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 根据控件的大小缩放bitmap
     * @param path
     * @param reqWidth
     * @param reqHeigt
     * @return
     */
    public static Bitmap decodeFileBitmap(String path, int reqWidth, int reqHeigt) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //使用获取到的insampleSize再次解析图片
        options.inSampleSize = calculateInsampleSize(options, reqWidth, reqHeigt);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInsampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
      //源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            //计算出实际宽高和目标宽高的比率
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            //选择宽和高中较小的比率作为sampleSize的值
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;

    }
}
