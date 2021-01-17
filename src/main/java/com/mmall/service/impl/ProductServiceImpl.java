package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guardWarm
 * @date 2021-01-16 13:11
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {


	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	@Autowired
	private ICategoryService iCategoryService;

	/**
	 * 插入或者更新产品信息
	 * --- 传了productId则更新，未传则插入
	 * @param product 封装好的产品信息
	 * @return 操作结果
	 */
	@Override
	public ServerResponse<String> saveOrUpdateProduct(Product product){
		if (product == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		// 主图为子图的第一张图片
		// 数据有一定冗余，但操作更方便
		if(StringUtils.isNotBlank(product.getSubImages())){
			String[] subImageArray = product.getSubImages().split(",");
			if(subImageArray.length > 0){
				product.setMainImage(subImageArray[0]);
			}
		}

		// 根据是否传了ID来决定是更新还是插入
		if(product.getId() != null){
			int rowCount = productMapper.updateByPrimaryKey(product);
			if(rowCount > 0){
				return ServerResponse.createBySuccess("更新产品成功");
			}
			return ServerResponse.createBySuccess("更新产品失败");
		}else{
			int rowCount = productMapper.insert(product);
			if(rowCount > 0){
				return ServerResponse.createBySuccess("新增产品成功");
			}
			return ServerResponse.createBySuccess("新增产品失败");
		}
	}


	/**
	 * 修改产品销售状态
	 * @param productId 产品ID
	 * @param status 销售状态 1-在售 2-下架 3-删除
	 * @return 修改结果
	 */
	@Override
	public ServerResponse<String> setSaleStatus(Integer productId, Integer status){
		// status 只有1，2，3三种状态
		if(productId == null || status == null || status < 1 || status > 3){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int rowCount = productMapper.updateByPrimaryKeySelective(product);
		if(rowCount > 0){
			return ServerResponse.createBySuccess("修改产品销售状态成功");
		}
		return ServerResponse.createByErrorMessage("修改产品销售状态失败");
	}


	/**
	 * 获取产品详情（后台--能获取到所有数据库中存在的商品详情）
	 * @param productId 产品ID
	 * @return 产品详情（ProductDetailVo对象）
	 */
	@Override
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
		if(productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("产品已下架或者删除");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}

	/**
	 * 将product对象封装为ProductDetailVo对象
	 * @param product 封装好的产品信息
	 * @return  更详尽的ProductDetailVo对象
	 */
	private ProductDetailVo assembleProductDetailVo(Product product){
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());

		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if(category == null){
			//默认为根节点
			productDetailVo.setParentCategoryId(0);
		}else{
			productDetailVo.setParentCategoryId(category.getParentId());
		}

		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVo;
	}


	/**
	 * 获取页面信息
	 * @param pageNum 页号（从1开始）
	 * @param pageSize 每页个数
	 * @return 封装好的对应页信息（pageInfo）
	 */
	@Override
	public ServerResponse<PageInfo<ProductListVo>> getProductList(int pageNum, int pageSize){
		//startPage--start
		PageHelper.startPage(pageNum,pageSize);

		//填充自己的sql查询逻辑
		List<Product> productList = productMapper.selectList();
		List<ProductListVo> productListVoList = Lists.newArrayList();
		productList.forEach(product -> productListVoList.add(assembleProductListVo(product)));

		//pageHelper-收尾
		PageInfo<ProductListVo> pageResult = new PageInfo<ProductListVo>(productListVoList);
		return ServerResponse.createBySuccess(pageResult);
	}

	/**
	 * 将product对象封装为ProductListVo对象
	 * @param product 封装好的产品信息
	 * @return  更合适的ProductListVo对象
	 */
	private ProductListVo assembleProductListVo(Product product){
		ProductListVo productListVo = new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setName(product.getName());
		productListVo.setCategoryId(product.getCategoryId());
		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		productListVo.setMainImage(product.getMainImage());
		productListVo.setPrice(product.getPrice());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setStatus(product.getStatus());
		return productListVo;
	}


	/**
	 * 依据产品名称（productName）或产品ID（productId）来对产品分页
	 * 	 * 二者只可选其一，都传入category优先
	 * @param productName 产品名称 模糊查询
	 * @param productId 产品ID
	 * @param pageNum 页号
	 * @param pageSize 页面大小
	 * @return 对应的页面信息
	 */
	@Override
	public ServerResponse<PageInfo<ProductListVo>> searchProduct(String productName, Integer productId, int pageNum, int pageSize){
		PageHelper.startPage(pageNum,pageSize);
		if(StringUtils.isNotBlank(productName)){
			productName = "%" + productName + "%";
		}
		List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for(Product productItem : productList){
			ProductListVo productListVo = assembleProductListVo(productItem);
			productListVoList.add(productListVo);
		}
		PageInfo pageResult = new PageInfo(productList);
		pageResult.setList(productListVoList);
		return ServerResponse.createBySuccess(pageResult);
	}


	/**
	 * 获取产品详情(前台--只能获取到在线的商品)
	 * @param productId 产品ID
	 * @return 产品详情（ProductDetailVo对象）
	 */
	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
		if(productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("产品已下架或者删除");
		}
		if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
			return ServerResponse.createByErrorMessage("产品已下架或者删除");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}


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
	@Override
	public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy){
		if(StringUtils.isBlank(keyword) && categoryId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Integer> categoryIdList = new ArrayList<Integer>();

		if(categoryId != null){
			Category category = categoryMapper.selectByPrimaryKey(categoryId);
			if(category == null && StringUtils.isBlank(keyword)){
				//没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
				PageHelper.startPage(pageNum,pageSize);
				List<ProductListVo> productListVoList = Lists.newArrayList();
				PageInfo pageInfo = new PageInfo(productListVoList);
				return ServerResponse.createBySuccess(pageInfo);
			}
			// 递归获取该分类下的所有子分类
			categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
		}
		if(StringUtils.isNotBlank(keyword)){
			keyword = "%" + keyword + "%";
		}

		PageHelper.startPage(pageNum,pageSize);
		//排序处理
		if(StringUtils.isNotBlank(orderBy)){
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
				String[] orderByArray = orderBy.split("_");
				PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
			}
		}
		List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

		List<ProductListVo> productListVoList = Lists.newArrayList();
		for(Product product : productList){
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}

		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);
		return ServerResponse.createBySuccess(pageInfo);
	}
}
