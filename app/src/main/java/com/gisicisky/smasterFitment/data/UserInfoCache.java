package com.gisicisky.smasterFitment.data;

public class UserInfoCache {

	private int id = -1;
	private String userAccount = "";
	private String userPwd = "";
	private String userName = "";
	
	public UserInfoCache(String account,String pwd,String name) {
		this.userAccount = account;
		this.userPwd = pwd;
		this.userName = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
