package com.example.mytt.helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SmartLinkExportObject {

	private String SSID,PWD;
	private JMSmartLinkEncoder jmSmartLink;
	private Context context;
	private boolean connection = false;
	private ArrayList<Integer> dataArray = new ArrayList<Integer>();
	private final int DEFAULT_PORT = 10000;
	private DatagramSocket udpSocket ;
	int i = 0;
	private UdpServer udpServer = null;
	public SmartLinkExportObject(Context con) {
		this.context = con;
		this.jmSmartLink = new JMSmartLinkEncoder();
	}

	/** 开启配置 */
	public void connectWithSSID(String ssid,String pwd,final Handler handler) {
		// 已经启动，则不再启动
		if (connection) {
			return;
		}

//		Log.e("SmartLinkExportObject", "开启UDPServer...");
//		udpServer = new UdpServer("255.255.255.255",10000,handler);
//        Thread thread = new Thread(udpServer);
//        thread.start();

		SSID = ssid;
		PWD = pwd;
		connection = true;
		dataArray = jmSmartLink.CreateSmartLinkEncorderWithSSID(SSID, PWD);
//		for(int n = 0;n<dataArray.size();n++) {
//			MyLog.e("StartConfig", "待处理数据：：："+dataArray.get(n)+"-----------：：："+n);
//		}
		i = 0;
//		handler.post(runnable);
//		startUDPService();
		Log.e("SmartLinkExportObject", "++++++++++++++++开始配置++++++++++++++++",null);
		new Thread(){
			public void run() {
				while (i < dataArray.size() && connection) {
					int length = dataArray.get(i);
					StringBuffer mData = new StringBuffer();
					int value = 0;
					for (int j = 0; j < length; j++) {
						mData.append(value+"");
					}
//						Log.e("StartConfig", "发送数据：：："+mData+"-----------：：："+i);
					DatagramPacket dataPacket = null;
					String udpIP = getUdpServiceIP(context);
					if (udpIP == null)
						return;
					try {
						if(udpSocket == null)
							udpSocket = new DatagramSocket();
						InetAddress broadcastAddr;
						broadcastAddr = InetAddress.getByName(udpIP);
						String order = mData.toString();
						byte[] data = order.getBytes("utf8");
//			    			Log.e("SmartLink", "发送数据长度：：："+data.length+"-----------");
						dataPacket = new DatagramPacket(data, data.length, broadcastAddr,
								DEFAULT_PORT);
						udpSocket.send(dataPacket);
						udpSocket.setSoTimeout(3000);

						try {
							sleep(4);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IOException e) {
//			    			udpSocket = null;
						e.printStackTrace();
						Log.e("SmartLinkExportObject", "配置异常："+e.getMessage(),null);
					}
					i++;
				}
				connection = false;
//				dataArray.clear();
				Log.e("SmartLinkExportObject", "++++++++++++++++开始配置++++++++++++++++",null);

//				stopUDPServer();

				handler.sendEmptyMessage(NetworkUtilsUDP.SMARTLINK_STOP);
//				Log.e("AirKissHelper", "配置结束！");
			};
		}.start();

	}


	/** 停止配置 */
	public void closeConnection() {
//		stopUDPServer();

		connection = false;
		dataArray.clear();

	}

	@SuppressWarnings("unused")
	private void stopUDPServer() {

		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (udpServer != null) {
					//关闭UDP
					udpServer.setUdpLife(false);
					while (udpServer.getLifeMsg()); //等待udp阻塞结束，这里就体现出超时的好处了
					Looper.getMainLooper();
				}
			}
		});
//		Log.e("SmartLinkExportObject", "++++++++++++++++关闭UDPServer++++++++++++++++");

		thread.start();

	}

	private String getUdpServiceIP(Context context) {
		return "255.255.255.255";
	}

}
