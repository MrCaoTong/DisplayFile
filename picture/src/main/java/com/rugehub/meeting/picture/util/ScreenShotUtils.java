package com.rugehub.meeting.picture.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ScreenShotUtils {

    /**
     * 保存截图位图
     *
     * @param bitmap
     * @return 是否保存成功
     */
    public static boolean saveScreenCutBitmap(Context context, Bitmap bitmap) {
        boolean success = false;
        if (bitmap != null) {
            String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Android"
                    + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "cache" + File.separator;
            String name = "screenShot.png";
            success = saveImage(context, bitmap, savePath, name);
            if (success) {
                //发送本地应用广播
                Intent intent = new Intent("RESOURCE_FILE_PATH");
                intent.putExtra("resource_file_path", savePath + name);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
        return success;
    }

    public static boolean getBitmapByUrl(Context context, String url) {
        boolean success = false;
        if (!TextUtils.isEmpty(url)) {
            String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Android"
                    + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "cache" + File.separator;
            String name = "browser.png";
            try {
                URL urlBitmap = new URL(url);
                URLConnection connection = urlBitmap.openConnection();
                connection.connect();
//                connection.setConnectTimeout(3000);
//                connection.setRequestMethod("GET");
//                if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                success = saveImage(context, bitmap, savePath, name);
                if (success) {
                    //发送本地应用广播
                    //Intent intent = new Intent("RESOURCE_FILE_PATH");
                    //intent.putExtra("resource_file_path", savePath + name);
                    Intent intent = new Intent("INSERT_BITMAP");
                    intent.putExtra("bitmap", bitmap);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * 保存图片文件
     *
     * @param context
     * @param bitmap
     * @param path
     * @return
     */
    public static boolean saveImage(Context context, Bitmap bitmap, String path, String fileName) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File mFile = new File(path + fileName);
            if (mFile.exists()) {
                mFile.delete();
            }

            FileOutputStream outputStream = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //获得图片的uri
            Uri uri = Uri.fromFile(mFile);
            //发送广播通知更新图库，这样系统图库可以找到这张图片
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
