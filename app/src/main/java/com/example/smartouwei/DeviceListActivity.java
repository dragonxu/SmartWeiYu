package com.example.smartouwei;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.DeviceAdapter;
import com.example.smartouwei.base.BaseActivity;
import com.example.smartouwei.base.MyApp;
import com.example.smartouwei.service.ControlService;
import com.example.smartouwei.view.AddMenuWindowDialog;
import com.example.smartouwei.view.AreaAddWindowHint;
import com.example.smartouwei.view.LoadingDialog;
import com.example.smartouwei.view.SetValueAreaAddWindow;
import com.example.smartouwei.view.AddMenuWindowDialog.PeriodListener;
import com.gisicisky.smasterFitment.data.CreateControlCMD;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.data.MyRunableTimer;
import com.gisicisky.smasterFitment.data.MyTimerTask;
import com.gisicisky.smasterFitment.db.DAO.DeviceInfoDao;
import com.gisicisky.smasterFitment.http.HttpManage;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.NetworkUtils;
import com.gisicisky.smasterFitment.utl.XlinkUtils;


public class DeviceListActivity extends BaseActivity implements OnClickListener {
    private Context mContext;
    private LinearLayout m_llParent;
    private DeviceInfoCache nowSelectDevice;
    private LoadingDialog loading;
    private ListView m_lvDevice;
    private DeviceAdapter adapter;
    private ImageView m_imgDeviceNull;
    private ArrayList<DeviceInfoCache> list = new ArrayList<DeviceInfoCache>();
    private String TAG = "";
    public static HashMap<String, Integer> MacCountMap = new HashMap<String, Integer>();
    private final int GETSTATE = 123;
    private int getStateCount = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        MacCountMap.clear();

        loading = new LoadingDialog(this, R.style.LoadingDialogStyle);
        mContext = this;

        TAG = "DeviceListActivity";

        Intent intentService = new Intent(this, ControlService.class);
        bindService(intentService, conn, Context.BIND_AUTO_CREATE);

        XlinkAgent.getInstance().start();

        initUI();
        reciverBand();
    }

    private void initUI() {
        m_llParent = (LinearLayout) findViewById(R.id.llParent);

        findViewById(R.id.imgQrcode).setOnClickListener(this);
        findViewById(R.id.imgLanScan).setOnClickListener(this);
        findViewById(R.id.imgAdd).setOnClickListener(this);
        findViewById(R.id.tvTitleName).setOnClickListener(this);

        m_imgDeviceNull = (ImageView) findViewById(R.id.imgDeviceNull);
        m_lvDevice = (ListView) findViewById(R.id.lvDevice);

        m_imgDeviceNull.setVisibility(View.VISIBLE);
        m_lvDevice.setVisibility(View.GONE);

        m_lvDevice.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DeviceInfoCache nowDevice = list.get(position);
                nowSelectDevice = nowDevice;
                // 正在连接 或 正 在验证密码，则不做任何操作
                if (nowSelectDevice.getOnLine() == BaseVolume.DEVICE_CONNECTING ||
                        nowSelectDevice.getOnLine() == BaseVolume.DEVICE_CHECK_PWD) {
                    return;
                }
                // 离线，则点击连接
                if (nowSelectDevice.getOnLine() == BaseVolume.DEVICE_NOT_LINE) {
//					startActivity(new Intent(mContext, ControlGuangBoActivity.class).putExtra(BaseVolume.DEVICE, nowSelectDevice));
                    nowSelectDevice.setOnLine(BaseVolume.DEVICE_CONNECTING);
                    adapter.updateList(list);
                    // 初始化设备
                    ArrayList<DeviceInfoCache> deviceList = new ArrayList<DeviceInfoCache>();
                    deviceList.add(nowSelectDevice);
                    initDevice(deviceList);
                    // 连接设备
                    connectDevice(nowSelectDevice);
                }
                // 未激活，则说明设备在线，但是和硬件上设的控制密码 校验异常，则弹窗，重新输入控制密码
                else if (nowSelectDevice.getOnLine() == BaseVolume.DEVICE_NOT_ACTIVE) {
                }
                // 在线，则跳转单个控制的界面
                else if (nowSelectDevice.getOnLine() == BaseVolume.DEVICE_ON_LINE) {
                    DeviceState nowDeviceState = DataAnalysisHelper.getSelf(mContext).getDataBufferByMac(nowSelectDevice.getMac());
                    if (nowDeviceState != null) {
                        startActivity(new Intent(mContext, ControlMainActivity.class)
                                .putExtra(BaseVolume.DEVICE, nowSelectDevice));
                    } else {
                        //查询设备类型
                        DataAnalysisHelper.getSelf(mContext).clearAllDataBufferByMac(nowDevice.getMac());
                        // 发送查询指令
                        String strData = NetworkUtils.bytesToHexString(CreateControlCMD.getDeviceState(nowDevice.getMac()));
                        nowDevice.setQueryInfo(true);
                        Intent intent1 = new Intent(BaseVolume.CONTROL_DEVICE);
                        intent1.putExtra("device", nowDevice);
                        intent1.putExtra("data", strData);
                        controlService.onStartCommand(intent1, 0, 0);
                    }
//					startActivity(new Intent(mContext, ControlGuangBoActivity.class).putExtra(BaseVolume.DEVICE, nowSelectDevice));
//					sendBroadcast(new Intent(BaseVolume.GOTO_CONTROL_DEVICE));
//					sendBroadcast(new Intent(BaseVolume.GOTO_CONTROL_DEVICE).putExtra(BaseVolume.DEVICE, nowSelectDevice));
                }
            }
        });
        m_lvDevice.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                DeviceInfoCache nowDevice = list.get(position);
                nowSelectDevice = nowDevice;
                // 设备编辑功能
                deviceSelectMenu();
                return true;
            }
        });

    }

    private ControlService controlService;
    private ServiceConnection conn;

    {
        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                controlService = ((ControlService.ServiceBinder) service).getService();

                Log.e(TAG, "onServiceConnected: 绑定成功！", null);
                // 初始化设备
                initData();
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
        myIntentFilter.addAction(BaseVolume.ADD_DEVICE);
        myIntentFilter.addAction(BaseVolume.QrCode_NewDEVICE);
        myIntentFilter.addAction(BaseVolume.BROADCAST_DEVICE_STATUE);
        myIntentFilter.addAction(BaseVolume.BROADCAST_ON_LOGIN);
        myIntentFilter.addAction(BaseVolume.BROADCAST_RECVPIPE_LAN);
        myIntentFilter.addAction(BaseVolume.BROADCAST_RECVPIPE);
        myIntentFilter.addAction(BaseVolume.USER_NOT_LINE);
        myIntentFilter.addAction(BaseVolume.REASH_DEVICE_STATE);
        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        DeviceInfoDao deviceInfoDao = new DeviceInfoDao(this);
        list = deviceInfoDao.queryAllDeviceByUID(ControlService.userEmail);
        deviceInfoDao.closeDb();
        if (list.size() > 0) {
            m_imgDeviceNull.setVisibility(View.GONE);
            m_lvDevice.setVisibility(View.VISIBLE);
        }

        adapter = new DeviceAdapter(list, mContext);

        m_lvDevice.setAdapter(adapter);

        // 数据展示完毕后，开始登录服务器
        loading.show();
        loading.updateStatusText(DeviceListActivity.this.getString(R.string.qing_shao_hou));
        startLogin();

    }

    /**
     * 开始登录
     */
    public void startLogin() {
        // 开始注册
        Intent intent1 = new Intent(BaseVolume.REGISTER_USER);
        controlService.onStartCommand(intent1, 0, 0);

    }


    /**
     * 设备编辑菜单选择功能
     */
    private void deviceSelectMenu() {
        List<String> list = new ArrayList<String>();
        list.add(DeviceListActivity.this.getString(R.string.chong_mingming));
        list.add(DeviceListActivity.this.getString(R.string.erweima_fenxiang));
        list.add(DeviceListActivity.this.getString(R.string.shanchu_shebei));

        AddMenuWindowDialog dialog1 = new AddMenuWindowDialog(this, R.style.Dialogstyle, list);
        dialog1.setListener(new PeriodListener() {
            public void refreshListener(int number) {
                // 重命名
                if (number == 0) {
                    reSetName();
                }
                // 二维码分享
                else if (number == 1) {
                    CreateShareCode(nowSelectDevice.getXDevice());
                }
                // 删除设备
                else {
                    hintDeleteDevice(DeviceListActivity.this.getString(R.string.wenxin_tishi), DeviceListActivity.this.getString(R.string.shifou_shanchu_shebei));
                }
            }
        });
        dialog1.show();

    }

    /**
     * 重命名
     */
    private void reSetName() {
        SetValueAreaAddWindow dialog1 = new SetValueAreaAddWindow(this, R.style.Dialogstyle,
                DeviceListActivity.this.getString(R.string.chong_mingming), DeviceListActivity.this.getString(R.string.qingshuru_shebei_xinmingcheng), new SetValueAreaAddWindow.PeriodListener() {
            public void refreshListener(String string) {
                // 名字没有改变，则不需要任何操作
                if (string.equals(nowSelectDevice.getName())) {
                    return;
                }
                nowSelectDevice.setName(string);
                DeviceInfoDao deviceInfoDao = new DeviceInfoDao(DeviceListActivity.this);
                deviceInfoDao.updateData(nowSelectDevice, ControlService.userEmail);
                deviceInfoDao.closeDb();
                adapter.notifyDataSetChanged();
                // 再修改对应的缓存，并更新列表
//				MyDeviceView myView =  myViewMap.get(nowSelectGroup.getId());
//				myView.reNameDevice(nowSelectDevice.getMac(), string);

            }
        }, nowSelectDevice.getName(), false);

        InputFilter inputFilter = NetworkUtils.getTextFilter(16);
        dialog1.setInputFilter(inputFilter);
        dialog1.show();
    }

    /**
     * 确认删除
     */
    private void hintDeleteDevice(String title, String content) {
        AreaAddWindowHint pwdDialog = new AreaAddWindowHint(DeviceListActivity.this,
                R.style.Dialogstyle, title,
                new AreaAddWindowHint.PeriodListener() {
                    public void refreshListener(String string) {
                        // 先删除数据库的信息
                        DeviceInfoDao deviceInfoDao = new DeviceInfoDao(DeviceListActivity.this);
                        deviceInfoDao.deleteData(nowSelectDevice.getMac(), ControlService.userEmail);
                        deviceInfoDao.closeDb();
                        // 清除缓存数据
                        controlService.deleteDataBufferByMac(nowSelectDevice.getMac());
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getMac().equalsIgnoreCase(nowSelectDevice.getMac())) {
                                list.remove(i);
                                break;
                            }
                        }
                        if (list.size() == 0) {
                            m_imgDeviceNull.setVisibility(View.VISIBLE);
                            m_lvDevice.setVisibility(View.GONE);
                        } else {
                            m_imgDeviceNull.setVisibility(View.GONE);
                            m_lvDevice.setVisibility(View.VISIBLE);
                        }
                        adapter.updateList(list);
                    }
                }, content);
        pwdDialog.show();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTitleName:
//                startActivity(new Intent(mContext, ControlMainActivity.class) .putExtra(BaseVolume.DEVICE, nowSelectDevice).putExtra("strType","GB"));
//				startActivity(new Intent(mContext, ControlGuangBoActivity.class).putExtra(BaseVolume.DEVICE, nowSelectDevice));
//				startActivity(new Intent(mContext, ControlShiZhengActivity.class).putExtra(BaseVolume.DEVICE, nowSelectDevice));
//				startActivity(new Intent(mContext, ControlYuGangActivity.class).putExtra(BaseVolume.DEVICE, nowSelectDevice));
                break;
            case R.id.imgQrcode:
//			startActivity(new Intent(mContext, ControlMainActivity.class)
//					.putExtra(BaseVolume.DEVICE, nowSelectDevice).putExtra("strType","ZF"));

                Intent intent = new Intent(DeviceListActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 100);

                break;
            case R.id.imgLanScan:
                if (!MyApp.isWifi(mContext)) {
                    XlinkUtils.shortTips(DeviceListActivity.this.getString(R.string.qingxian_lianjie_wifi));
                    return;
                }
                time = 5;
                find_list.clear();
                handler.sendEmptyMessage(FIND_DEVICE);
                break;
            case R.id.imgAdd:
//			startActivity(new Intent(mContext, ControlMainActivity.class)
//					.putExtra(BaseVolume.DEVICE, nowSelectDevice).putExtra("strType","YG"));
                AddMenuSelect();
                break;

            default:
                break;
        }
    }

    /**
     * 菜单选择
     */
    private void AddMenuSelect() {
        List<String> list = new ArrayList<String>();
        list.add(mContext.getString(R.string.sousuo_shebei));
        list.add(DeviceListActivity.this.getString(R.string.tianjia_shebei));
        list.add(mContext.getString(R.string.sao_yi_sao));
        AddMenuWindowDialog dialog1 = new AddMenuWindowDialog(this, R.style.Dialogstyle, list);
        dialog1.setListener(new PeriodListener() {
            public void refreshListener(int number) {
                // 局域网搜索
                if (number == 0) {
                    if (!MyApp.isWifi(mContext)) {
                        XlinkUtils.shortTips(DeviceListActivity.this.getString(R.string.qingxian_lianjie_wifi));
                        return;
                    }
                    time = 5;
                    find_list.clear();
                    handler.sendEmptyMessage(FIND_DEVICE);
                }
                // 添加设备
                else if (number == 1) {
//					startActivity(new Intent(DeviceListActivity.this, SmartLinkActivity.class));
                    startActivity(new Intent(DeviceListActivity.this, Smart8266Activity.class));
                }
                // 扫一扫
                else if (number == 2) {
                    Intent intent = new Intent(DeviceListActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 100);
                }
            }
        });
        dialog1.show();

    }

    private final int FIND_DEVICE = 111;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int wa = msg.what;
            switch (wa) {
                case FIND_DEVICE:
                    if (time > 0) {
                        String strCount = " " + find_list.size() + "";
                        showFind(DeviceListActivity.this.getString(R.string.zhengzai_sousuo) + strCount, DeviceListActivity.this.getString(R.string.sousuo_shebei) + "(" + time + "s)");
                        findDevice();
                        --time;
                        sendEmptyMessageDelayed(FIND_DEVICE, 1000);
                    } else {
                        time = 0;
                        find_list.clear();
                        m_popupWindowConfig.dismiss();
                        Log.e(TAG, "handleMessage: 全部停止！");
                    }
                    break;
                case GETSTATE:
                    Intent intent = (Intent) msg.obj;
                    controlService.onStartCommand(intent, 0, 0);

                    break;
                default:
                    break;
            }
        }
    };


    private View popupWindowWifi = null;
    private TextView popupText = null;
    private TextView popupTime = null;
    private TextView popupCancel = null;
    private TextView popupFinish = null;
    private PopupWindow m_popupWindowConfig = null;
    private View view1;
    private GifImageView gif2 = null;

    private void showFind(String sText, String sTime) {

        if (popupWindowWifi == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(DeviceListActivity.this);
            popupWindowWifi = layoutInflater.inflate(R.layout.popup_window_find_config_device, null);
            popupText = (TextView) popupWindowWifi.findViewById(R.id.tvFindDeviceText);
            popupTime = (TextView) popupWindowWifi.findViewById(R.id.tvTime);
            popupCancel = (TextView) popupWindowWifi.findViewById(R.id.tvCancel);
            popupFinish = (TextView) popupWindowWifi.findViewById(R.id.tvFinish);
            view1 = popupWindowWifi.findViewById(R.id.view1);
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
        popupFinish.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);
        popupTime.setText(sTime);
        SpannableString spannableString = new SpannableString(sText);
        spannableString.setSpan(new ForegroundColorSpan(DeviceListActivity.this.getResources().getColor(R.color.mo_lv)), sText.indexOf(":") + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        popupText.setText(spannableString);
        popupCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                time = 0;
                m_popupWindowConfig.dismiss();
            }
        });
        popupFinish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                m_popupWindowConfig.dismiss();
            }
        });
        m_popupWindowConfig.setFocusable(true);
        m_popupWindowConfig.showAtLocation(m_llParent, Gravity.CENTER, 0, 0);
    }

    private XDevice CreateXDevice(String mac, String deviceIP, int deviceID, int devicePort, boolean isInit, int accessKey, int subKey) {
        XDevice xDevice = null;
        // 将String 转换成 JSON;
        JSONObject jDevice = new JSONObject();
        try {
            jDevice.put("mucSoftVersion", 2);
            jDevice.put("mcuHardVersion", 1);
            jDevice.put("deviceName", "xlink_dev");
            jDevice.put("deviceID", deviceID);
            jDevice.put("deviceIP", deviceIP);
            jDevice.put("devicePort", devicePort);
            jDevice.put("macAddress", mac);
            jDevice.put("productID", BaseVolume.PRODUCTID);
            jDevice.put("version", 3);
            jDevice.put("deviceInit", isInit);
            jDevice.put("accesskey", accessKey);
            jDevice.put("subKey", subKey);
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
     * 查找设备回调
     */
    private int time = 5;
    private List<XDevice> find_list = new ArrayList<XDevice>();
    ScanDeviceListener scanListener = new ScanDeviceListener() {
        public void onGotDeviceByScan(XDevice xdevice) {
            JSONObject jo = XlinkAgent.deviceToJson(xdevice);
            Log.e(TAG, "onGotDeviceByScan: 找到设备：" + jo.toString(), null);

            DeviceInfoDao mDeviceInfoDao = new DeviceInfoDao(mContext);
            boolean isFind = mDeviceInfoDao.isDeviceExist(xdevice.getMacAddress(), ControlService.userEmail);
            DeviceInfoCache de = null;
            // 添加数据库
            if (!isFind) {
                de = new DeviceInfoCache(xdevice.getProductId(), DeviceListActivity.this.getString(R.string.xiangfen_zhilv), "0", xdevice.getMacAddress(), xdevice, BaseVolume.DEFAULT_DEVICE_TYPE, ControlService.userEmail);
                mDeviceInfoDao.insertSingleData(de);
                de = mDeviceInfoDao.featchDeviceByID(xdevice.getMacAddress(), ControlService.userEmail);
                mDeviceInfoDao.closeDb();
                de.setOnLine(BaseVolume.DEVICE_CONNECTING);
                // 添加设备到列表中
                list.add(de);
                adapter.updateList(list);

                m_imgDeviceNull.setVisibility(View.GONE);
                m_lvDevice.setVisibility(View.VISIBLE);

                ArrayList<DeviceInfoCache> list = new ArrayList<DeviceInfoCache>();
                list.add(de);
                // 初始化设备
                initDevice(list);
                // 连接设备
                connectDevice(de);
            } else {
                // 刷新设备状态
            }
            mDeviceInfoDao.closeDb();

            boolean blHave = false;
            for (XDevice d : find_list) {
                if (d.getMacAddress().equals(xdevice.getMacAddress())) {
                    blHave = true;
                }
            }
            if (!blHave) {
                find_list.add(xdevice);
            }

        }
    };

    /**
     * 查找设备
     */
    private void findDevice() {
        int ret = XlinkAgent.getInstance().scanDeviceByProductId(BaseVolume.PRODUCTID, scanListener);
        Log.e(TAG, "findDevice: 局域网搜索！！！返回状态：" + ret, null);
        if (ret < 0) {
//	    	Toast.makeText(mContext, "调用Scan失败!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改所有设备的状态
     */
    public void udpateDeviceState(int deviceState) {
        for (DeviceInfoCache device : list) {
            device.setOnLine(deviceState);
        }
        adapter.updateList(list);
    }

    /**
     * 监听的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BaseVolume.BROADCAST_ON_LOGIN)) {
                int code = intent.getIntExtra(BaseVolume.STATUS, -1);
                if (code == XlinkCode.SUCCEED) {
                } else {
//					XlinkUtils.shortTips(DeviceListActivity.this.getString(R.string.yuancheng_denglu_shibai));
                }
                loading.dismiss();
                // 不管登录成功，或失败，都要连接设备
//				for (MyDeviceView myView : myViewMap.values()) {
//					myView.startConnectDevice();
//				}
                udpateDeviceState(BaseVolume.DEVICE_NOT_LINE);
                // 没有物理网络，所以不需要重连
                if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK) {
                    Log.e(TAG, "onReceive: 登录返回，没有物理网络，所以还不需要连接！", null);
                } else {
                    Log.e(TAG, "onReceive: 登录返回，只要有物理网络，不管登录成功，或失败，都要连接设备！", null);
                    // 不管登录成功，或失败，都要连接设备
                    initDevice(list);
                    for (DeviceInfoCache device : list) {
                        device.setOnLine(BaseVolume.DEVICE_CONNECTING);
                        connectDevice(device);
                    }
                    adapter.updateList(list);
                }
            }
            // 二维码扫描的结果
            else if (action.equals(BaseVolume.QrCode_NewDEVICE)) {
                String sData = intent.getStringExtra("result");
                Log.e(TAG, "onReceive: 二维码扫描结果：" + sData, null);
//	        	 XlinkUtils.shortTips(sData);
                String[] strData = sData.split("_");
                String mac = strData[0];
                String name = DeviceListActivity.this.getString(R.string.xiangfen_zhilv);
                String deviceIP = strData[1];
                int deviceID = Integer.parseInt(strData[2]);
                int devicePort = Integer.parseInt(strData[3]);
                String strInit = strData[4];// 0:false, 1:true
                boolean isInit = strInit.equals("0") ? false : true;
                int accessKey = Integer.parseInt(strData[5]);
                int subKey = Integer.parseInt(strData[6]);

                XDevice xdevice = CreateXDevice(mac, deviceIP, deviceID, devicePort, isInit, accessKey, subKey);
                String strXdevice = XlinkAgent.deviceToJson(xdevice).toString();

                String strCode = strData[7];
                startTakeCode(mac, xdevice, strCode);
            }
            // 添加设备
            else if (action.equals(BaseVolume.ADD_DEVICE)) {
                DeviceInfoCache newDevice = (DeviceInfoCache) intent.getSerializableExtra(BaseVolume.DEVICE);
                newDevice.setOnLine(BaseVolume.DEVICE_CONNECTING);
                list.add(newDevice);
                adapter.updateList(list);
                m_imgDeviceNull.setVisibility(View.GONE);
                m_lvDevice.setVisibility(View.VISIBLE);
                // 初始化设备
                ArrayList<DeviceInfoCache> deviceList = new ArrayList<DeviceInfoCache>();
                deviceList.add(newDevice);
                initDevice(deviceList);
                // 连接设备
                connectDevice(newDevice);
            }
            // 设备连接回调
            else if (action.equals(BaseVolume.BROADCAST_DEVICE_STATUE)) {
                String commandDeviceMac = intent.getStringExtra("command_device");
                XDevice xDevice = (XDevice) intent.getSerializableExtra("xDevice");
                boolean statue = intent.getBooleanExtra(BaseVolume.BROADCAST_DEVICE_STATUE, false);
                boolean isLan = intent.getBooleanExtra(BaseVolume.BROADCAST_DEVICE_CONNECTE_IS_LAN, false);
                DeviceInfoDao deviceInfoDao = new DeviceInfoDao(DeviceListActivity.this);
                // 连接成功
                if (statue) {
                    for (DeviceInfoCache device : list) {
                        if (xDevice.getMacAddress().equalsIgnoreCase(device.getMac())) {
                            // 更新状态为 在线
                            device.setOnLine(BaseVolume.DEVICE_ON_LINE);
                            device.setXDevice(xDevice);
                            MacCountMap.put(device.getMac().toLowerCase(), 0);

                            DataAnalysisHelper.getSelf(mContext).clearAllDataBufferByMac(device.getMac());
                            // 发送查询指令
                            String strData = NetworkUtils.bytesToHexString(CreateControlCMD.getDeviceState(device.getMac()));
                            device.setQueryInfo(true);
                            Intent intent1 = new Intent(BaseVolume.CONTROL_DEVICE);
                            intent1.putExtra("device", device);
                            intent1.putExtra("data", strData);
//                            if (handler.hasMessages(GETSTATE))
//                                handler.removeMessages(GETSTATE);
//                            Message msg = new Message();
//                            msg.what = GETSTATE;
//                            msg.obj = intent1;
//                            handler.sendMessage(msg);

                            // 定时发送
                            Timer deviceTimeOut = new Timer();
                            MyTimerTask myTimerTask = new MyTimerTask(device.getMac(),deviceTimeOut,GETSTATE,handler,intent1);
                            timerMap.put(device.getMac().toLowerCase(),deviceTimeOut);
                            timerTaskMap.put(device.getMac().toLowerCase(),myTimerTask);
                            deviceTimeOut.schedule(myTimerTask,0,1000);// 立即执行
//                            MyRunableTimer myRunableTimer = new MyRunableTimer(GETSTATE,handler,intent1);
//                            runableMap.put(device.getMac().toLowerCase(),myRunableTimer);
//                            handler.post(myRunableTimer);

                            break;
                        }
                    }
                }
                // 连接失败
                else {
                    for (DeviceInfoCache device : list) {
                        if (xDevice.getMacAddress().equalsIgnoreCase(device.getMac())) {
                            // 更新状态为 离线
                            device.setOnLine(BaseVolume.DEVICE_NOT_LINE);
                            device.setXDevice(xDevice);
                        }
                    }

                }
                deviceInfoDao.closeDb();
                adapter.updateList(list);
            }
            // 更新设备状态
            else if (action.equals(BaseVolume.REASH_DEVICE_STATE)) {
                String strMac = intent.getStringExtra(BaseVolume.DEVICE_MAC);
                for (DeviceInfoCache device : list) {
                    if (device.getMac().equalsIgnoreCase(strMac) && device.getOnLine() == BaseVolume.DEVICE_NOT_LINE) {
                        device.setOnLine(BaseVolume.DEVICE_CONNECTING);
                        adapter.updateList(list);
                        // 初始化设备
                        ArrayList<DeviceInfoCache> deviceList = new ArrayList<DeviceInfoCache>();
                        deviceList.add(device);
                        initDevice(deviceList);
                        // 连接设备
                        connectDevice(device);
                        break;
                    }
                }
            } else if (action.equals(BaseVolume.USER_NOT_LINE)) {
                udpateDeviceState(BaseVolume.DEVICE_NOT_LINE);
            }
            // 接收到局域网数据
            else if (action.equals(BaseVolume.BROADCAST_RECVPIPE_LAN)) {
                XDevice xdevice = (XDevice) intent.getSerializableExtra(BaseVolume.RESULT_XDEVICE);
                String strData = intent.getStringExtra(BaseVolume.RESULT_DATA);
                dataAnalysis(xdevice.getMacAddress(), strData);
            }
            // 接收到外网数据
            else if (action.equals(BaseVolume.BROADCAST_RECVPIPE)) {
                XDevice xdevice = (XDevice) intent.getSerializableExtra(BaseVolume.RESULT_XDEVICE);
                String strData = intent.getStringExtra(BaseVolume.RESULT_DATA);
                dataAnalysis(xdevice.getMacAddress(), strData);
            }
        }
    };

    public static HashMap<String,Timer> timerMap = new HashMap<>();
    public static HashMap<String,MyTimerTask> timerTaskMap = new HashMap<>();
    public static HashMap<String,MyRunableTimer> runableMap = new HashMap<>();
    /**
     * 数据解析的回调
     */
    DataAnalysisHelper.AnalysisDataResultListener analysisDataResultListener = new DataAnalysisHelper.AnalysisDataResultListener() {
        @Override
        public void onSuccessFully(String strMac) {
//            if (handler.hasMessages(GETSTATE)) handler.removeMessages(GETSTATE);
            Timer nowTimer = timerMap.get(strMac.toLowerCase());
            if (nowTimer != null) {
                nowTimer.cancel();
                timerMap.remove(strMac.toLowerCase());
            }

            MyTimerTask nowTimerTask = timerTaskMap.get(strMac.toLowerCase());
            if (nowTimerTask != null) {
                nowTimerTask.setRun(false);
                nowTimerTask.cancel();
                timerTaskMap.remove(strMac.toLowerCase());
            }

//            MyRunableTimer myRunableTimer = runableMap.get(strMac.toLowerCase());
//            if (myRunableTimer != null) {
//                handler.removeCallbacks(myRunableTimer);
//            }



            Intent intent = new Intent();
            intent.setAction("aaaa");
//			mContext.sendBroadcast(intent);
//			DeviceListActivity.this.sendBroadcast(new Intent("aaaa").putExtra(BaseVolume.DEVICE_MAC,strMac));
            mContext.sendBroadcast(new Intent(BaseVolume.BROADCAST_DATA_ANALYSIS_FINISH).putExtra(BaseVolume.DEVICE_MAC, strMac));
            for (DeviceInfoCache info : list) {
                if (info.getMac().equalsIgnoreCase(strMac)) {
                    if (info.isQueryInfo()) {
                        info.setQueryInfo(false);
                        adapter.notifyDataSetChanged();
                    } else return;
                } else {
                    continue;
                }
            }


        }

        public void onError(String strMac) {

        }
    };

    /**
     * 数据解析
     */
    private void dataAnalysis(String strMac, String strData) {
        byte[] byteData = NetworkUtils.hexStringToBytes(strData);
        DataAnalysisHelper.getSelf(mContext).startAnalysisData(strMac, byteData, analysisDataResultListener);

    }

    /**
     * 创建设备分享码
     */
    private void CreateShareCode(XDevice xdevice) {
        loading.show();
        HttpManage.getInstance().shareDeviceByQrcode(xdevice.getDeviceId(), new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, HttpManage.Error error) {

                Log.e(TAG, "onError: 二维码分享，接口调用失败：" + error.toString(), null);
                loading.dismiss();
                XlinkUtils.shortTips("操作失败！");
            }

            public void onSuccess(int code, String response) {
                loading.dismiss();
                // 分享成功
                if (code == 200) {
                    // {"invite_code":"120fa2b634395002"}
                    Log.e(TAG, "onSuccess: 二维码分享成功：" + code + "，邀请码：" + response, null);
                    try {
                        JSONObject jsobject = new JSONObject(response);
                        String strCode = jsobject.optString("invite_code");
                        startActivity(new Intent(DeviceListActivity.this, ShareDeviceActivity.class).
                                putExtra(BaseVolume.DEVICE, nowSelectDevice).putExtra("invite_code", strCode));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    XlinkUtils.shortTips("操作失败！");
                }
            }
        });

    }

    /**
     * 接收二维码分享
     */
    private void startTakeCode(final String strMac, final XDevice xdevice, String strCode) {
        HttpManage.getInstance().acceptShare(strCode, new HttpManage.ResultCallback<String>() {
            public void onError(Header[] headers, HttpManage.Error error) {
                Log.e(TAG, "onError: 接收分享，接口异常！" + error.toString(), null);
                XlinkUtils.shortTips("该二维码已过期！");
            }

            public void onSuccess(int code, String response) {
                // 接收成功
                if (code == 200) {
                    Log.e(TAG, "onSuccess: 接收分享成功！" + code + "，" + response, null);
                    DeviceInfoDao mDeviceInfoDao = new DeviceInfoDao(mContext);
                    boolean isFind = mDeviceInfoDao.isDeviceExist(strMac, ControlService.userEmail);
                    DeviceInfoCache de = null;
                    if (isFind) {
                        return;
                    }
                    String name = DeviceListActivity.this.getString(R.string.xiangfen_zhilv);
                    de = new DeviceInfoCache(BaseVolume.PRODUCTID, name, "8888", strMac, xdevice, BaseVolume.DEFAULT_DEVICE_TYPE, ControlService.userEmail);
                    mDeviceInfoDao.insertSingleData(de);
                    de = mDeviceInfoDao.featchDeviceByID(strMac, ControlService.userEmail);
                    mDeviceInfoDao.closeDb();
                    de.setOnLine(BaseVolume.DEVICE_CONNECTING);

                    // 添加设备到列表中
                    list.add(de);
                    adapter.updateList(list);
                    if (list.size() != 0) {
                        m_lvDevice.setVisibility(View.VISIBLE);
                        m_imgDeviceNull.setVisibility(View.GONE);
                    }
                    // 初始化设备
                    ArrayList<DeviceInfoCache> deviceList = new ArrayList<DeviceInfoCache>();
                    deviceList.add(de);
                    initDevice(deviceList);
                    // 连接设备
                    connectDevice(de);
                } else {
                    Log.e(TAG, "onSuccess: 接收分享失败：" + code + "，" + response, null);
                    Toast.makeText(DeviceListActivity.this, "onSuccess: 接收分享失败：" + code + "，" + response, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化设备
     */
    private void initDevice(ArrayList<DeviceInfoCache> deviceList) {
        // 初始化设备
        Intent intent1 = new Intent(BaseVolume.INIT_DEVICE);
        intent1.putExtra("devices", deviceList);
        controlService.onStartCommand(intent1, 0, 0);
    }

    /**
     * 连接设备
     */
    private void connectDevice(DeviceInfoCache nowDevice) {
//    	Log.e("ControlDeviceActivity", "发送数据："+data);
        Intent intent = new Intent(BaseVolume.COMMAND_DEVICE);
        intent.putExtra("device", nowDevice);
        controlService.onStartCommand(intent, 0, 0);
    }

    /**
     * 发送指令
     */
    private void sendData(DeviceInfoCache nowDevice, String data) {
//    	Log.e("ControlDeviceActivity", "发送数据："+data);
        if (nowDevice == null) {
            return;
        }
        Intent intent = new Intent(BaseVolume.CONTROL_DEVICE);
        intent.putExtra("data", data);
        intent.putExtra("device", nowDevice);
        controlService.onStartCommand(intent, 0, 0);
    }


    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitAlert();
        }
        return super.dispatchKeyEvent(event);
    }

    private void exitAlert() {
        AreaAddWindowHint pwdDialog = new AreaAddWindowHint(this,
                R.style.Dialogstyle, DeviceListActivity.this.getResources().getString(R.string.wenxin_tishi),
                new AreaAddWindowHint.PeriodListener() {
                    public void refreshListener(String string) {
                        XlinkAgent.getInstance().stop();
                        finish();
                    }
                }, DeviceListActivity.this.getResources().getString(R.string.exit_content));
        pwdDialog.show();
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);
        for (Map.Entry<String, Timer> entry : timerMap.entrySet()) {
            entry.getValue().cancel();
        }
        timerMap.clear();
        for (Map.Entry<String, MyTimerTask> entry : timerTaskMap.entrySet()) {
            entry.getValue().setRun(false);
            entry.getValue().cancel();
        }
        timerTaskMap.clear();

//        for (Map.Entry<String, MyRunableTimer> entry : runableMap.entrySet()) {
//            handler.removeCallbacks(entry.getValue());
//        }
//        runableMap.clear();

    }
}
