package com.gisicisky.smasterFitment.db.DAO;

import java.util.ArrayList;
import java.util.List;

import com.gisicisky.smasterFitment.data.UserInfoCache;
import com.gisicisky.smasterFitment.db.DAO.DBContent.DeviceInfo;
import com.gisicisky.smasterFitment.db.DAO.SQLiteTemplate.RowMapper;
import com.gisicisky.smasterFitment.utl.BaseVolume;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UserInfoDao {

	static SQLiteDatabase mDB;
	DBBaseDao mBaseDao;

	public UserInfoDao(Context context) {
		String filePath = context.getFilesDir().getAbsolutePath() +"/" + BaseVolume.DB_NAME;
		mDB = SQLiteDatabase.openOrCreateDatabase(filePath,null);
		if (mDB != null){
			this.mBaseDao = new DBBaseDao(mDB);
		}
		if (!mBaseDao.tabIsExist(DeviceInfo.TABLE_NAME_USER)) {
			mDB.execSQL(DeviceInfo.getCreateSQLUser());
		}
	}


	public boolean getUserInfo(String account) {
		return featchUserByAccount(account) != null ? true : false;
	}

	/**
	 * 查找某个用户
	 */
	public UserInfoCache featchUserByAccount(String account){
		String sql = "select * from UserInfo where userAccount = '"+ account+"'" ;
		List<UserInfoCache> temp  =  mBaseDao.queryForListBySql(sql, mRowMapper_MessageData, null);
		if(temp.size()>0)
			return temp.get(0);
		return null;

	}

	/**
	 * 修改用户参数
	 */
	public int updateDataByDeviceMac(ContentValues contents,String  account) {
		int i = mDB.update(DeviceInfo.TABLE_NAME_USER, contents, "userAccount = '"+account +"'", null);

		return i;
	}

	/**
	 * 查询所有用户
	 * @param areaName
	 * @return
	 */
	public ArrayList<UserInfoCache> queryAllDevice(){
		String sql = "select * from UserInfo";
		return mBaseDao.queryForListBySql(sql, mRowMapper_MessageData, null);
	}

	private static final RowMapper<UserInfoCache> mRowMapper_MessageData = new RowMapper<UserInfoCache>() {
		public UserInfoCache mapRow(Cursor cursor, int rowNum) {
			int id = cursor.getInt(cursor.getColumnIndex(DeviceInfo.Columns.id));
			String Account = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.userAccount));
			String Name = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.userName));
			String Pwd = cursor.getString(cursor.getColumnIndex(DeviceInfo.Columns.userPwd));
			UserInfoCache temple = new UserInfoCache(Account,Pwd,Name);
			temple.setId(id);
			return temple;
		}
	};

	/*****
	 * 添加用户
	 * @param data
	 * @return
	 */
	public int insertSingleData(UserInfoCache user) {
		int result = 0;
		// 用户已经存在，则修改信息
		if (getUserInfo(user.getUserAccount())) {
			updateData(user);
		}
		else {
			try {
				mDB.insert(DeviceInfo.TABLE_NAME_USER,null,makeValues(user));
			} catch (Exception e) {
				e.printStackTrace();
				result = -1;
			}
		}
		return result;
	}

	private ContentValues makeValues(UserInfoCache temp) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DeviceInfo.Columns.userAccount, temp.getUserAccount());
		initialValues.put(DeviceInfo.Columns.userPwd, temp.getUserPwd());
		initialValues.put(DeviceInfo.Columns.userName, temp.getUserName());
		return initialValues;
	}

	/**
	 * 修改用户参数
	 */
	public int updateData(UserInfoCache user) {
		int i = mDB.update(DeviceInfo.TABLE_NAME_USER, makeValues(user), "userAccount = '"+user.getUserAccount() +"'", null);

		return i;
	}

	public void closeDb() {
		mDB.close();
	}

	/**
	 * 删除某个用户信息
	 */
	public int deleteData(UserInfoCache user) {
		String whereClause = "userAccount = ? ";
		int i;
		try {
			i = mDB.delete(DeviceInfo.TABLE_NAME_USER,  whereClause, new String[]{user.getUserAccount()});
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

}
