package com.example.mytt.helper;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 */
public class UdpServer implements Runnable {
    private String ip = "255.255.255.255";
    private int port = 10000;
    private DatagramPacket dpRcv = null,dpSend = null;
    private static DatagramSocket ds = null;
    private InetSocketAddress inetSocketAddress = null;
    private byte[] msgRcv = new byte[1024];
    private boolean udpLife = true;     //udp生命线程
    private boolean udpLifeOver = true; //生命结束标志，false为结束
    private Handler handler;
    public static final int TYPE_ELIAN = 0;
    public static final int TYPE_AIRKISS = 1;
    /** 配置时，监听的类型 */
    private int listenerType = 0;
    public UdpServer(Handler handler) {
        this.handler = handler;
//    	JMSmartLinkEncoder.listRandmo.clear();
    }

    public UdpServer(String mIp, int mPort,Handler handler) {
        this.ip = mIp;
        this.port = mPort;
        this.handler = handler;
    }

    private void SetSoTime(int ms) throws SocketException {
        ds.setSoTimeout(ms);
    }

    public int getListenerType() {
        return listenerType;
    }

    /** 监听类型：0Elian；1Airkiss */
    public void setListenerType(int listenerType) {
        this.listenerType = listenerType;
    }

    //返回udp生命线程因子是否存活
    public boolean isUdpLife(){
        if (udpLife){
            return true;
        }

        return false;
    }

    //返回具体线程生命信息是否完结
    public boolean getLifeMsg(){
        return udpLifeOver;
    }

    //更改UDP生命线程因子
    public void setUdpLife(boolean b){
        udpLife = b;
    }

    public void Send(String sendStr) throws IOException {
//        Log.i("SocketInfo","客户端IP："+ dpRcv.getAddress().getHostAddress() +"客户端Port:"+ dpRcv.getPort());
        dpSend = new DatagramPacket(sendStr.getBytes(),sendStr.getBytes().length,dpRcv.getAddress(),dpRcv.getPort());
        ds.send(dpSend);
    }


    private int returnCheckCount = 0;
    public void run() {
        inetSocketAddress = new InetSocketAddress(ip, port);
        try {
            ds = new DatagramSocket(inetSocketAddress);
            Log.e("SocketInfo", "++++++++++++++++UDPServer已经启动++++++++++++++++");

            SetSoTime(1000);
            //设置超时，不需要可以删除
        } catch (SocketException e) {
            e.printStackTrace();
        }

        dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
        while (udpLife) {
            try {
                ds.receive(dpRcv);
                String string = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());
//                Log.e("SocketInfo", "SocketInfo：收到信息：" + string);
                // Elian配置
                if (listenerType == 0) {
                    if (string.indexOf(":") > 0) {
                        handler.sendEmptyMessage(NetworkUtilsUDP.ELIAN_SUCCESS);
                    }
                }
                // Airkiss配置
                else if (listenerType == 1) {
                    if (string.length() == 1) {
//                		Log.e("SocketInfo", "SocketInfo：收到一个字节：" + string);
                        if (string.equals(JMSmartLinkEncoder.asciiRandom)) {
                            ++returnCheckCount;
                            Log.e("SocketInfo", "匹配到随机数！！！！！！！！！！："+string);
                            if (returnCheckCount == 5) {
                                returnCheckCount = 0;
                                JMSmartLinkEncoder.asciiRandom = "";
                                handler.sendEmptyMessage(NetworkUtilsUDP.SMARTLINK_SUCCESS);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ds.close();
        Log.e("SocketInfo","++++++++++++++++UDPServer已经关闭++++++++++++++++");
        //udp生命结束
        udpLifeOver = false;
    }


    /**
     * ASCII码字符串转数字字符串
     *
     * @param String
     *            ASCII字符串
     * @return 字符串
     */
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }

    /**
     * 十六进制字符串装十进制
     *
     * @param hex
     *            十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }


}