package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * @author guardWarm
 * @date 2021-01-16 13:09
 */
public interface IProductService {

	/**
	 * 插入或者更新产品信息
	 * --- 传了productId则更新，未传则插入
	 * @param product 封装好的产品信息
	 * @return 操作结果
	 */
	ServerResponse<String> saveOrUpdateProduct(Product product);

	/**
	 * 修改产品销售状态
	 * @param productId 产品ID
	 * @param status 销售状态 1-在售 2-下架 3-删除
	 * @return 修改结果
	 */
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);

	/**
	 * 获取产品详情（后台--能获取到所有数据库中存在的商品详情）
	 * @param productId 产品ID
	 * @return 产品详情（ProductDetailVo对象）
	 */
	ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

	/**
	 * 获取页面信息
	 * @param pageNum 页号（从1开始）
	 * @param pageSize 每页个数
	 * @return 封装好的对应页信息（pageInfo）
	 */
	ServerResponse getProductList(int pageNum, int pageSize);


	/**
	 * 依据产品名称（productName）或产品ID（productId）来对产品分页
	 * 	 * 二者只可选其一，都传入category优先
	 * @param productName 产品名称 模糊查询
	 * @param productId 产品ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 对应的页面信息
	 */
	ServerResponse searchProduct(String productName,Integer productId,int pageNum,int pageSize);

	/**
	 * 获取产品详情(前台--只能获取到在线的商品)
	 * @param productId 产品ID
	 * @return 产品详情（ProductDetailVo对象）
	 */
	ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

	/**
	 * 依据关键词（keyword）或商品分类（category）来对产品分页
	 * 二者只可选其一，都传入category优先
	 * @param keyword 关键词，模糊查询
	 * @param categoryId 分类ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @param orderBy 排序规则，目前支持"price_desc","price_asc"
	 * @return 对应的页面信息
	 */
	ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
