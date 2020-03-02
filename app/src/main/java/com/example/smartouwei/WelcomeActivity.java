package com.example.smartouwei;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.smartouwei.base.BaseActivity;
import com.gisicisky.smasterFitment.utl.SharedPreferencesUtil;
import com.gisicisky.smasterFitment.utl.XlinkUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.smartouwei.base.MyApp.userEmail;

/**
 * 欢迎界面
 *
 * @author Denny
 */
public class WelcomeActivity extends BaseActivity {

    public static SharedPreferences sharedPreferences;
    private int IS_FIRST = 0;
    // 该版本的打包时间
    private TelephonyManager tm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        userEmail = tm.getDeviceId();

        if(userEmail==null)
        {
            // android pad
            userEmail = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        userEmail = XlinkUtils.MD5(userEmail);
        userEmail = userEmail.substring(userEmail.length() - 8)+"@qq.com";

        sharedPreferences = getSharedPreferences("XlinkOfficiaDemo", Context.MODE_PRIVATE);
        IS_FIRST = SharedPreferencesUtil.queryIntValue(sharedPreferences, "IS_FIRST");
        Intent intent = new Intent(WelcomeActivity.this, DeviceListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
/*
        if (isPad(WelcomeActivity.this)) {
            System.exit(0);
        }*/
       /* new Thread() {
            public void run() {
                handler.sendEmptyMessageDelayed(1, 1500);
            }
        }.start();
*/
    }

    /*Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
*/

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
