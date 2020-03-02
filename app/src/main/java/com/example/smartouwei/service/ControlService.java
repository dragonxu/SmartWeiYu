package com.example.smartouwei.service;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;
import io.xlink.wifi.sdk.listener.XlinkNetListener;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smartouwei.base.MyApp;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.db.DAO.DBContent.DeviceInfo;
import com.gisicisky.smasterFitment.db.DAO.DeviceInfoDao;
import com.gisicisky.smasterFitment.http.HttpManage;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.CrashHandler;
import com.gisicisky.smasterFitment.utl.MyLog;
import com.gisicisky.smasterFitment.utl.NetworkUtils;
import com.gisicisky.smasterFitment.utl.SharedPreferencesUtil;
import com.gisicisky.smasterFitment.utl.XlinkUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


@SuppressLint("DefaultLocale")
public class ControlService extends Service implements XlinkNetListener {

    public IBinder onBind(Intent intent) {
        IBinder result = null;
        if (null == result)
            result = new ServiceBinder();
        return result;

    }

    public class ServiceBinder extends Binder {
        public ControlService getService() {
            return ControlService.this;
        }
    }

    private static final String TAG = "ControlService";
    public static int userID = 0;
    private String authKey = "";
    private TelephonyManager tm;
    private String uid, userpwd;
    public static String accessToken;
    public static String nickName = "";
    public static String userEmail = "";
    public static SharedPreferences sharedPreferences;
    /**
     * 局域网数据缓存
     */
    private HashMap<String, String> dataBufferMapLan = new HashMap<String, String>();
    /**
     * 外网数据缓存
     */
    private HashMap<String, String> dataBufferMapWan = new HashMap<String, String>();

    //	public static Map<String, DeviceInfoCache> deviceMap = new HashMap<String, DeviceInfoCache>();
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "启动Service");

        sharedPreferences = getSharedPreferences("XlinkOfficiaDemo", Context.MODE_PRIVATE);
        CrashHandler.init(this);

        XlinkAgent.init(this);
        XlinkAgent.getInstance().addXlinkListener(this);
        userID = SharedPreferencesUtil.queryIntValue(sharedPreferences, "appId");
        authKey = SharedPreferencesUtil.queryValue(sharedPreferences, "authKey", "");
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        userEmail = tm.getDeviceId();

        if (userEmail == null) {
            // android pad
            userEmail = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        }
        userEmail = XlinkUtils.MD5(userEmail);
        userEmail = userEmail.substring(userEmail.length() - 8) + "@qq.com";

    }

    public String versionName;
    public int versionCode;
    public String packageName;
    private static Handler mainHandler = null;
    /**
     * 待连接设备的缓存
     */
    private HashMap<String, DeviceInfoCache> willConnectDeviceMap = new HashMap<String, DeviceInfoCache>();

    public static void initHandler() {
        mainHandler = new Handler();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (action != null && action.equals(BaseVolume.REGISTER_USER)) {
//				String user = intent.getStringExtra(BaseVolume.APP_USER);
//				String pwd = intent.getStringExtra(BaseVolume.APP_PWD);
//				String name = intent.getStringExtra(BaseVolume.APP_NAME);
//				if (NetworkUtils.isZh(ControlService.this))
//					registerUserByMail(user,name,pwd,"zh-cn");
//				else
//					registerUserByMail(user,name,pwd,"en-us");

                if (NetworkUtils.isZh(ControlService.this))
                    registerUserByMail(userEmail, userEmail, userEmail, "zh-cn");
                else
                    registerUserByMail(userEmail, userEmail, userEmail, "en-us");
            } else if (action != null && action.equals(BaseVolume.LOGIN_USER)) {
                String user = intent.getStringExtra(BaseVolume.APP_USER);
                String pwd = intent.getStringExtra(BaseVolume.APP_PWD);
                uid = user;
                userpwd = pwd;
                getAppid(sharedPreferences, user, pwd);
            } else if (action != null && action.equals(BaseVolume.GET_USERINFO)) {
                getUserInf(userID);
            } else if (action != null && action.equals(BaseVolume.FORGOT_PWD)) {
                String user = intent.getStringExtra(BaseVolume.APP_USER);


                if (NetworkUtils.isZh(ControlService.this))
                    forgotPwd(user, "zh-cn");
                else
                    forgotPwd(user, "en-us");

            } else if (action != null && action.equals(BaseVolume.GET_DEVICELIST)) {
                getUserBindDeviceList();
            } else if (action != null && action.equals(BaseVolume.UPDATE_USER_PWD)) {
                String oldPwd = intent.getStringExtra(BaseVolume.OLD_PWD);
                String newPwd = intent.getStringExtra(BaseVolume.NEW_PWD);
                resetUserPwd(newPwd, oldPwd);
            } else if (action != null && action.equals(BaseVolume.UPDATE_USER_NAME)) {
                String name = intent.getStringExtra(BaseVolume.APP_NAME);
                resetUserName(name);
            } else if (action != null && action.equals(BaseVolume.INIT_DEVICE)) {
                ArrayList<DeviceInfoCache> list = (ArrayList<DeviceInfoCache>) intent.getSerializableExtra("devices");
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        // 向sdk初始化保存过的设??
                        int n = XlinkAgent.getInstance().initDevice(list.get(i).getXDevice());
                        Log.e("初始化设备", "初始化设备结果：n = " + n);
//						XlinkUtils.shortTips(list.get(i).getXDevice().getMacAddress()+"初始化设备结果：n = "+n);
                    }
                }
            } else if (action != null && action.equals(BaseVolume.SET_DEVICE_PWD)) {
                DeviceInfoCache device = (DeviceInfoCache) intent.getSerializableExtra("device");
                setDevicePWD(device);
            }
            // 本地设备进行连接时，只需要判断是否有授权码，
            else if (action != null && action.equals(BaseVolume.COMMAND_DEVICE)) {
                DeviceInfoCache device = (DeviceInfoCache) intent.getSerializableExtra("device");
                willConnectDeviceMap.put(device.getMac().toLowerCase(), device);
                // 连接步骤： 1。判断有无授权码，无则设置，2。进行连接。
                // 授权码为空，则需要设置授权码
                if (device.getXDevice().getAccessKey() == -1) {
                    XlinkAgent.getInstance().setDeviceAccessKey(device.getXDevice(), 8888, new SetDeviceAccessKeyListener() {
                        public void onSetLocalDeviceAccessKey(XDevice xdevice, int code, int msgId) {
                            if (code == XlinkCode.SUCCEED) {
                                Log.e("ControlService", xdevice.getMacAddress() + "设置授权码成功！");
                                DeviceInfoCache nowDevice = willConnectDeviceMap.get(xdevice.getMacAddress().toLowerCase());
                                nowDevice.setXDevice(xdevice);
                                willConnectDeviceMap.put(xdevice.getMacAddress().toLowerCase(), nowDevice);
                                connectDevice(nowDevice);
                            } else {
                                Log.e("ControlService", xdevice.getMacAddress() + "设置授权码失败！code = " + code);
                            }
                        }
                    });
                }
                // 有授权码，则直接连接
                else {
                    connectDevice(device);
                }

            }
            // 通过二维码分享过来的设备，连接前需要进行订阅
            else if (action != null && action.equals(BaseVolume.COMMAND_DEVICE_By_Qrcode)) {
                DeviceInfoCache device = (DeviceInfoCache) intent.getSerializableExtra("device");
                willConnectDeviceMap.put(device.getMac().toLowerCase(), device);
                // 步骤： 1。判断有无授权码，无则设置，2。获取订阅码进行订阅，3。订阅成功与否都进行连接。
                // 授权码为空，则需要设置授权码
                if (device.getXDevice().getAccessKey() == -1) {
                    XlinkAgent.getInstance().setDeviceAccessKey(device.getXDevice(), 8888, new SetDeviceAccessKeyListener() {
                        public void onSetLocalDeviceAccessKey(XDevice xdevice, int code, int msgId) {
                            if (code == XlinkCode.SUCCEED) {
                                Log.e("ControlService", xdevice.getMacAddress() + "设置授权码成功！");
                                DeviceInfoCache nowDevice = willConnectDeviceMap.get(xdevice.getMacAddress().toLowerCase());
                                nowDevice.setXDevice(xdevice);
                                willConnectDeviceMap.put(xdevice.getMacAddress().toLowerCase(), nowDevice);
//			            		// 开始订阅
                                startSubDevice(xdevice);
                            } else {
                                Log.e("ControlService", xdevice.getMacAddress() + "设置授权码失败！code = " + code);
                            }
                        }
                    });
                }
                // 有授权码，则进行订阅
                else {
//					Log.e("ControlService", "已经有授权码，则开始订阅！");
                    startSubDevice(device.getXDevice());
                }


            } else if (action != null && action.equals(BaseVolume.CONTROL_DEVICE)) {
                DeviceInfoCache device = (DeviceInfoCache) intent.getSerializableExtra("device");
                String data = intent.getStringExtra("data");
                sendControl(device, data);
            } else if (action != null && action.equals(BaseVolume.DELETE_BINDRELATION)) {
                DeviceInfoCache device = (DeviceInfoCache) intent.getSerializableExtra("device");
                deleteBindRelation(device.getXDevice().getDeviceId());
                XlinkAgent.getInstance().removeDevice(device.getXDevice());
            }
            // 清除缓存数据
//			else if (action != null && action.equals(BaseVolume.CLEAR_DATA_BUFFER_BY_DEVICE)) {
//				String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
//			}

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始订阅设备
     */
    private void startSubDevice(XDevice xdevice) {
        int iSubKey = xdevice.getSubKey();
        Log.e("ControlService", "开始订阅：" + xdevice.getMacAddress());
        XlinkAgent.getInstance().subscribeDevice(xdevice, xdevice.getSubKey(), new SubscribeDeviceListener() {
            public void onSubscribeDevice(XDevice arg0, int arg1) {
                Log.e("ControlService", arg0.getMacAddress().toLowerCase() + "订阅设备,回调：arg1 = " + arg1);
                DeviceInfoCache nowDevice = willConnectDeviceMap.get(arg0.getMacAddress().toLowerCase());
                nowDevice.setXDevice(arg0);
                willConnectDeviceMap.put(arg0.getMacAddress().toLowerCase(), nowDevice);
                // 订阅成功与否都进行连接。
                connectDevice(nowDevice);
            }
        });

    }

    private void resetUserPwd(String newPwd, String oldPwd) {

        HttpManage.getInstance().resetPassword(newPwd, oldPwd, new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {

//				sendBroadcast(new Intent(BaseVolume.UPDATE_USER_PWD).putExtra(BaseVolume.UPDATE_PWD, false).putExtra(BaseVolume.ERROR_CODE, error.getCode()));
            }

            public void onSuccess(int code, String response) {

//				sendBroadcast(new Intent(BaseVolume.UPDATE_USER_PWD).putExtra(BaseVolume.UPDATE_PWD, true));
            }
        });
    }


    private void resetUserName(final String nickname) {
        HttpManage.getInstance().modifyUser(userID, nickname, new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {

            }

            public void onSuccess(int code, String response) {
//				XlinkUtils.shortTips(ControlService.this.getResources().getString(R.string.set_success));
//				nickName = nickname;
//				sendBroadcast(new Intent(BaseVolume.UPDATE_USER_NAME).putExtra(BaseVolume.ADD_DEVICE, nickName));
            }
        });
    }


    private void deleteBindRelation(final int deviceID) {
        HttpManage.getInstance().unSubscribe(userID + "", deviceID, new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {

            }

            public void onSuccess(int code, String response) {

            }
        });
    }

    public void registerUserByMail(final String uid, final String name, final String pwd, String languageType) {

        HttpManage.getInstance().registerUserByMail(uid, name, pwd, languageType, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

                getAppid(sharedPreferences, uid, pwd);

//	            	int code = error.getCode();
//	            	sendBroadcast(new Intent(BaseVolume.REGISTER_ERROR).putExtra(BaseVolume.ERROR_CODE, code));

            }

            @Override
            public void onSuccess(int code, String response) {

//	            	Intent intent = new Intent(BaseVolume.REGISTER_SUCCESS);
//	            	intent.putExtra(BaseVolume.APP_USER, uid);
//	            	intent.putExtra(BaseVolume.APP_PWD, pwd);
//	            	intent.putExtra(BaseVolume.APP_NAME, name);
//	            	sendBroadcast(intent);

                getAppid(sharedPreferences, uid, pwd);
            }
        });
    }

    public void getAppid(final SharedPreferences s, String user, String pwd) {

        HttpManage.getInstance().login(user, pwd, new HttpManage.ResultCallback<Map<String, String>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

                sendBroad(BaseVolume.BROADCAST_ON_LOGIN, XlinkCode.SUCCEED);
            }

            public void onSuccess(int code, Map<String, String> response) {
                authKey = response.get("authorize");
                accessToken = response.get("access_token");
                userID = Integer.parseInt(response.get("user_id"));
                SharedPreferencesUtil.keepShared(s, "appId", userID);
                SharedPreferencesUtil.keepShared(s, "authKey", authKey);
                // 登录
                start();

                int i = XlinkAgent.getInstance().login(userID, authKey);
                Log.e("登录状态", "返回登录状：" + i);
                if (i == -7) {
//					sendBroad(BaseVolume.BROADCAST_ON_LOGIN, XlinkCode.SUCCEED);
                }
            }
        });
    }


    private void getUserInf(int user_id) {
        HttpManage.getInstance().getUserInfo(user_id, new HttpManage.ResultCallback<Map<String, String>>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {
//				sendBroadcast(new Intent(BaseVolume.LOGIN_SUCCESS));
//				sendBroadcast(new Intent(BaseVolume.LOGIN_ERROR).putExtra(BaseVolume.ERROR_CODE, -1));
            }

            public void onSuccess(int code, Map<String, String> response) {
//				nickName = response.get("nickname");
//				userEmail = response.get("email");
//				sendBroadcast(new Intent(BaseVolume.LOGIN_SUCCESS));
            }
        });
    }


    private void getUserBindDeviceList() {
        HttpManage.getInstance().getSubscribeList(userID, 1, new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {
            }

            public void onSuccess(int code, String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject deviceJson = jsonArray.getJSONObject(i);
                        getDeviceInfo(deviceJson, ControlService.this);
                    }
//					sendBroadcast(new Intent(BaseVolume.GET_DEVICELIST_OK));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void forgotPwd(String email, String languageType) {
        HttpManage.getInstance().forgetPasswd(email, languageType, new HttpManage.ResultCallback<Map<String, String>>() {
            public void onError(Header[] headers, com.gisicisky.smasterFitment.http.HttpManage.Error error) {
//				int code = error.getCode();
//            	sendBroadcast(new Intent(BaseVolume.FORGOT_ERROR).putExtra(BaseVolume.ERROR_CODE, code));
            }

            public void onSuccess(int code, Map<String, String> response) {
//				sendBroadcast(new Intent(BaseVolume.FORGOT_SUCCESS));
            }

        });
    }

    private void setDevicePWD(final DeviceInfoCache de) {
        XlinkAgent.getInstance().setDeviceAccessKey(de.getXDevice(), 8888,
                new SetDeviceAccessKeyListener() {
                    public void onSetLocalDeviceAccessKey(XDevice xdevice, int code, int msgId) {
//            	if (code == XlinkCode.SUCCEED) {
//            		connectDevice(de);
//            	}
//            	else {
//        			Intent intent = new Intent(BaseVolume.BROADCAST_DEVICE_STATUE);
//        			intent.putExtra("command_device", de.getMac());
//        			intent.putExtra("xDevice", xdevice);
//
//					intent.putExtra(BaseVolume.BROADCAST_DEVICE_PWD_ERROR, true);
//					sendBroadcast(intent);
//            	}
                    }
                });
    }


    public void connectDevice(DeviceInfoCache device) {
        JSONObject jo = XlinkAgent.deviceToJson(device.getXDevice());
        String strdevice = jo.toString();
        Log.e("ControlService", "连接设备：xDevice=" + strdevice);

//		int ret = XlinkAgent.getInstance().connectDevice(newDevice.getXDevice(),
//				"8888", connectDeviceListener);
//		int ret = XlinkAgent.getInstance().connectDevice(newDevice.getXDevice(),
//				Integer.parseInt("8888"), connectDeviceListener);
        int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), connectDeviceListener);
        Log.e("ControlService", "连接设备，接口调用：ret =" + ret);
        if (ret != 0) {
            switch (ret) {
                case XlinkCode.INVALID_DEVICE_ID:

                    break;
                case XlinkCode.NO_CONNECT_SERVER:

                    break;
                case XlinkCode.NETWORD_UNAVAILABLE:

                    break;
                case XlinkCode.NO_DEVICE:

//				XlinkAgent.getInstance().initDevice(newDevice.getXDevice());
                    break;
                case -7:
//				sendBroadcast(new Intent(BaseVolume.RECONNECTION_STOP));

                    break;
                default:

                    break;
            }

        }
    }


    private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

        public void onConnectDevice(XDevice xDevice, int result) {

            String strXInfo = XlinkAgent.deviceToJson(xDevice).toString();
            Log.e("ControlService", "连接设备，Device:" + strXInfo + "，接口调用 ：result=" + result);
//			XlinkUtils.shortTips("设备连接，Device:" + xDevice.getMacAddress() + "，result="+result);
            String sPwd = xDevice.getVersion() == 1 ? xDevice.getAuthkey() : xDevice.getAccessKey() + "";
            DeviceInfoDao mDeviceInfoDao = new DeviceInfoDao(ControlService.this);
            Intent intent = new Intent(BaseVolume.BROADCAST_DEVICE_STATUE);
            intent.putExtra("command_device", xDevice.getMacAddress());
            intent.putExtra("xDevice", xDevice);
            intent.putExtra("device_pwd", sPwd);

            // 清空缓存数据
            dataBufferMapLan.put(xDevice.getMacAddress().toLowerCase(), "");
            dataBufferMapWan.put(xDevice.getMacAddress().toLowerCase(), "");

            switch (result) {
                // 连接设备成功 设备处于内网
                case XlinkCode.DEVICE_STATE_LOCAL_LINK:

                    ContentValues initialValues = new ContentValues();
                    String sXDevice = XlinkAgent.deviceToJson(xDevice).toString();
                    initialValues.put(DeviceInfo.Columns.xDevice, sXDevice);
                    mDeviceInfoDao.updateDataByDeviceMac(initialValues, xDevice.getMacAddress(), userEmail);
                    mDeviceInfoDao.closeDb();
                    XlinkAgent.getInstance().sendProbe(xDevice);
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, true);
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_CONNECTE_IS_LAN, true);
                    sendBroadcast(intent);

                    int n = XlinkAgent.getInstance().subscribeDevice(xDevice, xDevice.getSubKey(), null);

                    break;
                // 新SDK，远程连接设备成功 设备处于云端
                case 13:
                    ContentValues initialValues11 = new ContentValues();
                    String sXDevice11 = XlinkAgent.deviceToJson(xDevice).toString();
                    initialValues11.put(DeviceInfo.Columns.xDevice, sXDevice11);
                    mDeviceInfoDao.updateDataByDeviceMac(initialValues11, xDevice.getMacAddress(), userEmail);
                    mDeviceInfoDao.closeDb();
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, true);
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_CONNECTE_IS_LAN, false);
                    sendBroadcast(intent);
                    int n1 = XlinkAgent.getInstance().subscribeDevice(xDevice, xDevice.getSubKey(), null);

                    break;
                // 连接设备成功 设备处于云端
                case XlinkCode.DEVICE_STATE_OUTER_LINK:

                    ContentValues initialValues1 = new ContentValues();
                    String sXDevice1 = XlinkAgent.deviceToJson(xDevice).toString();

                    initialValues1.put(DeviceInfo.Columns.xDevice, sXDevice1);
                    mDeviceInfoDao.updateDataByDeviceMac(initialValues1, xDevice.getMacAddress(), userEmail);
                    mDeviceInfoDao.closeDb();
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, true);
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_CONNECTE_IS_LAN, false);
                    sendBroadcast(intent);
                    int n2 = XlinkAgent.getInstance().subscribeDevice(xDevice, xDevice.getSubKey(), null);

                    break;

                case XlinkCode.CONNECT_DEVICE_INVALID_KEY:

                    ContentValues initialValues2 = new ContentValues();
                    String sXDevice2 = XlinkAgent.deviceToJson(xDevice).toString();
//					initialValues2.put(DeviceInfo.Columns.devicePwd,"8888");
                    initialValues2.put(DeviceInfo.Columns.xDevice, sXDevice2);
                    mDeviceInfoDao.updateDataByDeviceMac(initialValues2, xDevice.getMacAddress(), userEmail);
                    mDeviceInfoDao.closeDb();
//					intent.putExtra(BaseVolume.BROADCAST_DEVICE_PWD_ERROR, true);
//					sendBroadcast(intent);
                    break;

                case XlinkCode.CONNECT_DEVICE_OFFLINE:

                    ContentValues initialValues3 = new ContentValues();
                    String sXDevice3 = XlinkAgent.deviceToJson(xDevice).toString();
                    initialValues3.put(DeviceInfo.Columns.devicePwd, sPwd);
                    initialValues3.put(DeviceInfo.Columns.xDevice, sXDevice3);
                    mDeviceInfoDao.updateDataByDeviceMac(initialValues3, xDevice.getMacAddress(), userEmail);
                    mDeviceInfoDao.closeDb();
                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                    sendBroadcast(intent);
                    break;

                case XlinkCode.CONNECT_DEVICE_TIMEOUT:

                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                    sendBroadcast(intent);
                    break;

                case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:

                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                    sendBroadcast(intent);
                    break;
                case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:

                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                    sendBroadcast(intent);
                    break;
                default:

                    intent.putExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                    sendBroadcast(intent);
                    break;
            }
        }
    };


    private DeviceInfoCache nBufferDevice = null;
    private String sBufferData = "";

    public void sendControl(DeviceInfoCache now, String data) {
        Log.e("数据打印", now.getMac() + "ControlService，发送数据：" + data);

        XDevice xdevice = now.getXDevice();
        int xdeviceID = xdevice.getDeviceId();
        int controlret;
//		controlret = XlinkAgent.getInstance().sendPipeData(
//				now.getXDevice(), data.getBytes(), sendPideListner);
        byte[] sendByte = NetworkUtils.hexStringToBytes(data);
//		byte[] sendByte =  NetworkUtils.hexStringToBytes("0102");
        controlret = XlinkAgent.getInstance().sendPipeData(
                now.getXDevice(), sendByte, sendPideListner);

        Log.e("ControlService", xdevice.getMacAddress() + "，XlinkAgent.getInstance().sendPipeData === " + controlret);
    }

    SendPipeListener sendPideListner = new SendPipeListener() {

        public void onSendLocalPipeData(XDevice arg0, int arg1, int arg2) {

            if (arg1 == XlinkCode.SUCCEED) {

                Log.e("发送测试", "------------------------------------控制成功-------------------------------");
            } else if (arg1 == XlinkCode.TIMEOUT) {

                MyLog.e("发送测试", "------------------------------------发送超时！-------------------------------");

//				sendBroadcast(new Intent(BaseVolume.SEND_ERROR).putExtra(BaseVolume.SEND_ERROR, "发???超时！"));

//				Log.e("发???测??","------------------------------------发???超时！-------------------------------");
            } else if (arg1 == XlinkCode.SERVER_CODE_UNAUTHORIZED) {
                MyLog.e("发送测试", "发送测试------------------------------------云连接异常！-------------------------------");
//				sendBroadcast(new Intent(BaseVolume.SEND_ERROR).putExtra(BaseVolume.SEND_ERROR, "云连接异常！"));
//				Log.e("发???测??","------------------------------------云连接异常！-------------------------------");
                // 这个错误码，只有云连接才会出??
                // UserManage.getInstance().removeSubDevice(
                // MyApp.getIns().getAppid(), device.getMacAddress());
                // 订阅关系错误
//				XlinkUtils.shortTips("控制设备失败,当前帐号未订阅此设备，请重新订阅");
//				sendBroadcast(new Intent(BaseVolume.RECONNECTION));

            } else {
                MyLog.e("发送测试", "控制失败返回code=" + arg1);
//				sendBroadcast(new Intent(BaseVolume.SEND_ERROR).putExtra(BaseVolume.SEND_ERROR, "控制失败返回code??"+ arg1));
//				XlinkUtils.shortTips("控制失败返回code??"+ arg1);
                // 重新发???
//				sendControl(nBufferDevice,sBufferData);
            }
        }
    };

    /**
     * 执行在主线程任务
     *
     * @param runnable
     */
    public static void postToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void onDeviceStateChanged(XDevice xdevice, int state) {

        DeviceInfoCache device = new DeviceInfoDao(this).featchDeviceByID(xdevice.getMacAddress(), userEmail);
        if (device != null) {
            device.setXDevice(xdevice);
            Intent intent = new Intent(BaseVolume.BROADCAST_DEVICE_CHANGED);
            intent.putExtra(BaseVolume.DEVICE_MAC, xdevice.getMacAddress());
            intent.putExtra(BaseVolume.STATUS, state);

            MyApp.getApp().sendBroadcast(intent);
        }

    }

    public void onLocalDisconnect(int arg0) {
//		LOCAL_THREAD_ERROR	-1	无物理网络
//		LOCAL_SERVICE_KILL	-2	XlinkUdpServrce服务被异常杀死（如360等安全软件),需要重新调用start函数。
        if (arg0 == XlinkCode.LOCAL_THREAD_ERROR) {
            sendBroadcast(new Intent(BaseVolume.USER_NOT_LINE));
        } else if (arg0 == XlinkCode.LOCAL_SERVICE_KILL) {
            XlinkAgent.getInstance().start();
        }


    }

    @Override
    public void onDisconnect(int code) {
//		CLOUD_STATE_DISCONNECT	-1	网络问题导致和服务器连接中端(不需要处理，会自动重连)
//		CLOUD_KEEPALIVE_ERROR	-2	和服务器心跳异常，导致从服务器掉线(不需要处理，会自动重连)
//		CLOUD_SERVICE_KILL	-3	XlinkTcpServrce服务被异常杀死（如360等安全软件）.（需要重新调用login函数）
//		CLOUD_USER_EXTRUSION	-4	该用户在其他地方登录(提示用户，帐号被挤)
        // -1 网络问题导致和服务器连接中端(不需要处理，会自动重连)
        if (code == XlinkCode.CLOUD_STATE_DISCONNECT) {
            sendBroadcast(new Intent(BaseVolume.USER_NOT_LINE));
        }
        // -2	和服务器心跳异常，导致从服务器掉线(不需要处理，会自动重连)
        else if (code == XlinkCode.CLOUD_KEEPALIVE_ERROR) {
            sendBroadcast(new Intent(BaseVolume.USER_NOT_LINE));
        }
        // -3	XlinkTcpServrce服务被异常杀死（如360等安全软件）.（需要重新调用login函数）
        else if (code == XlinkCode.CLOUD_SERVICE_KILL) {
            if (userID != 0 && !TextUtils.isEmpty(authKey)) {
                XlinkAgent.getInstance().login(userID, authKey);
            }
        }
        // 该用户在其他地方登录(提示用户，帐号被挤)
        else if (code == XlinkCode.CLOUD_USER_EXTRUSION) {
//			sendBroadcast(new Intent(BaseVolume.USER_NOT_LINE));

//			XlinkUtils.shortTips(getResources().getText(R.string.wan_control_repairing).toString());
        }
//		XlinkUtils.shortTips(getResources().getText(R.string.wan_control_repairing).toString());
    }

    /**
     * ：start/login广播
     */
    public void sendBroad(String action, int code) {
        Intent intent = new Intent(action);
        intent.putExtra(BaseVolume.STATUS, code);
        sendBroadcast(intent);
    }


    public void onLogin(int code) {
        // TODO Auto-generated method stub
//		Log.e(TAG, "login code" + code);
        sendBroad(BaseVolume.BROADCAST_ON_LOGIN, code);
        if (code == XlinkCode.SUCCEED) {
            Log.e(TAG, "云智易，登录成功！！！");
        } else if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK
                || XlinkUtils.isConnected()) {

        } else {
//			sendBroadcast(new Intent(BaseVolume.LOGIN_ERROR).putExtra(BaseVolume.ERROR_CODE, code));

        }
    }

    private void start() {
        int sdkret = XlinkAgent.getInstance().start();
        Log.e("ControlService", "start调用：：：：：：：：：：：：：：：：" + sdkret);
        if (sdkret == 0) {
            Log.e("ControlService", "SDK调用成功！");
//			 XlinkUtils.shortTips("SDK调用成功!");
        } else if (sdkret < 0) {
            if (sdkret == XlinkCode.ALREADY_EXIST) {
                // 启用云端
                if (XlinkUtils.isNotNull(uid) && XlinkUtils.isNotNull(userpwd)) {
                    getAppid(sharedPreferences, uid, userpwd);
                } else {
//					XlinkUtils.shortTips("无账户名和密码！");
                }

            } else {
//				XlinkUtils.shortTips("SDK调用失败!");
            }
        }
    }

    private Timer timer_lan = new Timer();
    private boolean isReconnection = false;

    /**
     * 是否报警
     */
    private void isStartAlarm(String result_text, XDevice x) {

        if (result_text.substring(0, 6).equals("fefefe")) {
            String data = result_text.substring(0, result_text.indexOf("ffffff") + 6);
            if (data.length() == 28 * 2) {

                int num = Integer.parseInt(data.substring(6, 8), 16);
                String data_lengh = data.substring(8, data.length() - 8);

            }
        }
    }

    @Override
    public void onStart(int code) {
//		sendBroad(BaseVolume.BROADCAST_ON_START, code);
    }

    public void onEventNotify(EventNotify arg0) {

    }

    private synchronized void getDeviceInfo(JSONObject deviceJson, Context con) {
        int deviceID = deviceJson.optInt("id");
        String mac = deviceJson.optString("mac");
        boolean is_Active = deviceJson.optBoolean("is_active");
        String active_date = deviceJson.optString("active_date");
        boolean is_Online = deviceJson.optBoolean("is_online");
        String last_login = deviceJson.optString("last_login");
        String active_code = deviceJson.optString("active_code");
        String auth_code = deviceJson.optString("active_code");
        String mcu_mod = deviceJson.optString("mcu_mod");
        int mcu_version = deviceJson.optInt("mcu_version");
        String firmware_mod = deviceJson.optString("firmware_mod");
        int firmware_version = deviceJson.optInt("firmware_version");
        String product_id = deviceJson.optString("product_id");
        String access_key = deviceJson.optInt("access_key") + "";
        int role = deviceJson.optInt("role");

        DeviceInfoCache deviceInfo = new DeviceInfoCache();
        deviceInfo.setId(deviceID);
        deviceInfo.setMac(mac);
        deviceInfo.setIs_Active(is_Active);
        deviceInfo.setActive_date(active_date);
//		deviceInfo.setIs_Online(is_Online);
        deviceInfo.setLast_login(last_login);
        deviceInfo.setActive_code(active_code);
        deviceInfo.setAuth_code(auth_code);
        deviceInfo.setMcu_mod(mcu_mod);
        deviceInfo.setMcu_version(mcu_version);
        deviceInfo.setFirmware_mod(firmware_mod);
        deviceInfo.setFirmware_version(firmware_version);
        deviceInfo.setPid(product_id);
        deviceInfo.setRole(role);

        DeviceInfoDao mDeviceInfoDao = new DeviceInfoDao(con);

        if (!mDeviceInfoDao.isDeviceExist(mac, userEmail)) {
            deviceInfo.setName("设备");
            deviceInfo.setPwd("0");
            deviceInfo.setOnLine(BaseVolume.DEVICE_NOT_LINE);
            XDevice xDevice = CreateXDevice(mac, deviceID);
            deviceInfo.setXDevice(xDevice);
            deviceInfo.setUserID(userEmail);
            mDeviceInfoDao.insertSingleData(deviceInfo);

//			ArrayList<DeviceInfoCache> deviceList = DeviceListFragment.deviceMap.get(BaseVolume.NOT_GROUP);
//			deviceList.add(deviceInfo);
//			DeviceListFragment.deviceMap.put(BaseVolume.NOT_GROUP, deviceList);
        } else {
            XDevice xDevice = CreateXDevice(mac, deviceID);
            String sXDevice = XlinkAgent.deviceToJson(xDevice).toString();
            ContentValues initialValues = new ContentValues();
            initialValues.put(DeviceInfo.Columns.xDevice, sXDevice);
            mDeviceInfoDao.updateDataByDeviceMac(initialValues, mac, userEmail);

        }
        mDeviceInfoDao.closeDb();
    }

    private static XDevice CreateXDevice(String mac, int id) {
        XDevice xDevice = null;
        // 将String 转换?? JSON;
        JSONObject jDevice = new JSONObject();
        try {
            jDevice.put("mucSoftVersion", 1);
            jDevice.put("mcuHardVersion", 1);
            jDevice.put("deviceName", "");
            jDevice.put("deviceID", id);
            jDevice.put("deviceIP", "");
            jDevice.put("devicePort", 0);
            jDevice.put("macAddress", mac);
            jDevice.put("productID", BaseVolume.PRODUCTID);
            jDevice.put("version", 1);
            jDevice.put("deviceInit", true);

            JSONObject jTempXDevice = new JSONObject();
            jTempXDevice.put("device", jDevice);
            jTempXDevice.put("protocol", 1);
            xDevice = XlinkAgent.JsonToDevice(jTempXDevice);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return xDevice;
    }

    /**
     * v3
     */
    public void onRecvPipeData(short arg0, XDevice xdevice, byte[] data) {
        String res = NetworkUtils.bytesToHexString(data);
        Log.e("数据打印", xdevice.getMacAddress() + "ControlService,接收局域网数据：" + res + "，长度：" + data.length);
        res = res.toLowerCase();
        // 取出之前的缓存数据（未拼接完整的数据）
        String strOldBuffer = dataBufferMapLan.get(xdevice.getMacAddress().toLowerCase());
        if (strOldBuffer == null)
            strOldBuffer = "";
        {
            strOldBuffer = strOldBuffer + res;
            String strWillCheckData = "";
            while (strOldBuffer.length() >= 2) {
                strWillCheckData += strOldBuffer.substring(0, 2);
                strOldBuffer = strOldBuffer.substring(2);
                // 完整数据的长度是48个字节
                if (strWillCheckData.length() == 96) {
                    // aa,判断是包头
                    if (strWillCheckData.substring(0, 2).equalsIgnoreCase(BaseVolume.COMMAND_HEAD)) {
                        // 55,判断是不是包尾
                        if (strWillCheckData.substring(strWillCheckData.length() - 2).equalsIgnoreCase(BaseVolume.COMMAND_END)) {
                            // 有包头，有包尾，且长度足够，则开始判断校验位。
                            String strOldCheck = strWillCheckData.substring(strWillCheckData.length() - 4, strWillCheckData.length() - 2);
                            byte[] oldCheck = NetworkUtils.hexStringToBytes(strOldCheck);
                            // 从序号的下一位取到校验的前一位
                            String strValue = strWillCheckData.substring(4, strWillCheckData.length() - 4);
                            byte[] newCheck = NetworkUtils.SumCheck(strValue, 1);
                            // 数据中的校验 与 计算的校验一致，则认为是一条有效数据
                            if (oldCheck[0] == newCheck[0]) {
                                Log.e("ControlService", "完整数据：" + strWillCheckData);
                                Intent intent = new Intent(BaseVolume.BROADCAST_RECVPIPE_LAN);
                                intent.putExtra(BaseVolume.RESULT_DATA, strWillCheckData);
                                intent.putExtra(BaseVolume.RESULT_XDEVICE, xdevice);
                                sendBroadcast(intent);
                                strWillCheckData = "";
                            }
                            // 前有aa，后有55，长度又一致，但是校验不通过，说明不是一条有效数据，有可能是个断包，
                            // 那需要将现在对比的数据进行分割，从前到后，找到一个aa开头的为止
                            else {
                                strWillCheckData = strWillCheckData.substring(2);
                                Boolean isFindHead = false;
                                while (!isFindHead) {
                                    if (strWillCheckData.length() > 0) {
                                        String strHead = strWillCheckData.substring(0, 2);
                                        if (strHead.equalsIgnoreCase("aa"))
                                            isFindHead = true;
                                        else
                                            strWillCheckData = strWillCheckData.substring(2);
                                    } else {
                                        isFindHead = true;
                                    }
                                }
                            }
                        }
                        // 不是包尾，长度又是符合的，认为数据有问题，则进行切割丢弃
                        else {
                            strWillCheckData = strWillCheckData.substring(2);
                            Boolean isFindHead = false;
                            while (!isFindHead) {
                                if (strWillCheckData.length() > 0) {
                                    String strHead = strWillCheckData.substring(0, 2);
                                    if (strHead.equalsIgnoreCase("aa"))
                                        isFindHead = true;
                                    else
                                        strWillCheckData = strWillCheckData.substring(2);
                                } else {
                                    isFindHead = true;
                                }
                            }
                        }
                    }
                    // 第一个字节不是包头，则进行切割丢弃
                    else {
                        Boolean isFindHead = false;
                        while (!isFindHead) {
                            if (strWillCheckData.length() > 0) {
                                String strHead = strWillCheckData.substring(0, 2);
                                if (strHead.equalsIgnoreCase("aa"))
                                    isFindHead = true;
                                else
                                    strWillCheckData = strWillCheckData.substring(2);
                            } else {
                                isFindHead = true;
                            }
                        }
                    }
                }
            }
            // 已经校验完了所有数据
            strOldBuffer = strWillCheckData;
        }
        dataBufferMapLan.put(xdevice.getMacAddress().toLowerCase(), strOldBuffer);


    }

    public void onRecvPipeSyncData(short arg0, XDevice xdevice, byte[] data) {
        String res = NetworkUtils.bytesToHexString(data);
        Log.e("数据打印", "ControlService,接收云端数据：" + res);


       /* if (true)
            return;*/

        // 取出之前的缓存数据（未拼接完整的数据）
        String strOldBuffer = dataBufferMapWan.get(xdevice.getMacAddress().toLowerCase());

        if (strOldBuffer == null)
            strOldBuffer = "";
        {
            strOldBuffer = strOldBuffer + res;
            String strWillCheckData = "";
            while (strOldBuffer.length() >= 2) {
                strWillCheckData += strOldBuffer.substring(0, 2);
                strOldBuffer = strOldBuffer.substring(2);
                // 完整数据的长度是48个字节
                if (strWillCheckData.length() == 96) {
                    // aa,判断是包头
                    if (strWillCheckData.substring(0, 2).equalsIgnoreCase(BaseVolume.COMMAND_HEAD)) {
                        // 55,判断是不是包尾
                        if (strWillCheckData.substring(strWillCheckData.length() - 2).equalsIgnoreCase(BaseVolume.COMMAND_END)) {
                            // 有包头，有包尾，且长度足够，则开始判断校验位。
                            String strOldCheck = strWillCheckData.substring(strWillCheckData.length() - 4, strWillCheckData.length() - 2);
                            byte[] oldCheck = NetworkUtils.hexStringToBytes(strOldCheck);
                            // 从序号的下一位取到校验的前一位
                            String strValue = strWillCheckData.substring(4, strWillCheckData.length() - 4);
                            byte[] newCheck = NetworkUtils.SumCheck(strValue, 1);
                            // 数据中的校验 与 计算的校验一致，则认为是一条有效数据
                            if (oldCheck[0] == newCheck[0]) {
//                                Log.e("ControlService", "云端完整数据：" + strWillCheckData);
                                Intent intent = new Intent(BaseVolume.BROADCAST_RECVPIPE);
                                intent.putExtra(BaseVolume.RESULT_DATA, strWillCheckData);
                                intent.putExtra(BaseVolume.RESULT_XDEVICE, xdevice);
                                sendBroadcast(intent);
                                strWillCheckData = "";
                            }
                            // 前有aa，后有55，长度又一致，但是校验不通过，说明不是一条有效数据，有可能是个断包，
                            // 那需要将现在对比的数据进行分割，从前到后，找到一个aa开头的为止
                            else {
                                strWillCheckData = strWillCheckData.substring(2);
                                Boolean isFindHead = false;
                                while (!isFindHead) {
                                    if (strWillCheckData.length() > 0) {
                                        String strHead = strWillCheckData.substring(0, 2);
                                        if (strHead.equalsIgnoreCase("aa"))
                                            isFindHead = true;
                                        else
                                            strWillCheckData = strWillCheckData.substring(2);
                                    } else {
                                        isFindHead = true;
                                    }
                                }
                            }
                        }
                        // 不是包尾，长度又是符合的，认为数据有问题，则进行切割丢弃
                        else {
                            strWillCheckData = strWillCheckData.substring(2);
                            Boolean isFindHead = false;
                            while (!isFindHead) {
                                if (strWillCheckData.length() > 0) {
                                    String strHead = strWillCheckData.substring(0, 2);
                                    if (strHead.equalsIgnoreCase("aa"))
                                        isFindHead = true;
                                    else
                                        strWillCheckData = strWillCheckData.substring(2);
                                } else {
                                    isFindHead = true;
                                }
                            }
                        }
                    }
                    // 第一个字节不是包头，则进行切割丢弃
                    else {
                        Boolean isFindHead = false;
                        while (!isFindHead) {
                            if (strWillCheckData.length() > 0) {
                                String strHead = strWillCheckData.substring(0, 2);
                                if (strHead.equalsIgnoreCase("aa"))
                                    isFindHead = true;
                                else
                                    strWillCheckData = strWillCheckData.substring(2);
                            } else {
                                isFindHead = true;
                            }
                        }
                    }
                }
            }
            // 已经校验完了所有数据
            strOldBuffer = strWillCheckData;
        }
        dataBufferMapWan.put(xdevice.getMacAddress().toLowerCase(), strOldBuffer);

    }

    /**
     * 删除对应的数据缓存
     */
    public void deleteDataBufferByMac(String strMac) {
        dataBufferMapLan.remove(strMac.toLowerCase());
        dataBufferMapWan.remove(strMac.toLowerCase());

    }


    public void onDataPointUpdate(XDevice arg0, int arg1, Object arg2,
                                  int arg3, int arg4) {

    }

    public void onDataPointUpdate(XDevice arg0, List<DataPoint> arg1, int arg2) {
        // TODO Auto-generated method stub

    }


}
