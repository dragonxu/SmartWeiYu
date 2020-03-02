package com.gisicisky.smasterFitment.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

/**
 * 自定义http头
 *
 * @author 刘欣怡
 * @2015年1月7日上午9:49:50 </br>
 */
public class XTHeader implements Header {
	private String key;
	private String value;
	private HeaderElement[] elements;

	public XTHeader(String key, String value, HeaderElement[] elements) {
		this.elements = elements;
		this.key = key;
		this.value = value;
	}

	@Override
	public HeaderElement[] getElements() throws ParseException {
		return elements;
	}

	@Override
	public String getName() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

}
