package com.gisicisky.smasterFitment.db.DAO;

/**
 * 数据内容
 * @author Denny
 *
 */
public class DBContent {

	/**
	 * 设备列表
	 * @author Administrator
	 *
	 */
	public static class DeviceInfo {

		public static final String TABLE_NAME_DEVICE = "DeviceInfo";
		public static final String TABLE_NAME_USER= "UserInfo";
		public static final String TABLE_NAME_BIND="BindInfo";

		public static class Columns {
			/** 自动增长列ID */
			public static final String id = "ID";
			//-----------------------设备表-----------------
			/** 设备ID*/
			public static final String pid = "pid";
			/** 设备名称*/
			public static final String deviceName = "deviceName";
			/** 设备密码 */
			public static final String devicePwd = "devicePwd";
			/** 设备mac地址 */
			public static final String deviceMacAddress = "deviceMacAddress";
			/** 设备类型 */
			public static final String deviceType = "deviceType";
			/** 识别的设备xDevice */
			public static final String xDevice = "xDevice";
			//-----------------------用户表-----------------
			public static final String userAccount = "userAccount";
			public static final String userPwd = "userPwd";
			public static final String userName = "userName";
		}

		public static String getCreateSQL() {
			String sql = "";
			sql = "CREATE TABLE " + TABLE_NAME_DEVICE + "(" + //
					"'"+Columns.id+"' INTEGER PRIMARY KEY AUTOINCREMENT ," +
					"'"+Columns.pid+"' TEXT NOT NULL ," +
					"'"+Columns.deviceName+"' TEXT NOT NULL ," +
					"'"+Columns.devicePwd+"' TEXT ," +
					"'"+Columns.deviceMacAddress+"' TEXT NOT NULL ," +
					"'"+Columns.deviceType+"' TEXT NOT NULL ," +
					"'"+Columns.xDevice+"' TEXT NOT NULL, " +
					"'"+Columns.userAccount+"' TEXT NOT NULL "+
					")";
			return sql;
		}

		public static String getCreateSQLUser() {
			return "CREATE TABLE " + TABLE_NAME_USER + "(" + //
					"'"+Columns.id+"' INTEGER PRIMARY KEY AUTOINCREMENT ," +
					"'"+Columns.userAccount+"' TEXT NOT NULL ," +
					"'"+Columns.userPwd+"' TEXT  ," +
					"'"+Columns.userName+"' TEXT NOT NULL " +
					")";
		}

	}
}
