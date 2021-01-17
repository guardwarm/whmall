package com.mmall.util;

import java.math.BigDecimal;

/**
 * @author guardWarm
 * @date 2021-01-17 19:20
 * 解决浮点数计算丢失精度问题
 */
public class BigDecimalUtil {

	private BigDecimalUtil(){

	}

	public static BigDecimal add(double v1,double v2){
		BigDecimal b1 = BigDecimal.valueOf(v1);
		BigDecimal b2 = BigDecimal.valueOf(v2);
		return b1.add(b2);
	}

	public static BigDecimal sub(double v1,double v2){
		BigDecimal b1 = BigDecimal.valueOf(v1);
		BigDecimal b2 = BigDecimal.valueOf(v2);
		return b1.subtract(b2);
	}


	public static BigDecimal mul(double v1, double v2){
		BigDecimal b1 = BigDecimal.valueOf(v1);
		BigDecimal b2 = BigDecimal.valueOf(v2);
		return b1.multiply(b2);
	}

	public static BigDecimal div(double v1,double v2){
		BigDecimal b1 = BigDecimal.valueOf(v1);
		BigDecimal b2 = BigDecimal.valueOf(v2);
		//四舍五入,保留2位小数
		return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
	}

}
