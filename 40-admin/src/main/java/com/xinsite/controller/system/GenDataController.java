package com.xinsite.controller.system;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.codec.AesUtils;
import com.xinsite.common.uitls.extjs.JsonTree;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.BLL_Common;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.design.BLL_GenData;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.core.utils.FileWebUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.bean.Keys;
import com.xinsite.dal.dbhelper.DBFunction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-09-13
 * object name:系统管理->生成表单数据
 */

@RestController
@RequestMapping(value = "system/gen")
public class GenDataController extends BaseController {

    //获取生成对象树
    @RequestMapping(value = "object/tree")
    public String objectTree(HttpServletRequest request) {
        int node = getParaValue(request, "node", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request, item_id);
            JsonArray dt = BLL_GenData.getObjectTree(pager.where);
            return JsonTree.getTreeJsonByPid(dt, node + "");
        } catch (Exception ex) {
            LogError.write("获取所有生成对象树", LogEnum.Error, ex.toString());
        }
        return retGrid.getTreeFail();
    }

    //获取生成数据表
    @RequestMapping(value = "table/tree")
    public String tableTree(HttpServletRequest request) {
        int oid = getParaValue(request, "oid", 0);
        int expanded = getParaValue(request, "expanded", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request, item_id);
            pager.tables = "tb_gen_table a1,tb_gen_object b1";
            pager.where += " and a1.oid=b1.oid";
            if (oid > 0) {
                pager.where += BLL_Common.getPidFunctionWhere("getGenObjectPids", oid, "b1.oid", "b1.pid");
            }

            pager.showColumns = "a1.tid id,a1.pid,a1.table_explain text,a1.oid,b1.object_name,b1.object_type,b1.layout_type,a1.table_name,a1.table_key,a1.table_type,a1.tb_relation,\n" +
                    "                (select count(1) from tb_gen_field f where f.xtype!='displayfield' and f.isdefine=0 and f.tid=a1.tid) system_fields,\n" +
                    "                (select count(1) from tb_gen_field f where f.xtype!='displayfield' and f.isdefine=1 and f.tid=a1.tid) define_fields";

            JsonArray array = pager.getAllGrid("a1.pid,a1.serialcode");
            String otherAttr = "expanded:false";
            if (expanded == 1) otherAttr = "expanded:true";
            CommUtils.setFieldPassWord(array, "table_name");
            return JsonTree.getTreeJsonByPid(array, 0 + "", otherAttr, "");
        } catch (Exception ex) {
            LogError.write("生成数据表树形列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getTreeFail();
    }

    // 获取字段输入框类型列表数据源
    @RequestMapping(value = "xtype/combo")
    public String getXTypeCombo(HttpServletRequest request) {
        try {
            JsonArray dt = BLL_GenData.getFieldXTypeCombo();
            return retGrid.getGridJson(dt, dt.size(), "id,name");
        } catch (Exception ex) {
            LogError.write("输入框类型列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //获取信息
    @RequestMapping(value = "object/info")
    public String codeInfo(HttpServletRequest request) {
        int oid = getParaValue(request, "oid", 0);
        try {
            if (oid > 0) {
                return ret.getFormJson(BLL_Design.getObjectInfo(oid), "object");
            }
        } catch (Exception ex) {
            LogError.write("数据对象信息", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //编辑对象
    @RequestMapping(value = "object/mod")
    @RequiresPermissions("system:gen:data:mod")
    public String mod(HttpServletRequest request) {
        int oid = getParaValue(request, "oid", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            Map ht = new HashMap();
            ht.put("object_name", getParaValue(request, "object_name", ""));
            ht.put("item_method", getParaValue(request, "item_method", ""));
            ht.put("expanded", getParaValue(request, "expanded", ""));    //是否展开
            DBFunction.updateByTbName(ht, "tb_gen_object", "oid=" + oid);
            LogUtils.addOperateLog(item_id, "数据对象修改", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("对象修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //数据删除
    @RequestMapping(value = "data/delete")
    @RequiresPermissions("system:gen:data:delete")
    public String dataDelete(HttpServletRequest request) {
        int oid = getParaValue(request, "oid", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (oid > 0) {
                BLL_GenData.deleteObject(oid);
                LogUtils.addOperateLog(item_id, "数据对象删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("数据删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //数据导入
    @RequestMapping(value = "data/import")
    @RequiresPermissions("system:gen:data:import")
    public String dataImport(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String content = FileWebUtils.getFileUploadContent(request, "import_file");
            content = AesUtils.decode(content);
            Map map = GsonUtils.getMap(content);
            BLL_GenData.importData(map);
            LogUtils.addOperateLog(item_id, "数据对象导入", "成功");
            return ret.getSuccessResult();
        } catch (AppException ex) {
            return ret.getFailResult(ex.getError());
        } catch (Exception ex) {
            LogError.write("数据导入", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult("导入文件数据格式有错！");
    }

    //获取生成字段列表
    @RequestMapping(value = "field/grid")
    @RequiresPermissions("sys:gen:field:grid")
    public String fieldGrid(HttpServletRequest request) {
        int oid = getParaValue(request, "oid", 0);
        int tid = getParaValue(request, "tid", 0);
        int isdefine = getParaValue(request, "isdefine", 0);
        int item_id = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request, item_id);
            pager.querySql = "select distinct PrimaryKey from tb_gen_field a left join view_gen_xtype d on a.xtype=d.xtype,tb_gen_table b,tb_gen_object c where \n" +
                    "  a.tid=b.tid and b.oid=c.oid ";

            pager.addDataPerWhere(Keys.getKey("alias", "a"));  //加数据权限
            pager.where = " and a.xtype!='displayfield'";
            if (isdefine == 0) pager.where += " and a.isdefine=1";  //默认只显示用户自定义的字段
            if (tid != 0) pager.where += " and a.tid=" + tid;
            if (oid > 0 && tid == 0) {
                pager.where += BLL_Common.getPidFunctionWhere("getGenObjectPids", oid, "c.oid", "c.pid");
            }

            pager.showColumns = "a.fid,a.tid,c.object_name,b.table_explain,concat(b.table_name,a.extend_suf) table_name,b.tb_relation,a.field_name,a.field_explain,a.field_tag,a.issearchfield,a.isdefine,a.serialcode,a.xtype,a.editor_search,d.xtype_name";
            pager.loadPageGrid("c.oid,b.tid,a.serialcode");
            CommUtils.setFieldPassWord(pager.array, "table_name");
            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("生成数据表字段列表", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //生成字段编辑单元格存储
    @RequestMapping(value = "field/editing")
    @RequiresPermissions("sys:gen:field:editing")
    public String fieldEditing(HttpServletRequest request) {
        int Id = getParaValue(request, "id", 0);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (Id > 0 && !StringUtils.isEmpty(field)) {
                Map ht = new HashMap();
                ht.put(field, value);
                if (field.equalsIgnoreCase("field_name")) {
                    ht.put("dataIndex", value);
                }
                DBFunction.updateByTbName(ht, "tb_gen_field", "fid=" + Id);

                LogUtils.addOperateLog(item_id, "数据表字段编辑", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("数据表字段编辑", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //生成字段拖动排序
    @RequestMapping(value = "field/sort")
    @RequiresPermissions("sys:gen:field:sort")
    public String fieldSort(HttpServletRequest request) {
        String field = getParaValue(request, "field", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!sort_vals.equals("")) {
                BLL_GenData.saveFieldSort(sort_vals, field);
            }
            LogUtils.addOperateLog(item_id, "数据表字段排序", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("数据表字段排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 获取配置表单editor_form值
    @RequestMapping(value = "editor/info")
    public String getFieldEditorInfo(HttpServletRequest request) {
        int fid = getParaValue(request, "fid", 0);
        try {
            if (fid > 0) {
                String sql = StringUtils.format("select fid,editor_search from tb_gen_field where fid={0}", fid);
                JsonArray dt = DBFunction.executeJsonArray(sql);
                return ret.getFormJson(dt, "field");
            }
        } catch (Exception ex) {
            LogError.write("获取配置表单editor_form值", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //重新保存查询editor_search值
    @RequestMapping(value = "editor/save")
    @RequiresPermissions("sys:gen:field:editing")
    public String editorInfoSave(HttpServletRequest request) {
        int fid = getParaValue(request, "fid", 0);
        String editor_form = getParaValue(request, "editor_form", "");
        int item_id = getParaValue(request, "item_id", 0);
        try {
            if (fid > 0) {
                if (BLL_GenData.saveFieldEditorSearch(fid, editor_form)) {
                    LogUtils.addOperateLog(item_id, "查询输入框editor_search保存", "成功");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("保存生成表单editor_search值", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }


}


