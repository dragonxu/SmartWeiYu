package com.gisicisky.smasterFitment.utl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smartouwei.base.MyApp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

public class XlinkUtils {
    /**
     * Map ת��Ϊjson
     *
     * @param map
     * @return
     */
    public static JSONObject getJsonObject(Map<String, Object> map) {
	JSONObject jo = new JSONObject();
	Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
	while (iter.hasNext()) {
	    Entry<String, Object> entry = iter.next();
	    try {
		jo.put(entry.getKey(), entry.getValue());
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	return jo;

    }

    /**
     * BASE64����
     *
     * @param key
     * @return
     * @throws IOException
     */
    public static byte[] base64Decrypt(String key) {
	byte[] bs = Base64.decode(key, Base64.DEFAULT);
	if (bs == null || bs.length == 0) {
	    bs = key.getBytes();
	}
	return bs;
    }

    /**
     * �ж������Ƿ�����
     *
     * @return
     */
    public static boolean isConnected() {

	ConnectivityManager connectivity = (ConnectivityManager) MyApp.getApp()
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (null != connectivity) {

	    NetworkInfo info = connectivity.getActiveNetworkInfo();
	    if (info != null && info.isAvailable()) {
		return true;
	    }
	}
	return false;
    }

    public static String getHexBinString(byte[] bs) {
	StringBuffer log = new StringBuffer();
	for (int i = 0; i < bs.length; i++) {
	    log.append(String.format("%02X", (byte) bs[i]) + " ");
	}
	return log.toString();
    }

    /**
     * ��byteת���� ������.
     *
     * @param aByte
     * @return
     */
    public static String getBinString(byte aByte) {
	String out = "";
	int i = 0;
	for (i = 0; i < 8; i++) {
	    int v = (aByte << i) & 0x80;
	    v = (v >> 7) & 1;
	    out += v;
	}
	return out;
    }

    static private final int bitValue0 = 0x01; // 0000 0001
    static private final int bitValue1 = 0x02; // 0000 0010
    static private final int bitValue2 = 0x04; // 0000 0100
    static private final int bitValue3 = 0x08; // 0000 1000
    static private final int bitValue4 = 0x10; // 0001 0000
    static private final int bitValue5 = 0x20; // 0010 0000
    static private final int bitValue6 = 0x40; // 0100 0000
    static private final int bitValue7 = 0x80; // 1000 0000

    /**
     * ����flags
     *
     * @param index
     *            �ڼ���bit�����㿪ʼ��
     * @param value
     *            byteֵ
     * @return
     */
    public static byte setByteBit(int index, byte value) {
	if (index > 7) {
	    throw new IllegalAccessError("setByteBit error index>7!!! ");
	}
	byte ret = value;
	if (index == 0) {
	    ret |= bitValue0;
	} else if (index == 1) {
	    ret |= bitValue1;
	} else if (index == 2) {
	    ret |= bitValue2;
	} else if (index == 3) {
	    ret |= bitValue3;
	} else if (index == 4) {
	    ret |= bitValue4;
	} else if (index == 5) {
	    ret |= bitValue5;
	} else if (index == 6) {
	    ret |= bitValue6;
	} else if (index == 7) {
	    ret |= bitValue7;
	}
	return ret;
    }

    /**
     * ��ȡ flags ���Сbit
     *
     * @param anByte
     * @param index
     * @return
     */
    public static boolean readFlagsBit(byte anByte, int index) {
	if (index > 7) {
	    throw new IllegalAccessError("readFlagsBit error index>7!!! ");
	}
	int temp = anByte << (7 - index);
	temp = temp >> 7;
	temp &= 0x01;
	if (temp == 1) {
	    return true;
	}
	// if((anByte & (01<<index)) !=0){
	// return true;
	// }
	return false;
    }

    /**
     * ��16λ��shortת����byte����
     *
     * @param s
     *            short
     * @return byte[] ����Ϊ2
     * */
    public static byte[] shortToByteArray(short s) {
	byte[] targets = new byte[2];
	for (int i = 0; i < 2; i++) {
	    int offset = (targets.length - 1 - i) * 8;
	    targets[i] = (byte) ((s >>> offset) & 0xff);
	}
	return targets;
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getAdapterView(View convertView, int id) {
	SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
	if (viewHolder == null) {
	    viewHolder = new SparseArray<View>();
	    convertView.setTag(viewHolder);
	}
	View childView = viewHolder.get(id);
	if (childView == null) {
	    childView = convertView.findViewById(id);
	    viewHolder.put(id, childView);
	}
	return (T) childView;
    }

    public final static String MD5(String s) {
	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F' };
	try {
	    byte[] btInput = s.getBytes();
	    // ���MD5ժҪ�㷨�� MessageDigest ����
	    MessageDigest mdInst = MessageDigest.getInstance("MD5");
	    // ʹ��ָ�����ֽڸ���ժҪ
	    mdInst.update(btInput);
	    // �������
	    byte[] md = mdInst.digest();
	    // ������ת����ʮ�����Ƶ��ַ�����ʽ
	    int j = md.length;
	    char str[] = new char[j * 2];
	    int k = 0;
	    for (int i = 0; i < j; i++) {
		byte byte0 = md[i];
		str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		str[k++] = hexDigits[byte0 & 0xf];
	    }
	    return new String(str);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * BASE64����
     *
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static String base64EncryptUTF(byte[] key)
	    throws UnsupportedEncodingException {
	return new String(Base64.encode(key, Base64.DEFAULT), "UTF-8");
    }

    public static String base64Encrypt(byte[] key) {
	return new String(Base64.encode(key, Base64.DEFAULT));
    }

    /**
     * �ж��Ƿ���wifi����
     */
    public static boolean isWifi() {
	ConnectivityManager cm = (ConnectivityManager) MyApp.getApp()
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (cm == null || cm.getActiveNetworkInfo() == null) {
	    return false;
	}

	return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * ���������ý���
     */
    public static void openSetting(Activity activity) {
	Intent intent = null;
	if (android.os.Build.VERSION.SDK_INT > 10) {
	    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	} else {
	    intent = new Intent("/");
	    ComponentName cm = new ComponentName("com.android.settings",
		    "com.android.settings.WirelessSettings");
	    intent.setComponent(cm);
	    intent.setAction("android.intent.action.VIEW");
	}
	activity.startActivityForResult(intent, 0);
    }

    public static void shortTips(String tip) {
//	Log.e("Tips", tip);
	Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_SHORT).show();
    }

    public static void longTips(String tip) {
	Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_LONG).show();
    }


    /**
     * ��֤�ַ����Ƿ�Ϊ�գ�ȫΪ�հ��ַ�\"NULL"\"null"Ҳ��Ϊ�� ������: </br> ����: </br> ������Ա: liangxiaxu
     * </br> ����ʱ��: 2013-5-27 </br>
     *
     * @param string
     * @return
     */
    public static boolean isNotNull(String string) {
	if ((string != null) && (!"null".equalsIgnoreCase(string))
		&& (string.length() > 0) && (!string.trim().equals(""))) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * ��ȡ��ǰ����
     * @param lat γ��
     * @param lon ����
     * @param con
     * @return
     */
    public static String getNewCity(double lat,double lon,Context con) {
    	String city = "";

		 List<Address>   addList = null;
	       Geocoder ge = new Geocoder(con);
	        try {
	        addList = ge.getFromLocation(lat, lon, 1);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        if(addList!=null && addList.size()>0){
	            for(int i=0; i<addList.size(); i++){
	                Address ad = addList.get(i);
	                city =  ad.getLocality();
	            }
	        }

    	return city;

    }

}
