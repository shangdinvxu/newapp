package com.example.android.bluetoothlegatt.utils;

public class CommParams {
	/** APP的服务器根地址 */
	public final static String APP_ROOT_URL ="app.linkloving.com:6080/linkloving_server-watch/";
	/** APP的服务器根地址 */
	public final static String SERVER_CONTROLLER_URL_ROOT = "http://"+APP_ROOT_URL; 
	/** 用户上传头像的 */
	public final static String AVATAR_UPLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"UserAvatarUploadController";	
	/** 用户头像下载Servlet地址 */
	public final static String AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"UserAvatarDownloadController";
	/** 企业用户下载Servlet地址 */
	public final static String ENT_DOWNLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"BinaryDownloadController";
}
