package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author guardWarm
 * @date 2021-01-16 10:29
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

	private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * 新增分类
	 * @param categoryName 分类名
	 * @param parentId  上级分类ID
	 * @return 添加结果
	 */
	@Override
	public ServerResponse<String> addCategory(String categoryName, Integer parentId){
		if(parentId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByErrorMessage("添加品类参数错误");
		}

		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		//这个分类是可用的--默认值
		category.setStatus(true);

		int rowCount = categoryMapper.insert(category);
		if(rowCount > 0){
			return ServerResponse.createBySuccess("添加品类成功");
		}
		return ServerResponse.createByErrorMessage("添加品类失败");
	}

	/**
	 * 更新分类信息
	 * @param categoryId 分类ID
	 * @param categoryName 分类名
	 * @return 更新结果
	 */
	@Override
	public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName){
		if(categoryId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByErrorMessage("更新品类参数错误");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);

		int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
		if(rowCount > 0){
			return ServerResponse.createBySuccess("更新品类名字成功");
		}
		return ServerResponse.createByErrorMessage("更新品类名字失败");
	}

	/**
	 * 获取当前分类下所有平级（一级）子分类信息（不包括该分类本身）
	 * @param categoryId 分类ID
	 * @return 子分类信息集合
	 */
	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if(CollectionUtils.isEmpty(categoryList)){
			logger.info("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccess(categoryList);
	}


	/**
	 * 递归查找该分类下所有子分类信息（包括该分类本身）
	 * @param categoryId 分类ID
	 * @return 子分类ID集合
	 */
	@Override
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
		Set<Category> categorySet = Sets.newHashSet();
		findChildCategory(categorySet,categoryId);

		List<Integer> categoryIdList = Lists.newArrayList();
		if(categoryId != null){
			for(Category categoryItem : categorySet){
				categoryIdList.add(categoryItem.getId());
			}
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}

	/**
	 * 递归算法,算出子节点--类似递归遍历文件夹下所有文件
	 * @param categorySet 结果集合--在递归中传递，以保存所有子分类
	 * @param categoryId 子分类ID
	 */
	private void findChildCategory(Set<Category> categorySet ,Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if(category != null){
			categorySet.add(category);
		}
		// 查找子节点,递归算法一定要有一个退出的条件
		// 该分类下没有子分类--即不进入for循环--此为递归基
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		for(Category categoryItem : categoryList){
			findChildCategory(categorySet,categoryItem.getId());
		}
	}
}
