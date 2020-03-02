package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.smartbathroom.R;
import com.example.smartouwei.ControlMainActivity;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.service.ControlService;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-07-16.
 */

public class CYActivity extends BaseActivity {

    private ImageView imgChouYang;
    private DeviceState nowDeviceState;
    private String TAG;
    private RelativeLayout mrl_cy;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gb_chouyang);

        TAG = "CYActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {
        mrl_cy = (RelativeLayout) findViewById(R.id.Rl_cy);
        imgChouYang = (ImageView) findViewById(R.id.imgChouYang);
        imgChouYang.setTag(false);
        imgChouYang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_O3, "00");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_O3, "01");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);

            }
        });
        // 臭氧
        String strChou = nowDeviceState.getO3();
        if (strChou == null) // 没有此功能，则隐藏
            mrl_cy.setVisibility(View.GONE);

        updateUI();

    }


    private void updateUI() {

        String strCY = nowDeviceState.getO3();
        if (strCY == null) strCY = "false";
        boolean isOpen = Boolean.valueOf(strCY);
        imgChouYang.setTag(isOpen);
        if (isOpen)
            imgChouYang.setImageResource(R.drawable.img_switch_on);
        else
            imgChouYang.setImageResource(R.drawable.img_switch_off);


    }


    public static String getActivityName() {
        return "CYActivity";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
