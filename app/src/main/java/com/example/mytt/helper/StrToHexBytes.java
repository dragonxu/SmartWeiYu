package com.example.mytt.helper;

import java.util.ArrayList;
import java.util.List;

public class StrToHexBytes {
	public static void main(String[] args) {
//		byte[] a="01020304".getBytes();
//		System.out.println(bytesToHexString(hexStringToBytes("1234")));;
	/*	String msg2="01";
		String msg3="6C626D261E29";
		String msg4="00"; 
		byte[] a=hexStringToBytes(NettyConstant.hex_DELIMITER,msg2,msg3,msg4);
		System.out.println(bytesToHexString(a));
		byte[] aa="67696369736b79016c626d261e2900".getBytes();
		System.out.println(bytesToHexString(aa));
		System.out.println(checkHex(aa));*/
		System.out.println(hexStringToBytes("999999999999999").length);
	}
	public static String checkHex(byte[] cs){
		long sum=0;
		for (int j = 0; j < cs.length; j++) {
			sum +=(byte)cs[j];
		}
		long a=(~ sum)+1;
		String result=Long.toHexString(a);
		return result.substring(result.length()-2);
	}


	public static  byte[] hexStringToBytes(String header,String... obj){
		List<Byte> list = new ArrayList<Byte>();
		byte[] headerb= hexStringToBytes(header);
		for (int i = 0; i < headerb.length; i++) {
			list.add(headerb[i]);
		}
		for (String str : obj) {
			//list.addAll();
			byte[] b= hexStringToBytes(str);
			for (int i = 0; i < b.length; i++) {
				list.add(b[i]);
			}
		}
		Object[] srcbytes=list.toArray();
		byte[] resultbytes=new byte[srcbytes.length];
		for (int i = 0; i < srcbytes.length; i++) {
			resultbytes[i]=(Byte) srcbytes[i];
		}
		return resultbytes;
	}
	/**
	 * 16杩涘埗??楃涓?杞崲鎴恇yte瀛楄妭鏁扮粍
	 * @param hexString 16杩涘埗??楃涓?
	 * @return byte瀛楄妭鏁扮粍
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	/**
	 * byte瀛楄妭鏁扮粍杞崲鎴??杩涘埗??楃涓?
	 * @param byte[] 瀛楄妭鏁扮粍
	 * @return 16杩涘埗??楃涓?
	 */
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	/**
	 * 瀛楄妭杞??杩涘埗??楃涓?
	 * @param b
	 * @return
	 */
	public static String toHex(byte b) {
		String result = Integer.toHexString(b & 0xFF);
		if (result.length() == 1) {
			result = '0' + result;
		}
		return result;
	}
	/**
	 * Convert char to byte 
	 * @param c char 
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
