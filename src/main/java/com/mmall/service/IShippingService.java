package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * @author guardWarm
 * @date 2021-01-17 12:22
 */
public interface IShippingService {

	/**
	 * 添加收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shipping 封装好的shipping信息
	 * @return 添加结果
	 */
	ServerResponse add(Integer userId, Shipping shipping);

	/**
	 * 删除收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shippingId 待删除的shippingID
	 * @return 删除结果
	 */
	ServerResponse<String> del(Integer userId,Integer shippingId);

	/**
	 * 更新收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shipping 封装好的shipping信息
	 * @return 更新结果
	 */
	ServerResponse update(Integer userId, Shipping shipping);

	/**
	 * 查找收货地址详情
	 * @param userId 用户ID
	 * @param shippingId 收货地址ID
	 * @return 对应的收货地址详情
	 */
	ServerResponse<Shipping> select(Integer userId, Integer shippingId);

	/**
	 * 分页显示对应用户的所有订单详情
	 * @param userId 用户ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 封装好的对应页面所需的数据
	 */
	ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}

