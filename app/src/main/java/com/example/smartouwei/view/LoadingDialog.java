package com.example.smartouwei.view;

import com.example.smartbathroom.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;



public class LoadingDialog extends Dialog {

	private TextView mTextView;
	private Context mContext;

	public LoadingDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);

		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loading_dialog);
		this.setCanceledOnTouchOutside(false);
		mTextView = (TextView) findViewById(R.id.loading_text);
	}

	public void updateStatusText(String text) {
		this.mTextView.setText(text);
	}


	/**
	 *  设定超时显示
	 * @param iTimeOut 超时时间（秒）
	 */
	public void showAndTime(int iTimeOut) {
		super.show();
		this.iTimeOut = iTimeOut;
		handler.post(runnableTimeOut);
	}

	/** 隐藏并停止计时 */
	public void dismissAndTime() {
		handler.removeCallbacks(runnableTimeOut);
		this.dismiss();
	}

	private int iTimeOut = 0;
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case -999:
					Toast.makeText(mContext, "请求超时！", Toast.LENGTH_SHORT).show();
					dismiss();
					break;

				default:
					break;
			}

		}
	};

	Runnable runnableTimeOut = new Runnable() {
		public void run() {
			--iTimeOut;
			if (iTimeOut > 0) {
				handler.postDelayed(this, 1000);
			}
			else {
				handler.sendEmptyMessage(-999);
			}
		}
	};
}
