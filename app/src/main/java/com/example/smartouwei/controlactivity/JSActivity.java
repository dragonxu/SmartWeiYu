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

public class JSActivity extends BaseActivity {

    private ImageView imgZiDongJinShui, imgZiDongPaiShui;
    private DeviceState nowDeviceState;
    private RelativeLayout mrl_1, mrl_2;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_yg_zidongjinshui);

        TAG = "JSActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {
        imgZiDongJinShui = (ImageView) findViewById(R.id.imgSwitch);
        imgZiDongPaiShui = (ImageView) findViewById(R.id.imgSwitch1);
        mrl_1 = (RelativeLayout) findViewById(R.id.rl_1);
        mrl_2 = (RelativeLayout) findViewById(R.id.rl_2);
        imgZiDongJinShui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_JINSHUI, "00");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_JINSHUI, "01");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });

        imgZiDongPaiShui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_FANGSHUI, "00");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_FANGSHUI, "01");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });
        String strJiaShui = nowDeviceState.getJinShui();
        String strFangShui = nowDeviceState.getFangShui();
        if (strJiaShui == null) // 没有此功能，则隐藏
            mrl_1.setVisibility(View.GONE);
        if (strFangShui == null) // 没有此功能，则隐藏
            mrl_2.setVisibility(View.GONE);
        updateUI();
    }

    public static String getActivityName() {
        return "JSActivity";
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
        String strCY = nowDeviceState.getJinShui();
        String strCY1 = nowDeviceState.getFangShui();
        if (strCY == null) strCY = "false";
        if (strCY1 == null) strCY1 = "false";
        boolean isOpen = Boolean.valueOf(strCY);
        boolean isOpen1 = Boolean.valueOf(strCY1);
        imgZiDongJinShui.setTag(isOpen);
        imgZiDongPaiShui.setTag(isOpen1);
        if (isOpen)
            imgZiDongJinShui.setImageResource(R.drawable.img_switch_on);
        else
            imgZiDongJinShui.setImageResource(R.drawable.img_switch_off);
        if (isOpen1)
            imgZiDongPaiShui.setImageResource(R.drawable.img_switch_on);
        else
            imgZiDongPaiShui.setImageResource(R.drawable.img_switch_off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }

}
