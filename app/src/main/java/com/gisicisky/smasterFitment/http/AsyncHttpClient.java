package com.gisicisky.smasterFitment.http;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.ResponseHandlerInterface;

public class AsyncHttpClient {
	private DefaultHttpClient httpClient;
	private HttpContext httpContext;// 网络连接上下文
	private static final int DEFAULT_SOCKET_TIMEOUT = 8 * 1000;// 连接超时时间为8秒
	private static final int DEFAULT_BUFFERSIZE = 8192;// 默认缓存8k的数据
	private static final int DEFAULT_MAX_CONNECTIONS = 10;// 最大连接数
	private static final int MAX_RETRY_COUNTS = 5;
	private ThreadPoolExecutor threadPool;// 线程池

	public AsyncHttpClient() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params,
				DEFAULT_MAX_CONNECTIONS);
		ConnManagerParams.setTimeout(params, DEFAULT_SOCKET_TIMEOUT);// 设置连接超时时间
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// 设置代理
		HttpProtocolParams.setUserAgent(params, "android-async-http/1.3.1 "
				+ "(http://loopj.com/android-async-http)");
		HttpConnectionParams.setConnectionTimeout(params,
				DEFAULT_SOCKET_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, DEFAULT_BUFFERSIZE);
		HttpConnectionParams.setTcpNoDelay(params, true);

		SchemeRegistry reg = new SchemeRegistry();
		reg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),
				80));
		reg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),
				443));
		ThreadSafeClientConnManager man = new ThreadSafeClientConnManager(
				params, reg);
		httpClient = new DefaultHttpClient(man, params);
		httpClient
				.setHttpRequestRetryHandler(new RetryHandler(MAX_RETRY_COUNTS));
		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		// 发起重连时使用，找上次连接的连接信息
		httpContext = new BasicHttpContext(new BasicHttpContext());
	}

	/**
	 *
	 * @param url
	 * @param params
	 * @param listener
	 */
	public void get(RequestParams params) {
		// System.out.println(url+params);
		HttpGet request = new HttpGet(params.getUrl()
				+ params.getGetRequsetUrl());
		sendRequest(request, params);
	}

	// public void post(String url, HttpEntity entity, XlinkPacketListener
	// listener) {
	// post(url, entity, listener, null);
	// }

	/**
	 *
	 * @param url
	 *            请求网址
	 * @param entity
	 *            请求实体
	 * @param listener
	 *            数据监听
	 * @param header
	 *            http 头
	 */
	public void post(RequestParams params) {
		HttpPost request = new HttpPost(params.getUrl());
		if (params.getHeaders() != null && params.getHeaders().size() > 0) {
			for (Header header : params.getHeaders()) {
				request.addHeader(header);
			}
		}
		request.setEntity(params.getEntity());
		sendRequest(request, params);
	}

	/**
	 * 发送请求
	 *
	 * @param request
	 * @param listener
	 */
	private void sendRequest(HttpUriRequest request, HttpListener listener) {
		// 放到线程池中进行联网
		threadPool.execute(new AsyncHttpRequest(httpClient, httpContext,
				request, (ResponseHandlerInterface) listener));
	}
}
