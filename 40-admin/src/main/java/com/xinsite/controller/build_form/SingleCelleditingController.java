package com.xinsite.controller.build_form;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.codec.Md5Utils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.BLL_Common;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.design.BLL_Excel;
import com.xinsite.core.bll.build_form.BLL_SingleCellediting;
import com.xinsite.core.bll.system.BLL_Menu;
import com.xinsite.core.utils.ExcelUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import com.xinsite.core.utils.CommUtils;
import com.xinsite.core.utils.search.SearchUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by 系统管理员
 * create time: 2020-03-28
 * object name: 单表_单元格对象
 */

@RestController
@RequestMapping(value = "build/single_cellediting")
public class SingleCelleditingController extends BaseController {

    //单表_单元格编辑：查询列表
    @RequestMapping(value = "grid")
    @RequiresPermissions("build:single_cellediting:grid")
    public String grid(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            PageHelper pager = SearchUtils.getPageHelper(request, item_id);
            pager.where = " and a1.isdel=0";
            pager.showColumns = "a1.idleaf,a1.db_dyg_name,a1.db_dyg_sj,a1.db_dyg_dw,a1.db_dyg_sl,a1.db_dyg_je,a1.db_dyg_sfypz,a1.db_dyg_fkfs,a1.create_time";
            pager.tables = "de_single_cellediting a1";
            pager.where += " and a1.item_id=" + item_id;

            pager.loadPageGrid("a1.serialcode desc,a1.create_time desc");
            pager.addTextUserOrDept(); //列表加用户或者部门名称
            //下拉框选择值时替换文本
            BLL_Common.setGridListCodeText(pager.array, "db_dyg_dw", "work.company", "treepicker");
				BLL_Common.setGridListCodeText(pager.array, "db_dyg_sfypz", "code.yes.no", "singlecombobox");

            return retGrid.getGridJson(pager.array, pager.recordCount);
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-查询", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    //单表_单元格编辑：拖动排序
    @RequestMapping(value = "sort")
    @RequiresPermissions("build:single_cellediting:sort")
    public String sort(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!sort_vals.equals("")) {
                BLL_SingleCellediting.saveGridSort(sort_vals);
            }

            LogUtils.addOperateLog(item_id, "主表拖动排序", "成功");
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-排序", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //单表_单元格编辑：删除行
    @RequestMapping(value = "delete")
    @RequiresPermissions("build:single_cellediting:delete")
    public String delete(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String idleafs = getParaValue(request, "idleafs", "0");

        boolean success = false;
        try {
            idleafs = StringUtils.joinAsFilter(idleafs);
            if (!StringUtils.isEmpty(idleafs)) {
                success = BLL_SingleCellediting.deleteByIds(item_id, idleafs);
            }

            if (success) {
                LogUtils.addOperateLog(item_id, "主表信息删除", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-删除", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //单表_单元格编辑：新增行
    @RequestMapping(value = "add")
    @RequiresPermissions("build:single_cellediting:add")
    public String add(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String add_type = getParaValue(request, "add_type", "");

        try {
            if (item_id > 0) {
                Map map = new HashMap();
                map.put("create_time", DateUtils.getDateTime());                                //创建时间
				map.put("create_uid", UserUtils.getUserId());                                   //创建人
				map.put("org_id", UserUtils.getOrgId());                                        //机构号
				map.put("dept_id", UserUtils.getDeptId());                                      //部门号
				map.put("isdel", 0);                                                            //删除标识
				map.put("item_id", item_id);                                                    //栏目号
				map.put("serialcode", BLL_SingleCellediting.getSerialCode(add_type));           //排序号

                map.put("db_dyg_name", getParaValue(request, "db_dyg_name", UserUtils.getUserName()));  //标题
				map.put("db_dyg_sj", getParaValue(request, "db_dyg_sj", DateUtils.getDate()));          //时间
				map.put("db_dyg_dw", getParaValue(request, "db_dyg_dw", "52"));                         //单位
				map.put("db_dyg_sl", getParaValue(request, "db_dyg_sl", "null"));                       //数量
				map.put("db_dyg_je", getParaValue(request, "db_dyg_je", 35.8));                         //金额
				map.put("db_dyg_sfypz", getParaValue(request, "db_dyg_sfypz", "yes"));                  //是否有凭证
				map.put("db_dyg_fkfs", getParaValue(request, "db_dyg_fkfs", "1,2"));                    //付款方式


                long idleaf = BLL_SingleCellediting.addInfo(map, item_id);
                LogUtils.addOperateLog(item_id, "主表信息新增", idleaf);
                if (idleaf > 0) {
                    return ret.getSuccessResult(idleaf);
                }
            }
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-新增行", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //单表_单元格编辑：编辑单元格
    @RequestMapping(value = "mod")
    @RequiresPermissions("build:single_cellediting:mod")
    public String mod(HttpServletRequest request) {
        long idleaf = getParaValue(request, "idleaf", 0L);
        String field = getParaValue(request, "field", "");
        String value = getParaValue(request, "value", "");
        int item_id = getParaValue(request, "item_id", 0);

        try {
            if (idleaf > 0 && !StringUtils.isEmpty(field)) {
                Map map = new HashMap();
                map.put(field, value);
                map.put("modify_time", DateUtils.getDateTime());                                //修改时间
				map.put("modify_uid", UserUtils.getUserId());                                   //修改人


                idleaf = BLL_SingleCellediting.modInfo(map, item_id, idleaf);
                LogUtils.addOperateLog(item_id, "单元格编辑", idleaf);
                if (idleaf > 0) {
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-修改", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    //单表_单元格编辑：导出Excel
    @RequestMapping(value = "excel")
    @RequiresPermissions("build:single_cellediting:excel")
    public String excel(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String table_key = getParaValue(request, "table_key", "");
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (oid > 0 && item_id > 0) {
                String filename = Md5Utils.md5(request.getSession().getId());
                String MapUrl = ExcelUtils.getBuildExcelUrl(filename);
                Map build = BLL_Excel.getExportFieldCofing(UserUtils.getUserId(), oid);
                if (build != null && build.size() > 0) {
                    PageHelper pager = SearchUtils.getPageHelper(request, item_id);
                    pager.where = CommUtils.getFieldValue(build, "searchgrid_where");
                    pager.showColumns = CommUtils.getFieldValue(build, "searchgrid_showfields");
                    pager.tables = CommUtils.getFieldValue(build, "searchgrid_tables");

                    if (!pager.isValid()) return ret.getFailResult("无导出的列表");

                    JsonArray excel_fields = (JsonArray) build.get("excel_fields");
                    List<String> fields = ExcelUtils.getHeads(excel_fields, "", "field_name");

                    pager.showColumns = pager.showColumns.toLowerCase();
                    pager.where += " and a1.item_id=" + item_id;
                    pager.validExcelMaxCount();
                    JsonArray array = pager.getAllGrid("a1.serialcode desc,a1.create_time desc");

                    String item_name = BLL_Menu.getItemName(item_id);
                    ExcelWriter writer = ExcelUtils.getExcelWriter(MapUrl);
                    Sheet sheet1 = new Sheet(1, 0);
                    sheet1.setSheetName(item_name);
                    Table table = ExcelUtils.addHeadsText(excel_fields);

                    BLL_Common.setExcelCodeText(array, pager.showColumns, oid);
                    List<List<String>> data = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject dr = GsonUtils.getObject(array, i);
                        List<String> item = new ArrayList<>();
                        for (String field : fields) {
                            item.add(GsonUtils.tryParse(dr, field.toLowerCase(), ""));
                        }
                        data.add(item);
                    }
                    writer.write0(data, sheet1, table);
                    writer.finish();

                    LogUtils.addOperateLog("导出Excel", "成功", "栏目：" + item_name);
                    filename = String.format("static/tempfiles/%s.xlsx", filename);
                    ret.clear().addMap("filepath", filename).addMap("filename", item_name + ".xlsx");
                    return ret.getSuccessResult();
                }
            }
        } catch (Exception ex) {
            LogError.write("单表_单元格编辑-导出Excel", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
}
