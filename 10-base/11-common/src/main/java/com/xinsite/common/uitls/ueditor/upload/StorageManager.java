package com.xinsite.common.uitls.ueditor.upload;


import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.io.PropertiesUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.ueditor.define.AppInfo;
import com.xinsite.common.uitls.ueditor.define.BaseState;
import com.xinsite.common.uitls.ueditor.define.State;

import java.io.*;

public class StorageManager {
	
	public static final int BUFFER_SIZE = 8192;

	public StorageManager() {
		
	}

	public static State saveBinaryFile(byte[] data, String path) {
		File file = new File(path);

		State state = valid(file);

		if (!state.isSuccess()) {
			return state;
		}
		
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(data);
		} catch (IOException ioe) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}finally {
			if (bos != null){
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					;
				}
			}
		}
		
		// 验证允许的上传的文件类型（如果没有设置则不验证，默认不设置）
		String allowContentTypes = PropertiesUtils.getInstance()
				.getProperty("file.allowContentTypes");
		if(StringUtils.isNotBlank(allowContentTypes)){
			String rct = FileUtils.getRealContentType(file);
			if (!StringUtils.inString(rct, allowContentTypes.split(","))){
				file.delete();
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}
		}

		state = new BaseState(true, file.getAbsolutePath());
		state.putInfo( "size", data.length );
		state.putInfo( "title", file.getName() );
		return state;
	}

	public static State saveFileByInputStream(InputStream is, String path, long maxSize) {
		State state = null;

		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		BufferedOutputStream bos = null;
		try {
			try{
				bos = new BufferedOutputStream(
						new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);
				int count = 0;
				while ((count = bis.read(dataBuf)) != -1) {
					bos.write(dataBuf, 0, count);
				}
			}finally {
				if (bos != null){
					bos.flush();
					bos.close();
				}
			}

			if (tmpFile.length() > maxSize) {
				tmpFile.delete();
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			
			// 验证允许的上传的文件类型（如果没有设置则不验证，默认不设置）
			String allowContentTypes = PropertiesUtils.getInstance()
					.getProperty("file.allowContentTypes");
			if(StringUtils.isNotBlank(allowContentTypes)){
				String rct = FileUtils.getRealContentType(tmpFile);
				if (!StringUtils.inString(rct, allowContentTypes.split(","))){
					tmpFile.delete();
					return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
				}
			}

			state = saveTmpFile(tmpFile, path);

			if (!state.isSuccess()) {
				tmpFile.delete();
			}

			return state;
		} catch (IOException e) {
			;
		}finally {
			if (bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	public static State saveFileByInputStream(InputStream is, String path) {
		State state = null;

		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = null;
			try{
				bos = new BufferedOutputStream(new FileOutputStream(tmpFile),
						StorageManager.BUFFER_SIZE);
				int count = 0;
				while ((count = bis.read(dataBuf)) != -1) {
					bos.write(dataBuf, 0, count);
				}
			}finally {
				if (bos != null){
					bos.flush();
					bos.close();
				}
			}
			state = saveTmpFile(tmpFile, path);
			if (!state.isSuccess()) {
				tmpFile.delete();
			}
			return state;
		} catch (IOException e) {
			;
		}finally {
			if (bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static File getTmpFile() {
//		File tmpDir = FileUtils.getTempDirectory();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
//		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
//		return new File(tmpDir, tmpFileName);
		return new File(tmpDir, IdGenerate.randomBase62(10));
	}

	private static State saveTmpFile(File tmpFile, String path) {
		State state = null;
		File targetFile = new File(path);

		if (targetFile.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}
		try {
			FileUtils.moveFile(tmpFile, targetFile);
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}

		state = new BaseState(true);
		state.putInfo( "size", targetFile.length() );
		state.putInfo( "title", targetFile.getName() );
		
		return state;
	}

	private static State valid(File file) {
		File parentPath = file.getParentFile();

		if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
			return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
		}

		if (!parentPath.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}

		return new BaseState(true);
	}
	
	/**
	 * UEditor上传文件成功后调用事件
	 * @param physicalPath 上传文件实际路径
	 * @param storageState 返回到客户端的文件访问地址
	 */
	public static void uploadFileSuccess(String physicalPath, State storageState){
		
	}
	
}
