package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @author guardWarm
 * @date 2021-01-17 19:05
 */
public interface ICartService {
	/**
	 * 在对应用户购物车中添加count个product
	 * 数据库中购物车的实现是存在多条该userID的记录来表示该用户购物车中的多种物品
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param count 商品个数
	 * @return 添加结果
	 */
	ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

	/**
	 * 修改某商品的购买个数
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param count 商品个数
	 * @return 当前用户购物车详情
	 */
	ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

	/**
	 * 删除购物车中的某样商品
	 * @param userId 用户ID
	 * @param productIds 多个商品ID拼接成的字符串，以,分割
	 * @return 当前用户购物车详情
	 */
	ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

	/**
	 * 获取当前用户购物车信息详情
	 * @param userId 用户ID
	 * @return 购物车详细信息
	 */
	ServerResponse<CartVo> list (Integer userId);

	/**
	 * 将对应商品设置为checked状态
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param checked
	 *         int CHECKED = 1;---即购物车选中状态
	 *         int UN_CHECKED = 0;---购物车中未选中状态
	 * @return 当前用户购物车详情
	 */
	ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);

	/**
	 * 获取当前用户购物车中商品个数(包括未选中的商品)
	 * @param userId 用户ID
	 * @return 商品总数
	 */
	ServerResponse<Integer> getCartProductCount(Integer userId);
	
}
