package com.gisicisky.smasterFitment.data;

import java.io.Serializable;

public class GroupInfoCache implements Serializable{

	private int id = -1;
	private String name = "";
	private String strUser = "-1";
	private boolean isChecked = false;
	
	public GroupInfoCache (int id,String name,String strUser) {
		this.id = id;
		this.name = name;
		this.strUser = strUser;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getStrUser() {
		return strUser;
	}

	public void setStrUser(String strUser) {
		this.strUser = strUser;
	}

	
	
}
