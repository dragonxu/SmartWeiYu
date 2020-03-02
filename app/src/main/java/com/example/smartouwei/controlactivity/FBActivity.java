package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class FBActivity extends BaseActivity {

    private ImageView imgFengShan;
    private DeviceState nowDeviceState;
    private String TAG;
    private ImageView imgLeft;
    private TextView tv_fen;
    private String strNowDeviceType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gb_fengshan);
        strNowDeviceType = getIntent().getStringExtra("type");
        TAG = "FBActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }


    private void initUI() {
        imgFengShan = (ImageView) findViewById(R.id.imgFengShan);
        imgLeft = (ImageView) findViewById(R.id.imgLeft);
        tv_fen = (TextView) findViewById(R.id.tv_fen);

        if (!strNowDeviceType.equals("YG")) {
            imgLeft.setImageResource(R.drawable.discharge_);
            tv_fen.setText(R.string.paiqishan);
        }

        imgFengShan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                // 开着，则发关
                if ((Boolean) v.getTag())
                    hashMap.put(BaseVolume.COMMAND_LOC_FENGBENG, "00");
                else
                    hashMap.put(BaseVolume.COMMAND_LOC_FENGBENG, "01");

                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });

        updateUI();
    }

    public static String getActivityName() {
        return "FBActivity";
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
        String strCY = nowDeviceState.getFengBeng();
        if (strCY == null) strCY = "false";
        boolean isOpen = Boolean.valueOf(strCY);
        imgFengShan.setTag(isOpen);
        if (isOpen)
            imgFengShan.setImageResource(R.drawable.img_switch_on);
        else
            imgFengShan.setImageResource(R.drawable.img_switch_off);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
