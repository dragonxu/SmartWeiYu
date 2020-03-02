package com.example.smartouwei;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class FirstActivity extends Activity {

    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    @SuppressLint("InlinedApi")
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.RECORD_AUDIO,// 录制声音通过手机或耳机的麦克
//            Manifest.permission.MODIFY_AUDIO_SETTINGS, // 修改声音设置信息
//            Manifest.permission_group.CALENDAR, // 允许程序读取用户的日程信息
            Manifest.permission_group.CAMERA, // 允许访问摄像头进行拍照
//            Manifest.permission.READ_CONTACTS,// 允许应用访问联系人通讯录信息
//            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission_group.LOCATION,
            Manifest.permission_group.MICROPHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
//            Manifest.permission.CALL_PHONE, // 允许程序从非系统拨号器里输入电话号码
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
//            Manifest.permission.PROCESS_OUTGOING_CALLS, // 允许程序监视,修改或放弃播出电话
            Manifest.permission.SEND_RESPOND_VIA_MESSAGE,
            Manifest.permission_group.STORAGE
    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionsChecker(this);

    }

    public void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        } else {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }
}
