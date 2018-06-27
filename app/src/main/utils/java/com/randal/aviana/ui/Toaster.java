package com.randal.aviana.ui;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    private static Toast mToast;

    public static void showLongToast(Context context,String msg){
        if (mToast == null){
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }
    public static void showShortToast(Context context,String msg){
        if(mToast == null){
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}