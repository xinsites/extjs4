package com.xinsite.controller.info;

import com.google.gson.JsonArray;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.codec.EncodeUtils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.model.UploadModel;
import com.xinsite.core.utils.search.PageHelper;
import com.xinsite.core.bll.BLL_Object_Att;
import com.xinsite.core.utils.FileWebUtils;
import com.xinsite.core.utils.log.LogError;
import com.xinsite.core.utils.search.SearchUtils;
import com.xinsite.dal.dbhelper.DBFunction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-08-06
 * object name: 文件操作
 */

@RestController
@RequestMapping("info/file")
public class FileUploadController extends BaseController {

    //region 附件上传
    //文件上传
    @RequestMapping(value = "fileupload")
    public String fileUpload(HttpServletRequest request) {
        String upload_filetype = Global.getConfig("config.upload_filetype");
        try {
            String VisualPath = FileWebUtils.saveFile(request, upload_filetype, Global.getMaxFileSize());
            if (!StringUtils.isEmpty(VisualPath)) {
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return ret.getFailResult(ex.getMessage());
        }
        return ret.getFailResult();
    }

    //KindEditor富文本编辑器文件上传
    @RequestMapping(value = "kindfileupload")
    public String kindFileUpload(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        String upload_filetype = Global.getConfig("config.upload_filetype");
        try {
            String VisualPath = FileWebUtils.saveFile(request, upload_filetype, Global.getMaxFileSize());
            if (!StringUtils.isEmpty(VisualPath)) {
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
            return ret.getFailResult(ex.getMessage());
        }
        return ret.getFailResult();
    }
    //endregion

    //region 表单设计附件管理
    //多文件上传
    @RequestMapping(value = "swfupload")
    public String swfUpload(HttpServletRequest request) {
        String attach_add = "";
        int status = -4;
        int attach_id = 0;
        int item_id = getParaValue(request, "item_id", 0);
        int idleaf = getParaValue(request, "idleaf", 0);

        String upload_filetype = Global.getConfig("config.upload_filetype");
        try {
            UploadModel fileUpload = FileWebUtils.savefileupload(request, upload_filetype, Global.getMaxFileSize());
            if (!StringUtils.isEmpty(fileUpload.visualPath)) {
                attach_add = fileUpload.visualPath.replace("\\", "/");

                Map ht = new HashMap();
                ht.put("item_id", item_id);
                ht.put("idleaf", idleaf);
                ht.put("serialcode", 200000);

                ht.put("attach_name", fileUpload.fileName);
                ht.put("attach_add", attach_add);
                ht.put("attach_size", fileUpload.fileSize);
                ht.put("attach_type", fileUpload.extName);
                ht.put("create_time", DateUtils.getDateTime());
                attach_id = DBFunction.insertByTbName(ht, "tb_object_att");

                Hashtable jsn = new Hashtable();
                jsn.put("success", true);
                jsn.put("status", status);
                jsn.put("attach_id", attach_id);
                jsn.put("url", attach_add);
                return GsonUtils.toJson(jsn);
            }
        } catch (Exception ex) {
            String msg = ex.toString();
            if (msg.equals("文件上传失败，文件太大")) status = -7;
            else if (msg.equals("文件上传失败，文件类型不对")) status = -6;
            else status = -3;
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult("status", status);
    }

    // 附件列表
    @RequestMapping(value = "grid")
    public String grid(HttpServletRequest request) {
        int item_id = getParaValue(request, "item_id", 0);
        int idleaf = getParaValue(request, "idleaf", 0);
        String Ids = getParaValue(request, "ids", "");
        try {
            PageHelper pager = SearchUtils.getPageHelper(request);
            String where = StringUtils.format(" where item_id={0} and idleaf={1}", item_id, idleaf);
            if (item_id == 0 && idleaf == 0) {
                if (!StringUtils.isEmpty(Ids)) {
                    Ids = StringUtils.joinAsFilter(Ids);
                    where = StringUtils.format(" where attach_id in({0})", Ids);
                } else {
                    where = " where 1=0";
                }

            }
            pager.querySql = "select distinct PrimaryKey from tb_object_att " + where;
            pager.showColumns = "attach_id,serialcode,attach_name,attach_type,attach_size,attach_add";
            JsonArray array = pager.getAllGrid("serialcode");

            return retGrid.getGridJson(array, array.size());
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return retGrid.getFailResult();
    }

    // 修改附件名
    @RequestMapping(value = "rename")
    public String rename(HttpServletRequest request) {
        try {
            int id = getParaValue(request, "id", 0);
            String name = getParaValue(request, "name", "");
            if (!name.equals("")) {
                Map ht = new HashMap();
                ht.put("attach_name", name);
                DBFunction.updateByTbName(ht, "tb_object_att", "attach_id=" + id);
                return ret.getSuccessResult();
            }
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 删除附件
    @RequestMapping(value = "delete")
    public String delete(HttpServletRequest request) {
        String Ids = getParaValue(request, "ids", "");
        try {
            Ids = StringUtils.joinAsFilter(Ids);
            if (!StringUtils.isEmpty(Ids))
                DBFunction.deleteByTbNameIds("tb_object_att", "attach_id", Ids);
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }

    // 附件拖动排序
    @RequestMapping(value = "sort")
    public String sort(HttpServletRequest request) {
        try {
            String sort_vals = getParaValue(request, "sort_vals", "");
            if (!StringUtils.isEmpty(sort_vals)) {
                BLL_Object_Att.saveObjectAttSort(sort_vals);
            }
            return ret.getSuccessResult();
        } catch (Exception ex) {
            LogError.write(LogEnum.Error, ex.toString());
        }
        return ret.getFailResult();
    }
    //endregion

    //下载上传的附件
    @RequestMapping(value = "up/download", method = RequestMethod.GET)
    public Object downLoad(HttpServletRequest request, HttpServletResponse response) {
        String value = getParaValue(request, "value", "");
        try {
            if (!StringUtils.isEmpty(value)) {
                value = EncodeUtils.decodeBase64String(value);
                value = java.net.URLDecoder.decode(value, "utf-8");
                String[] strs = value.split("\\|\\|");
                String path = FileUtils.getUploadFildPath();
                if (strs.length == 2) {
                    value = FileWebUtils.fileDownLoad(request, response, path + strs[0], strs[1], "");
                    if (!StringUtils.isEmpty(value)) return value;
                } else if (value.indexOf("\\|\\|") == -1) {
                    String filename = value.substring(value.lastIndexOf("/"));
                    value = FileWebUtils.fileDownLoad(request, response, path + value, filename, "");
                    if (!StringUtils.isEmpty(value)) return value;
                }
            }
        } catch (Exception ex) {
            LogError.write("下载文件", LogEnum.Error, ex.toString());
        }
        return "下载文件出错";
    }

    //下载生成的附件
    @RequestMapping(value = "gen/download", method = RequestMethod.GET)
    public Object buildDownLoad(HttpServletRequest request, HttpServletResponse response) {
        String value = getParaValue(request, "value", "");
        try {
            if (!StringUtils.isEmpty(value)) {
                value = EncodeUtils.decodeBase64String(value);
                value = java.net.URLDecoder.decode(value, "utf-8");
                String[] strs = value.split("\\|\\|");
                if (strs.length == 2) {
                    String path = FileUtils.getRootPath();
                    value = FileWebUtils.fileDownLoad(request, response, path + strs[0], strs[1], "");
                    if (!StringUtils.isEmpty(value)) return value;
                }
            }
        } catch (Exception ex) {
            LogError.write("下载文件", LogEnum.Error, ex.toString());
        }
        return "下载文件出错";
    }
}


