package com.example.smartouwei.view;

import com.example.smartbathroom.R;
import com.gisicisky.smasterFitment.utl.XlinkUtils;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetValueAreaAddWindow extends  Dialog implements View.OnClickListener {
	private Context context;
	private Button confirmBtn;
	private Button cancelBtn;
	private EditText tvContent;
	private boolean isShowTost;

	private TextView titleTv,titleTvTwo;
	private PeriodListener listener;
	private String strTitle = "";
	private String strTitleTwo = "";
	private String defaultName = "";
	private boolean isCheckPwd = false;

	private InputFilter inputFilter = null;

	public SetValueAreaAddWindow(Context context) {
		super(context);
		this.context = context;
	}

	public SetValueAreaAddWindow(Context context, int theme, String strTitle,String strTitleTwo,PeriodListener listener,String defaultName,boolean isTost) {
		super(context, theme);
		this.context = context;
		this.strTitle = strTitle;
		this.strTitleTwo = strTitleTwo;
		this.listener = listener;
		this.defaultName = defaultName;
		this.isShowTost = isTost;
	}
	public SetValueAreaAddWindow(Context context, int theme, String strTitle,String strTitleTwo,PeriodListener listener,String defaultName,boolean isTost,boolean isCheckPwd) {
		super(context, theme);
		this.context = context;
		this.strTitle = strTitle;
		this.strTitleTwo = strTitleTwo;
		this.listener = listener;
		this.defaultName = defaultName;
		this.isShowTost = isTost;
		this.isCheckPwd = isCheckPwd;

	}



	/****
	 *
	 * @author mqw
	 *
	 */
	public interface PeriodListener {
		public void refreshListener(String string);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.window_area_set_value);
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		tvContent = (EditText) findViewById(R.id.areaName);
		titleTv = (TextView) findViewById(R.id.dialog_title);
		titleTvTwo = (TextView) findViewById(R.id.dialog_title_two);
		titleTv.setText(strTitle);
		titleTvTwo.setText(strTitleTwo);

		if (isShowTost) {
			findViewById(R.id.view1).setVisibility(View.GONE);
			cancelBtn.setVisibility(View.GONE);
		}

		if (isCheckPwd) {

		}

		tvContent.setFilters(new InputFilter[]{inputFilter});
		tvContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		tvContent.setText(defaultName);

	}

	/** 设置输入框内格式--长度 */
	public void setInputFilter(InputFilter inputFilter) {
		this.inputFilter = inputFilter;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			case R.id.cancel_btn:
				InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				dismiss();
				break;
			case R.id.confirm_btn:
				String strPeriod = tvContent.getText().toString();

				if (strPeriod.equals("")) {
//				XlinkUtils.shortTips("值不能为空！");
					return;
				}
				if (isCheckPwd) {
					if (strPeriod.length() != 4) {
						XlinkUtils.shortTips("请输入四位数密码！");
						return;
					}
				}

				InputMethodManager imm1 = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm1.hideSoftInputFromWindow(v.getWindowToken(), 0);

				listener.refreshListener(strPeriod);
				dismiss();
				break;

			default:
				break;
		}
	}



}