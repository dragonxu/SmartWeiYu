package com.example.mytt.helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FindDeviceMacIp {

	@SuppressWarnings("unused")
	private Context con;
	private final int DEFAULT_PORT = 988;
	//	private final int DEFAULT_PORT = 10008;
	private DatagramSocket udpSocket ;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	@SuppressWarnings("unused")
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	private String TAG = "";
	public  void startFindCommand(final Context context,final Handler handler) {
		TAG = "FindDeviceMacIp";
		con = context;
		new Thread(new Runnable() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				DatagramPacket dataPacket = null;
//		    		String udpIP = getUdpServiceIP(context);
				String udpIP = "255.255.255.255";
				if (udpIP == null)
					return;
				try {

					if(udpSocket == null)
						udpSocket = new DatagramSocket();

					InetAddress broadcastAddr;
					broadcastAddr = InetAddress.getByName(udpIP);

//		    			String order = "HLK";
					String order = "hlkATat+mac=?";
					byte[] data = order.getBytes("utf8");
					dataPacket = new DatagramPacket(data, data.length, broadcastAddr,
							DEFAULT_PORT);
					udpSocket.send(dataPacket);

					Log.e(TAG, "run: 发送UDP："+order,null);

					while(true) {
						byte[] dataReceive = new byte[1024];
						DatagramPacket packetReceive = new DatagramPacket(dataReceive,
								dataReceive.length);
						udpSocket.setSoTimeout(1000*5);
						udpSocket.receive(packetReceive);
						String udpresult = new String(packetReceive.getData(),
								packetReceive.getOffset(), packetReceive.getLength());
						String ip = packetReceive.getAddress().getHostAddress();
						Log.e(TAG, "run: 返回数据："+udpresult+"+"+ip,null);

						String deviceMac = getMac(udpresult);
						Bundle bundle = new Bundle();
						bundle.putString(NetworkUtilsUDP.DEVIEC_MAC,deviceMac);
						bundle.putString(NetworkUtilsUDP.DEVIEC_IP, ip);
						Message msg = new Message();
						msg.what = NetworkUtilsUDP.FIND_DEVICE_MAC;
						msg.obj = bundle;
						handler.sendMessage(msg);
					}

				} catch (IOException e) {
//		    			udpSocket = null;
					e.printStackTrace();
				}
//		    		udpSocket.close();
			}
		}).start();


	}

	private String getMac(String str) {
		String mac = "";
		String[] strMac = str.split(",");

		String one = Integer.toHexString(Integer.parseInt(strMac[0])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[0])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[0]));
		String two = Integer.toHexString(Integer.parseInt(strMac[1])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[1])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[1]));
		String three = Integer.toHexString(Integer.parseInt(strMac[2])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[2])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[2]));
		String four = Integer.toHexString(Integer.parseInt(strMac[3])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[3])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[3]));
		String five = Integer.toHexString(Integer.parseInt(strMac[4])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[4])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[4]));
		String six = Integer.toHexString(Integer.parseInt(strMac[5])).length() == 2 ? Integer.toHexString(Integer.parseInt(strMac[5])) : "0"+ Integer.toHexString(Integer.parseInt(strMac[5]));

//		mac = one +":"+ two +":"+ three + ":"+four +":"+ five +":"+ six;
		mac = one + two + three + four + five + six;
		return mac;
	}

	public void M30Brocast(String addr,String command)
	{
		String udpresult;
		byte[] DataReceive= new byte[512];
		DatagramPacket pack = new DatagramPacket(DataReceive,DataReceive.length);
		MulticastSocket ms=null;
		try {
			ms = new MulticastSocket();
			InetAddress address = InetAddress.getByName(addr);
			byte out[] = command.getBytes();
			DatagramPacket dataPacket = new DatagramPacket(out, out.length, address, 988);

			ms.send(dataPacket);
			ms.setSoTimeout(500);
			ms.receive(pack);
			udpresult = new String(pack.getData(), pack.getOffset(), pack.getLength());
			Log.e(TAG, "M30Brocast: udpresult："+udpresult,null);
			ms.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
