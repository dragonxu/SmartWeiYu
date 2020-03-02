package com.example.smartouwei;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;

import java.net.InetAddress;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartbathroom.R;
import com.example.smartouwei.base.BaseActivity;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.http.HttpManage;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.XlinkUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @author Denny
 *
 */
public class ShareDeviceActivity extends BaseActivity {

	private DeviceInfoCache nowDevice= null;
	private TextView m_tvName,m_tvDeviceType;
	private ImageView m_imgShare;
	private String data = "";
	private int QR_WIDTH = 700, QR_HEIGHT = 700;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_share);

		nowDevice = (DeviceInfoCache) getIntent().getSerializableExtra(BaseVolume.DEVICE);
		initUI();

		String strShareCode = getIntent().getStringExtra("invite_code");
		createQRcode(strShareCode);
//		HttpManage.getInstance().shareDeviceByQrcode(nowDevice.getXDevice().getDeviceId(), new HttpManage.ResultCallback<String>() {
//			public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {
//				Log.e("ShareDeviceActivity", "二维码分享，接口调用失败："+error.toString());
//			}
//			public void onSuccess(int code, String response) {
//				// 分享成功
//				if (code == 200) {
//					// {"invite_code":"120fa2b634395002"}
//					Log.e("ShareDeviceActivity", "二维码分享成功："+code+"，邀请码："+response);
//					try {
//						JSONObject jsobject = new JSONObject(response);
//						String strCode = jsobject.optString("invite_code");
//						createQRcode(strCode);
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//    	});

	}

	private void initUI() {

		findViewById(R.id.img_left).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
//			data = "gicisky_"+nowDevice.getName()+"_"+nowDevice.getMac()+"_"+nowDevice.getXDevice().getDeviceId();
		String strXdevice = nowDevice.getxDeviceString();
		strXdevice = strXdevice.replace("_", "");
//			data = "gicisky_"+nowDevice.getMac()+"_"+"0"+"_"+nowDevice.getXDevice().getDeviceId();
//			data = "gicisky_"+nowDevice.getMac()+"_"+"0"+"_"+strXdevice;

		XDevice xdevice = nowDevice.getXDevice();
		String strInit = "0";
		if (xdevice.isInit()) {
			strInit = "1";
		}

//			XlinkUtils.shortTips("分享："+data);
		Log.e("分享数据", "分享："+data);
		m_tvName = (TextView) findViewById(R.id.tvDeviceName);
		m_tvDeviceType = (TextView) findViewById(R.id.tvDeviceType);
		m_imgShare = (ImageView) findViewById(R.id.imgShare);

		m_tvName.setText(nowDevice.getName());
		m_tvDeviceType.setText("MAC:"+nowDevice.getMac());
	}

	private void createQRcode(String inviteCode) {
		XDevice xdevice = nowDevice.getXDevice();

		String strInit = "0";
		if (xdevice.isInit()) {
			strInit = "1";
		}
		data = "";
		data = data + xdevice.getMacAddress()+"_";// mac
		InetAddress inetAddress = xdevice.getAddress();
		data = data + inetAddress.getHostAddress()+"_";// ip
		data = data + xdevice.getDeviceId() +"_";// id
		data = data + xdevice.getPort()+"_";// port
		data = data + strInit+"_";// init
		data = data + xdevice.getAccessKey()+"_";// accesskey
		data = data + xdevice.getSubKey()+"_";// subkey
		data = data + inviteCode+"";// 邀请码
//		XlinkUtils.shortTips("分享："+data);
		Log.e("分享数据", "分享："+data);
		m_tvName.setText(nowDevice.getName());
		m_tvDeviceType.setText("MAC:"+nowDevice.getMac());
		m_imgShare.setImageBitmap(createQRImage(data));
	}

	public Bitmap Create2DCode(String str) throws WriterException {
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

		WindowManager wm = this.getWindowManager();

		int windowWidth = wm.getDefaultDisplay().getWidth();
		int windowWeight = wm.getDefaultDisplay().getHeight();

		int setWidth = (int) (windowWidth*0.75);

		BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, setWidth, setWidth);


		int width = matrix.getWidth();
		int height = matrix.getHeight();
		//二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(matrix.get(x, y)){
					pixels[y * width + x] = 0xff000000;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public Bitmap createQRImage(String url)
	{
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

			WindowManager wm = this.getWindowManager();

			int windowWidth = wm.getDefaultDisplay().getWidth();
			int windowWeight = wm.getDefaultDisplay().getHeight();

			int setWidth = (int) (windowWidth*0.6);
			QR_WIDTH = setWidth;
			QR_HEIGHT = setWidth;

			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
			//显示到一个ImageView上面
//			sweepIV.setImageBitmap(bitmap);
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	protected void onDestroy() {
		super.onDestroy();

	};
}
