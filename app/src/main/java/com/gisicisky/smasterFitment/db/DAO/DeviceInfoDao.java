package com.gisicisky.smasterFitment.db.DAO;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.db.DAO.DBContent.DeviceInfo;
import com.gisicisky.smasterFitment.db.DAO.SQLiteTemplate.RowMapper;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DeviceInfoDao {

	static SQLiteDatabase mDB;
	DBBaseDao mBaseDao;
	private static Context con;
	public DeviceInfoDao(Context context) {
		con = context;
		String filePath = context.getFilesDir().getAbsolutePath() +"/" + BaseVolume.DB_NAME;
		mDB = SQLiteDatabase.openOrCreateDatabase(filePath,null);
		if (mDB != null){
			this.mBaseDao = new DBBaseDao(mDB);
		}
		if (!mBaseDao.tabIsExist(DeviceInfo.TABLE_NAME_DEVICE)) {
			mDB.execSQL(DeviceInfo.getCreateSQL());
		}
	}

	/**
	 * 查找某个设备
	 */
	public Boolean isDeviceExist(String deviceMac,String userID){
		DeviceInfoCache temp = featchDeviceByID(deviceMac,userID);
		return temp==null?false:true;
	}

	public DeviceInfoCache featchDeviceByID(String deviceMac,String userID){
		String sql = "select * from DeviceInfo where deviceMacAddress = '"+ deviceMac+"' and userAccount = '"+userID+"'" ;
		List<DeviceInfoCache> temp  =  mBaseDao.queryForListBySql(sql, mRowMapper_MessageData, null);
		if(temp.size()>0)
			return temp.get(0);
		return null;

	}

	/**
	 * 修改设备参数
	 */
	public int updateDataByDeviceMac(ContentValues contents,String  deviceMac,String userID) {
		int i = mDB.update(DeviceInfo.TABLE_NAME_DEVICE, contents, "deviceMacAddress = '"+deviceMac +"' and userAccount = '"+userID+"'" , null);

		return i;
	}

	/**
	 * 查询所有设备
	 * @return
	 */
	public ArrayList<DeviceInfoCache> queryAllDeviceByUID(String userID){
		String sql = "select * from DeviceInfo where userAccount = '"+userID+"'";
		return mBaseDao.queryForListBySql(sql, mRowMapper_MessageData, null);
	}


	private static final RowMapper<DeviceInfoCache> mRowMapper_MessageData = new RowMapper<DeviceInfoCache>() {
		public DeviceInfoCache mapRow(Cursor cursor, int rowNum) {
			int id = cursor.getInt(cursor.getColumnIndex(DeviceInfo.Columns.id));
			String ID = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.pid));
			String Name = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.deviceName));
			String Pwd = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.devicePwd));
			String mac = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.deviceMacAddress));
			String type = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.deviceType));
			String sXDevice = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.xDevice));
			String uID = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.userAccount));
			XDevice xDevice = null;
			try {
				// 将String 转换成 JSON;
				JSONObject json = new JSONObject(sXDevice);
				// 将JSON 转换成 XDevice
				xDevice = XlinkAgent.JsonToDevice(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			DeviceInfoCache temple = new DeviceInfoCache(ID,Name,Pwd,mac,xDevice,type,uID);
			return temple;
		}
	};

	/*****
	 * 添加设备
	 * @param data
	 * @return
	 */
	public int insertSingleData(DeviceInfoCache data) {
		if (data == null) {
			return -1;
		}
		int result = 0;
		try {
			mDB.insert(DeviceInfo.TABLE_NAME_DEVICE,null,makeValues(data));
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	private ContentValues makeValues(DeviceInfoCache temp) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DeviceInfo.Columns.pid, temp.getId());
		initialValues.put(DeviceInfo.Columns.deviceName, temp.getName());
		initialValues.put(DeviceInfo.Columns.devicePwd, temp.getPwd());
		initialValues.put(DeviceInfo.Columns.deviceMacAddress, temp.getMac());
		initialValues.put(DeviceInfo.Columns.deviceType, temp.getDeviceType());
		initialValues.put(DeviceInfo.Columns.xDevice, temp.getxDeviceString());
		initialValues.put(DeviceInfo.Columns.userAccount, temp.getUserID());
		return initialValues;
	}

	/**
	 * 修改设备参数
	 */
	public int updateData(DeviceInfoCache data,String userID) {
		if (data == null) {
			return -1;
		}
		int i = mDB.update(DeviceInfo.TABLE_NAME_DEVICE, makeValues(data), "deviceMacAddress = '"+data.getMac() +"' and userAccount = '"+userID+"'", null);

		return i;
	}

	public void closeDb() {
		mDB.close();
	}

	/**
	 * 删除某个设备
	 */
	public int deleteData(String deviceMac,String userID) {
		String whereClause = "deviceMacAddress = ? and userAccount = ?";
		int i;
		try {
			i = mDB.delete(DeviceInfo.TABLE_NAME_DEVICE,  whereClause, new String[]{deviceMac,userID});
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

}
