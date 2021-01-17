package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author guardWarm
 * @date 2021-01-16 13:18
 */
public class FTPUtil {

	private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private static final String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");
	private static final Integer FTP_PORT = Integer.valueOf(PropertiesUtil.getProperty("ftp.server.port","21"));
	private static final String FTP_USER = PropertiesUtil.getProperty("ftp.user");
	private static final String FTP_PASS = PropertiesUtil.getProperty("ftp.pass");

	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;

	private FTPUtil(String ip,int port,String user,String pwd){
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	/**
	 * 上传文件
	 * @param fileList 待上传的文件列表
	 * @return 上传结果
	 * @throws IOException 文件操作异常
	 */
	public static boolean uploadFile(List<File> fileList) throws IOException {
		FTPUtil ftpUtil = new FTPUtil(FTP_IP,FTP_PORT, FTP_USER, FTP_PASS);
		logger.info("开始连接ftp服务器");
		boolean result = ftpUtil.uploadFile("/product/ftpfile/img/",fileList);
		logger.info("开始连接ftp服务器,结束上传,上传结果:{}",result);
		if (!result) {
			throw new IOException("上传文件至服务器失败");
		}
		return result;
	}


	private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
		boolean uploaded = true;
		FileInputStream fis = null;
		//连接FTP服务器
		if(connectServer(this.ip,this.port,this.user,this.pwd)){
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 我只有注释掉这个才能正常上传文件，不然会超时
				//ftpClient.enterLocalPassiveMode();
				for(File fileItem : fileList){
					fis = new FileInputStream(fileItem);
					ftpClient.storeFile(fileItem.getName(),fis);
				}
			} catch (IOException e) {
				logger.error("上传文件异常",e);
				uploaded = false;
				e.printStackTrace();
			} finally {
				if (fis != null) {
					fis.close();
				}
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}

	/**
	 * 根据提供的信息连接ftp服务器
	 * @param ip ip地址
	 * @param port 端口
	 * @param user 用户名
	 * @param pwd 密码
	 * @return 连接结果
	 */
	private boolean connectServer(String ip,int port,String user,String pwd){
		boolean isSuccess = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(ip);
			isSuccess = ftpClient.login(user,pwd);
		} catch (IOException e) {
			logger.error("连接FTP服务器异常",e);
		}
		return isSuccess;
	}
}
