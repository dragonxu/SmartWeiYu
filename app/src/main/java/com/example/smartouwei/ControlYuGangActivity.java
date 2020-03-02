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

public class ControlYuGangActivity extends BaseActivity implements View.OnClickListener {
    //    private ImageView imgGBKaiGuan,imgGBGuangBo,imgGBDeng,imgGBFengBeng,imgGBChouYang,imgGBYinYue,imgGBWenDu;
//    private ImageView imgSZKaiGuan,imgSZJiaRe,imgSZDeng,imgSZFengBeng,imgSZChouYang,imgSZYinYue,imgSZWenDu;
    private ImageView imgYGKaiGuan, imgYGShuiBeng, imgYGJiaRe, imgYGQiPao, imgYGDeng, imgYGJiaShui, imgYGChouYang, imgYGYinYue, imgYGWenDu;

    private LinearLayout llYuGang;
    private ViewPagerSlide viewPage;
    private TextView tvTitleName, tvNowTemp, tvWillTemp;
    private HorizontalScrollView scrollView;
    private List<View> list = new ArrayList<View>();
    private MyPagerAdapter adapter;


    // 总开关
    private ImageView imgAllSwitch;

    // 水泵界面
    private ImageView imgShuiBeng1, imgShuiBeng2, imgShuiBeng3;

    // 加热界面
    private ImageView imgJiaRe1, imgJiaRe2;
    private TextView tvYGYuYueTime;

    // 气泡界面
    private ImageView imgQiPao;

    // 灯界面
    private ImageView imgShuiDiDeng;

    // 自动进水界面
    private ImageView imgZiDongJinShui;

    // 臭氧界面
    private ImageView imgChouYang;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_yugang);

        initUI();
        initData();
    }

    private void initUI() {

        llYuGang = (LinearLayout) findViewById(R.id.layoutYuGang);
        viewPage = (ViewPagerSlide) findViewById(R.id.viewPage);
        viewPage.setSlide(false);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);

        // 底部控件
        imgYGKaiGuan = (ImageView) llYuGang.findViewById(R.id.imgKaiGuan);
        imgYGShuiBeng = (ImageView) llYuGang.findViewById(R.id.imgShuiBeng);
        imgYGJiaRe = (ImageView) llYuGang.findViewById(R.id.imgJiaRe);
        imgYGQiPao = (ImageView) llYuGang.findViewById(R.id.imgQiPao);
        imgYGYinYue = (ImageView) llYuGang.findViewById(R.id.imgYinYue);
        imgYGDeng = (ImageView) llYuGang.findViewById(R.id.imgDengGuang);
        imgYGChouYang = (ImageView) llYuGang.findViewById(R.id.imgChouYang);
        imgYGJiaShui = (ImageView) llYuGang.findViewById(R.id.imgJiaShui);
        imgYGWenDu = (ImageView) llYuGang.findViewById(R.id.imgWenDu);

        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        tvNowTemp = (TextView) findViewById(R.id.tvNowTemp);
        tvWillTemp = (TextView) findViewById(R.id.tvWillTemp);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.llKaiGuan).setOnClickListener(this);
        findViewById(R.id.llShuiBeng).setOnClickListener(this);
        findViewById(R.id.llJiaRe).setOnClickListener(this);
        findViewById(R.id.llQiPao).setOnClickListener(this);
        findViewById(R.id.llYinYue).setOnClickListener(this);
        findViewById(R.id.llDengGuang).setOnClickListener(this);
        findViewById(R.id.llChouYang).setOnClickListener(this);
        findViewById(R.id.llJiaShui).setOnClickListener(this);
        findViewById(R.id.llWenDu).setOnClickListener(this);
        findViewById(R.id.imgToLeft).setOnClickListener(this);
        findViewById(R.id.imgToRight).setOnClickListener(this);

    }

    private void initData() {

        View view1 = getLayoutInflater().inflate(R.layout.view_gb_kaiguan, null);
        View view2 = getLayoutInflater().inflate(R.layout.view_yg_shuibeng, null);
        View view3 = getLayoutInflater().inflate(R.layout.view_yg_jiare, null);
        View view4 = getLayoutInflater().inflate(R.layout.view_yg_paopaoyu, null);
        View view5 = getLayoutInflater().inflate(R.layout.view_yg_shuidideng, null);
        View view6 = getLayoutInflater().inflate(R.layout.view_gb_chouyang, null);
        View view7 = getLayoutInflater().inflate(R.layout.view_yg_zidongjinshui, null);

        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        list.add(view5);
        list.add(view6);
        list.add(view7);
        adapter = new MyPagerAdapter(list);
        viewPage.setAdapter(adapter);

        initUIBy1(view1);
        initUIBy2(view2);
        initUIBy3(view3);
        initUIBy4(view4);
        initUIBy5(view5);
        initUIBy6(view6);
        initUIBy7(view7);


    }

    /**
     * 总开关界面
     */
    private void initUIBy1(View view) {
        imgAllSwitch = (ImageView) view.findViewById(R.id.imgSwitch);
        imgAllSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    /**
     * 水泵界面
     */
    private void initUIBy2(View view) {
        imgShuiBeng1 = (ImageView) view.findViewById(R.id.imgSwitch1);
        imgShuiBeng2 = (ImageView) view.findViewById(R.id.imgSwitch2);
        imgShuiBeng3 = (ImageView) view.findViewById(R.id.imgSwitch3);

        imgShuiBeng1.setOnClickListener(sbOnClickListener);
        imgShuiBeng2.setOnClickListener(sbOnClickListener);
        imgShuiBeng3.setOnClickListener(sbOnClickListener);

    }

    /**
     * 加热界面
     */
    private void initUIBy3(View view) {
        imgJiaRe1 = (ImageView) view.findViewById(R.id.imgJiaRe);
        imgJiaRe2 = (ImageView) view.findViewById(R.id.imgYuYue);
        tvYGYuYueTime = (TextView) view.findViewById(R.id.tvYuYueTime);

        imgJiaRe1.setOnClickListener(jrOnClickListener);
        imgJiaRe2.setOnClickListener(jrOnClickListener);
        tvYGYuYueTime.setOnClickListener(jrOnClickListener);

    }

    /**
     * 泡泡浴界面
     */
    private void initUIBy4(View view) {
        imgQiPao = (ImageView) view.findViewById(R.id.imgSwitch);
        imgQiPao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 水底灯界面
     */
    private void initUIBy5(View view) {
        imgShuiDiDeng = (ImageView) view.findViewById(R.id.imgSwitch);
        imgShuiDiDeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 臭氧界面
     */
    private void initUIBy6(View view) {
        imgChouYang = (ImageView) view.findViewById(R.id.imgChouYang);

        imgChouYang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 自动进水界面
     */
    private void initUIBy7(View view) {
        imgZiDongJinShui = (ImageView) view.findViewById(R.id.imgSwitch);

        imgZiDongJinShui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /**
     * 水泵按钮的点击监听
     */
    private View.OnClickListener sbOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgSwitch1:
                    break;
                case R.id.imgSwitch2:
                    break;
                case R.id.imgSwitch3:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 加热按钮的点击监听
     */
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
                    String strTime = tvYGYuYueTime.getText().toString();
                    ShowSelectTimeDialog(strTime);
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
                tvTitleName.setText(getString(R.string.set));
                viewPage.setCurrentItem(0);
                break;

            case R.id.llShuiBeng:
                tvTitleName.setText(getString(R.string.water_pump));
                viewPage.setCurrentItem(1);
                break;

            case R.id.llJiaRe:
                tvTitleName.setText("加热");
                viewPage.setCurrentItem(2);
                break;

            case R.id.llQiPao:
                tvTitleName.setText("泡泡浴");
                viewPage.setCurrentItem(3);
                break;

            case R.id.llYinYue:
                startActivity(new Intent(mContext, MultiActivity.class));

                break;

            case R.id.llDengGuang:
                tvTitleName.setText("水底灯");
                viewPage.setCurrentItem(4);
                break;

            case R.id.llChouYang:
                tvTitleName.setText("臭氧消毒");
                viewPage.setCurrentItem(5);
                break;
            case R.id.llJiaShui:
                tvTitleName.setText("自动进水");
                viewPage.setCurrentItem(7);
                break;
            case R.id.llWenDu:
                startActivity(new Intent(mContext, ControlTemperActivity.class));
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
        ;

    }


    /**
     * 时间选择的弹窗
     */
    private void ShowSelectTimeDialog(String strStart) {
        SelectTimeWindowDialog selectTimeDialog = new SelectTimeWindowDialog(this, R.style.dialog_style, false, new SelectTimeWindowDialog.PeriodListener() {
            public void refreshListener(String strStartTime, String strStopTime) {
                tvYGYuYueTime.setText(strStartTime);
            }
        });
        selectTimeDialog.setValue(getString(R.string.preset_time), strStart);

        selectTimeDialog.show();
    }

}
