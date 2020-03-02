package com.gisicisky.smasterFitment.data;

import android.content.Context;
import android.content.Intent;

import com.gisicisky.smasterFitment.utl.BaseVolume;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-07-16.
 */

public class DataAnalysisHelper {
    private Context mContext;
    private static DataAnalysisHelper dataAnalysisHelper;
    private HashMap<String, DeviceState> hashMap = new HashMap<String, DeviceState>();

    public static DataAnalysisHelper getSelf(Context con) {
        if (dataAnalysisHelper == null) {
            dataAnalysisHelper = new DataAnalysisHelper(con);
        }
        return dataAnalysisHelper;
    }

    public DataAnalysisHelper(Context con) {
        this.mContext = con;
    }

    /**
     * 解析数据
     *
     * @param strMac
     * @param byData
     */
    public void startAnalysisData(String strMac, byte[] byData, AnalysisDataResultListener listener) {
        strMac = strMac.toLowerCase();
        DeviceState ds = hashMap.get(strMac);
        if (ds == null) {
            ds = new DeviceState(strMac);
        }
        ds.setNowStateBuffer(byData);
        // 开始解析数据
        ds.setiZhuJiType(byData[BaseVolume.COMMAND_LOC_LEIXING]);
        ds.setiJiaRe(byData[BaseVolume.COMMAND_LOC_JIARE]);
        ds.setiGuangBo1(byData[BaseVolume.COMMAND_LOC_GUANGBO_1]);
        ds.setiGuangBo2(byData[BaseVolume.COMMAND_LOC_GUANGBO_2]);
        ds.setiFengBeng(byData[BaseVolume.COMMAND_LOC_FENGBENG]);
        ds.setiShuiBeng1(byData[BaseVolume.COMMAND_LOC_SHUIBENG_1]);
        ds.setiShuiBeng2(byData[BaseVolume.COMMAND_LOC_SHUIBENG_2]);
        ds.setiShuiBeng3(byData[BaseVolume.COMMAND_LOC_SHUIBENG_3]);
        ds.setiXunHuanBeng(byData[BaseVolume.COMMAND_LOC_XUNHUAN_BENG]);
        ds.setiZhaoMing(byData[BaseVolume.COMMAND_LOC_ZHAOMING]);
        ds.setiBeiDeng(byData[BaseVolume.COMMAND_LOC_BEIDENG]);
        ds.setiShuiDiDeng(byData[BaseVolume.COMMAND_LOC_SHUIDI_DENG]);
        ds.setiChouFengShan(byData[BaseVolume.COMMAND_LOC_CHOUFENG_SHAN]);
        ds.setiO3(byData[BaseVolume.COMMAND_LOC_O3]);
        ds.setiJinShui(byData[BaseVolume.COMMAND_LOC_JINSHUI]);
        ds.setiFangShui(byData[BaseVolume.COMMAND_LOC_FANGSHUI]);
        ds.setiShuiWei(byData[BaseVolume.COMMAND_LOC_SHUIWEI]);
        ds.setiWenDuJiaoZhun(byData[BaseVolume.COMMAND_LOC_WENDU_JIAOZHUN]);
        ds.setiJianCeWenDu(byData[BaseVolume.COMMAND_LOC_JIANCE_WENDU]);
        ds.setiYuZhiWenDu(byData[BaseVolume.COMMAND_LOC_YUZHI_WENDU]);
        ds.setiDaoJiShiJian(byData[BaseVolume.COMMAND_LOC_DAOJI_SHIJIAN]);
        ds.setiYuZhiShiJian(byData[BaseVolume.COMMAND_LOC_YUZHI_SHIJIAN]);
        ds.setiFMPinLv(byData[BaseVolume.COMMAND_LOC_FM_PINLV]);
        ds.setiFMPinDao(byData[BaseVolume.COMMAND_LOC_FM_PINDAO]);
//        ds.setiBoFangShiJian(byData[BaseVolume.COMMAND_LOC_BOFANG_SHIJIAN]);
        ds.setiYuYueHour(byData[BaseVolume.COMMAND_LOC_BOFANG_SHIJIAN_HOUR]);
        ds.setiYuYueMin(byData[BaseVolume.COMMAND_LOC_BOFANG_SHIJIAN_MIN]);
//        ds.setiQuMuHao(byData[BaseVolume.COMMAND_LOC_QUMU_HAO]);
        ds.setiFM(byData[BaseVolume.COMMAND_LOC_FM]);
        ds.setiMP3(byData[BaseVolume.COMMAND_LOC_MP3]);
        ds.setiLanYa(byData[BaseVolume.COMMAND_LOC_LANYA]);
        ds.setiAux(byData[BaseVolume.COMMAND_LOC_AUX]);
        ds.setiYinLiang(byData[BaseVolume.COMMAND_LOC_YINLIANG]);
        ds.setiZhuJiZhuangTai(byData[BaseVolume.COMMAND_LOC_ZHUJI_ZHUANGTAI]);
        ds.setiDianHua(byData[BaseVolume.COMMAND_LOC_DIANHUA]);
        ds.setiSheShiDu(byData[BaseVolume.COMMAND_LOC_SHESHIDU_HUASHIDU]);
        ds.setiYuYueKaiGuan(byData[BaseVolume.COMMAND_LOC_YUYUE_KAIGUAN]);
        ds.setiYinYue(byData[BaseVolume.COMMAND_LOC_MP3]);
        ds.setiKongQi(byData[BaseVolume.COMMAND_LOC_KONGQI]);
        hashMap.put(strMac, ds);
        // 解析完成，通知上层刷新界面
        listener.onSuccessFully(strMac);
    }

    public interface AnalysisDataResultListener {

        void onSuccessFully(String strMac);

        void onError(String strMac);

    }

    public DeviceState getDataBufferByMac(String strMac) {
        strMac = strMac.toLowerCase();
        return hashMap.get(strMac);
    }

    /**
     * 清除指定设备的缓存数据
     *
     * @param strMac
     */
    public void clearAllDataBufferByMac(String strMac) {
        strMac = strMac.toLowerCase();
        hashMap.remove(strMac);
    }

    /**
     * 清除所有设备的缓存数据
     */
    public void clearAllDataBuffer() {
        hashMap.clear();
    }

}
