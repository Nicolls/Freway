package com.freway.ebike.net;

import java.io.InputStream;
import java.io.Serializable;

import com.google.gson.Gson;

/**
 * 请求网络返回实体基类
 * 
 * @author Nicolls
 * */
public class BaseResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3363337051933234794L;
	/** 正确请求返回的文本数据 */
	private String text;
	/** 返回二进制流格式的数据 */
	private InputStream inputStream;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String toString() {
		String result = "";
		if (this != null) {
			Gson gson = new Gson();
			result = gson.toJson(this);
		}

		return result;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
