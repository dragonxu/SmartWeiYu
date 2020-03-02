package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class JRActivity extends BaseActivity {

    private ImageView imgJiaRe1, imgJiaRe2, imgLeft, imgKongQi;
    private TextView tvJRYuYueTime, tvJiaRe;
    private DeviceState nowDeviceState;
    private String TAG;
    private RelativeLayout rl_jr1, rl_jr2;
    private String Type;
    private RelativeLayout rl_kq,rl_jr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sz_jiare);
        Type = getIntent().getStringExtra("Type");
        TAG = "JRActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void initUI() {
        rl_jr1 = (RelativeLayout) findViewById(R.id.Rl_jr1);
        rl_jr2 = (RelativeLayout) findViewById(R.id.Rl_jr2);
        imgLeft = (ImageView) findViewById(R.id.imgLeft);
        imgJiaRe1 = (ImageView) findViewById(R.id.imgJiaRe);
        imgKongQi = (ImageView) findViewById(R.id.imgKongQi);
        imgJiaRe2 = (ImageView) findViewById(R.id.imgYuYue);
        tvJRYuYueTime = (TextView) findViewById(R.id.tvYuYueTime);
        tvJiaRe = (TextView) findViewById(R.id.tvJiaRe);
        rl_kq = (RelativeLayout) findViewById(R.id.rl_kq);
        rl_jr = (RelativeLayout) findViewById(R.id.rl_jr);

        imgJiaRe1.setOnClickListener(jrOnClickListener);
        imgJiaRe2.setOnClickListener(jrOnClickListener);
        tvJRYuYueTime.setOnClickListener(jrOnClickListener);
        imgKongQi.setOnClickListener(jrOnClickListener);

        if (nowDeviceState.getZhuJiType().equalsIgnoreCase("YG")) {
            imgLeft.setImageResource(R.drawable.heating);
            tvJiaRe.setTextColor(getResources().getColor(R.color.red));
        } else {
            imgLeft.setImageResource(R.drawable.steam);
            tvJiaRe.setTextColor(Color.parseColor("#9b186b"));
        }
        if (Type != null) {
            if (Type.equals("ZF")) {
                rl_kq.setVisibility(View.GONE);
                tvJiaRe.setText(getString(R.string.zhengqi));
            } else {
                rl_kq.setVisibility(View.VISIBLE);
                tvJiaRe.setText(getString(R.string.heat));
            }
        }
        updateUI();
    }

    /**
     *
     */
    private View.OnClickListener jrOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgJiaRe:
                    HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_JIARE, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_JIARE, "01");
                    byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte);
                    break;
                case R.id.imgYuYue:
                    HashMap<Integer, String> hashMap1 = new HashMap<Integer, String>();
                    if ((Boolean) v.getTag())
                        hashMap1.put(BaseVolume.COMMAND_LOC_YUYUE_KAIGUAN, "00");
                    else
                        hashMap1.put(BaseVolume.COMMAND_LOC_YUYUE_KAIGUAN, "01");
                    byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap1);
                    ControlMainActivity.getMainAct().SendCommand(SendByte1);
                    break;
                case R.id.tvYuYueTime:
                    String strTime = tvJRYuYueTime.getText().toString();
                    ShowSelectTimeDialog(strTime);
                    break;
                case R.id.imgKongQi:
                    HashMap<Integer, String> kqMap = new HashMap<Integer, String>();
                    if ((Boolean) v.getTag())
                        kqMap.put(BaseVolume.COMMAND_LOC_KONGQI, "00");
                    else
                        kqMap.put(BaseVolume.COMMAND_LOC_KONGQI, "01");
                    byte[] kqByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), kqMap);
                    ControlMainActivity.getMainAct().SendCommand(kqByte);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     */
    private void ShowSelectTimeDialog(String strStart) {
        SelectTimeWindowDialog selectTimeDialog = new SelectTimeWindowDialog(this, R.style.dialog_style, false,
                new SelectTimeWindowDialog.PeriodListener() {
                    @Override
                    public void refreshListener(String Hour, String Min) {
                        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                        hashMap.put(BaseVolume.COMMAND_LOC_BOFANG_SHIJIAN_HOUR, String.format("%02x", Integer.parseInt(Hour)));
                        hashMap.put(BaseVolume.COMMAND_LOC_BOFANG_SHIJIAN_MIN, String.format("%02x", Integer.parseInt(Min)));

                        byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                        ControlMainActivity.getMainAct().SendCommand(SendByte);
                    }
                });
        selectTimeDialog.setValue(getString(R.string.preset_time), strStart);

        selectTimeDialog.show();
    }

    public static String getActivityName() {
        return "JRActivity";
    }

    /**
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH)) {
                String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
                if (nowDeviceState.getStrMac().equalsIgnoreCase(strMac)) {
                    Log.e(TAG, strMac + ",数据更新", null);
                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);
                    updateUI();
                }
            }
        }
    };

    private void updateUI() {
        String strJiaRe = nowDeviceState.getJiaRe();
        if (strJiaRe == null) {
            rl_jr.setVisibility(View.GONE);
        } else {
            boolean isJiaRe = Boolean.valueOf(strJiaRe);
            imgJiaRe1.setTag(isJiaRe);
            if (isJiaRe)
                imgJiaRe1.setImageResource(R.drawable.img_switch_on);
            else
                imgJiaRe1.setImageResource(R.drawable.img_switch_off);
        }
        String strKongQi = nowDeviceState.getKongQi();
        if (strKongQi == null) {
            rl_kq.setVisibility(View.GONE);
        } else {
            boolean isKongQi = Boolean.valueOf(strKongQi);
            imgKongQi.setTag(isKongQi);
            if (isKongQi)
                imgKongQi.setImageResource(R.drawable.img_switch_on);
            else
                imgKongQi.setImageResource(R.drawable.img_switch_off);
        }


        String strYuYue = nowDeviceState.getYuYueKaiGuan();
        if (strYuYue == null) {
            rl_jr1.setVisibility(View.GONE);
            rl_jr2.setVisibility(View.GONE);
        } else {
            if (strYuYue == null) strYuYue = "false";
            boolean isYuYue = Boolean.valueOf(strYuYue);
            imgJiaRe2.setTag(isYuYue);
            if (isYuYue)
                imgJiaRe2.setImageResource(R.drawable.img_switch_on);
            else
                imgJiaRe2.setImageResource(R.drawable.img_switch_off);

            tvJRYuYueTime.setText(String.format("%02d", nowDeviceState.getiYuYueHour()) + ":" + String.format("%02d", nowDeviceState.getiYuYueMin()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
