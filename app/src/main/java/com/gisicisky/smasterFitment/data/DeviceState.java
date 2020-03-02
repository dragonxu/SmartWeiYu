package com.gisicisky.smasterFitment.data;

public class DeviceState {

    private byte[] NowStateBuffer = null;
    private String strMac;
    private int iZhuJiType;
    private int iJiaRe;
    private int iKongQi;
    private int iFengBeng;
    private int iShuiBeng1;
    private int iShuiBeng2;
    private int iShuiBeng3;
    private int iXunHuanBeng;
    private int iChouFengShan;
    private int iO3;
    private int iJinShui;
    private int iFangShui;
    private int iGuangBo1;
    private int iGuangBo2;
    private int iZhaoMing;
    private int iBeiDeng;
    private int iShuiDiDeng;
    private int iShuiWei;
    private int iWenDuJiaoZhun;
    private int iJianCeWenDu;
    private int iYuZhiWenDu;
    private int iDaoJiShiJian;
    private int iYuZhiShiJian;
    private int iFMPinLv;
    private int iFMPinDao;
    private int iYuYueHour;
    private int iYuYueMin;
    private int iQuMuHao;
    private int iFM;
    private int iMP3;
    private int iLanYa;
    private int iAux;
    private int iYinLiang;
    private int iZhuJiZhuangTai;
    private int iDianHua;
    private int iSheShiDu;
    private int iYuYueKaiGuan;
    private int iYinYue;

    public int getiKongQi() {
        if (iKongQi < 0) {
            iKongQi += 256;
        }
        return iKongQi;
    }

    public void setiKongQi(int iKongQi) {
        this.iKongQi = iKongQi;
    }

    public int getiYinYue() {
        if (iYinYue < 0) {
            iYinYue += 256;
        }
        return iYinYue;
    }

    public void setiYinYue(int iYinYue) {
        this.iYinYue = iYinYue;
    }

    public int getiYuYueHour() {
        if (iYuYueHour < 0) {
            iYuYueHour += 256;
        }
        return iYuYueHour;
    }

    public void setiYuYueHour(int iYuYueHour) {
        this.iYuYueHour = iYuYueHour;
    }

    public int getiYuYueMin() {
        if (iYuYueMin < 0) {
            iYuYueMin += 256;
        }
        return iYuYueMin;
    }

    public void setiYuYueMin(int iYuYueMin) {
        this.iYuYueMin = iYuYueMin;
    }

    public DeviceState(String mac) {
        this.strMac = mac;
    }

    public byte[] getNowStateBuffer() {
        return NowStateBuffer;
    }

    public void setNowStateBuffer(byte[] nowStateBuffer) {
        NowStateBuffer = nowStateBuffer;
    }

    public String getStrMac() {
        return strMac;
    }

    public void setStrMac(String strMac) {
        this.strMac = strMac;
    }


    public void setiZhuJiType(int iZhuJiType) {
        this.iZhuJiType = iZhuJiType;
    }

    public void setiJiaRe(int iJiaRe) {
        this.iJiaRe = iJiaRe;
    }

    public void setiFengBeng(int iFengBeng) {
        this.iFengBeng = iFengBeng;
    }

    public void setiShuiBeng1(int iShuiBeng1) {
        this.iShuiBeng1 = iShuiBeng1;
    }

    public void setiShuiBeng2(int iShuiBeng2) {
        this.iShuiBeng2 = iShuiBeng2;
    }

    public void setiShuiBeng3(int iShuiBeng3) {
        this.iShuiBeng3 = iShuiBeng3;
    }

    public void setiXunHuanBeng(int iXunHuanBeng) {
        this.iXunHuanBeng = iXunHuanBeng;
    }

    public void setiChouFengShan(int iChouFengShan) {
        this.iChouFengShan = iChouFengShan;
    }

    public void setiO3(int iO3) {
        this.iO3 = iO3;
    }

    public void setiJinShui(int iJinShui) {
        this.iJinShui = iJinShui;
    }

    public void setiFangShui(int iFangShui) {
        this.iFangShui = iFangShui;
    }

    public void setiGuangBo1(int iGuangBo1) {
        this.iGuangBo1 = iGuangBo1;
    }

    public void setiGuangBo2(int iGuangBo2) {
        this.iGuangBo2 = iGuangBo2;
    }

    public void setiZhaoMing(int iZhaoMing) {
        this.iZhaoMing = iZhaoMing;
    }

    public void setiBeiDeng(int iBeiDeng) {
        this.iBeiDeng = iBeiDeng;
    }

    public void setiShuiDiDeng(int iShuiDiDeng) {
        this.iShuiDiDeng = iShuiDiDeng;
    }

    public void setiShuiWei(int iShuiWei) {
        this.iShuiWei = iShuiWei;
    }

    public void setiWenDuJiaoZhun(int iWenDuJiaoZhun) {
        this.iWenDuJiaoZhun = iWenDuJiaoZhun;
    }

    public void setiJianCeWenDu(int iJianCeWenDu) {
        this.iJianCeWenDu = iJianCeWenDu;
    }

    public void setiYuZhiWenDu(int iYuZhiWenDu) {
        this.iYuZhiWenDu = iYuZhiWenDu;
    }

    public void setiDaoJiShiJian(int iDaoJiShiJian) {
        this.iDaoJiShiJian = iDaoJiShiJian;
    }

    public void setiYuZhiShiJian(int iYuZhiShiJian) {
        this.iYuZhiShiJian = iYuZhiShiJian;
    }

    public void setiFMPinLv(int iFMPinLv) {
        this.iFMPinLv = iFMPinLv;
    }

    public void setiFMPinDao(int iFMPinDao) {
        this.iFMPinDao = iFMPinDao;
    }


    public void setiQuMuHao(int iQuMuHao) {
        this.iQuMuHao = iQuMuHao;
    }

    public void setiFM(int iFM) {
        this.iFM = iFM;
    }

    public void setiMP3(int iMP3) {
        this.iMP3 = iMP3;
    }

    public void setiLanYa(int iLanYa) {
        this.iLanYa = iLanYa;
    }

    public void setiAux(int iAux) {
        this.iAux = iAux;
    }

    public void setiYinLiang(int iYinLiang) {
        this.iYinLiang = iYinLiang;
    }

    public void setiZhuJiZhuangTai(int iZhuJiZhuangTai) {
        this.iZhuJiZhuangTai = iZhuJiZhuangTai;
    }

    public void setiDianHua(int iDianHua) {
        this.iDianHua = iDianHua;
    }

    public void setiSheShiDu(int iSheShiDu) {
        this.iSheShiDu = iSheShiDu;
    }

    public void setiYuYueKaiGuan(int iYuYueKaiGuan) {
        this.iYuYueKaiGuan = iYuYueKaiGuan;
    }

    /**
     * +++++++++++++++++++++++++++++++++++++++++
     */

    public int getiZhuJiType() {
        if (iZhuJiType < 0) {
            iZhuJiType += 256;
        }
        return iZhuJiType;
    }

    public String getZhuJiType() {

        if (iZhuJiType == 0x00) {
            return "YG";
        } else if (iZhuJiType == 0x01) {
            return "ZF";
        } else if (iZhuJiType == 0x02) {
            return "GB";
        } else {
            //return "YG";
            return String.valueOf(iZhuJiType);
        }

    }

    public int getiJiaRe() {
        if (iJiaRe < 0) {
            iJiaRe += 256;
        }
        return iJiaRe;
    }

    public String getJiaRe() {
        if (getiJiaRe() == 0xff) {
            return null;
        } else if (getiJiaRe() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public String getKongQi() {
        if (getiKongQi() == 0xff) {
            return null;
        } else if (getiKongQi() == 0x00) {
            return "false";
        } else {
            return "true";
        }
    }

    public String getYinYue() {
        if (getiYinYue() == 0xff) {
            return null;
        } else if (getiYinYue() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiFengBeng() {
        if (iFengBeng < 0) {
            iFengBeng += 256;
        }
        return iFengBeng;
    }

    public String getFengBeng() {

        if (getiFengBeng() == 0xff) {
            return null;
        } else if (getiFengBeng() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiShuiBeng1() {
        if (iShuiBeng1 < 0) {
            iShuiBeng1 += 256;
        }
        return iShuiBeng1;
    }

    public String getShuiBeng1() {

        if (getiShuiBeng1() == 0xff) {
            return null;
        } else if (getiShuiBeng1() == 0x00) {
            return "false";
        } else {
            return "true";
        }


    }

    public int getiShuiBeng2() {
        if (iShuiBeng2 < 0) {
            iShuiBeng2 += 256;
        }
        return iShuiBeng2;
    }

    public String getShuiBeng2() {
        if (getiShuiBeng2() == 0xff) {
            return null;
        } else if (getiShuiBeng2() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiShuiBeng3() {
        if (iShuiBeng3 < 0) {
            iShuiBeng3 += 256;
        }
        return iShuiBeng3;
    }

    public String getShuiBeng3() {
        if (getiShuiBeng3() == 0xff) {
            return null;
        } else if (getiShuiBeng3() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiXunHuanBeng() {
        if (iXunHuanBeng < 0) {
            iXunHuanBeng += 256;
        }
        return iXunHuanBeng;
    }

    public String getXunHuanBeng() {
        if (getiXunHuanBeng() == 0xff) {
            return null;
        } else if (getiXunHuanBeng() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiChouFengShan() {
        if (iChouFengShan < 0) {
            iChouFengShan += 256;
        }
        return iChouFengShan;
    }

    public String getChouFengShan() {
        if (iChouFengShan == 0xff) {
            return null;
        } else if (iChouFengShan == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiO3() {
        if (iO3 < 0) {
            iO3 += 256;
        }
        return iO3;
    }

    public String getO3() {
        if (getiO3() == 0xff) {
            return null;
        } else if (getiO3() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiJinShui() {
        if (iJinShui < 0) {
            iJinShui += 256;
        }
        return iJinShui;
    }

    public String getJinShui() {
        if (getiJinShui() == 0xff) {
            return null;
        } else if (getiJinShui() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiFangShui() {
        if (iFangShui < 0) {
            iFangShui += 256;
        }
        return iFangShui;
    }

    public String getFangShui() {
        if (getiFangShui() == 0xff) {
            return null;
        } else if (getiFangShui() == 0x00) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiGuangBo1() {
        if (iGuangBo1 < 0) {
            iGuangBo1 += 256;
        }
        return iGuangBo1;
    }

    public int getiGuangBo2() {
        if (iGuangBo2 < 0) {
            iGuangBo2 += 256;
        }
        return iGuangBo2;
    }

    public int getiZhaoMing() {
        if (iZhaoMing < 0) {
            iZhaoMing += 256;
        }
        return iZhaoMing;
    }

    public String getZhaoMing() {
        if (getiZhaoMing() == 0xff) {
            return null;
        } else if (getiZhaoMing() == 0x00 || getiZhaoMing() == 0xE0) {
            return "false";
        } else if (getiZhaoMing() == 0xef) {
            return "true";
        }
        return null;

    }

    // fix me 背灯，需要重点解析
    public int getiBeiDeng() {
        if (iBeiDeng < 0) {
            iBeiDeng += 256;
        }
        return iBeiDeng;
    }

    public int getBeiDengLight() {
        if (iBeiDeng == 0xff) {
            return -1;
        }
        // 10进制转16进制
        String strHex = String.format("%02x", iBeiDeng);
        String strLast = strHex.substring(1);
        if (strLast.equalsIgnoreCase("0")) {
            return 0;
        } else {
            return Integer.parseInt(strLast, 16);
        }
    }

    public String getBeiDengColor() {
        if (getiBeiDeng() == 0xff) {
            return null;
        }
        // 10进制转16进制
        String strHex = Integer.toHexString(getiBeiDeng());
        String strFirst = strHex.substring(0, 1);
        return strFirst;
    }

    public int getiShuiDiDeng() {
        if (iShuiDiDeng < 0) {
            iShuiDiDeng += 256;
        }
        return iShuiDiDeng;
    }

    public String getShuiDiDeng() {
        int iShuiDiDeng = getiShuiDiDeng();
        if (iShuiDiDeng == 0xff) {
            return null;
        } else if (iShuiDiDeng == 0xE0 || iZhaoMing == 0xE0) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiShuiWei() {
        if (iShuiWei < 0) {
            iShuiWei += 256;
        }
        return iShuiWei;
    }

    public String getShuiWei() {
        if (getiShuiWei() == 0xff) {
            return null;
        } else if (getiShuiWei() == 0x00) {
            return "false";
        } else {
            return "true";
        }
    }

    public int getiWenDuJiaoZhun() {
        if (iWenDuJiaoZhun < 0) {
            iWenDuJiaoZhun += 256;
        }
        return iWenDuJiaoZhun;
    }

    public int getiJianCeWenDu() {
        if (iJianCeWenDu < 0) {
            iJianCeWenDu += 256;
        }
        return iJianCeWenDu;
    }

    public int getiYuZhiWenDu() {
        if (iYuZhiWenDu < 0) {
            iYuZhiWenDu += 256;
        }
        return iYuZhiWenDu;
    }

    public int getiDaoJiShiJian() {
        return iDaoJiShiJian;
    }

    public int getiYuZhiShiJian() {
        return iYuZhiShiJian;
    }

    public int getiFMPinLv() {
        return iFMPinLv;
    }

    public float getfFMPinLv() {
        if (iFMPinLv == 0xff) {
            return 0xff;
        }

        if (iFMPinLv < 0) {
            iFMPinLv += 256;
        }
        float fPinLv = (float) (iFMPinLv / 10.0);

        return (fPinLv + 87.0f);
    }

    public int getiFMPinDao() {

        return iFMPinDao;
    }


    public int getiQuMuHao() {
        return iQuMuHao;
    }

    public int getiFM() {
        if (iFM < 0) {
            iFM += 256;
        }
        return iFM;
    }

    public String getFMState() {
        if (getiFM() == 0xff) {
            return null;
        }
        // 10进制转16进制
        String strHex = Integer.toHexString(iFM);
        String strLast = strHex.substring(1);
        if (strLast.equalsIgnoreCase("0")) {
            return "false";
        } else {
            return "true";
        }


    }

    public int getiMP3() {
        if (iMP3 < 0) {
            iMP3 += 256;
        }
        return iMP3;
    }

    public String getMP3State() {
        if (getiMP3() == 0xff) {
            return null;
        }
        // 10进制转16进制
        String strHex = Integer.toHexString(getiMP3());
        String strLast = strHex.substring(1);
        if (strLast.equalsIgnoreCase("0") || strLast.equalsIgnoreCase("2")) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiLanYa() {
        if (iLanYa < 0) {
            iLanYa += 256;
        }
        return iLanYa;
    }

    public String getLanYaState() {
        if (getiLanYa() == 0xff) {
            return null;
        }
        // 10进制转16进制
        String strHex = Integer.toHexString(getiLanYa());
        String strFirst = strHex.substring(0, 1);
        String strLast = strHex.substring(1);
        if (strFirst.equalsIgnoreCase("0")) {
            return "未连接";
        } else {
            if (strLast.equalsIgnoreCase("0") || strLast.equalsIgnoreCase("2")) {
                return "false";
            } else {
                return "true";
            }
        }
    }


    public int getiAux() {
        if (iAux < 0) {
            iAux += 256;
        }
        return iAux;
    }

    public String getAuxState() {
        if (getiAux() == 0xff) {
            return null;
        }
        // 10进制转16进制
        String strHex = Integer.toHexString(getiAux());
        String strLast = strHex.substring(1);
        if (strLast.equalsIgnoreCase("0")) {
            return "false";
        } else {
            return "true";
        }

    }

    public int getiYinLiang() {
        if (iYinLiang < 0) {
            iYinLiang += 256;
        }
        return iYinLiang;
    }

    public int getiZhuJiZhuangTai() {
        return iZhuJiZhuangTai;
    }

    public String getZhuJiZhuangTai() {
        if (iZhuJiZhuangTai == 0xff) {
            return null;
        } else if (iZhuJiZhuangTai == 0x00) {
            return "false";
        } else {
            return "true";
        }
    }

    public int getiDianHua() {
        return iDianHua;
    }

    public int getiSheShiDu() {
        return iSheShiDu;
    }

    public String getIsSheShiDu() {
        if (iSheShiDu == 0xff) {
            return null;
        } else if (iSheShiDu == 0x00) {
            return "true";
        } else {
            return "false";
        }
    }

    public int getiYuYueKaiGuan() {
        if (iYuYueKaiGuan < 0) {
            iYuYueKaiGuan += 256;
        }
        return iYuYueKaiGuan;
    }

    public String getYuYueKaiGuan() {
        if (getiYuYueKaiGuan() == 0xff) {
            return null;
        } else if (getiYuYueKaiGuan() == 0x00) {
            return "false";
        } else {
            return "true";
        }
    }

}
