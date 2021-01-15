package com.mmall.common;

/**
 * @author guardWarm
 * @date 2021-01-14 13:30
 */
public enum ResponseCode {
	/**
	 * 成功
	 */
	SUCCESS(0,"SUCCESS"),
	/**
	 * 失败
	 */
	ERROR(1,"ERROR"),
	/**
	 * 需要先登录才可操作
	 */
	NEED_LOGIN(10,"NEED_LOGIN"),
	/**
	 * 参数错误
	 */
	ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

	private final int code;
	private final String desc;


	ResponseCode(int code,String desc){
		this.code = code;
		this.desc = desc;
	}

	public int getCode(){
		return code;
	}
	public String getDesc(){
		return desc;
	}
}
