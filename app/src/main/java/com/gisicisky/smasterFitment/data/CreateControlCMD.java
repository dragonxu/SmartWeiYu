package com.gisicisky.smasterFitment.data;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.format.Time;

import com.example.smartouwei.DeviceListActivity;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import com.gisicisky.smasterFitment.utl.NetworkUtils;


public class CreateControlCMD {


    /**
     * 获取设备状态信息
     *
     * @return
     */
    public static byte[] getDeviceState(String strMac) {
        byte[] SendData = new byte[48];
        SendData[0] = (byte) 0xaa;
        int iCount = DeviceListActivity.MacCountMap.get(strMac.toLowerCase());
        ++iCount;
        DeviceListActivity.MacCountMap.put(strMac.toLowerCase(), iCount);
        SendData[1] = (byte) iCount;
        SendData[2] = (byte) 0xff;
        for (int i = 3; i < 46; i++) {
            SendData[i] = (byte) 0xff;
        }
        // 计算校验
        byte[] check = SumCheck(SendData, 1);
        SendData[SendData.length - 2] = check[0];
        SendData[SendData.length - 1] = 0x55;

        return SendData;
    }


    /**
     * 获取控制指令
     *
     * @param oldData  原本的数据，需要在上面修改
     * @param valueMap key:代表位置，value：代表数值（16进制的字符串）
     * @return
     */
    public static byte[] getCtrCommandData(String strMac, byte[] oldData, HashMap<Integer, String> valueMap) {

        int iCount = DeviceListActivity.MacCountMap.get(strMac.toLowerCase());
        ++iCount;
        DeviceListActivity.MacCountMap.put(strMac.toLowerCase(), iCount);

        // 遍历数组
        Iterator entries = valueMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer) entry.getKey();
            String value = (String) entry.getValue();
            // 在相对位置赋值
            oldData[key] = (byte) Integer.parseInt(value, 16);
        }
        byte[] SendData = new byte[48];
        // 先拿出需要算校验和的部分
        for (int i = 0; i < SendData.length - 2; i++) {
            SendData[i] = oldData[i];
        }

        // 次数
        SendData[1] = (byte) iCount;

        // 计算校验
        byte[] check = SumCheck(SendData, 1);
        SendData[SendData.length - 2] = check[0];
        SendData[SendData.length - 1] = 0x55;

        return SendData;

    }

    /**
     * 校验和
     *
     * @param msg0   需要计算校验和的byte数组
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    public static byte[] SumCheck(byte[] msg0, int length) {
        // 去掉前一位（因为是包头）
        byte[] msg = new byte[msg0.length - 2];
        for (int i = 0; i < msg.length; i++) {
            msg[i] = msg0[(i + 2)];
        }

        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return mByte;
    }


}
