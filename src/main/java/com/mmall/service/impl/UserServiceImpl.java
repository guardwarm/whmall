package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author guardWarm
 * @date 2021-01-14 14:00
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserMapper userMapper;

	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 登录结果
	 */
	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0 ){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}

		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user  = userMapper.selectLogin(username,md5Password);
		if(user == null){
			return ServerResponse.createByErrorMessage("密码错误");
		}

		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess("登录成功",user);
	}


	/**
	 * 用户注册
	 * @param user 封装好的注册信息
	 * @return 注册结果
	 */
	@Override
	public ServerResponse<String> register(User user){
		ServerResponse<String> validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
		if(!validResponse.isSuccess()){
			// 用户已存在
			return validResponse;
		}

		validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
		if(!validResponse.isSuccess()){
			// 邮箱已存在
			return validResponse;
		}
		// 默认值
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		int resultCount = userMapper.insert(user);
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("注册失败");
		}
		return ServerResponse.createBySuccessMessage("注册成功");
	}

	/**
	 * 检验查找的信息是否存在，不存在时返回success
	 * @param str   查找内容
	 * @param type  类型--用户名、邮箱
	 * @return  检验结果--成功（不存在）、失败（存在）
	 */
	@Override
	public ServerResponse<String> checkValid(String str, String type){
		if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
			//开始校验
			if(Const.USERNAME.equals(type)){
				int resultCount = userMapper.checkUsername(str);
				if(resultCount > 0 ){
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if(Const.EMAIL.equals(type)){
				int resultCount = userMapper.checkEmail(str);
				if(resultCount > 0 ){
					return ServerResponse.createByErrorMessage("email已存在");
				}
			}
		}else{
			return ServerResponse.createByErrorMessage("参数错误");
		}
		return ServerResponse.createBySuccessMessage("校验成功");
	}

	/**
	 * 查找用户找回密码的问题
	 * @param username  用户名
	 * @return 找回密码的问题
	 */
	@Override
	public ServerResponse<String> selectQuestion(String username){

		ServerResponse<String> validResponse = this.checkValid(username,Const.USERNAME);
		if(validResponse.isSuccess()){
			//用户不存在
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String question = userMapper.selectQuestionByUsername(username);
		if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("找回密码的问题是空的");
	}

	/**
	 * 检查该用户输入的答案是否正确
	 * @param username 用户名
	 * @param question 问题
	 * @param answer 答案
	 * @return 是否正确？正确时会在TokenCache中插入一个forgetToken用户后续修改密码
	 */
	@Override
	public ServerResponse<String> checkAnswer(String username, String question, String answer){
		int resultCount = userMapper.checkAnswer(username,question,answer);
		if(resultCount>0){
			//说明问题及问题答案是这个用户的,并且是正确的
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("问题的答案错误");
	}


	/**
	 * 非登录状态下重置密码
	 * @param username 用户名
	 * @param passwordNew 新密码
	 * @param forgetToken 重置密码的Token
	 * @return 修改密码结果
	 */
	@Override
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
		if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("参数错误,token需要传递");
		}
		ServerResponse<String> validResponse = this.checkValid(username,Const.USERNAME);
		if(validResponse.isSuccess()){
			//用户不存在
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		if(org.apache.commons.lang3.StringUtils.isBlank(token)){
			return ServerResponse.createByErrorMessage("token无效或者过期");
		}

		if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
			String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
			int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

			if(rowCount > 0){
				return ServerResponse.createBySuccessMessage("修改密码成功");
			}
		}else{
			return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
		}
		return ServerResponse.createByErrorMessage("修改密码失败");
	}


	/**
	 * 登录状态下重置密码
	 * @param passwordOld 旧密码
	 * @param passwordNew 新密码
	 * @param user 用户信息--用于根据新密码修改数据库中用户表
	 * @return 修改结果
	 */
	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
		//防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("旧密码错误");
		}

		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount > 0){
			return ServerResponse.createBySuccessMessage("密码更新成功");
		}
		return ServerResponse.createByErrorMessage("密码更新失败");
	}


	/**
	 * 更新用户信息
	 * @param user 封装好的待更新信息
	 * @return  更新结果
	 */
	@Override
	public ServerResponse<User> updateInformation(User user){
		//username是不能被更新的
		//email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
		if(resultCount > 0){
			return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());

		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount > 0){
			return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
		}
		return ServerResponse.createByErrorMessage("更新个人信息失败");
	}


	/**
	 * 更具userId获取用户信息
	 * @param userId    用户ID
	 * @return 该ID对应的用户信息
	 */
	@Override
	public ServerResponse<User> getInformation(Integer userId){
		User user = userMapper.selectByPrimaryKey(userId);
		if(user == null){
			return ServerResponse.createByErrorMessage("找不到当前用户");
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);

	}

	/**
	 * 校验是否是管理员
	 * @param user  用户信息
	 * @return  管理员（success），普通用户（error）
	 */
	@Override
	public ServerResponse<String> checkAdminRole(User user){
		if(user != null && user.getRole() == Const.Role.ROLE_ADMIN){
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

}
