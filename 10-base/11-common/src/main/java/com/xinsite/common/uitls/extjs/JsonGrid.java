package com.xinsite.common.uitls.extjs;

import com.xinsite.common.uitls.gson.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Extjs,GridPanel组件Json格式字符串
 *
 * @author www.xinsite.vip
 * @version 2018-9-11
 */
public class JsonGrid {

    /**
     *   分页Grid组件Json格式字符串
     */
    public static String getGridJson(JsonArray array) {
       return GsonUtils.toJson(array);
    }

    /**
     *   分页Grid组件Json格式字符串
     */
    public static String getGridJson(JsonArray array, String[] fields) {
        StringBuilder strResult = new StringBuilder("[");
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonObject = array.get(i).getAsJsonObject();
            if (i != 0) strResult.append(",");
            strResult.append("{");
            for (int j = 0; j < fields.length; j++) {
                if (j != 0) strResult.append(",");
                String value = GsonUtils.tryParse(jsonObject, fields[j], "");
                value = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                strResult.append(String.format("\"%s\":\"%s\"", fields[j], value));
            }
            strResult.append("}");
        }
        strResult.append("]");
        return strResult.toString();
    }
}
