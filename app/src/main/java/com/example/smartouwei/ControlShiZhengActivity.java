package com.example.smartouwei;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.MyPagerAdapter;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.view.SelectTimeWindowDialog;
import com.example.smartouwei.view.ViewPagerSlide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-07-09.
 */

public class ControlShiZhengActivity extends BaseActivity implements View.OnClickListener{


    //    private ImageView imgGBKaiGuan,imgGBGuangBo,imgGBDeng,imgGBFengBeng,imgGBChouYang,imgGBYinYue,imgGBWenDu;
    private ImageView imgSZKaiGuan,imgSZJiaRe,imgSZDeng,imgSZFengBeng,imgSZChouYang,imgSZYinYue,imgSZWenDu;
//    private ImageView imgYGKaiGuan,imgYGShuiBeng,imgYGJiaRe,imgYGQiPao,imgYGDeng,imgYGJiaShui,imgYGChouYang,imgYGYinYue,imgYGWenDu;

    private LinearLayout llShiZheng;
    private ViewPagerSlide viewPage;
    private TextView tvTitleName,tvNowTemp,tvWillTemp;
    private HorizontalScrollView scrollView;
    private List<View> list = new ArrayList<View>();
    private MyPagerAdapter adapter;


    // 总开关
    private ImageView imgAllSwitch;

    // 加热界面
    private ImageView imgJiaRe1,imgJiaRe2;
    private TextView tvJRYuYueTime;

    // 灯界面
    private ImageView imgDengSwitch1,imgDengSwitch2;


    // 风扇界面
    private ImageView imgFengShan;

    // 风扇界面
    private ImageView imgChouYang;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_shizheng);

        initUI();
        initData();
    }

    private void initUI() {

        llShiZheng = (LinearLayout) findViewById(R.id.layoutShiZheng);
        viewPage = (ViewPagerSlide) findViewById(R.id.viewPage);
        viewPage.setSlide(false);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);

        // 底部控件
        imgSZKaiGuan = (ImageView) llShiZheng.findViewById(R.id.imgKaiGuan);
        imgSZJiaRe = (ImageView) llShiZheng.findViewById(R.id.imgJiaRe);
        imgSZDeng = (ImageView) llShiZheng.findViewById(R.id.imgDeng);
        imgSZFengBeng = (ImageView) llShiZheng.findViewById(R.id.imgFengShan);
        imgSZChouYang = (ImageView) llShiZheng.findViewById(R.id.imgChouYang);
        imgSZYinYue = (ImageView) llShiZheng.findViewById(R.id.imgYinYue);
        imgSZWenDu = (ImageView) llShiZheng.findViewById(R.id.imgWenDu);

        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        tvNowTemp = (TextView) findViewById(R.id.tvNowTemp);
        tvWillTemp = (TextView) findViewById(R.id.tvWillTemp);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.llKaiGuan).setOnClickListener(this);
        findViewById(R.id.llJiaRe).setOnClickListener(this);
        findViewById(R.id.llDeng).setOnClickListener(this);
        findViewById(R.id.llFengShan).setOnClickListener(this);
        findViewById(R.id.llChouYang).setOnClickListener(this);
        findViewById(R.id.llYinYue).setOnClickListener(this);
        findViewById(R.id.llWenDu).setOnClickListener(this);
        findViewById(R.id.imgToLeft).setOnClickListener(this);
        findViewById(R.id.imgToRight).setOnClickListener(this);

    }

    private void initData() {

        View view1 = getLayoutInflater().inflate(R.layout.view_gb_kaiguan,null);
        View view2 = getLayoutInflater().inflate(R.layout.view_sz_jiare,null);
        View view3 = getLayoutInflater().inflate(R.layout.view_sz_deng,null);
        View view4 = getLayoutInflater().inflate(R.layout.view_gb_fengshan,null);
        View view5 = getLayoutInflater().inflate(R.layout.view_gb_chouyang,null);

        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        list.add(view5);
        adapter = new MyPagerAdapter(list);
        viewPage.setAdapter(adapter);

        initUIBy1(view1);
        initUIBy2(view2);
        initUIBy3(view3);
        initUIBy4(view4);
        initUIBy5(view5);


    }

    /** 总开关界面 */
    private void initUIBy1(View view){
        imgAllSwitch = (ImageView) view.findViewById(R.id.imgSwitch);
        imgAllSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    /** 加热界面 */
    private void initUIBy2(View view){
        imgJiaRe1 = (ImageView) view.findViewById(R.id.imgJiaRe);
        imgJiaRe2 = (ImageView) view.findViewById(R.id.imgYuYue);
        tvJRYuYueTime = (TextView) view.findViewById(R.id.tvYuYueTime);

        imgJiaRe1.setOnClickListener(jrOnClickListener);
        imgJiaRe2.setOnClickListener(jrOnClickListener);
        tvJRYuYueTime.setOnClickListener(jrOnClickListener);

    }

    /** 灯界面 */
    private void initUIBy3(View view) {
        imgDengSwitch1 = (ImageView) view.findViewById(R.id.imgDeng1);
        imgDengSwitch2 = (ImageView) view.findViewById(R.id.imgDeng2);

        imgDengSwitch1.setOnClickListener(dengOnClickListener);
        imgDengSwitch2.setOnClickListener(dengOnClickListener);

    }

    /** 风扇界面 */
    private void initUIBy4(View view) {
        imgFengShan = (ImageView) view.findViewById(R.id.imgFengShan);

        imgFengShan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /** 臭氧界面 */
    private void initUIBy5(View view) {
        imgChouYang = (ImageView) view.findViewById(R.id.imgChouYang);

        imgChouYang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /** 加热按钮的点击监听 */
    private View.OnClickListener jrOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgSwitchA:
                    break;
                case R.id.imgSwitchB:
                    break;
                case R.id.imgYuYue:
                    break;
                case R.id.tvYuYueTime:
                    String strTime = tvJRYuYueTime.getText().toString();
                    ShowSelectTimeDialog(strTime);
                    break;
                default:
                    break;
            }
        }
    };

    /** 灯按钮的点击监听 */
    private View.OnClickListener dengOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgDeng1:
                    break;
                case R.id.imgDeng2:
                    break;
                case R.id.imgZiDong:
                    break;
                case R.id.imgQieHuan:
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgBack:
                finish();
                break;

            case R.id.llKaiGuan:
                tvTitleName.setText("总开关");
                viewPage.setCurrentItem(0);
                break;

            case R.id.llJiaRe:
                tvTitleName.setText("加热");
                viewPage.setCurrentItem(1);
                break;

            case R.id.llDeng:
                tvTitleName.setText("灯");
                viewPage.setCurrentItem(2);
                break;

            case R.id.llFengShan:
                tvTitleName.setText("风扇");
                viewPage.setCurrentItem(3);
                break;

            case R.id.llChouYang:
                tvTitleName.setText("臭氧消毒");
                viewPage.setCurrentItem(4);
                break;

            case R.id.llYinYue:
                startActivity(new Intent(mContext,MultiActivity.class));
                break;

            case R.id.llWenDu:
                startActivity(new Intent(mContext,ControlTemperActivity.class));
                break;

            case R.id.imgToLeft:
                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                break;

            case R.id.imgToRight:
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                break;

            default:
                break;

        };

    }


    /** 时间选择的弹窗 */
    private void ShowSelectTimeDialog(String strStart) {
        SelectTimeWindowDialog selectTimeDialog = new SelectTimeWindowDialog(this,R.style.dialog_style, false,new SelectTimeWindowDialog.PeriodListener() {
            public void refreshListener(String strStartTime, String strStopTime) {
                tvJRYuYueTime.setText(strStartTime);
            }
        });
        selectTimeDialog.setValue(getString(R.string.preset_time),strStart);

        selectTimeDialog.show();
    }

}
