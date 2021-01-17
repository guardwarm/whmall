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


	ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);
	ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

	ServerResponse<CartVo> list (Integer userId);
	ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);
	ServerResponse<Integer> getCartProductCount(Integer userId);
}
