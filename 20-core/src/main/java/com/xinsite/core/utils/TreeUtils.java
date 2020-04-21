package com.xinsite.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.collect.ArrayUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.bll.BLL_Common;

import java.util.ArrayList;
import java.util.List;

public class TreeUtils {
    /**
     * 获取树形目录Id集合
     */
    public static String getTreeTablePids(JsonArray array, String pid) {
        List<Long> list = new ArrayList<>();
        if (array != null && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject dr = GsonUtils.getObject(array, i);
                long id = GsonUtils.tryParse(dr, pid, 0L);
                if (!list.contains(id)) list.add(id);
            }
        }
        return StringUtils.joinAsList(list);
    }

    /**
     * 获取树形目录及所有父Id集合
     */
    public static String getTreeTableAllPids(JsonArray dt, String tablename, String id, String pid) throws Exception {
        String ids = ArrayUtils.joinFields(dt, id, 0L);
        String pids = ArrayUtils.joinFieldsToRepeat(dt, pid, 0L);
        do {
            if (!StringUtils.isEmpty(pids)) ids += "," + pids;
            JsonArray dt_pid = BLL_Common.getTreeByIds(tablename, id, pid, pids);
            pids = ArrayUtils.joinFieldsToRepeat(dt_pid, pid, 0L);
        } while (!StringUtils.isEmpty(pids));
        return ids;
    }

    /**
     * 获取树形目录及所有父Id集合
     */
    public static String getTreeTableAllPids(JsonArray dt, String dt_id, String dt_pid, String tablename, String id, String pid) throws Exception {
        String ids = ArrayUtils.joinFields(dt, dt_id, 0L);
        String pids = ArrayUtils.joinFieldsToRepeat(dt, dt_pid, 0L);
        do {
            if (!StringUtils.isEmpty(pids)) ids += "," + pids;
            JsonArray dt_pids = BLL_Common.getTreeByIds(tablename, id, pid, pids);
            pids = ArrayUtils.joinFieldsToRepeat(dt_pids, pid, 0L);
        } while (!StringUtils.isEmpty(pids));
        return ids;
    }

    /**
     * 获取该树结点的所有子结点
     */
    public static String getTreeChildNodes(int id, String tablename, String id_field, String pid_field) throws Exception {
        String ids = id + "";
        String child_ids = "0";
        do {
            JsonArray tree_ids = BLL_Common.getTreeByPids(tablename, id_field, pid_field, ids);
            ids = ArrayUtils.joinFieldsToRepeat(tree_ids, id_field, 0L);
            if (!StringUtils.isEmpty(ids)) child_ids += "," + ids;
        } while (!StringUtils.isEmpty(ids));
        if (child_ids.equals("0")) return StringUtils.EMPTY;
        return child_ids.substring(2);
    }
}


