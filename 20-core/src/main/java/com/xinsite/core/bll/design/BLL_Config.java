package com.xinsite.core.bll.design;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.enums.FieldTagEnum;
import com.xinsite.core.model.design.ConfigureModel;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * 设计表参数配置
 * create by zhangxiaxin
 */
public class BLL_Config {

    public static JsonArray getConfigGrid(int item_id) throws Exception {
        String sql = "select * from tb_configvalue where item_id=" + item_id;
        return DBFunction.executeJsonArray(sql);
    }

    public static void setConfigureValue(ConfigureModel configure, JsonArray dt) {
        if (configure != null && dt != null) {
            for (int i = 0; i < dt.size(); i++) {
                JsonObject json = GsonUtils.getObject(dt, i);
                String config_key = GsonUtils.tryParse(json, "config_key", "");
                if (config_key.equalsIgnoreCase(configure.data_index)) {
                    configure.config_value = GsonUtils.tryParse(json, "config_value", "");
                    configure.config_text = GsonUtils.tryParse(json, "config_text", "");
                    break;
                }
            }
        }
    }

    public static void saveConfigureValue(int item_id, String config_key, String value) {
        String main_table = BLL_Design.getMainTableName(item_id);
        if (StringUtils.isNotEmpty(main_table)) {
            try {
                JsonArray array = BLL_Design.getTableFields(main_table);
                String primary_key = BLL_Design.getFieldName(array, FieldTagEnum.主键.getValue());
                String f_item_id = BLL_Design.getFieldName(array, FieldTagEnum.栏目号.getValue());
                String sql = "select max(%s) from %s where 1=1";
                sql = String.format(sql, primary_key, main_table);
                Map map = new HashMap();
                map.put(config_key, value);
                if (StringUtils.isNotEmpty(f_item_id)) {
                    sql += String.format(" and %s=%d", f_item_id, item_id);
                    map.put(f_item_id, item_id);
                }
                int idleaf = NumberUtils.strToInt(DBFunction.executeScalar(sql));
                if (idleaf > 0) {
                    DBFunction.updateByTbName(map, main_table, primary_key + "=" + idleaf);
                } else {
                    DBFunction.insertTable(map, main_table, 0);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

}

