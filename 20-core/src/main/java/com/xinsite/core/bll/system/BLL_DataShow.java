package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.mybatis.datasource.master.bll.BLL_SysDatashow;
import com.xinsite.mybatis.datasource.master.entity.SysDatashow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典管理：处理下拉是否显示、可选
 * create by zhangxiaxin
 */
public class BLL_DataShow {

    /**
     * 获取该数据源特殊值
     */
    public static JsonArray getDataShow(String data_type, String data_key) throws Exception {
        String sql = "select data_id,disabled,isshow from sys_datashow where data_type=@data_type and data_key=@data_key";

        return DBFunction.executeJsonArray(sql
                , new DBParameter("@data_type", "=", data_type)
                , new DBParameter("@data_key", "=", data_key));
    }

    /**
     * 获取不显示的数据源Ids
     */
    public static String getNotShowIds(JsonArray dt) {
        if (dt == null || dt.size() == 0) return StringUtils.EMPTY;
        JsonArray array = GsonUtils.getWhereArray(dt, "isshow", 0);
        if (array == null || array.size() == 0) return StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < array.size(); j++) {
            JsonObject r = GsonUtils.getObject(array, j);
            if (sb.length() != 0) sb.append(",");
            sb.append(GsonUtils.tryParse(r, "data_id", 0));
        }
        return sb.toString();
    }

    /**
     * 获取不显示的数据源Ids
     */
    public static String getNotShowIds(String data_type, String data_key) throws Exception {
        String sql = "select data_id from sys_datashow where data_type=@data_type and data_key=@data_key and isshow=0";

        if (StringUtils.isEmpty(data_key)) return StringUtils.EMPTY;
        JsonArray dt = DBFunction.executeJsonArray(sql
                , new DBParameter("@data_type", "=", data_type)
                , new DBParameter("@data_key", "=", data_key));
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < dt.size(); j++) {
            JsonObject r = GsonUtils.getObject(dt, j);
            if (sb.length() != 0) sb.append(",");
            sb.append(GsonUtils.tryParse(r, "data_id", 0));
        }
        return sb.toString();
    }

    /**
     * 该编码记录主键值
     */
    public static boolean isDelete(SysDatashow datashow, String field, String value) {
        if (field.equalsIgnoreCase("enabled") || field.equalsIgnoreCase("disabled")) {
            if (StringUtils.isEmpty(value) && datashow.getIsshow() == 1)
                return true;
        } else if ("1".equals(value) && StringUtils.isEmpty(datashow.getDisabled())) {
            return true;
        }
        return false;
    }

    /**
     * 该编码记录主键值
     */
    public static boolean isDelete(String field, String value) {
        if (field.equalsIgnoreCase("enabled") || field.equalsIgnoreCase("disabled")) {
            if (StringUtils.isEmpty(value)) return true;
        } else if ("1".equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 是否删除
     */
    public static void delete(long ds_id, String disabled, String isshow) throws Exception {
        if (StringUtils.isEmpty(disabled) || "1".equals(isshow) && ds_id > 0) {
            DBFunction.deleteByTbName("sys_datashow", "id", ds_id);
        }
    }

    /**
     * 该编码记录值是否存在
     */
    public static long save(String data_type, String data_key, int data_id, String field, String value) throws Exception {
        SysDatashow datashow = BLL_SysDatashow.getSysDatashow(data_type, data_key, data_id);
        Map<String, Object> map = new HashMap();
        if (field.equalsIgnoreCase("enabled")
                || field.equalsIgnoreCase("disabled")) {
            map.put("disabled", value);
        } else {
            map.put("isshow", value);
        }
        long id = 0l;
        if (datashow == null) {  //新增
            if (!BLL_DataShow.isDelete(field, value)) {
                map.put("data_type", data_type); //数据源类型，code:编码表；datasource：系统源
                map.put("data_key", data_key);
                map.put("data_id", data_id);
                id = DBFunction.insertTable(map, "sys_datashow", 0L);
            }
        } else {
            id = datashow.getId();
            if (BLL_DataShow.isDelete(datashow, field, value))
                DBFunction.deleteByTbName("sys_datashow", "id", id);
            else
                DBFunction.updateByTbName(map, "sys_datashow", "id=" + id);
        }
        return id;
    }

    /**
     * 编码列表计算是否可用、是否可显示
     */
    public static void setEnabled(JsonArray array, String data_type, String data_key) throws Exception {
        if (array == null || array.size() == 0) return;
        if (StringUtils.isEmpty(data_key)) return;
        JsonArray show = BLL_DataShow.getDataShow(data_type, data_key);
        if (show == null || show.size() == 0) return;
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            int id = GsonUtils.tryParse(dr, "id", 0);
            JsonObject object = GsonUtils.getWhereJsonObject(show, "data_id", id);
            if (object != null) {
                String disabled = GsonUtils.tryParse(object, "disabled", "");
                if ("disabled".equalsIgnoreCase(disabled)) {
                    dr.addProperty("enabled", "0");
                }
                dr.addProperty("isshow", GsonUtils.tryParse(object, "isshow", 1));
            }
        }
    }

    /**
     * 设置或者添加不可用状态，disableds是Id集合
     */
    public static void setGridDisableds(JsonArray array, String disableds) {
        if (!StringUtils.isEmpty(disableds) && array != null) {
            List<String> list = StringUtils.stringToList(disableds);
            for (String data_id : list) {
                JsonObject object = GsonUtils.getWhereJsonObject(array, "data_id", data_id);
                if (object != null) {
                    object.addProperty("disabled", "disabled");
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("data_id", data_id);
                    jsonObject.addProperty("disabled", "disabled");
                    jsonObject.addProperty("isshow", 1);
                    array.add(jsonObject);
                }
            }
        }
    }

    /**
     * 计算任务位置
     */
    public static void setDisabled(JsonArray array, JsonArray show) {
        if (array == null || array.size() == 0) return;
//        if (disableds.length > 0) BLL_Code.setGridDisableds(array, disableds[0]);
        if (show == null || show.size() == 0) return;
        String field_id = "id";
        if (CommUtils.columnsExists(array, "data_id")) field_id = "data_id";
        for (int i = 0; i < array.size(); i++) {
            JsonObject dr = GsonUtils.getObject(array, i);
            int data_id = GsonUtils.tryParse(dr, field_id, 0);
            JsonObject object = GsonUtils.getWhereJsonObject(show, "data_id", data_id);
            if (object != null) {
                dr.addProperty("disabled", GsonUtils.tryParse(object, "disabled", ""));
            }
        }
    }
}
