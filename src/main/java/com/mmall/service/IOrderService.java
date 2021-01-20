package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * @author guardWarm
 * @date 2021-01-19 20:39
 */
public interface IOrderService {
	/**
	 * 订单支付
	 * @param orderNo 订单号
	 * @param userId  用户ID
	 * @param path    支付二维码保存路径
	 * @return 订单支付结果
	 */
	ServerResponse pay(Long orderNo, Integer userId, String path);

	/**
	 * 处理回调信息
	 * @param params 回调参数
	 * @return 对回调信息的处理结果
	 */
	ServerResponse aliCallback(Map<String,String> params);

	/**
	 * 拆寻订单支付状态
	 * @param userId  用户ID
	 * @param orderNo 订单ID
	 * @return 订单状态
	 */
	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param shippingId 收货地址ID
	 * @return 创建结果
	 */
	ServerResponse createOrder(Integer userId,Integer shippingId);

	/**
	 * 取消订单
	 * @param userId 用户ID
	 * @param orderNo 订单ID
	 * @return 取消结果
	 */
	ServerResponse<String> cancel(Integer userId,Long orderNo);

	/**
	 * 订单中包含的商品
	 * @param userId 用户ID
	 * @return 订单中的商品详情
	 */
	ServerResponse getOrderCartProduct(Integer userId);

	/**
	 * 获取订单详情
	 * @param userId 用户ID
	 * @param orderNo 订单ID
	 * @return 封装好的订单信息
	 */
	ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

	/**
	 * 获取订单列表，用于分页展示
	 * @param userId 用户ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 封装好的对应页信息
	 */
	ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

	/**
	 * 供管理员使用，查询所有订单，并以分页形式展示
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 封装好的对应页信息
	 */
	ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

	/**
	 * 查看某一订单详情
	 * @param orderNo 订单号
	 * @return 订单详情
	 */
	ServerResponse<OrderVo> manageDetail(Long orderNo);

	/**
	 * 分页展示某一订单中的商品详情
	 * @param orderNo 订单号
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 封装好的对应页信息
	 */
	ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

	/**
	 * 订单发货
	 * @param orderNo 订单ID
	 * @return 若订单已付款则发货
	 */
	ServerResponse<String> manageSendGoods(Long orderNo);
}