package com.gisicisky.smasterFitment.http;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.example.smartouwei.base.MyApp;
import com.gisicisky.smasterFitment.utl.XlinkUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;

public class HttpAgent {
	private final String url = "http://app.xlink.cn";
	private static HttpAgent instance;
	// url
	public final String registerUrl = url + "/v1/user/register";
	public final String loginUrl = url + "/v1/user/login";
	// public final String putUrl = url + "/v1/bucket/put";
	// public final String getUrl = url + "/v1/bucket/get";
	// public final String deleteUrl = url + "/v1/bucket/delete";
	// public final String updataUrl = url + "/v1/bucket/update";
	// 3个签名头部
	private final static String AccessID = "X-AccessId";
	private final static String X_ContentMD5 = "X-ContentMD5";
	private final static String X_Sign = "X-Sign";
	/**
	 * xlink 企业后台管理 注册的 资源id和key
	 */
	public static String SECRET_KEY = "3190a07242b348629650fe29b3694151";
	public static String ACCESS_ID = "b66037dcdc314ce3bc0d3da4061df5dd";

	/**
	 * md5算法
	 *
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HttpAgent getInstance() {
		if (instance == null) {
			instance = new HttpAgent();
		}
		return instance;
	}

	/**
	 * 全局的http代理
	 */
	private static AsyncHttpClient client;

	private HttpAgent() {
		client = new AsyncHttpClient();
	}

	/**
	 * 通过SECRET_KEY 和内容md5 进行加密签名
	 *
	 * @param contentmd5
	 * @return
	 */
	private XHeader getSign(String contentmd5) {
		String singnMd5 = MD5(SECRET_KEY + contentmd5);
		XHeader header = new XHeader(X_Sign, singnMd5, null);
		return header;
	}

	/**
	 * http 注册接口
	 *
	 * @param uid
	 *            用户ID
	 * @param name
	 *            昵称（别名，仅供后台管理平台观看，对用户来说记住uid和pwd就行）
	 * @param pwd
	 *            密码
	 * @param listener
	 *            注册监听器
	 */
	public void onRegister(String uid, String name, String pwd,
						   TextHttpResponseHandler handler) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("name", name);
		map.put("pwd", pwd);
		JSONObject data = XlinkUtils.getJsonObject(map);
		// 请求entity
		StringEntity entity = null;
		entity = new StringEntity(data.toString(), "UTF-8");
		// 3个http 请求头部
		Header[] headers = new Header[3];
		// AccessID
		headers[0] = new XHeader(AccessID, ACCESS_ID, null);
		String contentMD5 = MD5(data.toString());
		// 内容md5验证
		headers[1] = new XHeader(X_ContentMD5, contentMD5, null);
		headers[2] = getSign(contentMD5); // 内容加SECRET_KEY 签名认证
		post(registerUrl, headers, entity, handler);

	}

	public void getAppId(String user, String pwd, TextHttpResponseHandler handler) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", user);
		map.put("pwd", pwd);
		JSONObject data = XlinkUtils.getJsonObject(map);
		// 请求entity
		StringEntity entity = null;
		entity = new StringEntity(data.toString(), "UTF-8");
		// 3个http 请求头部
		Header[] headers = new Header[3];
		// AccessID
		headers[0] = new XHeader(AccessID, ACCESS_ID, null);
		// entity md5签名后
		String contentMD5 = MD5(data.toString());
		// 内容md5验证
		headers[1] = new XHeader(X_ContentMD5, contentMD5, null);
		// 内容加SECRET_KEY 签名认证
		headers[2] = getSign(contentMD5);
		post(loginUrl, headers, entity, handler);
	}

	/**
	 *
	 * @param url
	 *            url地址
	 * @param headers
	 *            http请求头部
	 * @param entity
	 *            http 实体
	 * @param handler
	 *            回调
	 */
	private void post(String url, Header[] headers, HttpEntity entity,
					  AsyncHttpResponseHandler handler) {
		client.post(MyApp.getApp(), url, headers, entity, "text/html", handler);
	}
}
