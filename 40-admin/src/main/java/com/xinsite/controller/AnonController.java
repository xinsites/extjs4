package com.xinsite.controller;

import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.base.BaseController;
import com.xinsite.common.uitls.web.http.ServletUtils;
import com.xinsite.core.cache.SysConfigCache;
import com.xinsite.core.utils.user.ShiroUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 匿名操作
 * create by zhangxiaxin
 */
@RestController
@RequestMapping(value = "anon")
public class AnonController extends BaseController {
    /**
     * 用户是否注销
     */
    @RequestMapping(value = "islogoff", method = {RequestMethod.POST})
    public Map<String, Object> isLogoff(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap.put("success", ShiroUtils.isSessionOut());
            resultMap.put("msg", "Session超时,请重新登录！");
        } catch (Exception ex) {
            resultMap.put("success", false);
        }
        return resultMap;
    }

    //主页面加载文件
    @RequestMapping(value = "loadfile", method = {RequestMethod.POST})
    public Map<String, Object> loadFile(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int version = SysConfigCache.getFileLoadVersion();
        List<String> csslist = new ArrayList<>();
        csslist.add("javascript/plugins/my97/skin/WdatePicker.css");

        List<String> jslist = new ArrayList<>();
        ///////////ExtJs扩展组件
        jslist.add("javascript/extjs4/ux/Ext.ux.ComboSingle.js?t=" + version);   //下拉单选(模板)
        jslist.add("javascript/extjs4/ux/Ext.ux.ComboMulti.js?t=" + version);    //下拉多选(模板)
        jslist.add("javascript/extjs4/ux/Ext.ux.ComboTree.js?t=" + version);     //下拉树(插件)
        jslist.add("javascript/extjs4/ux/Ext.ux.DefinePicker.js?t=" + version);  //自定义下拉框(插件)
        jslist.add("javascript/extjs4/ux/Ext.ux.DefineTrigger.js?t=" + version); //自定义弹出框(插件)
        jslist.add("javascript/extjs4/ux/Ext.ux.PickerColor.js?t=" + version);   //颜色选择器
        jslist.add("javascript/extjs4/ux/Ext.ux.PagesizeSlider.js?t=" + version); //拖动改变分页数(插件)
        jslist.add("javascript/extjs4/ux/Ext.ux.DataTip.js?t=" + version);        //Grid编辑行时提示
        jslist.add("javascript/extjs4/ux/Ext.ux.ColumnCheckDefine.js?t=" + version);    //复选框组列(每列复选框不同)
        jslist.add("javascript/extjs4/ux/Ext.ux.ColumnCheckGroup.js?t=" + version);    //Grid编辑列复选框组
        jslist.add("javascript/extjs4/ux/Ext.ux.ColumnRadioGroup.js?t=" + version);    //Grid编辑列单选框组
        jslist.add("javascript/extjs4/ux/Ext.ux.DateTimeField.js?t=" + version);    //日期+时分秒组件
        jslist.add("javascript/extjs4/ux/Ext.ux.CascadeTree.js?t=" + version);     //级联选择树
        jslist.add("javascript/extjs4/ux/Ext.ux.TabScrollerMenu.js?t=" + version);  //选项卡溢出右边加菜单
        jslist.add("javascript/extjs4/swfUpload/swfupload.js?t=" + version);   //多文件上传控件
        jslist.add("javascript/extjs4/swfUpload/uploadpanel.js?t=" + version);
        jslist.add("javascript/plugins/ueditor/lang/zh-cn/zh-cn.js");
//        jslist.add("javascript/plugins/kindeditor/lang/zh_CN.js");
        jslist.add("javascript/extjs4/locale/ext-lang-zh_CN.js");

        ///////////ExtJs公共组件
        jslist.add("javascript/extjs4/ext.extend.js?t=" + version);  //表单自定义验证
        jslist.add("javascript/itemclick.js?t=" + version);          //栏目点击加载
        jslist.add("javascript/common/data_store.js?t=" + version);  //各下拉列表的store
        jslist.add("javascript/common/design_build.js?t=" + version); //表单设计(模板生成的动态表单通用方法)
        jslist.add("javascript/common/listeners.js?t=" + version);

        ///////////ExtJs封装第三方输入控件
        jslist.add("javascript/extjs4/ux/field/My97Date.js");
        jslist.add("javascript/extjs4/ux/field/UEditor.js");
        jslist.add("javascript/extjs4/ux/field/KindEditor.js");

        resultMap.put("state", true);
        resultMap.put("cssfiles", csslist);
        resultMap.put("jsfiles", jslist);
        return resultMap;
    }

    //登录时加载文件
    @RequestMapping(value = "cachefile", method = {RequestMethod.POST})
    public Map<String, Object> cacheFile(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int version = SysConfigCache.getFileLoadVersion();
        List<String> csslist = new ArrayList<>();
        csslist.add("javascript/plugins/my97/skin/WdatePicker.css");

        List<String> jslist = new ArrayList<>();
        ///////////ExtJs公共组件
        jslist.add("javascript/extjs4/ext.extend.js?t=" + version);  //表单自定义验证
        jslist.add("javascript/itemclick.js?t=" + version);         //系统栏目点击加载
        jslist.add("javascript/common/data_store.js?t=" + version);        //各下拉列表的store
        jslist.add("javascript/common/design_build.js?t=" + version);      //表单设计(模板生成的动态表单通用方法)
        jslist.add("javascript/common/listeners.js?t=" + version);

        ///////////ExtJs封装第三方输入控件
        jslist.add("javascript/extjs4/ux/field/My97Date.js");
        jslist.add("javascript/extjs4/ux/field/UEditor.js");
        jslist.add("javascript/extjs4/ux/field/KindEditor.js");


        resultMap.put("state", true);
        resultMap.put("cssfiles", csslist);
        resultMap.put("jsfiles", jslist);
        return resultMap;
    }
}
