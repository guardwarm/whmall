package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @author guardWarm
 * @date 2021-01-14 13:25
 */
public interface IUserService {

	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 登录结果
	 */
	ServerResponse<User> login(String username, String password);

	/**
	 * 用户注册
	 * @param user 封装好的注册信息
	 * @return 注册结果
	 */
	ServerResponse<String> register(User user);

	/**
	 * 检验查找的信息是否存在，不存在时返回success
	 * @param str   查找内容
	 * @param type  类型--用户名、邮箱
	 * @return  检验结果--成功（不存在）、失败（存在）
	 */
	ServerResponse<String> checkValid(String str,String type);

	/**
	 * 查找用户找回密码的问题
	 * @param username  用户名
	 * @return 找回密码的问题
	 */
	ServerResponse<String> selectQuestion(String username);

	/**
	 * 检查该用户输入的答案是否正确
	 * @param username 用户名
	 * @param question 问题
	 * @param answer 答案
	 * @return 是否正确？正确时会在TokenCache中插入一个forgetToken用户后续修改密码
	 */
	ServerResponse<String> checkAnswer(String username,String question,String answer);

	/**
	 * 非登录状态下重置密码
	 * @param username 用户名
	 * @param passwordNew 新密码
	 * @param forgetToken 重置密码的Token
	 * @return 修改密码结果
	 */
	ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

	/**
	 * 登录状态下重置密码
	 * @param passwordOld 旧密码
	 * @param passwordNew 新密码
	 * @param user 用户信息--用于根据新密码修改数据库中用户表
	 * @return 修改结果
	 */
	ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

	/**
	 * 更新用户信息
	 * @param user 封装好的待更新信息
	 * @return  更新结果
	 */
	ServerResponse<User> updateInformation(User user);

	/**
	 * 更具userId获取用户信息
	 * @param userId    用户ID
	 * @return 该ID对应的用户信息
	 */
	ServerResponse<User> getInformation(Integer userId);

	/**
	 * 校验是否是管理员
	 * @param user  用户信息
	 * @return  管理员（success），普通用户（error）
	 */
	ServerResponse<String> checkAdminRole(User user);
}
