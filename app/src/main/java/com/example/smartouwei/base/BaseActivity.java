package com.example.smartouwei.base;

import java.io.Serializable;

//import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class BaseActivity extends Activity implements Serializable{
	
	protected Context mContext;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
	}
	
	public void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}

}
