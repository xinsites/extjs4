package com.xinsite.core.utils.build;

import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.FileWebUtils;
import com.xinsite.core.utils.user.UserUtils;

import java.util.List;

/**
 * 枚举类型生成方法
 * create by zhangxiaxin
 */
public class EnumUtils {

    /**
     * 获取参数方法
     */
    public static String replaceAuthor(String content) {
        if (!StringUtils.isEmpty(content)) {
            content = content.replace("${author}", "zhangxiaxin");
            content = content.replace("${create_time}", DateUtils.getDate("yyyy-MM-dd"));
        }
        return content;
    }

    /**
     * 获取参数方法
     */
    public static String getBuildDir(String path) {
        return StringUtils.format("{0}static/generator/{1}/", FileUtils.getRootPath(), path);
    }

    /**
     * 生成MyBatis所有文件
     */
    public static String generateEnums(String file_name, String explain, String store_datas) {
        String buildDir = EnumUtils.getBuildDir("01_enum/" + UserUtils.getLoginUser().getLoginName());
        String buildFile = FileWebUtils.getMapPath(buildDir, StringUtils.format("{0}.java", file_name));

        String header;
        String content = FileWebUtils.readFileToString("templates/build/enum_model.html");
        content = content.replace("enum_model", file_name);
        if (!StringUtils.isEmpty(explain)) {
            header = "/**\n" +
                    " * ${explain}\n" +
                    " */";
            content = content.replace("${header}", header);
            content = content.replace("${explain}", explain);
        } else {
            header = "/**\n" +
                    " * create by ${author}\n" +
                    " * create time: ${create_time}\n" +
                    " */";
            content = content.replace("${header}", header);
            content = EnumUtils.replaceAuthor(content);
        }

        StringBuffer buffer = new StringBuffer();
        List<String[]> list = GsonUtils.getList(store_datas, String[].class);
        for (String[] strs : list) {
            if (strs.length == 2) {
                if (!StringUtils.isEmpty(buffer.toString())) buffer.append(",\n");
                buffer.append(String.format("\t%s(\"%s\")", getStr(strs[1]), strs[0]));
            }
        }
        buffer.append(";");
        content = content.replace("${enum_item}", buffer.toString());
        FileWebUtils.createFile(buildFile, content);

        return String.format("static/generator/01_enum/%s/", UserUtils.getLoginUser().getLoginName());
    }

    private static String getStr(String val) {
        val = val.replace("(", "_");
        val = val.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5_]+", "");
        return val;
    }
}
