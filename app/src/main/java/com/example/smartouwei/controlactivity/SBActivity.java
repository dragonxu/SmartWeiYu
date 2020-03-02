package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.smartbathroom.R;
import com.example.smartouwei.ControlMainActivity;
import com.example.smartouwei.base.BaseActivity;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-07-16.
 */

public class SBActivity extends BaseActivity {

    // 水泵界面
    private ImageView imgShuiBeng1, imgShuiBeng2, imgShuiBeng3, imgShuiBeng4;
    private RelativeLayout mrl_sb1, mrl_sb2, mrl_sb3, mrl_sb4;
    private DeviceState nowDeviceState;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_yg_shuibeng);

        TAG = "SBActivity";
        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void initUI() {
        imgShuiBeng1 = (ImageView) findViewById(R.id.imgSwitch1);
        imgShuiBeng2 = (ImageView) findViewById(R.id.imgSwitch2);
        imgShuiBeng3 = (ImageView) findViewById(R.id.imgSwitch3);
        imgShuiBeng4 = (ImageView) findViewById(R.id.imgSwitch4);
        mrl_sb1 = (RelativeLayout) findViewById(R.id.Rl_sb1);
        mrl_sb2 = (RelativeLayout) findViewById(R.id.Rl_sb2);
        mrl_sb3 = (RelativeLayout) findViewById(R.id.Rl_sb3);
        mrl_sb4 = (RelativeLayout) findViewById(R.id.Rl_sb4);

        imgShuiBeng1.setOnClickListener(sbOnClickListener);
        imgShuiBeng2.setOnClickListener(sbOnClickListener);
        imgShuiBeng3.setOnClickListener(sbOnClickListener);
        imgShuiBeng4.setOnClickListener(sbOnClickListener);

        // 水泵
        String strShuiBeng1 = nowDeviceState.getShuiBeng1();
        String strShuiBeng2 = nowDeviceState.getShuiBeng2();
        String strShuiBeng3 = nowDeviceState.getShuiBeng3();
        String strShuiBeng4 = nowDeviceState.getXunHuanBeng();
        if (strShuiBeng1 == null) // 没有此功能，则隐藏
            mrl_sb1.setVisibility(View.GONE);
        if (strShuiBeng2 == null) // 没有此功能，则隐藏
            mrl_sb2.setVisibility(View.GONE);
        if (strShuiBeng3 == null) // 没有此功能，则隐藏
            mrl_sb3.setVisibility(View.GONE);
        if (strShuiBeng4 == null) // 没有此功能，则隐藏
            mrl_sb4.setVisibility(View.GONE);
        updateUI();
    }

    /**
     * 水泵按钮的点击监听
     */
    private View.OnClickListener sbOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            switch (v.getId()) {
                case R.id.imgSwitch1:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_1, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_1, "01");
                    break;
                case R.id.imgSwitch2:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_2, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_2, "01");
                    break;
                case R.id.imgSwitch3:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_3, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_SHUIBENG_3, "01");
                    break;
                case R.id.imgSwitch4:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_XUNHUAN_BENG, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_XUNHUAN_BENG, "01");
                    break;
                default:
                    break;
            }
            byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
            ControlMainActivity.getMainAct().SendCommand(SendByte);
        }
    };

    public static String getActivityName() {
        return "SBActivity";
    }

    /**
     * 监听的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 数据解析成功
            if (action.equalsIgnoreCase(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH)) {
                String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
                if (nowDeviceState.getStrMac().equalsIgnoreCase(strMac)) {
                    Log.e(TAG, strMac + ",数据更新！", null);
                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);
                    updateUI();
                }
            }
        }
    };

    private void updateUI() {
        String strSB1 = nowDeviceState.getShuiBeng1();
        if (strSB1 == null) strSB1 = "false";
        boolean isOpen = Boolean.valueOf(strSB1);
        imgShuiBeng1.setTag(isOpen);
        if (isOpen)
            imgShuiBeng1.setImageResource(R.drawable.img_switch_on);
        else
            imgShuiBeng1.setImageResource(R.drawable.img_switch_off);

        String strSB2 = nowDeviceState.getShuiBeng2();
        if (strSB2 == null) strSB2 = "false";
        boolean isOpen2 = Boolean.valueOf(strSB2);
        imgShuiBeng2.setTag(isOpen2);
        if (isOpen2)
            imgShuiBeng2.setImageResource(R.drawable.img_switch_on);
        else
            imgShuiBeng2.setImageResource(R.drawable.img_switch_off);

        String strSB3 = nowDeviceState.getShuiBeng3();
        if (strSB3 == null) strSB3 = "false";
        boolean isOpen3 = Boolean.valueOf(strSB3);
        imgShuiBeng3.setTag(isOpen3);
        if (isOpen3)
            imgShuiBeng3.setImageResource(R.drawable.img_switch_on);
        else
            imgShuiBeng3.setImageResource(R.drawable.img_switch_off);

        String strSB4 = nowDeviceState.getXunHuanBeng();
        if (strSB4 == null) strSB4 = "false";
        boolean isOpen4 = Boolean.valueOf(strSB4);
        imgShuiBeng4.setTag(isOpen4);
        if (isOpen4)
            imgShuiBeng4.setImageResource(R.drawable.img_switch_on);
        else
            imgShuiBeng4.setImageResource(R.drawable.img_switch_off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
