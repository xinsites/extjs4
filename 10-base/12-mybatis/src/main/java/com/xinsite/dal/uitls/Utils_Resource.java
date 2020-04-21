package com.xinsite.dal.uitls;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源供给类
 * @author zhangxiaxin
 * @version 2018-07-15
 */
public class Utils_Resource extends org.springframework.util.ResourceUtils {
	
	private static ResourceLoader resourceLoader;
	private static ResourcePatternResolver resourceResolver;
	static{
		resourceLoader = new DefaultResourceLoader();
		resourceResolver = new PathMatchingResourcePatternResolver(resourceLoader);
	}
	
	/**
	 * 获取资源加载器（可读取jar内的文件）
	 * @author zhangxiaxin
	 */
	public static ResourceLoader getResourceLoader() {
		return resourceLoader;
	}
	
	/**
	 * 获取ClassLoader
	 */
	public static ClassLoader getClassLoader() {
		return resourceLoader.getClassLoader();
	}
	
	/**
	 * 获取资源加载器（可读取jar内的文件）
	 */
	public static Resource getResource(String location) {
		return resourceLoader.getResource(location);
	}
	
	/**
	 * 获取资源文件流（用后记得关闭）
	 * @param location
	 * @author zhangxiaxin
	 * @throws IOException 
	 */
	public static InputStream getResourceFileStream(String location) throws IOException{
		Resource resource = resourceLoader.getResource(location);
		return resource.getInputStream();
	}
	
	/**
	 * 获取资源文件内容
	 * @param location
	 * @author zhangxiaxin
	 */
	public static String getResourceFileContent(String location){
		InputStream is = null;
		try{
			is = Utils_Resource.getResourceFileStream(location);
			return Utils_IO.toString(is, "UTF-8");
		}catch (IOException e) {
			throw Utils_Comm.unchecked(e);
		}finally{
			Utils_IO.closeQuietly(is);
		}
	}
	
	/**
	 * Spring 搜索资源文件
	 * @param locationPattern
	 * @author zhangxiaxin
	 */
	public static Resource[] getResources(String locationPattern){
		try {
			Resource[] resources = resourceResolver.getResources(locationPattern);
			return resources;
		} catch (IOException e) {
			throw Utils_Comm.unchecked(e);
		}
	}
	
}
