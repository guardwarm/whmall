package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author guardWarm
 * @date 2021-01-16 10:21
 */
public interface ICategoryService {
	/**
	 * 新增分类
	 * @param categoryName 分类名
	 * @param parentId  上级分类ID
	 * @return 添加结果
	 */
	ServerResponse<String> addCategory(String categoryName, Integer parentId);

	/**
	 * 更新分类名称
	 * @param categoryId 分类ID
	 * @param categoryName 分类名
	 * @return 更新结果
	 */
	ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);

	/**
	 * 获取当前分类下所有平级（一级）子分类信息（不包括该分类本身）
	 * @param categoryId 分类ID
	 * @return 子分类信息集合
	 */
	ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

	/**
	 * 递归查找该分类下所有子分类信息（包括该分类本身）
	 * @param categoryId 分类ID
	 * @return 子分类ID集合
	 */
	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}

