package util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class getImageRealPathFromURI {
    /***
     * 返回图片的路径
     * @param context
     * @param contentUri
     * @return
     */
    public static String getImageRealPathFromURI(Context context, Uri contentUri) { //传入图片uri地址

        if (null == contentUri) return null;
        final String scheme = contentUri.getScheme();
        String data = null;
        if (scheme == null) {
            data = contentUri.getPath();
            Log.e("URI1", "AAAAAA");
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = contentUri.getPath();
            Log.e("URI1", "BBBBBB");
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(contentUri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    Log.e("URI1", "CCCCCC");
                    if (index > -1) {
                        data = cursor.getString(index);
                        Log.e("URI1", "DDDDDD");
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}