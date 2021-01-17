package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author guardWarm
 * @date 2021-01-17 19:05
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;

	/**
	 * 在对应用户购物车中添加count个product
	 * 数据库中购物车的实现是存在多条该userID的记录来表示该用户购物车中的多种物品
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param count 商品个数
	 * @return 当前用户的购物车
	 */
	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){
		if(productId == null || count == null || count <= 0){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
		if(cart == null){
			// 这个产品不在这个购物车里,需要新增一个这个产品的记录
			Cart cartItem = new Cart();
			cartItem.setQuantity(count);
			cartItem.setChecked(Const.Cart.CHECKED);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			cartMapper.insert(cartItem);
		}else{
			//这个产品已经在购物车里了.
			//如果产品已存在,数量相加
			count = cart.getQuantity() + count;
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		return list(userId);
	}

	/**
	 * 修改某商品的购买个数
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param count 商品个数
	 * @return 当前用户购物车详情
	 */
	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
		if(productId == null || count == null || count <= 0){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
		if(cart != null){
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKey(cart);
		return list(userId);
	}

	/**
	 * 删除购物车中的某样商品
	 * @param userId 用户ID
	 * @param productIds 多个商品ID拼接成的字符串，以,分割
	 * @return 当前用户购物车详情
	 */
	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds){
		List<String> productList
				= Splitter.on(",")
				.splitToList(productIds);
		if(CollectionUtils.isEmpty(productList)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		cartMapper.deleteByUserIdProductIds(userId,productList);
		return list(userId);
	}


	/**
	 * 获取当前用户购物车信息详情
	 * @param userId 用户ID
	 * @return 购物车详细信息
	 */
	@Override
	public ServerResponse<CartVo> list (Integer userId){
		CartVo cartVo = getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}


	/**
	 * 将对应商品设置为checked状态
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param checked
	 *         int CHECKED = 1;---即购物车选中状态
	 *         int UN_CHECKED = 0;---购物车中未选中状态
	 * @return 当前用户购物车详情
	 */
	@Override
	public ServerResponse<CartVo> selectOrUnSelect (Integer userId, Integer productId, Integer checked){
		cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
		return list(userId);
	}

	/**
	 * 获取当前用户购物车中商品个数
	 * @param userId 用户ID
	 * @return 商品总数
	 */
	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId){
		if(userId == null){
			return ServerResponse.createBySuccess(0);
		}
		return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
	}

	/**
	 * 结合库存获取当前用户的真实购物车信息
	 * 存在一些购物车中个数大于库存的情况，在此处优化掉
	 * @param userId 用户ID
	 * @return 当前用户购物车详情
	 */
	private CartVo getCartVoLimit(Integer userId){
		CartVo cartVo = new CartVo();

		List<Cart> cartList = cartMapper.selectCartByUserId(userId);
		List<CartProductVo> cartProductVoList = Lists.newArrayList();

		BigDecimal cartTotalPrice = new BigDecimal("0");

		if(CollectionUtils.isNotEmpty(cartList)){
			for(Cart cartItem : cartList){
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(userId);
				cartProductVo.setProductId(cartItem.getProductId());

				Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
				// 该商品存在时根据库存进行优化
				if(product != null){
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
					//判断库存
					int buyLimitCount;
					if(product.getStock() >= cartItem.getQuantity()){
						//库存充足的时候
						buyLimitCount = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					}else{
						buyLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
						//购物车中更新有效库存
						Cart cartForQuantity = new Cart();
						cartForQuantity.setId(cartItem.getId());
						cartForQuantity.setQuantity(buyLimitCount);
						cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVo.setQuantity(buyLimitCount);
					//计算总价
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
					cartProductVo.setProductChecked(cartItem.getChecked());
				}

				if(cartItem.getChecked() == Const.Cart.CHECKED){
					//如果已经勾选,增加到整个的购物车总价中
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(getAllCheckedStatus(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

		return cartVo;
	}

	/**
	 * 当前用户购物车中的商品是否全部处于选中状态
	 * @param userId 用户ID
	 * @return success（全部选中），false（有未选中的商品）
	 */
	private boolean getAllCheckedStatus(Integer userId){
		if(userId == null){
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

	}
}

