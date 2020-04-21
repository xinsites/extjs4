package com.xinsite.controller.ueditor;

import com.baidu.ueditor.ActionEnter;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.log.LogError;
import org.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * create by zhangxiaxin
 * create time: 2019-11-28
 * object name:Ueditor编辑器操作
 */

@RestController
@RequestMapping(value = "ueditor")
public class UeditorController extends BaseController {

    @RequestMapping("/config")
    public void config(HttpServletResponse response, HttpServletRequest request) {
        //String rootPath = "此处配置项目中config.json文件路径";
        response.setContentType("application/json");

        try {
            String actionType = request.getParameter("action");
            String rootPath = FileUtils.getRootPath();
            //FileUtils.getResourcePath("static/javascript/plugins/ueditor"); //config.json文件路径
            String exec = new ActionEnter(request, rootPath, "static/javascript/plugins/ueditor").exec();

            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("UEditorConfig")
    public String getUEditorConfig(HttpServletResponse response, HttpServletRequest request) {
        String json = FileUtils.readFileToString("static/javascript/plugins/ueditor/config.json");
        if (!StringUtils.isEmpty(json)) {
            json = StringUtils.clearComment(json);
        }
        return json;
    }

    /**
     * Ueditor上传文件
     * 这里以上传图片为例，图片上传后，imgPath将存储图片的保存路径，返回到编辑器中做展示
     */
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        String result = "";
        if (!file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();

            // 这里写你的文件上传逻辑
            // String imgPath = fileUtil.uploadImg(file);

            String imgPath = "";
            result = "{\n" +
                    "    \"state\": \"SUCCESS\",\n" +
                    "    \"url\": \"" + imgPath + "\",\n" +
                    "    \"title\": \"" + originalFileName + "\",\n" +
                    "    \"original\": \"" + originalFileName + "\"\n" +
                    "}";
        }
        return result;
    }
}
