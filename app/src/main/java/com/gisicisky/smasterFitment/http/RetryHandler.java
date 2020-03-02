package com.gisicisky.smasterFitment.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
//请求重新连接
public class RetryHandler implements HttpRequestRetryHandler {
	// 最大重连数
	private int maxCount;
	private static final int RETRY_SLEEP_TIME = 1000;
	// 白名单
	private static List<Class<?>> whitelist = new ArrayList<Class<?>>();
	// 黑名单
	private static List<Class<?>> blacklist = new ArrayList<Class<?>>();
	// 静态语句快
	static {
		// Retry if the server dropped connection on us
		whitelist.add(NoHttpResponseException.class);
		// retry-this, since it may happens as part of a Wi-Fi to 3G failover
		whitelist.add(UnknownHostException.class);
		// retry-this, since it may happens as part of a Wi-Fi to 3G failover
		whitelist.add(SocketException.class);
		// 不可以重试的
		// never retry timeouts
		blacklist.add(InterruptedIOException.class);
		// never retry SSL handshake failures
		blacklist.add(SSLHandshakeException.class);

	}

	public RetryHandler(int maxCount) {
		this.maxCount = maxCount;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
								HttpContext context) {
		Boolean b = (Boolean) context
				.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sent = false;
		if (b != null) {
			sent = true;
		}
		boolean retry;
		if (executionCount > maxCount) {
			retry = false;
		} else if (whitelist.contains(exception.getClass())) {
			retry = true;
		} else if (blacklist.contains(exception.getClass())) {
			retry = false;
		} else if (!sent) {
			retry = true;
		} else {
			HttpUriRequest request = (HttpUriRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			String method = request.getMethod();
			if (method.equalsIgnoreCase("past")) {
				retry = true;
			} else if (method.equalsIgnoreCase("get")) {
				retry = false;
			} else {
				retry = true;
			}
		}
		if (retry) {
			try {
				Thread.sleep(RETRY_SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retry;
	}

}
