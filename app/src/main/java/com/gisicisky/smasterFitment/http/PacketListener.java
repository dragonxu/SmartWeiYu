package com.gisicisky.smasterFitment.http;

public interface PacketListener {
	void onSucceed(String msg);

	void onFail(int code);
}
