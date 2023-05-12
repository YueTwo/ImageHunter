package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

public class DialogUtil {
    /**
     * 显示一个“是否”对话框
     */
    public static AlertDialog showNormalDialog(Context context, boolean cancelable, String title, String msg,
                                               String positiveString, DialogInterface.OnClickListener listener,
                                               String negativeString, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveString, listener);
        builder.setNegativeButton(negativeString, negativeListener);
        if (!cancelable) {
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else {
                        return false; //默认返回 false
                    }
                }
            });
            alertDialog.show();
            return alertDialog;
        }
        return builder.show();
    }

    /**
     * 显示一个“是”普通对话框
     */
    public static AlertDialog showNormalDialog(Context context, boolean cancelable, String title, String msg,
                                               String positiveString, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveString, listener);
        if (!cancelable) {
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else {
                        return false; //默认返回 false
                    }
                }
            });
            alertDialog.show();
            return alertDialog;
        }
        return builder.show();
    }




    /**
     * 日期选择
     *
     * @param activity
     */
    public static void showDatePickerDialog(Activity activity, final MyOnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        new DatePickerDialog(activity, 0, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                listener.onDateSet(view,  year,  monthOfYear,  dayOfMonth);
            }
        }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    public interface MyOnDateSetListener {
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

}



