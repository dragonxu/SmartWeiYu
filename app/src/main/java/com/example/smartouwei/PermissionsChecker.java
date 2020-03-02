package com.example.smartouwei;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 检查权限的工具类
 * <p/>
 * Created by wangchenlong on 16/1/26.
 */
public class PermissionsChecker {

	private final Context mContext;
	PackageManager pm = null;
    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
        pm = mContext.getPackageManager();  
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
    	 boolean isPer = (PackageManager.PERMISSION_DENIED ==  
 	            pm.checkPermission(permission, mContext.getPackageName())); 
    	 return isPer;
    }
	
	
}
