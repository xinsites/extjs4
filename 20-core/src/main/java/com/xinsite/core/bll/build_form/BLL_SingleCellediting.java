package com.xinsite.core.bll.build_form;

import com.google.gson.JsonArray;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.design.BLL_Share;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.dal.dbhelper.DBFunction;

import java.util.Map;

/**
 * create by 系统管理员
 * create time: 2020-03-28
 * object name: 单表_单元格对象
 */
public class BLL_SingleCellediting {
    /**
     * 获取排序号
     */
    public static long getSerialCode(String add_type) {
        String str = "ifnull(max(serialcode),0)+1";
        if (add_type.equals("last")) str = "ifnull(min(serialcode),0)-1";
        String strSql = StringUtils.format("select {0} from de_single_cellediting", str);
        return NumberUtils.strToLong(DBFunction.executeScalar(strSql));
    }

    /**
     * 保存排序
     */
    public static void saveGridSort(String sortVal) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "update de_single_cellediting set serialcode={0} where idleaf={1};";
        String[] Items = sortVal.split(";");
        for (String item : Items) {
            String[] arr = item.split(":");
            if (arr.length == 2)
                sb.append(StringUtils.format(sql, NumberUtils.strToInt(arr[1]), NumberUtils.strToInt(arr[0])));
        }
        DBFunction.executeNonQuery(sb.toString());
    }

    /**
     * 删除列表
     */
    public static boolean deleteByIds(int itemid, String idsVal) {
        boolean result = false;
        DBFunction.startTransaction();
        try {
            idsVal = StringUtils.sqlFilter(idsVal);
            String strSql = StringUtils.format("update de_single_cellediting set isdel=1 where idleaf in({0})", idsVal);
            result = DBFunction.executeNonQuery(strSql) > 0;

            BLL_Share.deleteInfoShare("de_single_cellediting", itemid, idsVal);
            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return false;
        }
        return result;
    }

    /**
     * 新增保存单表信息
     */
    public static long addInfo(Map map, int item_id) {
        long idleaf = 0;
        DBFunction.startTransaction();
        try {
            idleaf = DBFunction.insertTable(map, "de_single_cellediting", 0L);
            BLL_Share.addInfoShare(map, "de_single_cellediting", item_id, idleaf);

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return 0;
        }
        return idleaf;
    }

    /**
     * 修改保存单表信息
     */
    public static long modInfo(Map map, int item_id, long idleaf) {
        DBFunction.startTransaction();
        try {
            DBFunction.updateByTbName(map, "de_single_cellediting", "idleaf=" + idleaf);
            BLL_Share.modInfoShare(map, "de_single_cellediting", item_id, idleaf);

            DBFunction.commit();
        } catch (Exception ex) {
            DBFunction.rollback();
            LogError.write(LogEnum.Error, ex.toString());
            return 0;
        }
        return idleaf;
    }
}

