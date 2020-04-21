package com.xinsite.core.utils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zhangxiaxin
 * object name: Excel导出工具
 */
public class ExcelUtils {

    public static ExcelWriter getExcelWriter(String MapUrl) throws Exception {
        FileOutputStream out = new FileOutputStream(MapUrl);
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
        return writer;
    }

    public static List<String> getHeads(JsonArray array, String pre, String field_name) throws Exception {
        List<String> head = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            head.add(pre + GsonUtils.tryParse(dr, field_name, ""));
        }
        return head;
    }

    public static Table addHeadsText(JsonArray array) throws Exception {
        List<List<String>> head = new ArrayList<List<String>>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            List<String> headCoulumn = new ArrayList<String>();
            headCoulumn.add(GsonUtils.tryParse(dr, "field_explain", ""));
            head.add(headCoulumn);
        }
        Table table = new Table(1);
        table.setHead(head);
        return table;
    }

    public static String getBuildExcelUrl(String filename) throws Exception {
        String savePath = FileUtils.getUploadFildPath("tempfiles"); //获取项目动态绝对路径
        savePath = savePath.replace("\\", "/");
        String MapUrl = savePath + filename + ".xlsx";
        File tmpFile = new File(savePath);
        if (!tmpFile.exists()) tmpFile.mkdir();
        return MapUrl;
    }
}
