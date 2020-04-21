package com.xinsite.core.bll.info;

import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.enums.system.ExtendEnum;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLL_UploadFile {
    /**
     * 保存栏目允许上传的附件类型
     */
    public static boolean saveInfo(String item_ids, String attach_types) throws Exception {
        if (StringUtils.isNotEmpty(item_ids)) {
            String table_name = ExtendEnum.文件类型.getValue();
            List<Integer> list = StringUtils.splitToList(item_ids);
            DBFunction.startTransaction();
            try {
                for (int item_id : list) {
                    BLL_Extend_Info.delete(table_name, item_id);
                    if (StringUtils.isNotEmpty(attach_types)) {
                        Map map = new HashMap();
                        map.put("field_extend", "uploadfile_type");
                        map.put("field_value", attach_types);
                        map.put("flag_1", "栏目上传文件类型");
                        BLL_Extend_Info.saveInfo(map, table_name, item_id);
                    }
                }
                DBFunction.commit();
            } catch (Exception ex) {
                DBFunction.rollback();
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     * 获取栏目允许上传的附件类型
     */
    public static String getInfo(int item_id) throws Exception {
        List<DBParameter> ls = new ArrayList<>();
        ls.add(new DBParameter("@table_name", ExtendEnum.文件类型.getValue()));
        ls.add(new DBParameter("@table_id", item_id));
        return BLL_Extend_Info.getFieldValue(ls, StringUtils.EMPTY);
    }

}

