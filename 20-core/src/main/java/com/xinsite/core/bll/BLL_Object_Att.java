package com.xinsite.core.bll;

import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.common.uitls.lang.StringUtils;

/**
 * 附件管理
 * create by zhangxiaxin
 */
public class BLL_Object_Att {
    /**
     * 保存排序
     */
    public static void saveObjectAttSort(String sort_vals) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update tb_object_att set serialcode={0} where attach_id={1};";
        String[] Items = sort_vals.split(";");
        for (String item : Items) {
            String[] arr = item.split(":");
            if (arr.length == 2)
                sb.append(StringUtils.format(sql, arr[1], arr[0]));
        }
        DBFunction.executeNonQuery(sb.toString());
    }
}

