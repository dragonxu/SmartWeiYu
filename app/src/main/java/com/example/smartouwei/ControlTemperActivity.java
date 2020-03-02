package com.example.smartouwei;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.view.ProgressBarView;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.NetworkUtils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-07-10.
 */

public class ControlTemperActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvNowTemp, tvWillTemp, tvSetValue;
    private ImageView imgC, imgF;
    private RelativeLayout rlContent;
    private ProgressBarView progressBar;
    private DeviceState nowDeviceState;
    private String TAG;
    private SeekBar sbJiaoZhun;
    private String strNowDeviceType = "GB";
    //private LoadingDialog dialog;

    /**
     * 默认是摄氏度，最大值为112
     */
    private int iTempMaxValueC = 60;
    private int iTempMinValueC = 20;


    private int iTempMaxValueF = 140;
    private int iTempMinValueF = 68;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_temper);
        //dialog = new LoadingDialog(this, R.style.dialog_style);
        TAG = "ControlTemperActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void initUI() {
        sbJiaoZhun = (SeekBar) findViewById(R.id.sbJiaoZhun);
        tvNowTemp = (TextView) findViewById(R.id.tvNowTemp);
        tvWillTemp = (TextView) findViewById(R.id.tvWillTemp);
        tvSetValue = (TextView) findViewById(R.id.tvSetValue);
        imgC = (ImageView) findViewById(R.id.imgC);
        imgF = (ImageView) findViewById(R.id.imgF);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);

        if (nowDeviceState != null) {
            strNowDeviceType = nowDeviceState.getZhuJiType();
        }

        if (strNowDeviceType.equalsIgnoreCase("GB")) {
            iTempMinValueC = 20;
            iTempMaxValueC = 85 - iTempMinValueC;

            iTempMinValueF = 68;
            iTempMaxValueF = 185 - iTempMinValueF;
        }
        // 蒸房
        else if (strNowDeviceType.equalsIgnoreCase("ZF")) {
            iTempMinValueC = 20;
            iTempMaxValueC = 60 - iTempMinValueC;

            iTempMinValueF = 68;
            iTempMaxValueF = 140 - iTempMinValueF;
        }
        // 浴缸
        else if (strNowDeviceType.equalsIgnoreCase("YG")) {
            iTempMinValueC = 20;
            iTempMaxValueC = 45 - iTempMinValueC;

            iTempMinValueF = 68;
            iTempMaxValueF = 113 - iTempMinValueF;
        }

        rlContent.post(new Runnable() {
            @Override
            public void run() {
                progressBar = new ProgressBarView(mContext);
                progressBar.setTag("C");
                int iWidth = rlContent.getWidth();
                int viewY = (int) (28.0 / 326.0 * iWidth);
                int viewWidth = (int) (272.0 / 326.0 * iWidth);
                int viewHeight = (int) (186.0 / 302.0 * iWidth);
                int progressWidth = (int) (25.0 / 326.0 * iWidth);
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);//与父容器水平居中
                lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
                lp1.topMargin = viewY;
                lp1.width = viewWidth;
                lp1.height = viewHeight;
//              progressBar.setBackgroundResource(R.color.green);
                progressBar.setLayoutParams(lp1);//设置布局参数
                progressBar.setMax(iTempMaxValueC);
                progressBar.setDraggingEnabled(true);
                progressBar.setProgressBarHouDu(progressWidth);
                progressBar.setProgressListener(listener);
                rlContent.addView(progressBar);//RelativeLayout添加子View
            }
        });

        sbJiaoZhun.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int sbValue = seekBar.getProgress();
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                hashMap.put(BaseVolume.COMMAND_LOC_WENDU_JIAOZHUN, Integer.toHexString(sbValue));
                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(this);
        imgC.setOnClickListener(this);
        imgF.setOnClickListener(this);

        // 延时500毫秒再更新界面
        handler.sendEmptyMessageDelayed(111, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgC:
                if ((Boolean) v.getTag()) {
                    return;
                }
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                hashMap.put(BaseVolume.COMMAND_LOC_SHESHIDU_HUASHIDU, "00");
                byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                ControlMainActivity.getMainAct().SendCommand(SendByte);
                break;
            case R.id.imgF:
                if ((Boolean) v.getTag()) {
                    return;
                }
                HashMap<Integer, String> hashMap1 = new HashMap<Integer, String>();
                hashMap1.put(BaseVolume.COMMAND_LOC_SHESHIDU_HUASHIDU, "66");
                byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap1);
                ControlMainActivity.getMainAct().SendCommand(SendByte1);
                break;
            default:
                break;

        }
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            updateUI();

        }
    };

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
    int isUpDate = 0;

    private void updateUI() {
        /*if (dialog != null) {
            dialog.dismiss();
        }*/
        if (isUpDate == 1) {
            isUpDate = 0;
            return;
        }
        isUpDate++;
        // 标题栏的温度
        int iJianCe = nowDeviceState.getiJianCeWenDu();
        int iYuZhi = nowDeviceState.getiYuZhiWenDu();
        String strDanWei = nowDeviceState.getIsSheShiDu();
        if (strDanWei == null) strDanWei = "false";
        boolean isSheShiDu = Boolean.valueOf(strDanWei);
        if (isSheShiDu) {
            tvNowTemp.setText(iJianCe + "℃");
            tvWillTemp.setText(iYuZhi + "℃");

            imgC.setTag(true);
            imgF.setTag(false);
            imgC.setImageResource(R.drawable.img_temp_c);
            imgF.setImageResource(R.drawable.img_temp_f_no);

            if (progressBar != null && progressBar.getOldProgress() != iYuZhi) {
                updateProgressView(iYuZhi - iTempMinValueC);
            }

        } else {
            tvNowTemp.setText(iJianCe + "℉");
            tvWillTemp.setText(iYuZhi + "℉");

            imgC.setTag(false);
            imgF.setTag(true);
            imgC.setImageResource(R.drawable.img_temp_c_no);
            imgF.setImageResource(R.drawable.img_temp_f);

            if (progressBar != null && progressBar.getOldProgress() != iYuZhi) {
                updateProgressView(iYuZhi - iTempMinValueF);
            }
//            updateProgressView(dYuZhi);
        }
        int iJiaoZhun = nowDeviceState.getiWenDuJiaoZhun();
        sbJiaoZhun.setProgress(iJiaoZhun);
    }

    /**
     * 更新自定义进度条
     */
    private void updateProgressView(int yuzhi) {
        // 先删除，后添加
        rlContent.removeView(progressBar);
        progressBar = new ProgressBarView(mContext);
        if ((Boolean) imgC.getTag()) {
            progressBar.setTag("C");
            progressBar.setMax(iTempMaxValueC);
            progressBar.setMin(iTempMinValueC);
        } else {
            progressBar.setTag("F");
            progressBar.setMax(iTempMaxValueF);
            progressBar.setMin(iTempMinValueF);
        }

        int iWidth = rlContent.getWidth();
        int viewY = (int) (28.0 / 326.0 * iWidth);
        int viewWidth = (int) (272.0 / 326.0 * iWidth);
        int viewHeight = (int) (186.0 / 302.0 * iWidth);
        int progressWidth = (int) (25.0 / 326.0 * iWidth);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);//与父容器水平居中
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
        lp1.topMargin = viewY;
        lp1.width = viewWidth;
        lp1.height = viewHeight;
//                progressBar.setBackgroundResource(R.color.green);
        progressBar.setLayoutParams(lp1);//设置布局参数
        progressBar.setDraggingEnabled(true);
        progressBar.setProgressBarHouDu(progressWidth);
        progressBar.setProgress(yuzhi);

        progressBar.setProgressListener(listener);
        rlContent.addView(progressBar);//RelativeLayout添加子View
    }
    /**
     * 自定义进度条的监听事件
     */
    private ProgressBarView.ProgressBarListener listener = new ProgressBarView.ProgressBarListener() {
        public void onValueChange(int NewValue) {
            // 摄氏度
            if (progressBar.getTag().toString().equalsIgnoreCase("C")) {
                tvSetValue.setText(NewValue + iTempMinValueC + "");
            } else {
                tvSetValue.setText(NewValue + iTempMinValueF + "");
            }
        }

        @Override
        public void onMotionEventUp(int NewValue) {
            //dialog.show();
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            // 摄氏度
            hashMap.put(BaseVolume.COMMAND_LOC_YUZHI_WENDU, Integer.toHexString(NewValue));

            byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
            ControlMainActivity.getMainAct().SendCommand(SendByte);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }
}
