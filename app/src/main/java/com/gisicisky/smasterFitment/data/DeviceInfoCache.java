package com.gisicisky.smasterFitment.data;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smartouwei.DeviceListActivity;
import com.example.smartouwei.adapter.DeviceAdapter;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;

public class DeviceInfoCache implements Serializable {


    private int id = -1;
    private boolean is_Active = false;
    private String active_date = "";
    private String last_login = "";
    private String active_code = "";
    private String auth_code = "";
    private String mcu_mod = "";
    private int mcu_version = -1;
    private String firmware_mod = "";
    private int firmware_version = -1;
    private int role = -1;
    private String PID = "";
    private String name = "";
    private String pwd = "";
    private String mac = "";
    private XDevice xDevice = null;
    // 默认状态，离线
    private int Online = BaseVolume.DEVICE_NOT_LINE;
    private String userID = "";
    private boolean isLan = false;
    private boolean is_checked = false;
    /**
     * 设备类型： GB光波，ZF蒸房，YG浴缸
     */
    private String deviceType = "GB";
    private boolean IsQueryInfo;

    public boolean isQueryInfo() {
        return IsQueryInfo;
    }

    public void setQueryInfo(boolean queryInfo) {
        IsQueryInfo = queryInfo;
    }
    //	private TimeOutListener timeOutListener;

    public DeviceInfoCache() {
    }

    public DeviceInfoCache(String pid, String name, String pwd, String m, XDevice x, String type, String uId) {
        this.PID = pid;
        this.name = name;
        this.pwd = pwd;
        this.mac = m;
        this.deviceType = type;
        this.xDevice = x;
        this.userID = uId;
    }


    public boolean isLan() {
        return isLan;
    }

    public void setLan(boolean isLan) {
        this.isLan = isLan;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isIs_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public boolean isIs_Active() {
        return is_Active;
    }

    public void setIs_Active(boolean is_Active) {
        this.is_Active = is_Active;
    }

    public String getActive_date() {
        return active_date;
    }

    public void setActive_date(String active_date) {
        this.active_date = active_date;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getActive_code() {
        return active_code;
    }

    public void setActive_code(String active_code) {
        this.active_code = active_code;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getMcu_mod() {
        return mcu_mod;
    }

    public void setMcu_mod(String mcu_mod) {
        this.mcu_mod = mcu_mod;
    }

    public int getMcu_version() {
        return mcu_version;
    }

    public void setMcu_version(int mcu_version) {
        this.mcu_version = mcu_version;
    }

    public String getFirmware_mod() {
        return firmware_mod;
    }

    public void setFirmware_mod(String firmware_mod) {
        this.firmware_mod = firmware_mod;
    }

    public int getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(int firmware_version) {
        this.firmware_version = firmware_version;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPid() {
        return PID;
    }

    public void setPid(String pid) {
        this.PID = pid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public XDevice getXDevice() {
        return xDevice;
    }

    public void setXDevice(XDevice de) {
        this.xDevice = de;
    }

    public String getxDeviceString() {
        String xdevice = "";
        JSONObject jo = XlinkAgent.deviceToJson(xDevice);
        xdevice = jo.toString();
        return xdevice;
    }

    public String getId() {
        return PID;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public int getOnLine() {
        return Online;
    }

    public void setOnLine(int online) {
        Online = online;

    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    private static XDevice CreateXDevice(String mac, int id) {
        XDevice xDevice = null;
        // 将String 转换成 JSON;
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
//			jDevice.put("accesskey", accessKey);
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

}
