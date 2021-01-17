package com.mmall.util;

import com.mmall.TestBase;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author guardWarm
 * @date 2021-01-17 19:17
 * 根据如下三个测试得出，必须利用BigDecimal的string构造器方可正确经行浮点数运算
 */
public class BigDecimalTest extends TestBase {

	@Test
	public void test1(){
		System.out.println(0.05+0.01);
		System.out.println(1.0-0.42);
		System.out.println(4.015*100);
		System.out.println(123.3/100);
	}

	@Test
	public void test2(){
		BigDecimal b1 = new BigDecimal(0.05);
		BigDecimal b2 = new BigDecimal(0.01);
		System.out.println(b1.add(b2));
	}

	@Test
	public void test3(){
		BigDecimal b1 = new BigDecimal("0.05");
		BigDecimal b2 = new BigDecimal("0.01");
		System.out.println(b1.add(b2));

	}

}