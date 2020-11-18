package com.example.es100dome;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.es100.*;
import com.es100.BuildConfig;
import com.es100.util.Const;
import com.es100.util.PhoneModel;
import com.es100.util.UIHelper;
import com.es100.util.http.HttpHelper;
import com.ifreecomm.base.EncoderOrientation;
import com.ifreecomm.debug.MLog;
import com.ifreecomm.media.Media;
import com.tencent.mars.xlog.Xlog;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Administrator on 2018/6/21 0021.
 */

public class MyApp extends App {
    private static String mVersionCode;
    private static String mVersionName;
    public int count  = 0;

    private static App myApplication = null;
    public static App getApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
        myApplication = this;
        AppContext = getApplicationContext();
        MLog.init(BuildConfig.DEBUG ? Xlog.LEVEL_NONE: Xlog.LEVEL_NONE, Const.XLOG_FILE_DIR, Const.XLOG_CACHE_DIR);

        HttpHelper.init(getApplicationContext(), "");
         /*开启网络广播监听*/

        AppConfig.init(this);
        UIHelper.getInstance();
        // 初始化网络编码器的方向
        EncoderOrientation.instance().setEncoderOrientation(16, 9);
        // 初始化录像编码器的方向 . 虽然无录像功能 , 还是调用一下
        EncoderOrientation.instance().setRecEncoderLandscape(16, 9);

        //初始化设备型号
        PhoneModel.initMstPhoneModel();
        //全局字体
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/iconfont.ttf")
                .build());
        MstManager.getInstance(AppContext).initMst();
        //初始化媒体模块
        Media.initInstance(AppContext);
        mVersionCode = getVersionCode();
        mVersionName = getVersionName();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    MyState.getInstance().isAppBackground = false;
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if(count==0){
                    MyState.getInstance().isAppBackground = true;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
    public static String getHostUrl() {
        return "http://" + AppConfig.getInstance().getString(Const.GK_IP, "") + ":" + AppConfig.getInstance()
                .getString(Const.GK_164, "") + "/";
    }
    public static String getCode() {
        return mVersionCode;
    }
    public static String getName() {
        return mVersionName;
    }
    public String getVersionCode() {
        PackageManager manager = getPackageManager();
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getVersionName() {
        PackageManager manager = getPackageManager();
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
