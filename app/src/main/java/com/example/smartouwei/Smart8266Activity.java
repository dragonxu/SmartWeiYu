package com.example.smartouwei;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

import com.example.mytt.helper.NetworkUtilsUDP;
import com.example.mytt.helper.SmartLinkExportObject;
import com.example.mytt.helper.UdpServer;
import com.example.smartbathroom.R;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.esp32.EspWifiAdminSimple;
import com.example.smartouwei.esp32.EsptouchTask;
import com.example.smartouwei.esp32.IEsptouchListener;
import com.example.smartouwei.esp32.IEsptouchResult;
import com.example.smartouwei.esp32.IEsptouchTask;
import com.example.smartouwei.service.ControlService;
import com.example.smartouwei.view.AreaAddWindowHint;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.db.DAO.DeviceInfoDao;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.XlinkUtils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Smart8266Activity extends BaseActivity {

    private Button m_btnConfig;
    private EditText m_edPwd;
    private String SSID, PWD;
    private LinearLayout m_llParent;
    private boolean showPwd = false;
    private UdpServer udpServer = null;
    private TextView m_tvNowSSID;
    private EspWifiAdminSimple mWifiAdmin;
    private IEsptouchTask mEsptouchTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airkiss);

        mWifiAdmin = new EspWifiAdminSimple(this);

        udpServer = new UdpServer(handler);
        udpServer.setListenerType(UdpServer.TYPE_AIRKISS);
        Thread thread = new Thread(udpServer);
        thread.start();

        m_edPwd = (EditText) findViewById(R.id.edPwd);
        m_btnConfig = (Button) findViewById(R.id.tvConfig);
        m_llParent = (LinearLayout) findViewById(R.id.llParent);
        m_tvNowSSID = (TextView) findViewById(R.id.tvNowSSID);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        String ssid = "";
        ssid = wifiInfo.getSSID();
        int version = getAndroidSDKVersion();
        if (version > 13) {
            ssid = ssid.replaceAll("\"", "");
        }
        SSID = ssid;

        m_tvNowSSID.setText(getString(R.string.dangqian_wangluo) + SSID);

        m_btnConfig.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) Smart8266Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                PWD = m_edPwd.getText().toString();
                if (PWD.equals("")) {
                    hintRegister(Smart8266Activity.this.getString(R.string.wenxin_tishi), Smart8266Activity.this.getString(R.string.mima_wei_shuru));
                } else {
                    Log.e("AKeyConfigActivity", "开始配置！");
                    count = 60;
                    find_list.clear();
                    handler.post(runnableTime);

                    String apBssid = mWifiAdmin.getWifiConnectedBssid();
                    // 8266的配置
                    new EsptouchAsyncTask3().execute(SSID, apBssid, PWD, "2");

                }
            }
        });

        findViewById(R.id.btnFinish).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.imgBack).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.llParent).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) Smart8266Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void hintRegister(String title, String content) {
        AreaAddWindowHint pwdDialog = new AreaAddWindowHint(Smart8266Activity.this,
                R.style.Dialogstyle, title,
                new AreaAddWindowHint.PeriodListener() {
                    public void refreshListener(String string) {
                        Log.e("AKeyConfigActivity", "开始配置！");
                        count = 60;
                        find_list.clear();
                        handler.post(runnableTime);
                        String apBssid = mWifiAdmin.getWifiConnectedBssid();
                        // 8266的配置
                        new EsptouchAsyncTask3().execute(SSID, apBssid, PWD, "2");
                    }
                }, content);
        pwdDialog.show();
    }

    private int count = 0;
    private List<XDevice> find_list = new ArrayList<XDevice>();
    private static final int CONFIG_DEVICE = 111;
    private final int ESP_8266_CONFIG_SUCCESS = 1122;
    private final int ESP_8266_CONFIG_ERROR = 3344;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONFIG_DEVICE:
                    if (count > 0) {
                        String strCount = " " + find_list.size() + "";
                        showFind(Smart8266Activity.this.getString(R.string.zhengzai_peizhi) + strCount, Smart8266Activity.this.getString(R.string.lianwang_peizhi) + " (" + count + "s)");
                        if (count % 2 == 0) {
                            Log.e("一键配置", "开始查找！");
                            findDevice();
                        }
                    } else {
                        Log.e("一键配置", "配置时间结束！");
                        // 8266停止配置
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                        if (m_popupWindowConfig != null) {
                            m_popupWindowConfig.dismiss();
                        }
                    }
                    break;
                case ESP_8266_CONFIG_SUCCESS:
                    // 时间未到，继续配置
                    if (count > 0) {
                        String apBssid = mWifiAdmin.getWifiConnectedBssid();
                        // 8266的配置
                        new EsptouchAsyncTask3().execute(SSID, apBssid, PWD, "2");
                    }
                    break;
                case ESP_8266_CONFIG_ERROR:
                    // 时间未到，继续配置
                    if (count > 0) {
                        String apBssid = mWifiAdmin.getWifiConnectedBssid();
                        // 8266的配置
                        new EsptouchAsyncTask3().execute(SSID, apBssid, PWD, "2");
                    }
                    break;

                default:
                    break;
            }
        }

    };

    /**
     * 配网倒计时
     */
    private Runnable runnableTime = new Runnable() {
        public void run() {
            if (count > 0) {
                --count;
                handler.sendEmptyMessage(CONFIG_DEVICE);
                handler.postDelayed(this, 1000);
            }

        }
    };

    private void findDevice() {
		Log.e("设备","开始局域网搜索！");
        int ret = XlinkAgent.getInstance().scanDeviceByProductId(BaseVolume.PRODUCTID, scanListener);
        Log.e("设备", "局域网搜索！！！接口返回状态：" + ret, null);
        if (ret < 0) {
//	    	Toast.makeText(mContext, "调用Scan失败!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查找设备回调
     */
    ScanDeviceListener scanListener = new ScanDeviceListener() {
        public void onGotDeviceByScan(XDevice device) {
            JSONObject jo = XlinkAgent.deviceToJson(device);
            Log.e("设备", "找到设备：" + jo.toString());
            DeviceInfoDao mDeviceInfoDao = new DeviceInfoDao(Smart8266Activity.this);
            boolean isFind = mDeviceInfoDao.isDeviceExist(device.getMacAddress(), ControlService.userEmail);
            DeviceInfoCache de = null;
            // 添加数据库
            if (!isFind) {
                de = new DeviceInfoCache(device.getProductId(), Smart8266Activity.this.getString(R.string.xiangfen_zhilv), "0", device.getMacAddress(), device, BaseVolume.DEFAULT_DEVICE_TYPE, ControlService.userEmail);
                mDeviceInfoDao.insertSingleData(de);
                de = mDeviceInfoDao.featchDeviceByID(device.getMacAddress(), ControlService.userEmail);
                de.setOnLine(BaseVolume.DEVICE_NOT_LINE);
                Intent intent = new Intent();
                intent.setAction(BaseVolume.ADD_DEVICE);
                intent.putExtra(BaseVolume.DEVICE, de);
                Smart8266Activity.this.sendBroadcast(intent);
                mDeviceInfoDao.closeDb();
            } else {
                Smart8266Activity.this.sendBroadcast(new Intent(BaseVolume.REASH_DEVICE_STATE).putExtra(BaseVolume.DEVICE_MAC, device.getMacAddress()));
            }
            boolean have = false;
            for (XDevice d : find_list) {
                if (d.getMacAddress().equals(device.getMacAddress())) {
                    have = true;
                }
            }
            if (!have) {
                find_list.add(device);
            }
        }
    };

    private View popupWindowWifi = null;
    private TextView popupText = null;
    private TextView popupTime = null;
    private TextView popupCancel = null;
    private TextView popupFinish = null;
    private PopupWindow m_popupWindowConfig = null;
    private GifImageView gif2 = null;

    private void showFind(String sText, String sTime) {

        if (popupWindowWifi == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(Smart8266Activity.this);
            popupWindowWifi = layoutInflater.inflate(R.layout.popup_window_find_config_device, null);
            popupText = (TextView) popupWindowWifi.findViewById(R.id.tvFindDeviceText);
            popupTime = (TextView) popupWindowWifi.findViewById(R.id.tvTime);
            popupCancel = (TextView) popupWindowWifi.findViewById(R.id.tvCancel);
            popupFinish = (TextView) popupWindowWifi.findViewById(R.id.tvFinish);
            /**android-gif-drawable
             *底层使用C解析Gif，效率更高，能很好的兼容大容量的Gif图片
             *保持Gif的动画效果，不会发生过快的情况
             */
            gif2 = (GifImageView) popupWindowWifi.findViewById(R.id.gif2);
            gif2.setBackgroundResource(R.drawable.img_wait_gif);
        }
        if (m_popupWindowConfig == null) {
            m_popupWindowConfig = new PopupWindow(popupWindowWifi, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        popupTime.setText(sTime);
        SpannableString spannableString = new SpannableString(sText);
        spannableString.setSpan(new ForegroundColorSpan(Smart8266Activity.this.getResources().getColor(R.color.mo_lv)), sText.indexOf(":") + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        popupText.setText(spannableString);
        popupText.setText(spannableString);

        popupCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                handler.removeCallbacks(runnableTime);
                // 8266停止配置
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
                m_popupWindowConfig.dismiss();
            }
        });
        popupFinish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                handler.removeCallbacks(runnableTime);
                // 8266停止配置
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
                m_popupWindowConfig.dismiss();
                finish();
            }
        });

        if (!m_popupWindowConfig.isShowing()) {
            m_popupWindowConfig.setFocusable(true);
            m_popupWindowConfig.showAtLocation(m_llParent, Gravity.CENTER, 0, 0);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (udpServer != null) {
                    //关闭UDP
                    udpServer.setUdpLife(false);
                    while (udpServer.getLifeMsg()) ; //等待udp阻塞结束，这里就体现出超时的好处了
                    Looper.getMainLooper();
                }
            }
        });
        thread.start();
    }

    /**
     * 获取SDK版本号
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {

                // !!!NOTICE
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);

                Log.e("doInBackground", "开始配置：apSSID = " + apSsid + "，apBssis = " + apBssid + "，apPassword = " + apPassword + "，taskResultCountStr = " + taskResultCountStr);

                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, mContext);
                // 配置成功的回调
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    // 这里是配置成功之后的一些参数返回，包括模块的mac地址，IP等信息
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("MAC："
                                + resultInList.getBssid()
                                + "\n IP："
                                + resultInList.getInetAddress().getHostAddress());
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                    Log.e("onPostExecute", "配置结果：" + sb.toString());

                } else {
                    Log.e("onPostExecute", "配置结束！");

                }
            }
        }
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            public void run() {
//				handler.sendEmptyMessage(ESP_8266_CONFIG_SUCCESS);

                // 提示模块已经配置成功！
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(mContext, text,
                        Toast.LENGTH_LONG).show();
            }

        });
    }

}
