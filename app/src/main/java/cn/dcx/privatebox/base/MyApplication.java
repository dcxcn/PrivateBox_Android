package cn.dcx.privatebox.base;

import android.app.Application;
import android.content.Context;

import cn.dcx.privatebox.utils.SharedPreferencesUtil;

public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
        SharedPreferencesUtil.getInstance(this,"PBox");
    }
    public static Context getGlobalContext(){
        return mContext;
    }

}
