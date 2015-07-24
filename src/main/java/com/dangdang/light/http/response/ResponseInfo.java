package com.dangdang.light.http.response;

/**
 * Response info struct
 * 
 * @author yuxuan.wang
 * 
 */
public final class ResponseInfo {

	private int code;
	private String msg;
	private Object result;

	public ResponseInfo(int code, String msg, Object result) {
		super();
		this.code = code;
		this.msg = msg;
		this.result = result;
	}

	public int getCode() {
		return this.code;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return this.result;
	}
	

}
