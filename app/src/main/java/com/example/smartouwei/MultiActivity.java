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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.MyPagerAdapter;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.view.ViewPagerSlide;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2018-07-11.
 */

public class MultiActivity extends BaseActivity implements View.OnClickListener {

    private ViewPagerSlide viewPage;
    private ImageView imgFM, imgMP3, imgBlue, imgBlueTitle, imgAUX;
    private TextView tvFM, tvMP3, tvBlue, tvAUX, tvTitleName, tvNowTemp, tvWillTemp, Tv_L, Tv_Y;
    private LinearLayout llFM, llMP3, llBlue, llAux;
    private List<View> list = new ArrayList<View>();
    private MyPagerAdapter adapter;

    /**
     * FM
     */
    private TextView tvFMValue, tvFMCH;
    private ImageView imgFMLeft, imgFMRight, imgFMMEM, imgFMAUTO, imgFMJIAN, imgFMJIA;
    private TextView tvFMSoundV;
    private ImageView imgFMS, imgFMA;

    /**
     * MP3
     */
    private TextView tvMP3Time;
    private ImageView imgMP3Left, imgMP3Right, imgMP3Pause;
    private TextView tvMP3SoundV;
    private ImageView imgMP3S, imgMP3A;

    /**
     * Blue
     */
    private ImageView imgBlueLeft, imgBlueRight, imgBluePause;
    private TextView tvBlueSoundV;
    private ImageView imgBlueS, imgBlueA;

    /**
     * Aux
     */
    private TextView tvAUXSoundV;
    private ImageView imgAUXS, imgAUXA;

    private DeviceState nowDeviceState;
    private String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);

        TAG = "MultiActivity";

        nowDeviceState = ControlMainActivity.getMainAct().getDeviceState();

        initUI();
        initData();

        updateUI();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);


        if (nowDeviceState.getiZhuJiZhuangTai() == 0x03) {
            selectMenu(1);
        } else if (nowDeviceState.getiZhuJiZhuangTai() == 0x04) {
            selectMenu(2);
        } else if (nowDeviceState.getiZhuJiZhuangTai() == 0x05) {
            selectMenu(3);
        } else if (nowDeviceState.getiZhuJiZhuangTai() == 0x06) {
            selectMenu(4);
        }
        /*if (llFM.getVisibility() != View.GONE) {// 打开界面自动设置成FM模式
            selectMenu(0);
        } else if (llMP3.getVisibility() != View.GONE) {// 打开界面自动设置成mp3模式
            selectMenu(1);
        } else if (llBlue.getVisibility() != View.GONE) {// 打开界面自动设置成blue模式
            selectMenu(2);
        } else if (llAux.getVisibility() != View.GONE) {// 打开界面自动设置成aux模式
            selectMenu(3);
        }*/

    }

    private void initUI() {
        Tv_L = (TextView) findViewById(R.id.tvL);
        Tv_Y = (TextView) findViewById(R.id.tvY);
        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        tvNowTemp = (TextView) findViewById(R.id.tvNowTemp);
        tvWillTemp = (TextView) findViewById(R.id.tvWillTemp);
        viewPage = (ViewPagerSlide) findViewById(R.id.viewPage);
        viewPage.setSlide(false);

        imgFM = (ImageView) findViewById(R.id.imgFM);
        imgMP3 = (ImageView) findViewById(R.id.imgMP3);
        imgBlue = (ImageView) findViewById(R.id.imgBlue);
        imgAUX = (ImageView) findViewById(R.id.imgAUX);

        tvFM = (TextView) findViewById(R.id.tvFM);
        tvMP3 = (TextView) findViewById(R.id.tvMP3);
        tvBlue = (TextView) findViewById(R.id.tvBlue);
        tvAUX = (TextView) findViewById(R.id.tvAUX);

        llFM = (LinearLayout) findViewById(R.id.llFM);
        llMP3 = (LinearLayout) findViewById(R.id.llMP3);
        llBlue = (LinearLayout) findViewById(R.id.llBlue);
        llAux = (LinearLayout) findViewById(R.id.llAUX);

        llFM.setTag(true);
        llMP3.setTag(true);
        llBlue.setTag(true);
        llAux.setTag(true);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.llFM).setOnClickListener(this);
        findViewById(R.id.llMP3).setOnClickListener(this);
        findViewById(R.id.llBlue).setOnClickListener(this);
        findViewById(R.id.llAUX).setOnClickListener(this);
    }

    private void initData() {
        View view0 = getLayoutInflater().inflate(R.layout.view_blank, null);
        View view1 = getLayoutInflater().inflate(R.layout.view_multi_fm, null);
        View view2 = getLayoutInflater().inflate(R.layout.view_multi_usb, null);
        View view3 = getLayoutInflater().inflate(R.layout.view_multi_blue, null);
        View view4 = getLayoutInflater().inflate(R.layout.view_multi_aux, null);

        list.add(view0);
        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        adapter = new MyPagerAdapter(list);
        viewPage.setAdapter(adapter);

        initUIByFM(view1);
        initUIByMP3(view2);
        initUIByBlue(view3);
        initUIByAUX(view4);
    }

    /**
     *Fm控件
     *
     * @param view
     */
    private void initUIByFM(View view) {
        this.tvFMValue = (TextView) view.findViewById(R.id.tvFMValue);
        this.tvFMCH = (TextView) view.findViewById(R.id.tvCH);
        this.tvFMSoundV = (TextView) view.findViewById(R.id.tvSoundV);
        this.imgFMLeft = (ImageView) view.findViewById(R.id.imgLeft);
        this.imgFMRight = (ImageView) view.findViewById(R.id.imgRight);
        this.imgFMMEM = (ImageView) view.findViewById(R.id.imgMem);
        this.imgFMAUTO = (ImageView) view.findViewById(R.id.imgAuto);
        this.imgFMJIAN = (ImageView) view.findViewById(R.id.imgJian);
        this.imgFMJIA = (ImageView) view.findViewById(R.id.imgJia);
        this.imgFMS = (ImageView) view.findViewById(R.id.imgSoundS);
        this.imgFMA = (ImageView) view.findViewById(R.id.imgSoundA);

        this.imgFMS.setTag("FM");
        this.imgFMA.setTag("FM");
        this.imgFMS.setOnClickListener(soundSubListener);
        this.imgFMA.setOnClickListener(soundAddListener);

        this.imgFMLeft.setOnClickListener(fmOnClickListener);
        this.imgFMRight.setOnClickListener(fmOnClickListener);
        this.imgFMMEM.setOnClickListener(fmOnClickListener);
        this.imgFMAUTO.setOnClickListener(fmOnClickListener);
        this.imgFMJIAN.setOnClickListener(fmOnClickListener);
        this.imgFMJIA.setOnClickListener(fmOnClickListener);

    }

    /**
     * Mp3控件
     *
     * @param view
     */
    private void initUIByMP3(View view) {
        this.tvMP3Time = (TextView) view.findViewById(R.id.tvTime);
        this.imgMP3Left = (ImageView) view.findViewById(R.id.imgLeft);
        this.imgMP3Right = (ImageView) view.findViewById(R.id.imgRight);
        this.imgMP3Pause = (ImageView) view.findViewById(R.id.imgPause);
        this.tvMP3SoundV = (TextView) view.findViewById(R.id.tvSoundV);
        imgMP3Pause.setTag(false);

        this.imgMP3S = (ImageView) view.findViewById(R.id.imgSoundS);
        this.imgMP3A = (ImageView) view.findViewById(R.id.imgSoundA);
        this.imgMP3S.setTag("MP3");
        this.imgMP3A.setTag("MP3");
        this.imgMP3S.setOnClickListener(soundSubListener);
        this.imgMP3A.setOnClickListener(soundAddListener);

        this.imgMP3Left.setOnClickListener(mp3OnClickListener);
        this.imgMP3Right.setOnClickListener(mp3OnClickListener);
        this.imgMP3Pause.setOnClickListener(mp3OnClickListener);

    }

    /**
     * Blue界面
     *
     * @param view
     */
    private void initUIByBlue(View view) {
        this.imgBlueLeft = (ImageView) view.findViewById(R.id.imgLeft);
        this.imgBlueRight = (ImageView) view.findViewById(R.id.imgRight);
        this.imgBluePause = (ImageView) view.findViewById(R.id.imgPause);
        this.imgBlueTitle = (ImageView) view.findViewById(R.id.imgBlueTitle);
        this.tvBlueSoundV = (TextView) view.findViewById(R.id.tvSoundV);
        imgBluePause.setTag(false);

        this.imgBlueS = (ImageView) view.findViewById(R.id.imgSoundS);
        this.imgBlueA = (ImageView) view.findViewById(R.id.imgSoundA);
        this.imgBlueS.setTag("Blue");
        this.imgBlueA.setTag("Blue");
        this.imgBlueS.setOnClickListener(soundSubListener);
        this.imgBlueA.setOnClickListener(soundAddListener);

        this.imgBlueLeft.setOnClickListener(blueOnClickListener);
        this.imgBlueRight.setOnClickListener(blueOnClickListener);
        this.imgBluePause.setOnClickListener(blueOnClickListener);
    }

    /**
     * AUX界面
     *
     * @param view
     */
    private void initUIByAUX(View view) {

        this.tvAUXSoundV = (TextView) view.findViewById(R.id.tvSoundV);
        this.imgAUXS = (ImageView) view.findViewById(R.id.imgSoundS);
        this.imgAUXA = (ImageView) view.findViewById(R.id.imgSoundA);
        this.imgAUXS.setTag("AUX");
        this.imgAUXA.setTag("AUX");
        this.imgAUXS.setOnClickListener(soundSubListener);
        this.imgAUXA.setOnClickListener(soundAddListener);

    }

    private View.OnClickListener soundSubListener = new View.OnClickListener() {
        public void onClick(View v) {
            int iOldV = 0;
            if (v.getTag().equals("FM")) {
                iOldV = Integer.parseInt(tvFMSoundV.getText().toString());
            } else if (v.getTag().equals("MP3")) {
                iOldV = Integer.parseInt(tvMP3SoundV.getText().toString());
            } else if (v.getTag().equals("Blue")) {
                iOldV = Integer.parseInt(tvBlueSoundV.getText().toString());
            } else if (v.getTag().equals("AUX")) {
                iOldV = Integer.parseInt(tvAUXSoundV.getText().toString());
            }
            --iOldV;

            if (iOldV < 0)
                iOldV = 0;

            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            hashMap.put(BaseVolume.COMMAND_LOC_YINLIANG, String.format("%02x", iOldV));
            byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
            ControlMainActivity.getMainAct().SendCommand(SendByte);


        }
    };


    private View.OnClickListener soundAddListener = new View.OnClickListener() {
        public void onClick(View v) {
            int iOldV = 0;
            if (v.getTag().equals("FM")) {
                iOldV = Integer.parseInt(tvFMSoundV.getText().toString());
            } else if (v.getTag().equals("MP3")) {
                iOldV = Integer.parseInt(tvMP3SoundV.getText().toString());
            } else if (v.getTag().equals("Blue")) {
                iOldV = Integer.parseInt(tvBlueSoundV.getText().toString());
            } else if (v.getTag().equals("AUX")) {
                iOldV = Integer.parseInt(tvAUXSoundV.getText().toString());
            }
            ++iOldV;
            if (iOldV > 16)
                iOldV = 16;
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            hashMap.put(BaseVolume.COMMAND_LOC_YINLIANG, String.format("%02x", iOldV));
            byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
            ControlMainActivity.getMainAct().SendCommand(SendByte);


        }
    };


    /**
     * FM
     */
    private View.OnClickListener fmOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            switch (v.getId()) {
                case R.id.imgLeft:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "03");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINLV, "FF");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINDAO, "FF");
                    byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte);
                    break;
                case R.id.imgRight:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "04");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINLV, "FF");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINDAO, "FF");
                    byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte1);
                    break;
                case R.id.imgMem:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "08");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINLV, "FF");
                    hashMap.put(BaseVolume.COMMAND_LOC_FM_PINDAO, "FF");
                    byte[] SendByte2 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte2);
                    break;
                case R.id.imgAuto:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "05");
                    byte[] SendByte3 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte3);
                    break;
                case R.id.imgJian:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "07");
                    byte[] SendByte5 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte5);
                    break;
                case R.id.imgJia:
                    hashMap.put(BaseVolume.COMMAND_LOC_FM, "06");
                    byte[] SendByte4 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte4);
                    break;
                default:
                    break;
            }

            hashMap.put(BaseVolume.COMMAND_LOC_FM, "0f");
            byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);

            Message msg = new Message();
            msg.obj = SendByte1;
            mHandler.sendMessageDelayed(msg, 200);


        }
    };

    /**
     * MP3
     */
    private View.OnClickListener mp3OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            switch (v.getId()) {
                case R.id.imgLeft:
                    hashMap.put(BaseVolume.COMMAND_LOC_MP3, "03");
                    byte[] SendByte0 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte0);
                    break;
                case R.id.imgRight:
                    hashMap.put(BaseVolume.COMMAND_LOC_MP3, "04");
                    byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte1);
                    break;
                case R.id.imgPause:
                    hashMap.put(BaseVolume.COMMAND_LOC_MP3, "09");
                   /* if ((Boolean) imgMP3Pause.getTag())

                    else
                        hashMap.put(BaseVolume.COMMAND_LOC_MP3, "01");*/
                    byte[] SendByte2 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte2);
                    break;
                default:
                    break;
            }
            hashMap.put(BaseVolume.COMMAND_LOC_MP3, "0f");
            byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);

            Message msg = new Message();
            msg.obj = SendByte1;
            mHandler.sendMessageDelayed(msg, 200);

        }
    };

    /**
     * Blue界面
     */
    private View.OnClickListener blueOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            switch (v.getId()) {
                case R.id.imgLeft:
                    hashMap.put(BaseVolume.COMMAND_LOC_LANYA, "13");
                    byte[] SendByte0 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte0);
                    break;
                case R.id.imgRight:
                    hashMap.put(BaseVolume.COMMAND_LOC_LANYA, "14");
                    byte[] SendByte2 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte2);
                    break;
                case R.id.imgPause:
                    hashMap.put(BaseVolume.COMMAND_LOC_LANYA, "09");
                    byte[] SendByte4 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
                    ControlMainActivity.getMainAct().SendCommand(SendByte4);
                    break;
                default:
                    break;
            }
            hashMap.put(BaseVolume.COMMAND_LOC_LANYA, "0f");
            byte[] SendByte1 = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);

            Message msg = new Message();
            msg.obj = SendByte1;
            mHandler.sendMessageDelayed(msg, 200);

        }
    };

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            byte[] SendByte1 = (byte[]) msg.obj;
            ControlMainActivity.getMainAct().SendCommand(SendByte1);

        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgBack:
                finish();
                break;

            case R.id.llFM:
                selectMenu(1);
                break;

            case R.id.llMP3:
                selectMenu(2);
                break;

            case R.id.llBlue:
                selectMenu(3);
                break;

            case R.id.llAUX:
                selectMenu(4);
                break;

            default:
                break;

        }
    }

    private void setFalse(LinearLayout layout) {
        llFM.setTag(true);
        llMP3.setTag(true);
        llBlue.setTag(true);
        llAux.setTag(true);
        layout.setTag(false);
    }

    private void selectMenu(int position) {
        tvFM.setTextColor(getResources().getColor(R.color.mulit_select_no));
        tvMP3.setTextColor(getResources().getColor(R.color.mulit_select_no));
        tvBlue.setTextColor(getResources().getColor(R.color.mulit_select_no));
        tvAUX.setTextColor(getResources().getColor(R.color.mulit_select_no));

        imgFM.setImageResource(R.drawable.img_radio_no);
        imgMP3.setImageResource(R.drawable.img_usb_no);
        imgBlue.setImageResource(R.drawable.img_bluetooth_no);
        imgAUX.setImageResource(R.drawable.img_aux_no);

        viewPage.setCurrentItem(position);
        switch (position) {
            case 1:
                if ((Boolean) llFM.getTag()) {
                    tvTitleName.setText("FM-RADIO");
                    tvFM.setTextColor(getResources().getColor(R.color.mulit_select));
                    imgFM.setImageResource(R.drawable.img_radio);
                    changeMode("03");
                    setFalse(llFM);
                } else {
                    changeMode("02");
                    llFM.setTag(true);
                }
                break;
            case 2:
                if ((Boolean) llMP3.getTag()) {
                    tvTitleName.setText("MP3");
                    tvMP3.setTextColor(getResources().getColor(R.color.mulit_select));
                    imgMP3.setImageResource(R.drawable.img_usb);
                    changeMode("04");
                    setFalse(llMP3);
                } else {
                    changeMode("02");
                    llMP3.setTag(true);
                }
                break;
            case 3:
                if ((Boolean) llBlue.getTag()) {
                    tvTitleName.setText("Bluetooth");
                    tvBlue.setTextColor(getResources().getColor(R.color.mulit_select));
                    imgBlue.setImageResource(R.drawable.img_bluetooth);
                    changeMode("05");
                    setFalse(llBlue);
                } else {
                    changeMode("02");
                    llBlue.setTag(true);
                }
                break;
            case 4:
                if ((Boolean) llAux.getTag()) {
                    tvTitleName.setText("AUX");
                    tvAUX.setTextColor(getResources().getColor(R.color.mulit_select));
                    imgAUX.setImageResource(R.drawable.img_aux);
                    changeMode("06");
                    setFalse(llAux);
                } else {
                    changeMode("02");
                    llAux.setTag(true);
                }
                break;
            default:
                break;

        }
        ;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 数据解析成功
            if (action.equalsIgnoreCase(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH)) {
                String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
                if (nowDeviceState.getStrMac().equalsIgnoreCase(strMac)) {
                    Log.e(TAG, strMac + "MultiActivity", null);
                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);
                    updateUI();

                }
            }
        }
    };


    private void changeMode(String strType) {
        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        hashMap.put(BaseVolume.COMMAND_LOC_ZHUJI_ZHUANGTAI, strType);
        byte[] SendByte = CreateControlCMD.getCtrCommandData(nowDeviceState.getStrMac(), nowDeviceState.getNowStateBuffer(), hashMap);
        ControlMainActivity.getMainAct().SendCommand(SendByte);
    }

    private void updateUI() {

        if (nowDeviceState.getFMState() == null) {
            llFM.setVisibility(View.GONE);
        }
        if (nowDeviceState.getMP3State() == null) {
            llMP3.setVisibility(View.GONE);
        }
        if (nowDeviceState.getLanYaState() == null) {
            llBlue.setVisibility(View.GONE);
        }
        if (nowDeviceState.getAuxState() == null) {
            llAux.setVisibility(View.GONE);
        }


        // AUX界面
        int iSound = nowDeviceState.getiYinLiang();
        tvFMSoundV.setText(iSound + "");
        tvMP3SoundV.setText(iSound + "");
        tvBlueSoundV.setText(iSound + "");
        tvAUXSoundV.setText(iSound + "");

        String strBlueState = nowDeviceState.getLanYaState();
        if (strBlueState == null || strBlueState.equalsIgnoreCase("未连接"))
            imgBlueTitle.setImageResource(R.drawable.img_blue_title_offline);
        else {
            imgBlueTitle.setImageResource(R.drawable.img_blue_title);
            imgBluePause.setTag(Boolean.valueOf(strBlueState));
//            if (Boolean.valueOf(strBlueState))
//                imgBluePause.setImageResource(R.drawable.img_start);
//            else
//                imgBluePause.setImageResource(R.drawable.img_puase);
        }

        // MP3界面
        String strMP3State = nowDeviceState.getMP3State();
        if (strMP3State == null) {
            // mp3功能没用
        } else {
            imgMP3Pause.setTag(Boolean.valueOf(strMP3State));
            imgMP3Pause.setImageResource(R.drawable.img_p_r);

            /*if (Boolean.valueOf(strMP3State))
                imgMP3Pause.setImageResource(R.drawable.img_start);
            else*/
        }

        // FM界面
        tvFMValue.setText("FM-" + nowDeviceState.getfFMPinLv());
        tvFMCH.setText("CH" + nowDeviceState.getiFMPinDao());


        // 温度
        int iJianCeWen = nowDeviceState.getiJianCeWenDu();
        int iYuZhiWen = nowDeviceState.getiYuZhiWenDu();
        int iJiaoZhunWen = nowDeviceState.getiWenDuJiaoZhun();
        if (iJianCeWen == 0xff || iYuZhiWen == 0xff || iJiaoZhunWen == 0xff) {
            tvNowTemp.setVisibility(View.GONE);
            tvWillTemp.setVisibility(View.GONE);
            Tv_L.setVisibility(View.GONE);
            Tv_Y.setVisibility(View.GONE);
        } else {
            int iJianCe = nowDeviceState.getiJianCeWenDu();
            int iYuZhi = nowDeviceState.getiYuZhiWenDu();
            String strDanWei = nowDeviceState.getIsSheShiDu();
            if (strDanWei == null) strDanWei = "false";
            boolean isSheShiDu = Boolean.valueOf(strDanWei);
            if (isSheShiDu) {
                tvNowTemp.setText(iJianCe + "℃");
                tvWillTemp.setText(iYuZhi + "℃");
            } else {
                tvNowTemp.setText(iJianCe + "℉");
                tvWillTemp.setText(iYuZhi + "℉");
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
