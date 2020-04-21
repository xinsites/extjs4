package com.xinsite.controller.design;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.core.bll.design.BLL_Design;
import com.xinsite.core.bll.design.BLL_Excel;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.log.LogUtils;
import com.xinsite.core.utils.user.UserUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * create by zhangxiaxin
 * create time: 2019-12-06
 * object name: 设计表Excel导出
 */

@RestController
@RequestMapping(value = "design/excel")
public class ExcelController extends BaseController {

    //导出字段列表获取
    @RequestMapping(value = "field/grid")
    public String getExportFieldGrid(HttpServletRequest request) {
        int itemid = getParaValue(request, "item_id", 0);
        int isexport = getParaValue(request, "isexport", 0); //1=只包含导出字段
        int show_table = getParaValue(request, "show_table", 0); //1=显示表名
        String table_key = getParaValue(request, "table_key", "");
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (itemid > 0 && oid > 0) {
                JsonArray dt = BLL_Excel.getExportFieldGrid(UserUtils.getUserId(), oid, isexport, show_table);
                return retGrid.getGridJson(dt);
            }
        } catch (Exception ex) {
            LogError.write("导出字段列表获取", LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 对象导出字段列表保存
    @RequestMapping(value = "field/save")
    public String saveExportFieldGrid(HttpServletRequest request) {
        int itemid = getParaValue(request, "item_id", 0);
        String saveVal = getParaValue(request, "saveVal", "");
        String table_key = getParaValue(request, "table_key", "");
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (!saveVal.equals("") && itemid > 0 && oid > 0) {
                if (BLL_Excel.saveExportFieldGrid(UserUtils.getUserId(), oid, saveVal)) {
                    LogUtils.addOperateLog(itemid, "导出字段保存", "成功");
                    return ret.getSuccessResult();
                } else {
                    LogUtils.addOperateLog(itemid, "导出字段保存", "失败");
                }
            }
        } catch (Exception ex) {
            LogError.write("导出字段保存", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 清空导出列表配置
    @RequestMapping(value = "field/clear")
    public String clearExportFieldGrid(HttpServletRequest request) {
        String table_key = getParaValue(request, "table_key", "");
        int itemid = getParaValue(request, "item_id", 0);
        try {
            int oid = BLL_Design.getObjectId(table_key);
            if (itemid > 0 && oid > 0) {
                BLL_Excel.clearExportFieldGrid(UserUtils.getUserId(), oid);
                LogUtils.addOperateLog(itemid, "导出字段清空", "成功");
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write("导出字段清空", LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

//    @ResponseBody
//    @RequestMapping(value = "download", method = RequestMethod.GET)
//    public Object downLoadExcel(HttpServletRequest request, HttpServletResponse response) {
//        String filepath = getParaValue(request, "filepath", "");
//        String filename = getParaValue(request, "filename", "");
//        try {
//            String path = FileUtils.getUploadFildPath();//获取项目动态绝对路径
//            String filePath = path + "tempfiles\\" + filepath + ".xlsx";
//            filename = filename + DateUtils.getDate("_yyyy-MM-dd") + ".xlsx";
//
//            String value = FileWebUtils.fileDownLoad(request, response, filePath, filename, "application/ms-excel");
//            if (!StringUtils.isEmpty(value)) return value;
//            return null;
//        } catch (Exception ex) {
//            LogError.write("下载导出的Excel", LogEnum.Error, ex.toString());
//        }
//        return "下载Excel出错";
//    }

}


