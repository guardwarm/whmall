package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author guardWarm
 * @date 2021-01-16 13:17
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


	/**
	 * 文件上传
	 * @param file 待上传文件
	 * @param path 本机暂存路径
	 * @return 上传到服务器上的文件名
	 */
	@Override
	public String upload(MultipartFile file, String path){
		String fileName = file.getOriginalFilename();
		//扩展名  abc.jpg--》jpg
		String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
		String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
		logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

		File fileDir = new File(path);
		if(!fileDir.exists()){
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		File targetFile = new File(path,uploadFileName);
		try {
			// 先将文件存储在本机path路径下
			file.transferTo(targetFile);
			//在上传到ftp服务器
			FTPUtil.uploadFile(Lists.newArrayList(targetFile));
			//删除本机文件
			targetFile.delete();
		} catch (IOException e) {
			logger.error("上传文件异常",e);
			return null;
		}

		return targetFile.getName();
	}

}