package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author guardWarm
 * @date 2021-01-16 13:16
 */
public interface IFileService {
	/**
	 * 文件上传
	 * @param file 待上传文件
	 * @param path 本机暂存路径
	 * @return 上传到服务器上的文件名
	 */
	String upload(MultipartFile file, String path);
}
