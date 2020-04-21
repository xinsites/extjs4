package com.xinsite.common.uitls.ueditor.upload;

import com.xinsite.common.uitls.ueditor.PathFormat;
import com.xinsite.common.uitls.ueditor.define.AppInfo;
import com.xinsite.common.uitls.ueditor.define.BaseState;
import com.xinsite.common.uitls.ueditor.define.FileType;
import com.xinsite.common.uitls.ueditor.define.State;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class Base64Uploader {

	public static State save(HttpServletRequest request, String content, Map<String, Object> conf) {
		
		byte[] data = decode(content);

		long maxSize = ((Long) conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, AppInfo.MAX_SIZE);
		}

		String suffix = FileType.getSuffix("JPG");

		String savePath = PathFormat.parse((String) conf.get("savePath"),
				(String) conf.get("filename"));
		
		savePath = savePath + suffix;
		String physicalPath = (String) conf.get("rootPath") + savePath;

		State storageState = StorageManager.saveBinaryFile(data, physicalPath);

		if (storageState.isSuccess()) {
			String ctx = request.getContextPath(); //  修正上传图片后返回无contextpath问题
			storageState.putInfo("url", ctx + PathFormat.format(savePath));
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
			
			// UEditor上传文件成功后调用事件
			StorageManager.uploadFileSuccess(physicalPath, storageState);
		}

		return storageState;
	}

	private static byte[] decode(String content) {
		return Base64.decodeBase64(content);
	}

	private static boolean validSize(byte[] data, long length) {
		return data.length <= length;
	}
	
}