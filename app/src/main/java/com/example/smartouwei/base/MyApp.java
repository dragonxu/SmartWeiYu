package com.example.smartouwei.base;

import com.gisicisky.smasterFitment.data.GroupInfoCache;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.XlinkUtils;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import io.xlink.wifi.sdk.XlinkAgent;

public class MyApp extends Application   {

    private static final String TAG = "MyApp";
    private static MyApp application;

//    public static SharedPreferences sharedPreferences;
    // 判断程序是否正常启动
    public boolean auth;

	public static String userEmail = "";
	public static boolean isRunInBackground = false;
	public static int appCount = 0;
    @Override
    public void onCreate() {
		super.onCreate();
		application = this;
		
//		FeedbackAPI.init(this, "24809050","a48e03f137f705964423c2d70fb5e6d0");

        initBackgroundCallBack();
		
		
    }
    

    public static MyApp getApp() {
	return application;
    }
    // 当前的activity
    private Activity currentActivity;

    public Activity getCurrentActivity() {
	return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
	this.currentActivity = currentActivity;
    }

    public static boolean isWifi(Context mContext) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
        if (activeNetInfo != null  
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
            return true;  
        }  
        return false;  
    }



    private void initBackgroundCallBack() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
                if (isRunInBackground) {
                    //应用从后台回到前台 需要做的操作
                    back2App(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
                if (appCount == 0) {
                    //应用进入后台 需要做的操作
                    leaveApp(activity);
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

    /**
     * 从后台回到前台需要执行的逻辑
     *
     * @param activity
     */
    private void back2App(Activity activity) {
        isRunInBackground = false;
    }

    /**
     * 离开应用 压入后台或者退出应用
     *
     * @param activity
     */
    private void leaveApp(Activity activity) {
        isRunInBackground = true;
        Log.e(TAG, "leaveApp: 退出到后台！" );
        XlinkAgent.getInstance().stop();
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//常规java、c#的标准退出法，返回值为0代表正常退出
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());
    }


}
