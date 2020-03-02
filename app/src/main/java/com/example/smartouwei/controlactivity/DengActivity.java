package com.example.smartouwei.controlactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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

public class DengActivity extends BaseActivity {

    // 灯界面
    private ImageView imgDengSwitch1, imgDengSwitch2, imgDengZiDong, imgDengQieHuan;
    private TextView tvDengValue;
    private RelativeLayout mRl_deng1, mRl_deng2;
    private SeekBar sbDeng;
    private LinearLayout llQiCai;
    private DeviceState nowDeviceState;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gb_deng);

        TAG = "DengActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void initUI() {
        llQiCai = (LinearLayout) findViewById(R.id.llQiCai);
        imgDengSwitch1 = (ImageView) findViewById(R.id.imgDeng1);
        imgDengSwitch2 = (ImageView) findViewById(R.id.imgDeng2);
        imgDengZiDong = (ImageView) findViewById(R.id.imgZiDong);
        imgDengQieHuan = (ImageView) findViewById(R.id.imgQieHuan);
        tvDengValue = (TextView) findViewById(R.id.tvLVale);
        mRl_deng1 = (RelativeLayout) findViewById(R.id.Rl_deng1);
        mRl_deng2 = (RelativeLayout) findViewById(R.id.Rl_deng2);

        imgDengSwitch1.setTag(false);
        imgDengSwitch2.setTag(false);

        imgDengSwitch1.setOnClickListener(dengOnClickListener);
        imgDengSwitch2.setOnClickListener(dengOnClickListener);
        imgDengZiDong.setOnClickListener(dengOnClickListener);
        imgDengQieHuan.setOnClickListener(dengOnClickListener);

        imgDengZiDong.setTag(false);
        imgDengQieHuan.setTag("B");

        sbDeng = (SeekBar) findViewById(R.id.sbLight);
        sbDeng.setTag("Deng");
        sbDeng.setOnSeekBarChangeListener(seekBarChangeListener);

        String strZhaoMing = nowDeviceState.getZhaoMing();
        String strBeiDeng = nowDeviceState.getBeiDengColor();
        if (strZhaoMing == null) // 没有此功能，则隐藏
            mRl_deng1.setVisibility(View.GONE);
        if (strBeiDeng == null) // 没有此功能，则隐藏
            mRl_deng2.setVisibility(View.GONE);


//        updateUI();
    }

    /**
     * 灯按钮的点击监听
     */
    private View.OnClickListener dengOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            switch (v.getId()) {
                case R.id.imgDeng1:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_ZHAOMING, "ef");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_ZHAOMING, "e0");
                    break;
                case R.id.imgDeng2:
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        hashMap.put(BaseVolume.COMMAND_LOC_BEIDENG, "00");
                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_BEIDENG, "0f");
                    break;
                case R.id.imgZiDong:
                    int iLight = sbDeng.getProgress() + 1;
                    // 保持当前亮度
                    String strRight = Integer.toHexString(iLight);
                    // 改变 变色或不变色
                    String strLeft = "";
                    // 开着，则发关
                    if ((Boolean) v.getTag())
                        strLeft = "B";
                    else
                        strLeft = "A";
                    hashMap.put(BaseVolume.COMMAND_LOC_BEIDENG, (strLeft + strRight));
                    break;
                case R.id.imgQieHuan:
                    String strV = getChangeColor();
                    hashMap.put(BaseVolume.COMMAND_LOC_BEIDENG, strV);
                    break;
                default:
                    break;
            }
            byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
            ControlMainActivity.getMainAct().SendCommand(SendByte);


        }
    };

    /**
     * seekbar滑动监听
     */
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
            if (strTag.equalsIgnoreCase("Deng")) {
                int iV = seekBar.getProgress() + 1;
                tvDengValue.setText(iV + "%");
                // 保持当前亮度
                String strRight = Integer.toHexString(iV);
                String strLeft = (String) imgDengQieHuan.getTag();
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                hashMap.put(BaseVolume.COMMAND_LOC_BEIDENG, (strLeft + strRight));
                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        }
    };

    public static String getActivityName() {
        return "DengActivity";
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

    private String getChangeColor() {
        int iLight = sbDeng.getProgress() + 1;
        // 保持当前亮度
        String strValue = Integer.toHexString(iLight);
        if (imgDengQieHuan.getTag().toString().equalsIgnoreCase("A")
                || imgDengQieHuan.getTag().toString().equalsIgnoreCase("B")
                || imgDengQieHuan.getTag().toString().equalsIgnoreCase("6")) {
            strValue = "0" + strValue;
        } else {
            int iTag = Integer.parseInt(imgDengQieHuan.getTag().toString());
            ++iTag;
            strValue = iTag + strValue;
        }

        return strValue;
    }

    private void updateUI() {
        // 照明灯系列
        String strZM = nowDeviceState.getZhaoMing();
        if (strZM == null) strZM = "false";
        boolean isZM = Boolean.valueOf(strZM);
        imgDengSwitch1.setTag(isZM);
        if (isZM)
            imgDengSwitch1.setImageResource(R.drawable.img_switch_on);
        else
            imgDengSwitch1.setImageResource(R.drawable.img_switch_off);

        // 七彩灯系列
        int qcLight = nowDeviceState.getBeiDengLight();
        if (qcLight <= 0) {
            imgDengSwitch2.setTag(false);
            llQiCai.setVisibility(View.GONE);
        } else {
            imgDengSwitch2.setTag(true);
            llQiCai.setVisibility(View.VISIBLE);
            sbDeng.setProgress(qcLight - 1);
        }
        String qcColor = nowDeviceState.getBeiDengColor();
        if (qcColor != null) {
            imgDengQieHuan.setTag(qcColor);
            if (qcColor.equalsIgnoreCase("A")) {
                imgDengZiDong.setTag(true);
                imgDengZiDong.setImageResource(R.drawable.img_zidong_run);
            } else {
                imgDengZiDong.setTag(false);
                imgDengZiDong.setImageResource(R.drawable.img_zidong_pause);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }

}
