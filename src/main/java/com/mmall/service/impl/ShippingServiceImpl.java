package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author guardWarm
 * @date 2021-01-17 12:23
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {


	@Autowired
	private ShippingMapper shippingMapper;

	/**
	 * 添加收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shipping 封装好的shipping信息
	 * @return 添加结果
	 */
	@Override
	public ServerResponse add(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int rowCount = shippingMapper.insert(shipping);
		if(rowCount > 0){
			Map result = Maps.newHashMap();
			result.put("shippingId",shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功",result);
		}
		return ServerResponse.createByErrorMessage("新建地址失败");
	}

	/**
	 * 删除收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shippingId 待删除的shippingID
	 * @return 删除结果
	 */
	@Override
	public ServerResponse<String> del(Integer userId, Integer shippingId){
		int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
		if(resultCount > 0){
			return ServerResponse.createBySuccess("删除地址成功");
		}
		return ServerResponse.createByErrorMessage("删除地址失败");
	}

	/**
	 * 更新收货地址
	 * @param userId 根据session拿到的，防止横向越权
	 * @param shipping 封装好的shipping信息
	 * @return 更新结果
	 */
	@Override
	public ServerResponse update(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int rowCount = shippingMapper.updateByShipping(shipping);
		if(rowCount > 0){
			return ServerResponse.createBySuccess("更新地址成功");
		}
		return ServerResponse.createByErrorMessage("更新地址失败");
	}

	/**
	 * 查找收货地址详情
	 * @param userId 用户ID
	 * @param shippingId 收货地址ID
	 * @return 对应的收货地址详情
	 */
	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
		Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
		if(shipping == null){
			return ServerResponse.createByErrorMessage("无法查询到该地址");
		}
		return ServerResponse.createBySuccess("查询地址成功",shipping);
	}

	/**
	 * 分页显示对应用户的所有订单详情
	 * @param userId 用户ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 封装好的对应页面所需的数据
	 */
	@Override
	public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
		PageHelper.startPage(pageNum,pageSize);
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(shippingList);
		return ServerResponse.createBySuccess(pageInfo);
	}
}
