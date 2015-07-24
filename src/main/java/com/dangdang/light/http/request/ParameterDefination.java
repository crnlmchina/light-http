package com.dangdang.light.http.request;

/**
 * 请求参数定义
 * 
 * @author wangyuxuan
 * 
 */
public class ParameterDefination {

	private String name;
	private boolean numeric;
	private int maxLen;

	/**
	 * @param name
	 *            参数名
	 */
	public ParameterDefination(String name) {
		this(name, false);
	}

	/**
	 * @param name
	 *            参数名
	 * @param numeric
	 *            是否为数值
	 */
	public ParameterDefination(String name, boolean numeric) {
		this(name, numeric, -1);
	}

	/**
	 * @param name
	 *            参数名
	 * @param numeric
	 *            是否为数值
	 * @param maxLen
	 *            参数值最大长度限制,-1代表无限制
	 */
	public ParameterDefination(String name, boolean numeric, int maxLen) {
		super();
		this.name = name;
		this.numeric = numeric;
		this.maxLen = maxLen;
	}

	public String getName() {
		return this.name;
	}

	public boolean isNumeric() {
		return this.numeric;
	}

	public int getMaxLen() {
		return this.maxLen;
	}

	@Override
	public String toString() {
		return "ParameterDefination [name=" + this.name + ", numeric=" + this.numeric + ", maxLen=" + this.maxLen + "]";
	}

}
