package com.xinsite.runner;

import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.json.JSON;
import com.xinsite.common.uitls.json.JSONObject;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.FileWebUtils;
import com.xinsite.core.utils.build.EnumUtils;
import com.xinsite.core.utils.user.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 动态配置ueditor的config.json信息
 */

@Component
public class UeditorConfigRunner implements CommandLineRunner {
    // 设置cookie的有效访问路径
    @Value("${shiro.cookie.path}")
    private String path;

    @Override
    public void run(String... args) {
        try {
            String rootPath = FileUtils.getRootPath();
            String fileDir = "static/javascript/plugins/ueditor/config.json";
            String json = FileUtils.readFileToString(fileDir);
            if (!StringUtils.isEmpty(json)) {
                json = StringUtils.clearComment(json);
            }
            String contextPath = path + "/uploadfiles";
            JSONObject obj = JSON.unmarshal(json, JSONObject.class);
            obj.put("imageUrlPrefix", contextPath);          //图片访问路径前缀
            obj.put("scrawlUrlPrefix", contextPath);         //涂鸦访问路径前缀
            obj.put("snapscreenUrlPrefix", contextPath);     //截图访问路径前缀
            obj.put("catcherUrlPrefix", contextPath);        //抓取远程图片访问路径前缀
            obj.put("videoUrlPrefix", contextPath);          //上传视频访问路径前缀
            obj.put("fileUrlPrefix", contextPath);           //上传文件访问路径前缀
            obj.put("imageManagerUrlPrefix", contextPath);   //列出指定目录下的图片访问路径前缀
            obj.put("fileManagerUrlPrefix", contextPath);    //列出指定目录下的图片访问路径前缀
            FileWebUtils.createFile(rootPath + fileDir, obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}