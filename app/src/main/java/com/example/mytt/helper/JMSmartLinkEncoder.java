package com.example.mytt.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JMSmartLinkEncoder {

	private ArrayList<Integer> arrayList = new ArrayList<Integer>();
	/** ????? */
	private String strRandom = "";
	public static String asciiRandom = "";

//	public static List<String> listRandmo = new ArrayList<String>();

	public ArrayList<Integer> CreateSmartLinkEncorderWithSSID(String SSID,String PWD) {

		arrayList.clear();
		int times  = 5;
		// ????????????(127????)
		Random rand = new Random();
		int iRandom = rand.nextInt(127);
		strRandom = Integer.toHexString(iRandom);
		strRandom = strRandom.length() == 2? strRandom : "0"+strRandom;
		asciiRandom = NetworkUtilsUDP.convertHexToString(strRandom);

		while(times >0) {
			times--;
			getLeadingPart();
			getMagicCodeWithSSID(SSID,PWD);
			for (int i = 0; i < 15; i++) {
				getPrefixCodeWithPSW(PWD);
				StringBuilder data = NetworkUtilsUDP.parseAscii(PWD);
				if (PWD.equals("")) {
					data = data.append("00");
				}
				data.append(strRandom);
				data.append(NetworkUtilsUDP.parseAscii(SSID));

				int size = 8;
				int index = 0;
				String tempData = "";
				for (index = 0;index < (data.length() / size); index++) {
					// 8???????4?????
					tempData = data.substring(index * size, (index+1) * size);
					getSequenceWithIndex(index,tempData);
				}

				if ((data.length() % size) != 0) {
					// 8???????4?????
					tempData = data.substring(index * size, (index * size+(data.length() % size)));
					getSequenceWithIndex(index,tempData);
				}
			}
		}

		ArrayList<Integer> newList = new ArrayList<Integer>();

		for (Integer obj : arrayList) {
			newList.add(obj);
		}
		for (Integer obj : arrayList) {
			newList.add(obj);
		}

		return newList;

	}

	private void getSequenceWithIndex(int index,String data) {
		int newIndex       = index & 0xFF;
		//	String test  =  Integer.toHexString(newIndex);

		String mData = String.format("%02x", newIndex);
		mData = mData + data;

		byte  originUData[] = Hex2Str.hexStringToByte(data);
		byte  newUData[] = Hex2Str.hexStringToByte(mData);
		int crc8   =   CRC8(newUData, newUData.length);
		arrayList.add(0x80 | crc8);
		arrayList.add(0x80 | index);
		for (int i = 0;i < originUData.length;i++) {
			int value = 0x100 | originUData[i];
			arrayList.add(value);
		}

	}

	/**
	 * prefix code
	 * @param pwd
	 */
	private void getPrefixCodeWithPSW(String pwd) {
		int length = pwd.length();

		byte legthByte[]  = {(byte)length};
		int crc8 = CRC8(legthByte, 1);
		// ????????????
		int prefixCode[] = new int[]{0x00,0x00,0x00,0x00};
		prefixCode[0]       = 0x40 | (length >> 4 & 0xF);
		prefixCode[1]       = 0x50 | (length & 0xF);
		prefixCode[2]       = 0x60 | (crc8 >> 4 & 0xF);
		prefixCode[3]       = 0x70 | (crc8 & 0xF);

		for (int j = 0; j < 4; ++j) {
			arrayList.add(prefixCode[j]);
		}


	}

	/**
	 * int????4????byte???? 
	 * @param num
	 * @return
	 */
	public static byte[] int2byteArray(int num) {
		byte[] result = new byte[4];
		result[0] = (byte)(num >>> 24);//????8λ???0?±?
		result[1] = (byte)(num >>> 16);//??θ?8????1?±?
		result[2] = (byte)(num >>> 8); //??ε?8λ???2?±?
		result[3] = (byte)(num );      //????8λ???3?±?
		return result;
	}

	/**
	 * magic code
	 * @param ssid
	 * @param pwd
	 */
	private void getMagicCodeWithSSID(String ssid,String pwd) {
		int length = ssid.length() + pwd.length() + 1;
		// ????????????
		int magicCode[] = new int[]{0x00,0x00,0x00,0x00};
		magicCode[0] = 0x00 | (length >> 4 & 0xF);
		if (magicCode[0] == 0) {
			magicCode[0] = 0x08;
		}
		magicCode[1]  = 0x10 | (length & 0xF);




//		byte  ssidData[] = Hex2Str.hexStringToByte(Hex2Str.toHexString(ssid));
		byte ssidData[] = ssid.getBytes();
		int crc8 = CRC8(ssidData, ssidData.length);
		magicCode[2] = 0x20 | (crc8 >> 4 & 0xF);
		magicCode[3] = 0x30 | (crc8 & 0xF);

		for (int i = 0; i < 20; ++i) {
			for (int j = 0; j < 4; ++j) {
				arrayList.add(magicCode[j]);
			}
		}


	}

	/** CRC8λУ?? */
	private int CRC8( byte  data[] ,int len) {
		int cFcs = 0;
		int i, j;
		for( i = 0; i < len; i ++ ) {
			cFcs ^= data[i];
			for(j = 0; j < 8; j ++) {
				boolean b = ((cFcs &0x01)==0)?false:true;

				if(b) {
					cFcs ^= 0x18; /* CRC (X(8) + X(5) + X(4) + 1) */
					cFcs >>= 1;
					cFcs |= 0x80;
					//cFcs = (BYTE)((cFcs >> 1) ^ AL2_FCS_COEF);
				} else {
					cFcs >>= 1;
				}
			}
		}

		return cFcs;
	}

	/**????????*/
	private void getLeadingPart() {
		for (int i = 0; i < 50; ++i) {
			for (int j = 1; j <= 4; ++j) {
				arrayList.add(j);
			}
		}
	}

}
