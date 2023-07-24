package util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class getTakePhotoPath {
    /***
     * 获取拍照后的路径
     * @param data
     * @return
     */
    public static String getTakePhotoPath(Intent data) {
        Bitmap photo = null;
        Uri uri = data.getData();
        if (uri != null) {
            photo = BitmapFactory.decodeFile(uri.getPath());
        }
        if (photo == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data");
            } else {

                return "";
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = Environment.getExternalStorageDirectory() + "/fiberstore_photos";
            // 新建目录
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdir();
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("xiebin");
            String filename = "MT" + (t.format(new Date())) + ".jpg";
            /**新建文件*/
            File file = new File(saveDir, filename);
            /***打开文件输出流*/
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            /**
             * 对应Bitmap的compress(Bitmap.CompressFormat format, int quality, OutputStream stream)方法中第一个参数。
             * CompressFormat类是个枚举，有三个取值：JPEG、PNG和WEBP。其中，
             * PNG是无损格式（忽略质量设置），会导致方法中的第二个参数压缩质量失效，
             * JPEG不解释，
             * 而WEBP格式是Google新推出的，据官方资料称“在质量相同的情况下，WEBP格式图像的体积要比JPEG格式图像小40%。
             */
            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            /***相片的完整路径*/
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}