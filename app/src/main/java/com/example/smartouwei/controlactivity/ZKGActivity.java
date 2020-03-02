package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ZKGActivity extends BaseActivity {

    private ImageView imgAllSwitch;
    private DeviceState nowDeviceState;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gb_kaiguan);

        TAG = "ZKGActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {
        imgAllSwitch = (ImageView) findViewById(R.id.imgSwitch);

        /*findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.et_text);
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

                hashMap.put(BaseVolume.COMMAND_LOC_HIDE, text.getText().toString());
                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });*/

        imgAllSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_ZHUJI_ZHUANGTAI, "00");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_ZHUJI_ZHUANGTAI, "02");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });

        updateUI();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public static String getActivityName() {
        return "ZKGActivity";
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
        String strCY = nowDeviceState.getZhuJiZhuangTai();
        if (strCY == null) strCY = "false";
        boolean isOpen = Boolean.valueOf(strCY);
        imgAllSwitch.setTag(isOpen);
        if (isOpen)
            imgAllSwitch.setImageResource(R.drawable.img_switch_on);
        else
            imgAllSwitch.setImageResource(R.drawable.img_switch_off);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
