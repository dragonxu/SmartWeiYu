package com.example.smartouwei.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.MenuSelectAdapter;

public class SelectTimeWindowDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private PeriodListener periodListener;
    private MenuSelectAdapter adapter;

    private PickerView pickerStartHour, pickerStartMin, pickerStopHour, pickerStopMin;

    List<String> strHourList = new ArrayList<String>();
    List<String> strMinList = new ArrayList<String>();

    private String nowStartHour, nowStartMin, nowStopHour, nowStopMin;
    private boolean IsTwo = false;
    private String title1, title2;
    private TextView tvTitle1, tvTitle2;
    private LinearLayout llTime2;

    public SelectTimeWindowDialog(Context context) {
        super(context);
        this.context = context;
    }

    public SelectTimeWindowDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public SelectTimeWindowDialog(Context context, int theme, boolean isTwo, PeriodListener listener) {
        super(context, theme);
        this.context = context;

        this.IsTwo = isTwo;
        this.periodListener = listener;
    }

    public interface PeriodListener {
        public void refreshListener(String Hour, String Min);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.window_select_time);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);

        initUI();

    }

    public void setListener(PeriodListener listener) {
        this.periodListener = listener;
    }

    private void initUI() {

        for (int i = 0; i < 24; i++) {
            strHourList.add((i < 10 ? "0" + i : i + ""));
        }
        for (int i = 0; i < 60; i++) {
            strMinList.add((i < 10 ? "0" + i : i + ""));
        }

        llTime2 = (LinearLayout) findViewById(R.id.llTwo);

        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
        tvTitle2 = (TextView) findViewById(R.id.tvTitle2);

        pickerStartHour = (PickerView) findViewById(R.id.pickerStartHour);
        pickerStartMin = (PickerView) findViewById(R.id.pickerStartMin);
        pickerStopHour = (PickerView) findViewById(R.id.pickerStopHour);
        pickerStopMin = (PickerView) findViewById(R.id.pickerStopMin);

//		pickerStartHour.setData(strHourList);
        pickerStartHour.setData(strHourList);
        pickerStartMin.setData(strMinList);
        pickerStopHour.setData(strHourList);
        pickerStopMin.setData(strMinList);

        pickerStartHour.setSelected(nowStartHour);
        pickerStartMin.setSelected(nowStartMin);

//		tvTitle1.setText(title1);
        tvTitle2.setText(title2);
        if (IsTwo) {
            llTime2.setVisibility(View.VISIBLE);
            pickerStopHour.setSelected(nowStopHour);
            pickerStopMin.setSelected(nowStopMin);
        }

        findViewById(R.id.tvConfirm).setOnClickListener(this);

    }

    public void setValue(String strTitle, String strValue) {
        this.title1 = strTitle;
        if (strValue.indexOf("-1") != -1) {
            nowStartHour = "12";
            nowStartMin = "00";
        } else {
            String[] startTime = strValue.split(":");
            nowStartHour = startTime[0];
            nowStartMin = startTime[1];
        }
    }

    public void setValue(String strTitle1, String strValue1, String strTitle2, String strValue2) {
        this.title1 = strTitle1;
        String[] startTime = strValue1.split(":");
        nowStartHour = startTime[0];
        nowStartMin = startTime[1];

        this.title2 = strTitle2;
        String[] stopTime = strValue2.split(":");
        nowStopHour = stopTime[0];
        nowStopMin = stopTime[1];
    }

    private void setPullLvHeight(ListView pull) {
        int totalHeight = 0;
        ListAdapter adapter = pull.getAdapter();
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            View listItem = adapter.getView(i, null, pull);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        LayoutParams params = pull.getLayoutParams();
        params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
        pull.setLayoutParams(params);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvConfirm:
            /*String strStart = pickerStartHour.getNowSelectValue() + ":" + pickerStartMin.getNowSelectValue();
            String strStop = pickerStopHour.getNowSelectValue() + ":" + pickerStopMin.getNowSelectValue();*/
                periodListener.refreshListener(pickerStartHour.getNowSelectValue(), pickerStartMin.getNowSelectValue());
                dismiss();
                break;

            default:
                break;
        }
    }


}