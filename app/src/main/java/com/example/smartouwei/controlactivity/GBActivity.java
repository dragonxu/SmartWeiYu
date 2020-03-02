package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.ControlMainActivity;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.view.SelectTimeWindowDialog;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-07-16.
 */

public class GBActivity extends BaseActivity {

    private ImageView imgGBSwitchA,imgGBSwitchB,imgGBSwitchYuyue;
    private TextView tvGBAValue,tvGBBValue,tvGBYuYueTime;
    private SeekBar sbGBA,sbGBB;

    private DeviceState nowDeviceState;
    private String TAG;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gb_guangbo);

        TAG = "GBActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {

        imgGBSwitchA = (ImageView) findViewById(R.id.imgSwitchA);
        imgGBSwitchB = (ImageView) findViewById(R.id.imgSwitchB);
        imgGBSwitchYuyue = (ImageView) findViewById(R.id.imgYuYue);

        tvGBAValue = (TextView) findViewById(R.id.tvAValue);
        tvGBBValue = (TextView) findViewById(R.id.tvBValue);
        tvGBYuYueTime = (TextView) findViewById(R.id.tvYuYueTime);

        imgGBSwitchA.setOnClickListener(gbOnClickListener);
        imgGBSwitchB.setOnClickListener(gbOnClickListener);
        imgGBSwitchYuyue.setOnClickListener(gbOnClickListener);
        tvGBYuYueTime.setOnClickListener(gbOnClickListener);

        sbGBA = (SeekBar) findViewById(R.id.sbA);
        sbGBB = (SeekBar) findViewById(R.id.sbB);

        sbGBA.setTag("GBA");
        sbGBB.setTag("GBB");
        sbGBA.setOnSeekBarChangeListener(seekBarChangeListener);
        sbGBB.setOnSeekBarChangeListener(seekBarChangeListener);
//        updateUI();
    }

    /** 光波按钮的点击监听 */
    private View.OnClickListener gbOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgSwitchA:
                    HashMap<Integer,String> hashMap = new HashMap<Integer, String>();
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_GUANGBO_1,"00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_GUANGBO_1,"05");
                    byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(),nowDeviceState.getNowStateBuffer(),hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte);
                    break;
                case R.id.imgSwitchB:
                    HashMap<Integer,String> hashMap1 = new HashMap<Integer, String>();
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap1.put(BaseVolume.COMMAND_LOC_GUANGBO_2,"00");
                    else
                        hashMap1.put(BaseVolume.COMMAND_LOC_GUANGBO_2,"05");
                    byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(),nowDeviceState.getNowStateBuffer(),hashMap1);
                    ControlMainActivity.getMainAct().SendCommand(SendByte1);
                    break;
                case R.id.imgYuYue:
                    HashMap<Integer,String> hashMap2 = new HashMap<Integer, String>();
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap2.put(BaseVolume.COMMAND_LOC_YUYUE_KAIGUAN,"00");
                    else
                        hashMap2.put(BaseVolume.COMMAND_LOC_YUYUE_KAIGUAN,"01");
                    byte[] SendByte2 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(),nowDeviceState.getNowStateBuffer(),hashMap2);
                    ControlMainActivity.getMainAct().SendCommand(SendByte2);
                    break;
                case R.id.tvYuYueTime:
                    String strTime = tvGBYuYueTime.getText().toString();
                    ShowSelectTimeDialog(strTime);
                    break;
                default:
                    break;
            }
        }
    };

    /** 时间选择的弹窗 */
    private void ShowSelectTimeDialog(String strStart) {
        SelectTimeWindowDialog selectTimeDialog = new SelectTimeWindowDialog(this,R.style.dialog_style, false,new SelectTimeWindowDialog.PeriodListener() {
            public void refreshListener(String strStartTime, String strStopTime) {
                tvGBYuYueTime.setText(strStartTime);

            }
        });
        selectTimeDialog.setValue(getString(R.string.preset_time),strStart);

        selectTimeDialog.show();
    }

    /** seekbar滑动监听 */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            String strTag = (String) seekBar.getTag();
            if (strTag.equalsIgnoreCase("GBA")) {
                int iV = seekBar.getProgress()+1;
                tvGBAValue.setText(iV*20+"%");


            }
            else if (strTag.equalsIgnoreCase("GBB")) {
                int iV = seekBar.getProgress()+1;
                tvGBBValue.setText(iV*20+"%");
            }
        }
    };


    public static String getActivityName() {
        return "GBActivity";
    }


    /** 监听的广播 */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 数据解析成功
            if (action.equalsIgnoreCase(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH)) {
                String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
                if (nowDeviceState.getStrMac().equalsIgnoreCase(strMac)) {
                    Log.e(TAG,strMac+",数据更新！",null);
                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);

                    updateUI();

                }
            }
        }
    };

    private void updateUI() {

        String strYuYue = nowDeviceState.getYuYueKaiGuan();
        if (strYuYue == null) strYuYue = "false";
        boolean isYuYue = Boolean.valueOf(strYuYue);
        imgGBSwitchYuyue.setTag(isYuYue);
        if (isYuYue)
            imgGBSwitchYuyue.setImageResource(R.drawable.img_switch_on);
        else
            imgGBSwitchYuyue.setImageResource(R.drawable.img_switch_off);

        int iGB1 = nowDeviceState.getiGuangBo1();
        // 关着的
        if (iGB1 == 0xff || iGB1 == 0x00) {
            imgGBSwitchA.setImageResource(R.drawable.img_switch_off);
            sbGBA.setFocusable(false);
        }
        else {
            imgGBSwitchA.setImageResource(R.drawable.img_switch_on);
            sbGBA.setFocusable(true);
            sbGBA.setProgress(iGB1-1);
            tvGBAValue.setText((iGB1*20)+"%");
        }

        int iGB2 = nowDeviceState.getiGuangBo2();
        // 关着的
        if (iGB2 == 0xff || iGB2 == 0x00) {
            imgGBSwitchB.setImageResource(R.drawable.img_switch_off);
            sbGBB.setFocusable(false);
        }
        else {
            imgGBSwitchB.setImageResource(R.drawable.img_switch_on);
            sbGBB.setFocusable(true);
            sbGBB.setProgress(iGB2-1);
            tvGBBValue.setText((iGB2*20)+"%");
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
