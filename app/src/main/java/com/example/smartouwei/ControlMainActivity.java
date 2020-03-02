package com.example.smartouwei;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.MyPagerAdapter;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.controlactivity.CYActivity;
import com.example.smartouwei.controlactivity.DengActivity;
import com.example.smartouwei.controlactivity.DengYGActivity;
import com.example.smartouwei.controlactivity.FBActivity;
import com.example.smartouwei.controlactivity.GBActivity;
import com.example.smartouwei.controlactivity.JRActivity;
import com.example.smartouwei.controlactivity.JSActivity;
import com.example.smartouwei.controlactivity.SBActivity;
import com.example.smartouwei.controlactivity.ZKGActivity;
import com.example.smartouwei.service.ControlService;
import com.example.smartouwei.view.LoadingDialog;
import com.example.smartouwei.view.ViewPagerSlide;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.db.DAO.DeviceInfoDao;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.NetworkUtils;
import com.gisicisky.smasterFitment.utl.XlinkUtils;

import java.util.ArrayList;
import java.util.List;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;

/**
 * Created by Administrator on 2018-07-16.
 */

public class ControlMainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgKaiGuan, imgGuangBo, imgShuiBeng, imgJiaRe, imgJiaReYG, imgDeng, imgFengShan, imgYinYue, imgChouYang, imgJiaShui, imgWenDu;
    private LinearLayout llKaiGuan, llGuangBo, llShuiBeng, llJiaRe, llJiaReYG, llDeng, llFengBeng, llYinYue, llChouYang, llJiaShui, llWenDu;
    private LinearLayout llBottom, container;
    private ViewPagerSlide viewPage;
    private TextView tvTitleName, tvNowTemp, tvWillTemp, Tv_L, Tv_Y, tv_fan;
    private HorizontalScrollView scrollView;
    private RelativeLayout rlBg;
    private List<View> list = new ArrayList<View>();
    private List<String> activityKeyList = new ArrayList<String>();
    private MyPagerAdapter adapter;
    private DeviceInfoCache nowDevice = new DeviceInfoCache();
    private DeviceState nowDeviceState;
    private String strNowDeviceType = "GB";
    private LocalActivityManager manager;
    private static ControlMainActivity controlMinActivity;
    private String TAG;
    private LoadingDialog loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_main);

        TAG = "ControlMainActivity";
        controlMinActivity = this;
        loading = new LoadingDialog(this, R.style.LoadingDialogStyle);

        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);//必须写上这一行代码，不然会报错

        nowDevice = (DeviceInfoCache) getIntent().getSerializableExtra(BaseVolume.DEVICE);
        if (nowDevice == null) {
            nowDevice = new DeviceInfoCache();
        }
        nowDeviceState = DataAnalysisHelper.getSelf(this).getDataBufferByMac(nowDevice.getMac());

        if (nowDeviceState != null) {
            strNowDeviceType = nowDeviceState.getZhuJiType();
        }

        initUI();
        initData();
        reciverBand();

        Intent intentService = new Intent(this, ControlService.class);
        bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }

    private ControlService controlService;
    private ServiceConnection conn;

    {
        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                controlService = ((ControlService.ServiceBinder) service).getService();
                Log.e(TAG, "onServiceConnected: 绑定成功！", null);
            }

            public void onServiceDisconnected(ComponentName name) {
                controlService = null;
            }
        };
    }

    /**
     * 监听广播
     */
    private void reciverBand() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH);
        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void initUI() {
        rlBg = (RelativeLayout) findViewById(R.id.rlBg);
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        imgDeng = (ImageView) findViewById(R.id.imgDeng);
        imgFengShan = (ImageView) findViewById(R.id.imgFengShan);
        container = (LinearLayout) findViewById(R.id.container);
        viewPage = (ViewPagerSlide) findViewById(R.id.viewPage);
        viewPage.setSlide(false);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);
        Tv_L = (TextView) findViewById(R.id.tvL);
        Tv_Y = (TextView) findViewById(R.id.tvY);
        tv_fan = (TextView) findViewById(R.id.tv_fan);


        // 底部控件
        llKaiGuan = (LinearLayout) llBottom.findViewById(R.id.llKaiGuan);
        llGuangBo = (LinearLayout) llBottom.findViewById(R.id.llGuangBo);
        llShuiBeng = (LinearLayout) llBottom.findViewById(R.id.llShuiBeng);
        llJiaRe = (LinearLayout) llBottom.findViewById(R.id.llJiaRe);
        llJiaReYG = (LinearLayout) llBottom.findViewById(R.id.llJiaReYG);
        llDeng = (LinearLayout) llBottom.findViewById(R.id.llDeng);
        llFengBeng = (LinearLayout) llBottom.findViewById(R.id.llFengBeng);
        llYinYue = (LinearLayout) llBottom.findViewById(R.id.llYinYue);
        llChouYang = (LinearLayout) llBottom.findViewById(R.id.llChouYang);
        llJiaShui = (LinearLayout) llBottom.findViewById(R.id.llJiaShui);
        llWenDu = (LinearLayout) llBottom.findViewById(R.id.llWenDu);


        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        tvNowTemp = (TextView) findViewById(R.id.tvNowTemp);
        tvWillTemp = (TextView) findViewById(R.id.tvWillTemp);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.imgToLeft).setOnClickListener(this);
        findViewById(R.id.imgToRight).setOnClickListener(this);

        tvTitleName.setText(getString(R.string.set));
        changeViewByIndex(0);

        // 光波
        if (strNowDeviceType.equalsIgnoreCase("GB")) {
            Tv_L.setTextColor(Color.WHITE);
            Tv_Y.setTextColor(Color.WHITE);
            imgFengShan.setImageResource(R.drawable.discharge_);
            tv_fan.setText(R.string.paiqishan);
            rlBg.setBackgroundResource(R.drawable.img_guangbo_bg);

            llShuiBeng.setVisibility(View.GONE);
            llJiaRe.setVisibility(View.GONE);
            llJiaReYG.setVisibility(View.GONE);
            llJiaShui.setVisibility(View.GONE);

            llKaiGuan.setOnClickListener(gbClickListener);
            llGuangBo.setOnClickListener(gbClickListener);
            llDeng.setOnClickListener(gbClickListener);
            llFengBeng.setOnClickListener(gbClickListener);
            llYinYue.setOnClickListener(gbClickListener);
            llChouYang.setOnClickListener(gbClickListener);
            llWenDu.setOnClickListener(gbClickListener);

            // 光波
            int iGuangBo1 = nowDeviceState.getiGuangBo1();
            int iGuangBo2 = nowDeviceState.getiGuangBo2();
            if (iGuangBo1 == 0xff && iGuangBo2 == 0xff)
                llGuangBo.setVisibility(View.VISIBLE);

            // 灯
            String strZhaoMing = nowDeviceState.getZhaoMing();
            String strBeiDeng = nowDeviceState.getBeiDengColor();
            if (strZhaoMing == null && strBeiDeng == null) // 没有此功能，则隐藏
                llDeng.setVisibility(View.GONE);

            // 气泵
            String strFengBeng = nowDeviceState.getFengBeng();
            if (strFengBeng == null) // 没有此功能，则隐藏
                llFengBeng.setVisibility(View.GONE);

            // 多媒体
            String strFM = nowDeviceState.getFMState();
            String strYinYue = nowDeviceState.getYinYue();
            String strLanYa = nowDeviceState.getLanYaState();
            String strAux = nowDeviceState.getAuxState();
            if (strFM == null && strYinYue == null && strLanYa == null && strAux == null) // 没有此功能，则隐藏
                llYinYue.setVisibility(View.GONE);

            // 臭氧
            String strChou = nowDeviceState.getO3();
            if (strChou == null) // 没有此功能，则隐藏
                llChouYang.setVisibility(View.GONE);

            // 温度
            int iJianCeWen = nowDeviceState.getiJianCeWenDu();
            int iYuZhiWen = nowDeviceState.getiYuZhiWenDu();
            int iJiaoZhunWen = nowDeviceState.getiWenDuJiaoZhun();
            if (iJianCeWen == 0xff || iYuZhiWen == 0xff || iJiaoZhunWen == 0xff) { // 没有此功能，则隐藏
                llWenDu.setVisibility(View.GONE);
                tvNowTemp.setVisibility(View.GONE);
                tvWillTemp.setVisibility(View.GONE);
                Tv_L.setVisibility(View.GONE);
                Tv_Y.setVisibility(View.GONE);
            }
        }
        // 蒸房
        else if (strNowDeviceType.equalsIgnoreCase("ZF")) {
            Tv_L.setTextColor(Color.WHITE);
            Tv_Y.setTextColor(Color.WHITE);
            rlBg.setBackgroundResource(R.drawable.img_shizheng_bg);
            imgFengShan.setImageResource(R.drawable.discharge_);
            tv_fan.setText(R.string.paiqishan);
            llGuangBo.setVisibility(View.GONE);
            llJiaReYG.setVisibility(View.GONE);
            llShuiBeng.setVisibility(View.GONE);
            llJiaShui.setVisibility(View.GONE);

            llKaiGuan.setOnClickListener(zfClickListener);
            llJiaRe.setOnClickListener(zfClickListener);
            llDeng.setOnClickListener(zfClickListener);
            llFengBeng.setOnClickListener(zfClickListener);
            llYinYue.setOnClickListener(zfClickListener);
            llChouYang.setOnClickListener(zfClickListener);
            llWenDu.setOnClickListener(zfClickListener);

            // 加热
            String strJiaRe = nowDeviceState.getJiaRe();
            if (strJiaRe == null) // 没有此功能，则隐藏
            {
                llJiaRe.setVisibility(View.GONE);
                llJiaReYG.setVisibility(View.GONE);
            }

            // 灯
            String strZhaoMing = nowDeviceState.getZhaoMing();
            String strBeiDeng = nowDeviceState.getBeiDengColor();
            if (strZhaoMing == null && strBeiDeng == null) // 没有此功能，则隐藏
                llDeng.setVisibility(View.GONE);

            // 气泵
            String strFengBeng = nowDeviceState.getFengBeng();
            if (strFengBeng == null) // 没有此功能，则隐藏
                llFengBeng.setVisibility(View.GONE);

            // 多媒体
            String strFM = nowDeviceState.getFMState();
            String strYinYue = nowDeviceState.getYinYue();
            String strLanYa = nowDeviceState.getLanYaState();
            String strAux = nowDeviceState.getAuxState();
            if (strFM == null && strYinYue == null && strLanYa == null && strAux == null) // 没有此功能，则隐藏
                llYinYue.setVisibility(View.GONE);

            // 臭氧
            String strChou = nowDeviceState.getO3();
            if (strChou == null) // 没有此功能，则隐藏
                llChouYang.setVisibility(View.GONE);

            // 温度
            int iJianCeWen = nowDeviceState.getiJianCeWenDu();
            int iYuZhiWen = nowDeviceState.getiYuZhiWenDu();
            int iJiaoZhunWen = nowDeviceState.getiWenDuJiaoZhun();
            if (iJianCeWen == 0xff || iYuZhiWen == 0xff || iJiaoZhunWen == 0xff) { // 没有此功能，则隐藏
                llWenDu.setVisibility(View.GONE);
                tvNowTemp.setVisibility(View.GONE);
                tvWillTemp.setVisibility(View.GONE);
                Tv_L.setVisibility(View.GONE);
                Tv_Y.setVisibility(View.GONE);
            }
        }
        // 浴缸
        else if (strNowDeviceType.equalsIgnoreCase("YG")) {
            rlBg.setBackgroundResource(R.drawable.img_yugang_bg);
            imgDeng.setImageResource(R.drawable.floodlight);
            llGuangBo.setVisibility(View.GONE);
            llJiaRe.setVisibility(View.GONE);

            llKaiGuan.setOnClickListener(ygClickListener);
            llShuiBeng.setOnClickListener(ygClickListener);
            llJiaReYG.setOnClickListener(ygClickListener);
            llDeng.setOnClickListener(ygClickListener);
            llFengBeng.setOnClickListener(ygClickListener);
            llYinYue.setOnClickListener(ygClickListener);
            llChouYang.setOnClickListener(ygClickListener);
            llJiaShui.setOnClickListener(ygClickListener);
            llWenDu.setOnClickListener(ygClickListener);

            // 水泵
            String strShuiBeng1 = nowDeviceState.getShuiBeng1();
            String strShuiBeng2 = nowDeviceState.getShuiBeng2();
            String strShuiBeng3 = nowDeviceState.getShuiBeng3();
            String strShuiBeng4 = nowDeviceState.getXunHuanBeng();
            if (strShuiBeng1 == null && strShuiBeng2 == null && strShuiBeng3 == null && strShuiBeng4 == null) // 没有此功能，则隐藏
                llShuiBeng.setVisibility(View.GONE);
            // 加热
            String strJiaRe = nowDeviceState.getJiaRe();
            String strKongQi = nowDeviceState.getKongQi();

            if (strJiaRe == null && strKongQi == null) // 没有此功能，则隐藏
            {
                llJiaRe.setVisibility(View.GONE);
                llJiaReYG.setVisibility(View.GONE);
            }
            // 灯
            String strZhaoMing = nowDeviceState.getZhaoMing();
            if (strZhaoMing == null) // 没有此功能，则隐藏
                llDeng.setVisibility(View.GONE);

            // 气泵
            String strFengBeng = nowDeviceState.getFengBeng();
            if (strFengBeng == null) // 没有此功能，则隐藏
                llFengBeng.setVisibility(View.GONE);

            // 多媒体
            String strFM = nowDeviceState.getFMState();
            String strYinYue = nowDeviceState.getYinYue();
            String strLanYa = nowDeviceState.getLanYaState();
            String strAux = nowDeviceState.getAuxState();
            if (strFM == null && strYinYue == null && strLanYa == null && strAux == null) // 没有此功能，则隐藏
                llYinYue.setVisibility(View.GONE);

            // 臭氧
            String strChou = nowDeviceState.getO3();
            if (strChou == null) // 没有此功能，则隐藏
                llChouYang.setVisibility(View.GONE);

            // 加水
            String strShuiWei = nowDeviceState.getShuiWei();
            if (strShuiWei == null) // 没有此功能，则隐藏
                llJiaShui.setVisibility(View.GONE);

            // 加水
            String strJiaShui = nowDeviceState.getJinShui();
            String strFangShui = nowDeviceState.getFangShui();
            if (strJiaShui == null && strFangShui == null) // 没有此功能，则隐藏
                llJiaShui.setVisibility(View.GONE);

            // 温度
            int iJianCeWen = nowDeviceState.getiJianCeWenDu();
            int iYuZhiWen = nowDeviceState.getiYuZhiWenDu();
            int iJiaoZhunWen = nowDeviceState.getiWenDuJiaoZhun();
            if (iJianCeWen == 0xff || iYuZhiWen == 0xff || iJiaoZhunWen == 0xff) { // 没有此功能，则隐藏
                llWenDu.setVisibility(View.GONE);
                tvNowTemp.setVisibility(View.GONE);
                tvWillTemp.setVisibility(View.GONE);
                Tv_L.setVisibility(View.GONE);
                Tv_Y.setVisibility(View.GONE);
            }
        }
        updateUI();
    }

    private void initData() {
        // 光波：总开关，光波，灯，气泵，臭氧
        // 蒸房：总开关，加热，灯，气泵，臭氧
        // 浴缸：总开关，水泵，加热，灯，气泵，臭氧，进水
        // 总开关
        Intent intentItem = new Intent(mContext, ZKGActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
        list.add(manager.startActivity(ZKGActivity.getActivityName(), intentItem).getDecorView());//将Activity转化为View然后放入View集合
        activityKeyList.add(ZKGActivity.getActivityName());
        // 光波
        if (strNowDeviceType.equalsIgnoreCase("GB")) {
            Intent intentItem1 = new Intent(getApplicationContext(), GBActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            list.add(manager.startActivity(GBActivity.getActivityName(), intentItem1).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(GBActivity.getActivityName());
        }
        // 水泵
        if (strNowDeviceType.equalsIgnoreCase("YG")) {
            Intent intentItem2 = new Intent(getApplicationContext(), SBActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            list.add(manager.startActivity(SBActivity.getActivityName(), intentItem2).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(SBActivity.getActivityName());
        }
        // 加热
        if (strNowDeviceType.equalsIgnoreCase("ZF") || strNowDeviceType.equalsIgnoreCase("YG")) {
            Intent intentItem3 = new Intent(getApplicationContext(), JRActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            intentItem3.putExtra("Type", strNowDeviceType);
            list.add(manager.startActivity(JRActivity.getActivityName(), intentItem3).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(JRActivity.getActivityName());
        }
        // 灯(光波 和 蒸房的一样)
        if (strNowDeviceType.equalsIgnoreCase("GB") || strNowDeviceType.equalsIgnoreCase("ZF")) {
            Intent intentItem4 = new Intent(getApplicationContext(), DengActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            list.add(manager.startActivity(DengActivity.getActivityName(), intentItem4).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(DengActivity.getActivityName());
        }// 浴缸的灯和其他的有点不同
        else {
            Intent intentItem4 = new Intent(getApplicationContext(), DengYGActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            list.add(manager.startActivity(DengYGActivity.getActivityName(), intentItem4).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(DengYGActivity.getActivityName());
        }
        // 气泵
        Intent intentItem5 = new Intent(getApplicationContext(), FBActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
        intentItem5.putExtra("type", strNowDeviceType);
        list.add(manager.startActivity(FBActivity.getActivityName(), intentItem5).getDecorView());//将Activity转化为View然后放入View集合
        activityKeyList.add(FBActivity.getActivityName());
        // 音乐（跳转页面，则不考虑）
        // 臭氧
        Intent intentItem7 = new Intent(getApplicationContext(), CYActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
        list.add(manager.startActivity(CYActivity.getActivityName(), intentItem7).getDecorView());//将Activity转化为View然后放入View集合
        activityKeyList.add(CYActivity.getActivityName());
        // 加水
        if (strNowDeviceType.equalsIgnoreCase("YG")) {
            Intent intentItem8 = new Intent(getApplicationContext(), JSActivity.class);//这个类的第一个参数是上下文，第二个参数是你需要转化的Activity
            list.add(manager.startActivity(JSActivity.getActivityName(), intentItem8).getDecorView());//将Activity转化为View然后放入View集合
            activityKeyList.add(JSActivity.getActivityName());
        }
        // 温度（跳转页面，则不考虑）
//        container.removeAllViews();
//        container.addView(list.get(0), LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        adapter = new MyPagerAdapter(list);
        viewPage.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgToLeft:
                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                break;
            case R.id.imgToRight:
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                break;
            default:
                break;

        }
    }

    /**
     * 光波按钮的点击事件
     */
    private View.OnClickListener gbClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 光波：总开关，光波，灯，气泵，臭氧
            switch (v.getId()) {
                case R.id.llKaiGuan:
                    tvTitleName.setText(getString(R.string.set));
                    changeViewByIndex(0);
//                    viewPage.setCurrentItem(0);
                    break;
                case R.id.llGuangBo:
                    tvTitleName.setText(R.string.guangbo);
                    changeViewByIndex(1);
//                    viewPage.setCurrentItem(1);
                    break;
                case R.id.llDeng:
                    tvTitleName.setText(R.string.light);
                    changeViewByIndex(2);
//                    viewPage.setCurrentItem(2);
                    break;
                case R.id.llFengBeng:
                    tvTitleName.setText(R.string.paiqishan);
                    changeViewByIndex(3);
//                    viewPage.setCurrentItem(3);
                    break;
                case R.id.llYinYue:
                    startActivity(new Intent(mContext, MultiActivity.class));
                    break;
                case R.id.llChouYang:
                    tvTitleName.setText(R.string.antibacteria);
                    changeViewByIndex(4);
//                    viewPage.setCurrentItem(4);
                    break;
                case R.id.llWenDu:
                    startActivity(new Intent(mContext, ControlTemperActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 蒸房按钮的点击事件
     */
    private View.OnClickListener zfClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 蒸房：总开关，加热，灯，气泵，臭氧
            switch (v.getId()) {
                case R.id.llKaiGuan:
                    tvTitleName.setText(R.string.set);
                    changeViewByIndex(0);
//                    viewPage.setCurrentItem(0);
                    break;
                case R.id.llJiaRe:
                    tvTitleName.setText(R.string.zhengqi);
                    changeViewByIndex(1);
//                    viewPage.setCurrentItem(1);
                    break;
                case R.id.llDeng:
                    tvTitleName.setText(R.string.light);
                    changeViewByIndex(2);
//                    viewPage.setCurrentItem(2);
                    break;
                case R.id.llFengBeng:
                    tvTitleName.setText(R.string.paiqishan);
                    changeViewByIndex(3);
//                    viewPage.setCurrentItem(3);
                    break;
                case R.id.llYinYue:
                    startActivity(new Intent(mContext, MultiActivity.class));
                    break;
                case R.id.llChouYang:
                    tvTitleName.setText(R.string.antibacteria);
                    changeViewByIndex(4);
//                    viewPage.setCurrentItem(4);
                    break;
                case R.id.llWenDu:
                    startActivity(new Intent(mContext, ControlTemperActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 浴缸按钮的点击事件
     */
    private View.OnClickListener ygClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 浴缸：总开关，水泵，加热，灯，气泵，臭氧，进水
            switch (v.getId()) {
                case R.id.llKaiGuan:
                    tvTitleName.setText(getString(R.string.set));
                    changeViewByIndex(0);
//                    viewPage.setCurrentItem(0);
                    break;
                case R.id.llShuiBeng:
                    tvTitleName.setText(getString(R.string.water_pump));
                    changeViewByIndex(1);
//                    viewPage.setCurrentItem(1);
                    break;
                case R.id.llJiaReYG:
                    tvTitleName.setText(getString(R.string.heat));
                    changeViewByIndex(2);
//                    viewPage.setCurrentItem(2);
                    break;

                case R.id.llDeng:
                    tvTitleName.setText(getString(R.string.light));
                    changeViewByIndex(3);
//                    viewPage.setCurrentItem(3);
                    break;
                case R.id.llFengBeng:
                    tvTitleName.setText(getString(R.string.fan));
                    changeViewByIndex(4);
//                    viewPage.setCurrentItem(4);
                    break;
                case R.id.llYinYue:
                    startActivity(new Intent(mContext, MultiActivity.class));
                    break;
                case R.id.llChouYang:
                    tvTitleName.setText(getString(R.string.antibacteria));
                    changeViewByIndex(5);
//                    viewPage.setCurrentItem(5);
                    break;
                case R.id.llJiaShui:
                    tvTitleName.setText(getString(R.string.water));
                    changeViewByIndex(6);
//                    viewPage.setCurrentItem(6);
                    break;
                case R.id.llWenDu:
                    startActivity(new Intent(mContext, ControlTemperActivity.class));
                    break;
                default:
                    break;

            }
        }
    };

    private void changeViewByIndex(int iNumber) {

        viewPage.setCurrentItem(iNumber);

//        container.removeAllViews();
//        container.addView(list.get(iNumber), LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

    }

    public static ControlMainActivity getMainAct() {
        return controlMinActivity;
    }

    public DeviceState getDeviceState() {
        if (nowDeviceState == null) {
            nowDeviceState = new DeviceState("12345678");
        }
        return nowDeviceState;
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
                if (nowDevice.getMac().equalsIgnoreCase(strMac)) {
                    Log.e(TAG, strMac + ",数据更新！", null);
                    loading.dismissAndTime();

                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);

                    updateUI();
//                    nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(strMac);

                }
            }
        }
    };

    private void updateUI() {
        // 标题栏的温度
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


    /**
     * 发送数据
     */
    public void SendCommand(byte[] SendData) {

//        loading.showAndTime(5);

        String strData = NetworkUtils.bytesToHexString(SendData);

        Intent intent1 = new Intent(BaseVolume.CONTROL_DEVICE);
        intent1.putExtra("device", nowDevice);
        intent1.putExtra("data", strData);
        controlService.onStartCommand(intent1, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (String strKey : activityKeyList) {
            manager.destroyActivity(strKey, true);
        }

        unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);

    }
}
