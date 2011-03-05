package com.lehms.util;

import android.util.Log;

public class AppLog {
    private static final String APP_TAG = "LEHMS";
    
    public static int info(String message){
            return Log.i(APP_TAG, message);
    }

    public static int error(String message){
        return Log.e(APP_TAG, message);
    }

    public static int error(String message, Throwable e){
        return Log.e(APP_TAG, message, e);
    }

    public static int debug(String message){
        return Log.d(APP_TAG, message);
    }

    public static int warn(String message){
        return Log.w(APP_TAG, message);
    }

    public static int warn(String message, Throwable e){
        return Log.w(APP_TAG, message, e);
    }

    public static int verbose(String message){
        return Log.v(APP_TAG, message);
    }
}
