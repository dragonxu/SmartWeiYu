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

public class DengYGActivity extends BaseActivity {

    private ImageView imgShuiDiDeng;

    private DeviceState nowDeviceState;
    private RelativeLayout mRl_deng1;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_yg_shuidideng);

        TAG = "DengYGActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {
        imgShuiDiDeng = (ImageView) findViewById(R.id.imgSwitch);
        mRl_deng1 = (RelativeLayout) findViewById(R.id.Rl_deng1);

        imgShuiDiDeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_ZHAOMING, "E0");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_ZHAOMING, "EF");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });
        String strZhaoMing = nowDeviceState.getZhaoMing();

        if (strZhaoMing == null) // 没有此功能，则隐藏
            mRl_deng1.setVisibility(View.GONE);
        updateUI();

    }

    public static String getActivityName() {
        return "DengYGActivity";
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
        String strCY = nowDeviceState.getZhaoMing();
        if (strCY == null) strCY = "false";
        boolean isOpen = Boolean.valueOf(strCY);
        imgShuiDiDeng.setTag(isOpen);
        if (isOpen)
            imgShuiDiDeng.setImageResource(R.drawable.img_switch_on);
        else
            imgShuiDiDeng.setImageResource(R.drawable.img_switch_off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
