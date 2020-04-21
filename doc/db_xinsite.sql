/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50721
Source Host           : 127.0.0.1:3306
Source Database       : db_xinsite_release

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2020-04-14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_code
-- ----------------------------
DROP TABLE IF EXISTS `sys_code`;
CREATE TABLE `sys_code` (
  `id`                int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `pid`               int(11)           DEFAULT NULL                  COMMENT '父结点Id',
  `codetype_id`       int(11)           DEFAULT NULL                  COMMENT '编码类型Id',
  `text`              varchar(80)       DEFAULT NULL                  COMMENT '编码名称',
  `value`             varchar(100)      DEFAULT NULL                  COMMENT '编码值',
  `expanded`          varchar(10)       DEFAULT NULL                  COMMENT '默认展开，true、false',
  `remark`            varchar(200)      DEFAULT NULL                  COMMENT '备注',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  `issys`             int(11)           DEFAULT '0'                   COMMENT '是否系统编码，1：不可删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 COMMENT='编码表';

-- ----------------------------
-- Records of sys_code
-- ----------------------------
INSERT INTO `sys_code` VALUES ('1', '0', '3', '男', 'male', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('2', '0', '3', '女', 'female', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('3', '0', '8', '博士', '22', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('4', '0', '6', '博士', '106', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('5', '0', '6', '硕士', '122', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('6', '0', '6', '学士', '123', 'false', '', '3', '0', '1');
INSERT INTO `sys_code` VALUES ('7', '0', '4', 'aaaa', 'zjzjg', 'true', '', '1', '0', '0');
INSERT INTO `sys_code` VALUES ('8', '0', '5', '总经理', 'zongjingli', 'false', '', '1', '0', '0');
INSERT INTO `sys_code` VALUES ('9', '0', '5', '总监', 'zongjian', 'false', '', '2', '0', '0');
INSERT INTO `sys_code` VALUES ('10', '0', '5', '经理', 'jingli', 'false', '', '3', '0', '0');
INSERT INTO `sys_code` VALUES ('11', '0', '5', '主管', 'zhuguan', 'false', '', '4', '0', '0');
INSERT INTO `sys_code` VALUES ('12', '0', '5', '办事员', 'banshiyuan', 'false', '', '5', '0', '0');
INSERT INTO `sys_code` VALUES ('13', '0', '5', '暂无', 'zanwu', 'false', '', '6', '0', '0');
INSERT INTO `sys_code` VALUES ('14', '0', '7', '常规部门', '1', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('15', '0', '7', '项目部门', '2', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('16', '0', '7', '销售部门', '3', 'false', '', '3', '0', '1');
INSERT INTO `sys_code` VALUES ('17', '0', '7', '研发部门', '4', 'false', '', '4', '0', '1');
INSERT INTO `sys_code` VALUES ('18', '0', '8', '登录日志', '1', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('19', '0', '8', '访问日志', '2', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('20', '0', '6', '国家机关', '1', 'false', '', '1', '0', '1');
INSERT INTO `sys_code` VALUES ('21', '0', '6', '房地产', '2', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('22', '0', '6', '服务业', '3', 'false', '', '3', '0', '1');
INSERT INTO `sys_code` VALUES ('23', '0', '6', '互联网', '4', 'false', '', '4', '0', '1');
INSERT INTO `sys_code` VALUES ('24', '0', '6', '金融业', '5', 'false', '', '5', '0', '1');
INSERT INTO `sys_code` VALUES ('25', '0', '6', '制造业', '6', 'false', '', '7', '0', '1');
INSERT INTO `sys_code` VALUES ('26', '0', '6', '其他行业', '7', 'false', '', '6', '0', '1');
INSERT INTO `sys_code` VALUES ('27', '0', '8', '操作日志', '3', 'false', '', '11', '0', '1');
INSERT INTO `sys_code` VALUES ('28', '0', '8', '异常日志', '4', 'false', '', '12', '0', '1');
INSERT INTO `sys_code` VALUES ('29', '7', '4', 'bbbbb', 'zjg', 'false', '', '1', '0', '0');
INSERT INTO `sys_code` VALUES ('30', '7', '4', 'ffffffff', 'zjc', 'true', '', '2', '0', '0');
INSERT INTO `sys_code` VALUES ('31', '7', '4', '其他单位', 'qtdw', 'false', '', '4', '0', '0');
INSERT INTO `sys_code` VALUES ('32', '29', '4', 'ccccccccc', 'stbz', 'false', '', '1', '0', '0');
INSERT INTO `sys_code` VALUES ('33', '29', '4', 'ddd', 'dyy', 'false', '', '2', '0', '0');
INSERT INTO `sys_code` VALUES ('34', '30', '4', 'e222dd', 'jl', 'false', '', '2', '0', '0');
INSERT INTO `sys_code` VALUES ('35', '30', '4', 'ade33', 'jc', 'false', '', '1', '0', '0');
INSERT INTO `sys_code` VALUES ('36', '0', '9', '是', 'yes', 'false', '', '13', '0', '1');
INSERT INTO `sys_code` VALUES ('37', '0', '9', '否', 'no', 'false', '', '14', '0', '1');
INSERT INTO `sys_code` VALUES ('38', '0', '8', '硕士', '33', 'false', '', '2', '0', '1');
INSERT INTO `sys_code` VALUES ('39', '0', '8', '本科', 'bk', 'false', '', '3', '0', '1');
INSERT INTO `sys_code` VALUES ('40', '0', '8', '大专', 'dz', 'false', '', '4', '0', '1');
INSERT INTO `sys_code` VALUES ('41', '0', '8', '高中', 'gz', 'false', '', '5', '0', '1');
INSERT INTO `sys_code` VALUES ('42', '30', '4', 'tes', 'q', 'false', '', '4', '0', '0');
INSERT INTO `sys_code` VALUES ('43', '30', '4', 'ww', 'e', 'false', 'ew', '3', '0', '0');

-- ----------------------------
-- Table structure for sys_codetype
-- ----------------------------
DROP TABLE IF EXISTS `sys_codetype`;
CREATE TABLE `sys_codetype` (
  `id`                int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `pid`               int(11)           DEFAULT NULL                  COMMENT '父结点',
  `name`              varchar(50)       DEFAULT NULL                  COMMENT '类型名称',
  `code_type`         varchar(10)       DEFAULT NULL                  COMMENT '应用所在地，app：应用编码；sys：系统编码',
  `data_key`          varchar(20)       DEFAULT NULL                  COMMENT '数据源标识，唯一码',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '机构号',
  `istree`            int(11)           DEFAULT '0'                   COMMENT '是否树形编码',
  `expanded`          varchar(10)       DEFAULT 'false'               COMMENT '是否展开，true、false',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  `ispublic`          int(11)           DEFAULT '0'                   COMMENT '是否公共编码，是所有机构共用',
  `issys`             int(11)           DEFAULT '0'                   COMMENT '是否系统编码，1：不可以删除',
  `code_deleted`      int(11)           NOT NULL DEFAULT '0'          COMMENT '1对应的编码 都不可以删除，特殊应用',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='编码类型表';

-- ----------------------------
-- Records of sys_codetype
-- ----------------------------
INSERT INTO `sys_codetype` VALUES ('1', '0', '系统编码', 'sys', 'sys.code', '1', '0', 'false', '0', '1', '1', '0', '1');
INSERT INTO `sys_codetype` VALUES ('2', '0', '应用编码', 'sys', 'apply.code', '1', '0', 'true', '0', '0', '0', '0', '2');
INSERT INTO `sys_codetype` VALUES ('3', '1', '性别', 'app', 'code.sex', '1', '0', 'false', '0', '1', '1', '0', '2');
INSERT INTO `sys_codetype` VALUES ('4', '2', '工作单位', 'app', 'work.company', '1', '1', 'false', '0', '0', '0', '0', '3');
INSERT INTO `sys_codetype` VALUES ('5', '2', '工作职务', 'app', 'work.post', '1', '0', 'false', '0', '0', '0', '0', '2');
INSERT INTO `sys_codetype` VALUES ('6', '1', '公司性质', 'sys', 'company.type', '1', '0', 'false', '0', '1', '0', '0', '1');
INSERT INTO `sys_codetype` VALUES ('7', '1', '部门类型', 'sys', 'dept.type', '1', '0', 'false', '0', '1', '0', '0', '1');
INSERT INTO `sys_codetype` VALUES ('8', '1', '日志类型', 'sys', 'log.type', '1', '0', 'false', '0', '1', '1', '0', '4');
INSERT INTO `sys_codetype` VALUES ('9', '1', '是否', 'app', 'code.yes.no', '1', '0', 'false', '0', '1', '1', '0', '1');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id`                int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '机构号Id',
  `item_id`           int(11)           DEFAULT NULL                  COMMENT '栏目菜单Id',
  `field_explain`     varchar(50)       DEFAULT NULL                  COMMENT '变量描述',
  `config_key`        varchar(50)       DEFAULT NULL                  COMMENT '变量名称',
  `config_value`      varchar(1000)     DEFAULT NULL                  COMMENT '变量值',
  `config_text`       varchar(1000)     DEFAULT NULL                  COMMENT '变量文本描述，如下拉框时的文本值',
  `config_editor`     varchar(2000)     DEFAULT NULL                  COMMENT '输入框配置参数',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `modify_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建修改时间',
  `issys`             int(11)           NOT NULL DEFAULT '0'          COMMENT '1：系统变量；0：用户变量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='系统参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', '1', '18', '√默认分页数', 'page_size', '40', '', '{   xtype: \'numberfield\',   name: \'page_size\',   allowBlank: false,   maxLength: 8,   minValue: 1,  maxValue: 100,   step: 2,   decimalPrecision: 0 }', '3', '2019-11-09 22:30:16', '1');
INSERT INTO `sys_config` VALUES ('2', '1', '18', '√新增用户默认密码', 'add_password', '111111', '', '{   xtype: \'textfield\',   name: \'add_password\', allowBlank: false,   maxLength: 16,   minLength: 6 }', '4', '2017-11-23 00:47:10', '1');
INSERT INTO `sys_config` VALUES ('3', '1', '18', '√登录错误次数(0不限制)', 'login_errors', '3', '', '{   xtype: \'numberfield\',   name: \'login_errors\',   allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 5,   step: 1,   decimalPrecision: 0 }', '1', '2019-11-30 01:58:00', '1');
INSERT INTO `sys_config` VALUES ('4', '1', '18', '√历史数据分表记录数(万)', 'subtable_records', '102', '', '{   xtype: \'numberfield\',   name: \'subtable_records\',  allowBlank: false,   maxLength: 8,   minValue: 10,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '6', '2019-11-23 17:42:02', '1');
INSERT INTO `sys_config` VALUES ('6', '1', '18', '√用户重置密码', 'reset_password', '111111', '', '{   xtype: \'textfield\',   name: \'reset_password\',  allowBlank: false,   maxLength: 16,   minLength: 6 }', '4', '2019-11-22 01:35:00', '1');
INSERT INTO `sys_config` VALUES ('7', '1', '18', '系统logo图标(72*72)', 'logo_icon', '555', '', '{   xtype: \'textfield\',   name: \'logo_icon\',   allowBlank: true,   maxLength: 50 }', '14', '2019-11-30 03:32:07', '1');
INSERT INTO `sys_config` VALUES ('8', '1', '18', '系统默认皮肤', 'default_skin', 'dsdss', '', '{   xtype: \'textfield\',   name: \'default_skin\',    allowBlank: true,   maxLength: 25 }', '15', '2019-11-30 03:32:13', '1');
INSERT INTO `sys_config` VALUES ('9', '1', '18', '√登录错误锁定时间(最多60分钟)', 'login_locked', '20', '', '{   xtype: \'numberfield\',   name: \'login_locked\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 60,   step: 1,   decimalPrecision: 0 }', '2', '2019-11-30 02:03:51', '1');
INSERT INTO `sys_config` VALUES ('10', '1', '18', '√导出Excel最大记录数', 'excel_max_count', '10000', '', '{   xtype: \'numberfield\',   name: \'login_locked\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '10', '2019-11-19 18:52:05', '1');
INSERT INTO `sys_config` VALUES ('11', '0', '18', '默认分页数', 'page_size', '40', '', '{   xtype: \'numberfield\',   name: \'page_size\',  allowBlank: false,   maxLength: 8,   minValue: 1,   maxValue: 100,   step: 2,   decimalPrecision: 0 }', '3', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('12', '0', '18', '新增用户默认密码', 'add_password', '111111', '', '{   xtype: \'textfield\',   name: \'add_password\',  allowBlank: false,   maxLength: 16,   minLength: 6 }', '3', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('13', '0', '18', '登录错误次数(0不限制)', 'login_errors', '4', '', '{   xtype: \'numberfield\',   name: \'login_errors\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 5,   step: 1,   decimalPrecision: 0 }', '5', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('14', '0', '18', '历史数据分表记录数(万)', 'subtable_records', '100', '', '{   xtype: \'numberfield\',   name: \'subtable_records\', allowBlank: false,   maxLength: 8,   minValue: 10,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '6', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('16', '0', '18', '用户重置密码', 'reset_password', '111111', '', '{   xtype: \'textfield\',   name: \'reset_password\',  allowBlank: false,   maxLength: 16,   minLength: 6 }', '4', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('17', '0', '18', '系统logo图标(72*72)', 'logo_icon', 'dds', '', '{   xtype: \'textfield\',   name: \'logo_icon\',  allowBlank: true,   maxLength: 50 }', '7', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('18', '0', '18', '系统默认皮肤', 'default_skin', 'dd', '', '{   xtype: \'textfield\',   name: \'default_skin\',   allowBlank: true,   maxLength: 25 }', '8', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('19', '0', '18', '√登录错误锁定时间(最多60分钟)', 'login_locked', '32', '', '{   xtype: \'numberfield\',   name: \'login_locked\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 60,   step: 1,   decimalPrecision: 0 }', '1', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('20', '0', '18', '导出Excel最大记录数', 'excel_max_count', '10000', '', '{   xtype: \'numberfield\',   name: \'login_locked\',   allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '10', '2019-04-16 16:28:56', '1');
INSERT INTO `sys_config` VALUES ('21', '1', '43', '分栏目，便于分权限给不同人员', 'key_1', 'abceee', '', '{   xtype: \'textfield\',   name: \'default_skin\',  allowBlank: true,   maxLength: 25 }', '1', '2019-10-30 01:09:40', '0');
INSERT INTO `sys_config` VALUES ('22', '1', '18', '√固定栏目到面板最大数目', 'max_fixed_tabs', '3', '', '{   xtype: \'numberfield\',   allowBlank: false,   maxLength: 8,   minValue: 1,   maxValue: 5,   step: 1,   decimalPrecision: 0 }', '10', '2020-01-04 00:29:11', '1');
INSERT INTO `sys_config` VALUES ('23', '1', '18', '√同一用户同时登录的平台数(0不限制)', 'max_session', '3', '', '{   xtype: \'numberfield\',   allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 100,   step: 1,   decimalPrecision: 0 }', '11', '2019-11-21 23:00:04', '1');
INSERT INTO `sys_config` VALUES ('24', '1', '43', '[时效]审批任务时效提醒邮件模板', 'alloc_warn_email', '#UserName您好！\n       #ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，过期未审批流程失效！\n\n                                                                        日期：#Today', '', '{ xtype:\'textareafield\', itemId: \'alloc_warn_email\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'#UserName您好！\\n            #ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，过期未审批流程失效！\\n日期：#Today\' }', '2', '2019-07-13 11:24:23', '0');
INSERT INTO `sys_config` VALUES ('25', '1', '43', '[时效]审批任务时效提醒短信模板', 'alloc_warn_mobile', '【XinSite】#ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，过期未审批流程失效！', '', '{ xtype:\'textareafield\', itemId: \'alloc_warn_mobile\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'【明日科技】#ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，过期未审批流程失效！\' }', '3', '2019-10-30 01:09:37', '0');
INSERT INTO `sys_config` VALUES ('26', '1', '43', '[预警]审批失效提前预警邮件模板', 'overtime_email', '#UserName您好！\n       #ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，还剩#OverTime小时，过期未审批流程失效！\n\n                                                                        日期：#Today', '', '{ xtype:\'textareafield\', itemId: \'overtime_email\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'#UserName您好！\\n            #ApplyUser提交的申请#Title，所处审批环节#TaskName到期时间是#ExpireTime，过期未审批流程失效！\\n日期：#Today\' }', '4', '2019-07-05 06:29:33', '0');
INSERT INTO `sys_config` VALUES ('27', '1', '43', '[预警]审批失效提前预警短信模板', 'overtime_mobile', '【XinSite】#ApplyUser提交的申请#Title，所处审批环节#TaskName，到期时间是#ExpireTime，需要您的审批，还剩#OverTime小时，过期未审批流程失效！', '', '{ xtype:\'textareafield\', itemId: \'overtime_mobile\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'【明日科技】#ApplyUser提交的申请#Title，所处审批环节#TaskName到期时间是#ExpireTime，过期未审批流程失效！\' }', '5', '2019-07-05 06:29:55', '0');
INSERT INTO `sys_config` VALUES ('28', '1', '43', '[完成]审批任务完成提醒邮件模板', 'task_finish_email', '#UserName您好！\n            #ApplyUser提交的申请#Title，#TaskName审批环节已经审批通过，特此提醒！\n\n                                                                   日期：#Today', '', '{ xtype:\'textareafield\', itemId: \'task_finish_email\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'#UserName您好！\\n            #ApplyUser提交的申请#Title，审批环节#TaskName已经审批通过，特此提醒！\\n日期：#Today\' }', '6', '2019-07-05 06:30:35', '0');
INSERT INTO `sys_config` VALUES ('29', '1', '43', '[完成]审批任务完成提醒短信模板', 'task_finish_mobile', '【XinSite】#ApplyUser提交的申请#Title，#TaskName审批环节已经审批通过，特此提醒！', '', '{ xtype:\'textareafield\', itemId: \'task_finish_mobile\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'【明日科技】#ApplyUser提交的申请#Title，审批环节#TaskName已经审批通过，特此提醒！\' }', '7', '2019-07-05 06:30:46', '0');
INSERT INTO `sys_config` VALUES ('30', '1', '43', '[撤销]审批撤销邮件模板，已经发出的时效、预警、完成更正提醒', 'revoke_warn_email', '#UserName您好！\n            #ApplyUser提交的申请#Title，由于审批被审批人撤销，目前在#TaskName审批环节，流程状态为#TaskStatus，特此提醒！\n\n                                                                   日期：#Today', '', '{ xtype:\'textareafield\', itemId: \'revoke_warn_email\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'#UserName您好！\\n            #ApplyUser提交的申请#Title，#TaskName审批环节已经被撤销，目前审批环节为#AllocStatus状态，流程状态为#TaskStatus，特此提醒！\\n日期：#Today\' }', '8', '2019-07-15 12:09:53', '0');
INSERT INTO `sys_config` VALUES ('31', '1', '43', '[撤销]审批撤销短信模板，已经发出的时效、预警、完成更正提醒', 'revoke_warn_mobile', '【XinSite】#ApplyUser提交的申请#Title，由于审批被审批人撤销，目前在#TaskName审批环节，流程状态为#TaskStatus，特此提醒！', '', '{ xtype:\'textareafield\', itemId: \'revoke_warn_mobile\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'【明日科技】#ApplyUser提交的申请#Title，#TaskName审批环节已经被撤销，目前审批环节为#AllocStatus状态，流程状态为#TaskStatus，特此提醒！\' }', '9', '2019-07-15 12:10:29', '0');
INSERT INTO `sys_config` VALUES ('32', '1', '43', '[回退]审批回退邮件模板，已经发出的时效、预警、完成更正提醒', 'back_warn_email', '#UserName您好！\n            #ApplyUser提交的申请#Title，由于审批人的退回操作，目前在#TaskName审批环节，流程状态为#TaskStatus，特此提醒！\n\n                                                                   日期：#Today', '', '{ xtype:\'textareafield\', itemId: \'back_warn_email\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'#UserName您好！\\n            #ApplyUser提交的申请#Title，#TaskName审批环节已经被回退，目前审批环节为#AllocStatus状态，流程状态为#TaskStatus，特此提醒！\\n日期：#Today\' }', '10', '2019-07-15 12:18:22', '0');
INSERT INTO `sys_config` VALUES ('33', '1', '43', '[回退]审批回退短信模板，已经发出的时效、预警、完成更正提醒', 'back_warn_mobile', '【XinSite】#ApplyUser提交的申请#Title，由于审批人的退回操作，目前在#TaskName审批环节，流程状态为#TaskStatus，特此提醒！', '', '{ xtype:\'textareafield\', itemId: \'back_warn_mobile\',allowBlank: true,flex: 1,width: 500,height: 360,margin: \"0\", maxLength: 500,emptyText: \'【明日科技】#ApplyUser提交的申请#Title，#TaskName审批环节已经被回退，目前审批环节为#AllocStatus状态，流程状态为#TaskStatus，特此提醒！\' }', '11', '2019-07-15 12:18:27', '0');
INSERT INTO `sys_config` VALUES ('34', '2', '18', '默认分页数', 'page_size', '40', '', '{   xtype: \'numberfield\',   name: \'page_size\',   allowBlank: false,   maxLength: 8,   minValue: 1,   maxValue: 100,   step: 2,   decimalPrecision: 0 }', '2', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('35', '2', '18', '新增用户默认密码', 'add_password', '111111', '', '{   xtype: \'textfield\',   name: \'add_password\',  allowBlank: false,   maxLength: 16,   minLength: 6 }', '3', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('36', '2', '18', '登录错误次数(0不限制)', 'login_errors', '4', '', '{   xtype: \'numberfield\',   name: \'login_errors\',   allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 5,   step: 1,   decimalPrecision: 0 }', '5', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('37', '2', '18', '历史数据分表记录数(万)', 'subtable_records', '100', '', '{   xtype: \'numberfield\',   name: \'subtable_records\',  allowBlank: false,   maxLength: 8,   minValue: 10,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '6', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('38', '2', '18', '菜单栏目默认宽度', 'leftmenu_width', '300', '', '{   xtype: \'numberfield\',   name: \'leftmenu_width\',   allowBlank: false,   maxLength: 8,   minValue: 1,   maxValue: 500,   step: 5 }', '9', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('39', '2', '18', '用户重置密码', 'reset_password', '111111', '', '{   xtype: \'textfield\',   name: \'reset_password\',  allowBlank: false,   maxLength: 16,   minLength: 6 }', '4', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('40', '2', '18', '系统logo图标(72*72)', 'logo_icon', 'dds', '', '{   xtype: \'textfield\',   name: \'logo_icon\',   allowBlank: false,   maxLength: 50 }', '7', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('41', '2', '18', '系统默认皮肤', 'default_skin', 'dd', '', '{   xtype: \'textfield\',   name: \'default_skin\',   allowBlank: false,   maxLength: 25 }', '8', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('42', '2', '18', '登录错误锁定时间(分钟)', 'login_locked', '32', '', '{   xtype: \'numberfield\',   name: \'login_locked\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 60,   step: 1,   decimalPrecision: 0 }', '1', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('43', '2', '18', '导出Excel最大记录数', 'excel_max_count', '10000', '', '{   xtype: \'numberfield\',   name: \'login_locked\',  allowBlank: false,   maxLength: 8,   minValue: 0,   maxValue: 10000,   step: 1,   decimalPrecision: 0 }', '10', '2019-10-21 16:44:41', '0');
INSERT INTO `sys_config` VALUES ('44', '1', '18', '√前端静态文件变更版本号(版本更新使用)', 'file_load_version', '2', '', '{   xtype: \'numberfield\',   allowBlank: false,   maxLength: 8,   minValue: 0,    step: 1,   decimalPrecision: 0 }', '12', '2020-04-13 03:21:10', '1');
INSERT INTO `sys_config` VALUES ('46', '1', '18', '系统变量1', 'config_key1', '5', '', '{\n  xtype: \'numberfield\',\n  name: \'login_errors\',\n  allowBlank: false,\n  maxLength: 8,\n  minValue: 0,\n  maxValue: 5,\n  step: 1,\n  decimalPrecision: 0\n}', '16', '2019-11-30 03:24:20', '0');
INSERT INTO `sys_config` VALUES ('47', '1', '18', '√右上导航栏目可固定到面板，多个栏目“,”分隔', 'bar_remind_items', '5', '', '{\n  xtype: \'textfield\',\n  allowBlank: true,\n  maxLength: 50\n}', '13', '2020-03-30 21:10:15', '1');

-- ----------------------------
-- Table structure for sys_datashow
-- ----------------------------
DROP TABLE IF EXISTS `sys_datashow`;
CREATE TABLE `sys_datashow` (
  `id`                bigint(20)        NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `data_type`         varchar(10)       DEFAULT NULL                  COMMENT '数据源类型，code:编码表；datasource：系统源',
  `data_key`          varchar(50)       DEFAULT NULL                  COMMENT '数据源标识',
  `data_id`           bigint(20)        DEFAULT NULL                  COMMENT '数据源表id，编码表是sys_code，系统数据源表都有可能',
  `disabled`          varchar(10)       DEFAULT NULL                  COMMENT '是否可以选择',
  `isshow`            int(11)           DEFAULT '1'                   COMMENT '是否显示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='编码表是否显示可用辅助表';

-- ----------------------------
-- Records of sys_datashow
-- ----------------------------
INSERT INTO `sys_datashow` VALUES ('1', 'code', 'work.post', '12', 'disabled', '1');

-- ----------------------------
-- Table structure for sys_datasource
-- ----------------------------
DROP TABLE IF EXISTS `sys_datasource`;
CREATE TABLE `sys_datasource` (
  `id`                int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `pid`               int(11)           DEFAULT '0'                   COMMENT '父结点',
  `data_name`         varchar(50)       DEFAULT NULL                  COMMENT '数据源名称',
  `code_type`         varchar(10)       DEFAULT NULL                  COMMENT '应用所在地，app：应用编码；sys：系统编码',
  `data_key`          varchar(50)       DEFAULT NULL                  COMMENT '数据源标识，唯一码，默认ds.开头与sys_codetype区分',
  `data_type`         varchar(10)       DEFAULT NULL                  COMMENT 'tree：树列表；combo：下拉列表',
  `data_page`         varchar(10)       DEFAULT NULL                  COMMENT '下拉显示类别，page：分页；all：所有；level：逐层加载',
  `table_name`        varchar(30)       DEFAULT NULL                  COMMENT '数据源主表名',
  `primary_key`       varchar(20)       DEFAULT NULL                  COMMENT '数据源主表主键',
  `parent_field`      varchar(20)       DEFAULT NULL                  COMMENT '数据源主表父结点',
  `query_field`       varchar(100)      DEFAULT NULL                  COMMENT '条件筛选的字段，可以多个，第一个是文本字段',
  `query_sql`         varchar(500)      DEFAULT NULL                  COMMENT '查询语句，默认别名a1',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='系统数据源配置表';

-- ----------------------------
-- Records of sys_datasource
-- ----------------------------
INSERT INTO `sys_datasource` VALUES ('1', '0', '用户列表', 'app', 'ds.sys.user', 'combo', 'page', 'sys_user', 'user_id', null, 'user_name,login_name', 'select a1.user_id id,a1.user_id value,0 pid,a1.user_name text,a1.user_name name,a1.login_name,\'true\' leaf,\'\' disabled,1 enabled,1 isshow\r\nfrom sys_user a1 where a1.isdel=0 and a1.org_id={org_id}{where} order by a1.serialcode desc,a1.create_time desc', '1', '0');
INSERT INTO `sys_datasource` VALUES ('2', '0', '组织机构', 'app', 'ds.sys.org', 'tree', 'all', 'sys_organize', 'org_id', 'pid', 'company_name,short_name', 'select a1.org_id id,a1.pid,a1.company_name text,\'\' disabled,1 enabled,1 isshow,\r\ncase when (select count(1) from sys_organize b1 where b1.isdel=0 and b1.pid=a1.org_id)>0 then \'false\' else \'true\' end leaf\r\nfrom sys_organize a1 where a1.isdel=0{where} order by a1.pid,a1.serialcode', '2', '1');
INSERT INTO `sys_datasource` VALUES ('3', '0', '部门管理', 'app', 'ds.sys.dept', 'tree', 'all', 'sys_dept', 'dept_id', 'pid', 'dept_name', 'select a1.dept_id id,a1.pid,a1.dept_name text,a1.dept_name name,\'\' disabled,1 enabled,1 isshow,\r\ncase when (select count(1) from sys_dept b1 where b1.isdel=0 and b1.org_id={org_id} and b1.pid=a1.dept_id)>0 then \'false\' else \'true\' end leaf\r\nfrom sys_dept a1 where a1.isdel=0 and a1.org_id={org_id}{where} order by a1.pid,a1.serialcode', '2', '0');
INSERT INTO `sys_datasource` VALUES ('4', '0', '角色管理', 'app', 'ds.sys.role', 'combo', 'page', 'sys_role', 'role_id', null, 'role_name', 'select a1.role_id id,a1.role_id value,0 pid,a1.role_name text,a1.role_name name,\'true\' leaf,\'\' disabled,1 enabled,1 isshow\r\nfrom sys_role a1 where a1.isdel=0 and a1.org_id={org_id}{where} order by a1.serialcode asc,a1.create_time desc', '3', '0');
INSERT INTO `sys_datasource` VALUES ('5', '0', '栏目管理', 'app', 'ds.sys.item', 'tree', 'level', 'sys_menu', 'item_id', 'pid', 'item_name', 'select a1.item_id id,a1.pid,a1.item_name text,a1.expanded expand,\'\' disabled,1 enabled,1 isshow,\r\ncase when (select count(1) from sys_menu b1 where b1.isdel=0 and (b1.org_id=0 or b1.org_id={org_id}) and b1.pid=a1.item_id)>0 then \'false\' else \'true\' end leaf\r\nfrom sys_menu a1 where a1.isdel=0 and a1.isused=1 and (a1.org_id=0 or a1.org_id={org_id}){where} order by a1.pid,a1.item_sort', '7', '0');
INSERT INTO `sys_datasource` VALUES ('6', '0', '设计表对象', 'sys', 'ds.design.object', 'tree', 'all', 'tb_gen_object', 'oid', 'pid', 'object_name', 'select a1.oid id,a1.pid,a1.object_name text,\'\' disabled,1 enabled,1 isshow,\r\ncase when (select count(1) from tb_gen_object b1 where b1.pid=a1.oid)>0 then \'false\' else \'true\' end leaf\r\nfrom tb_gen_object a1 where a1.object_type!=\'trial\'{where} order by a1.pid asc,a1.serialcode', '8', '0');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id`           int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `pid`               int(11)           DEFAULT NULL                  COMMENT '父结点Id',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '机构号',
  `dept_name`         varchar(100)      DEFAULT NULL                  COMMENT '部门名称',
  `dept_short_name`   varchar(100)      DEFAULT NULL                  COMMENT '部门简称',
  `dept_type`         varchar(100)      DEFAULT NULL                  COMMENT '部门类型',
  `dept_code`         varchar(50)       DEFAULT NULL                  COMMENT '部门编号',
  `dept_phone`        varchar(20)       DEFAULT NULL                  COMMENT '电话号码',
  `dept_fax`          varchar(50)       DEFAULT NULL                  COMMENT '传真',
  `dept_remark`       varchar(200)      DEFAULT NULL                  COMMENT '备注',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time`       datetime          DEFAULT NULL                  COMMENT '修改时间',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('1', '0', '1', '管理部', '管理部', '1', '1000.01', '', '', '', '1', '2017-12-21 23:38:09', '2019-12-05 03:48:20', '0');
INSERT INTO `sys_dept` VALUES ('2', '4', '1', '市场部', '市场部', '1', '1000.05.02', '', '', '', '2', '2018-10-27 23:31:43', '2019-04-07 02:21:24', '0');
INSERT INTO `sys_dept` VALUES ('3', '0', '1', '品质部', '品质部', '1', '1000.02', '', '', '', '2', '2017-12-22 00:02:19', '2019-04-07 02:04:18', '0');
INSERT INTO `sys_dept` VALUES ('4', '0', '1', '业务部', '业务部', '1', '1000.05', '', '', '', '5', '2017-12-22 00:09:09', '2019-04-07 02:05:09', '0');
INSERT INTO `sys_dept` VALUES ('5', '0', '1', '其他部门', '', '1', '1000.09', '', '', '', '6', '2018-10-27 23:43:45', '2018-10-27 23:43:45', '0');
INSERT INTO `sys_dept` VALUES ('6', '4', '1', '销售部', '销售部', '1', '1000.05.01', '', '', '', '1', '2019-04-07 02:21:05', '2019-04-07 02:21:16', '0');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `log_id`            int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '机构号',
  `log_ip`            varchar(50)       DEFAULT NULL                  COMMENT 'Ip地址',
  `log_type`          int(11)           DEFAULT NULL                  COMMENT '日志类型',
  `log_fun`           varchar(200)      DEFAULT NULL                  COMMENT '系统功能',
  `log_result`        varchar(30)       DEFAULT NULL                  COMMENT '执行结果',
  `log_message`       varchar(1000)     DEFAULT NULL                  COMMENT '日志信息描述',
  `action_type`       varchar(30)       DEFAULT NULL                  COMMENT '操作类型',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id`           int(11)           DEFAULT NULL                  COMMENT '操作用户Id',
  `login_name`        varchar(100)      DEFAULT NULL                  COMMENT '操作用户登录名',
  `dept_id`           int(11)           DEFAULT NULL                  COMMENT '操作用户部门号',
  `serialcode`        int(11)           DEFAULT '0'                   COMMENT '排序号',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '0：未删除；1：删除',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统日志表';

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_member
-- ----------------------------
DROP TABLE IF EXISTS `sys_member`;
CREATE TABLE `sys_member` (
  `table_name`        varchar(50)       NOT NULL                      COMMENT '所属成员所在表名',
  `table_id`          int(11)           NOT NULL                      COMMENT '所在表主键',
  `user_id`           int(11)           NOT NULL                      COMMENT '成员用户Id',
  `man_type`          int(11)           NOT NULL                      COMMENT '1:领导; 负责人:2; 成员:3',
  PRIMARY KEY (`table_name`,`table_id`,`user_id`,`man_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='成员辅助记录表';

-- ----------------------------
-- Records of sys_member
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `item_id`           int(11)           NOT NULL AUTO_INCREMENT       COMMENT '菜单栏目Id',
  `pid`               int(11)           DEFAULT NULL                  COMMENT '父结点Id',
  `org_id`            int(11)           DEFAULT '0'                   COMMENT '机构号',
  `item_name`         varchar(100)      DEFAULT NULL                  COMMENT '栏目名称',
  `item_sort`         int(11)           DEFAULT NULL                  COMMENT '排序号',
  `item_type`         varchar(20)       DEFAULT NULL                  COMMENT '栏目类型(method、page、list)',
  `item_method`       varchar(50)       DEFAULT NULL                  COMMENT '栏目地址或者页面创建方法',
  `open_type`         varchar(20)       DEFAULT NULL                  COMMENT '打开方式(winediting、tabediting）',
  `per_value`         varchar(50)       DEFAULT NULL                  COMMENT '栏目控制器列表权限值 ，页面没有列表权限值为空',
  `iconcls`           varchar(20)       DEFAULT NULL                  COMMENT '栏目树图标',
  `expanded`          varchar(10)       DEFAULT NULL                  COMMENT 'true：树加载父目录默认打开；false：父目录不打开',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  `isused`            int(11)           DEFAULT '0'                   COMMENT '是否使用，0：不使用；1：使用',
  `isdataper`         int(11)           DEFAULT '0'                   COMMENT '0：无列表数据权限；1：有数据权限',
  `ishistory`         int(11)           DEFAULT '0'                   COMMENT '1：新增修改保存到历史记录；0：不记录',
  `isrecycle`         int(11)           DEFAULT '0'                   COMMENT '1：信息删除后进入回收站；0：信息删除后不可恢复',
  `isfun`             int(11)           DEFAULT '1'                   COMMENT '1：有栏目功能；0：无',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time`       datetime          DEFAULT NULL                  COMMENT '修改时间',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8 COMMENT='系统菜单栏目表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '0', '系统管理', '6', 'list', '', null, null, 'icon_list', 'false', '0', '1', '0', '0', '0', '0', '2018-08-30 00:14:46', '2020-03-25 01:09:12');
INSERT INTO `sys_menu` VALUES ('2', '0', '0', '系统监控', '5', 'list', '', null, null, 'icon_list', 'false', '0', '1', '0', '0', '0', '0', '2019-11-28 03:37:11', '2019-12-02 01:15:52');
INSERT INTO `sys_menu` VALUES ('5', '0', '0', '待办任务', '1', 'method', 'itemClick_WaitTask(tree,record)', 'tabediting', '', '', 'false', '1', '1', '0', '0', '0', '0', '2019-03-22 18:50:54', '2019-07-01 17:19:50');
INSERT INTO `sys_menu` VALUES ('6', '2', '0', '系统日志', '3', 'method', 'itemClick_MonitorSysLog(tree,record)', null, 'monitor:log:grid', '', 'false', '0', '1', '1', '0', '0', '1', '2018-08-30 00:14:46', '2019-12-17 16:41:12');
INSERT INTO `sys_menu` VALUES ('7', '2', '0', '在线用户', '1', 'method', 'itemClick_MonitorOnline(tree, record)', null, 'monitor:online:grid', '', 'false', '0', '1', '0', '0', '0', '1', '2019-11-28 03:37:34', '2019-11-28 04:24:52');
INSERT INTO `sys_menu` VALUES ('8', '2', '0', '数据监控', '2', 'page', 'druid/index', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2019-11-28 03:38:11', '2019-11-29 01:20:37');
INSERT INTO `sys_menu` VALUES ('9', '2', '0', '服务器监控', '4', 'page', 'monitor/server/index', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2019-11-28 03:38:22', '2019-11-29 03:13:29');
INSERT INTO `sys_menu` VALUES ('10', '3', '0', '回收站', '1', 'method', 'itemClick_Recycle(tree, record)', '', 'recycle:grid', '', 'false', '0', '1', '1', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-25 02:33:26');
INSERT INTO `sys_menu` VALUES ('11', '3', '0', '历史记录', '2', 'method', 'itemClick_History(tree,record)', '', 'history:grid', '', 'false', '0', '1', '1', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-25 02:33:04');
INSERT INTO `sys_menu` VALUES ('12', '1', '0', '用户管理', '1', 'method', 'itemClick_SysUser(tree,record)', null, 'system:user:grid', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-26 04:17:04');
INSERT INTO `sys_menu` VALUES ('13', '1', '0', '单位组织', '2', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2018-08-30 00:14:46', '2020-03-22 15:45:38');
INSERT INTO `sys_menu` VALUES ('14', '13', '0', '机构管理', '1', 'method', 'itemClick_SysOrg(tree,record)', null, 'system:org:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-09-15 12:37:38', '2019-12-19 05:51:01');
INSERT INTO `sys_menu` VALUES ('15', '13', '0', '部门管理', '2', 'method', 'itemClick_SysDept(tree,record)', 'tabediting', 'system:dept:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-15 05:46:19');
INSERT INTO `sys_menu` VALUES ('16', '13', '0', '角色管理', '3', 'method', 'itemClick_SysRole(tree,record)', null, 'system:role:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2020-03-20 16:52:36');
INSERT INTO `sys_menu` VALUES ('18', '1', '0', '系统参数配置', '7', 'method', 'itemClick_SysConfig(tree,record)', null, 'system:config:grid', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-30 02:59:12');
INSERT INTO `sys_menu` VALUES ('20', '1', '0', '菜单栏目管理', '8', 'method', 'itemClick_SysItem(tree, record)', null, 'system:menu:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2020-02-16 21:04:15');
INSERT INTO `sys_menu` VALUES ('21', '1', '0', '权限管理', '9', 'method', 'itemClick_SysPower(tree, record)', null, 'system:power:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-25 02:20:45');
INSERT INTO `sys_menu` VALUES ('24', '30', '0', '编码管理', '2', 'method', 'itemClick_SysCode(tree, record)', null, 'system:code:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2019-11-25 01:57:06');
INSERT INTO `sys_menu` VALUES ('25', '30', '0', '系统数据源', '3', 'method', 'itemClick_SysDataSource(tree,record)', null, 'system:datasource:tree', '', 'false', '0', '1', '0', '0', '0', '1', '2018-06-10 17:12:46', '2019-11-25 01:57:46');
INSERT INTO `sys_menu` VALUES ('27', '42', '1', '用户意见', '2', 'method', 'itemClick_InfoForum(tree,record)', 'tabediting', null, '', 'false', '1', '1', '0', '0', '0', '1', '2018-08-30 00:14:46', '2018-10-13 21:11:18');
INSERT INTO `sys_menu` VALUES ('28', '1', '0', '信息管理', '4', 'method', '', null, null, '', 'false', '1', '1', '0', '0', '0', '0', '2018-10-11 00:28:37', '2019-01-13 21:59:15');
INSERT INTO `sys_menu` VALUES ('29', '42', '0', '通知公告', '3', 'method', 'itemClick_InfoNotice(tree,record)', null, null, '', 'false', '1', '1', '1', '0', '0', '1', '2018-10-11 00:37:45', '2018-11-03 22:33:37');
INSERT INTO `sys_menu` VALUES ('30', '1', '0', '字典管理', '3', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2018-06-12 01:03:25', '2019-12-20 01:23:36');
INSERT INTO `sys_menu` VALUES ('31', '1', '0', '导入表单数据', '5', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2020-01-13 03:25:55', '2020-02-13 14:58:00');
INSERT INTO `sys_menu` VALUES ('32', '31', '0', '生成数据表', '1', 'method', 'itemClickGenTable(tree,record)', null, null, '', 'false', '0', '1', '0', '0', '0', '1', '2020-01-13 03:28:18', '2020-01-13 04:10:45');
INSERT INTO `sys_menu` VALUES ('33', '31', '0', '生成数据字段', '2', 'method', 'itemClickGenField(tree,record)', null, 'sys:gen:field:grid', '', 'false', '0', '1', '0', '0', '0', '1', '2020-01-13 03:28:29', '2020-01-16 05:14:12');
INSERT INTO `sys_menu` VALUES ('101', '0', '0', '普通表单生成', '1', 'list', '', null, null, 'icon_list', 'false', '0', '1', '0', '0', '0', '0', '2020-03-27 03:10:58', '2020-03-27 03:37:59');
INSERT INTO `sys_menu` VALUES ('103', '101', '0', '单表对象', '1', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2020-03-27 03:12:07', '2020-03-27 03:12:07');
INSERT INTO `sys_menu` VALUES ('104', '101', '0', '多表对象', '2', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2020-03-27 03:12:22', '2020-03-27 03:12:22');
INSERT INTO `sys_menu` VALUES ('105', '101', '0', '树形对象', '3', 'list', '', null, null, '', 'false', '0', '1', '0', '0', '0', '0', '2020-03-27 03:12:34', '2020-03-27 03:12:34');

-- ----------------------------
-- Table structure for sys_menu_fun
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_fun`;
CREATE TABLE `sys_menu_fun` (
  `fun_id`            int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `item_id`           int(11)           DEFAULT '0'                   COMMENT '菜单栏目Id',
  `name`              varchar(100)      DEFAULT NULL                  COMMENT '功能名称',
  `itemid`            varchar(30)       DEFAULT NULL                  COMMENT '前端控件ItemId值',
  `per_value`         varchar(50)       DEFAULT NULL                  COMMENT '控制器权限值，唯一索引',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`fun_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8 COMMENT='菜单功能表';

-- ----------------------------
-- Records of sys_menu_fun
-- ----------------------------
INSERT INTO `sys_menu_fun` VALUES ('1', '0', '新增', 'btn_add', 'add', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('2', '0', '修改', 'btn_mod', 'mod', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('3', '0', '导出Excel', 'btn_excel', 'excel', '6', '0');
INSERT INTO `sys_menu_fun` VALUES ('4', '0', '保存', 'btn_save', 'save', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('5', '0', '删除', 'btn_del', 'delete', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('6', '0', '排序', 'btn_sort', 'sort', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('7', '0', '撤销', 'btn_canc', 'cancel', '7', '0');
INSERT INTO `sys_menu_fun` VALUES ('8', '0', '审批修改记录', 'btn_trial_grid', 'trialgrid', '9', '0');
INSERT INTO `sys_menu_fun` VALUES ('9', '10', '清空回收站', 'btn_clear', 'recycle:clear', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('10', '10', '彻底删除', 'btn_delete', 'recycle:delete', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('11', '10', '恢复删除', 'btn_recovery', 'recycle:recovery', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('12', '11', '清空记录', 'btn_clear', 'history:clear', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('13', '11', '记录还原', 'btn_restore', 'history:restore', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('14', '11', '记录查看', 'btn_show', 'history:show', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('15', '11', '记录删除', 'btn_delete', 'history:delete', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('16', '12', '新增', 'btn_add', 'system:user:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('17', '12', '修改', 'btn_mod', 'system:user:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('18', '12', '删除', 'btn_del', 'system:user:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('19', '12', '排序', 'btn_sort', 'system:user:sort', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('20', '12', '重置密码', 'btn_reset_pwd', 'system:user:reset_pwd', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('21', '14', '新增', 'btn_add', 'system:org:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('22', '14', '修改', 'btn_mod', 'system:org:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('23', '14', '删除', 'btn_del', 'system:org:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('24', '14', '排序', 'btn_sort', 'system:org:sort', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('25', '15', '新增', 'btn_add', 'system:dept:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('26', '15', '修改', 'btn_mod', 'system:dept:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('27', '15', '删除', 'btn_del', 'system:dept:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('28', '15', '排序', 'btn_sort', 'system:dept:sort', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('29', '16', '新增', 'btn_add', 'system:role:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('30', '16', '修改', 'btn_mod', 'system:role:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('31', '16', '删除', 'btn_del', 'system:role:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('32', '16', '排序', 'btn_sort', 'system:role:sort', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('33', '18', '修改', 'btn_mod', 'system:config:mod', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('34', '20', '新增', 'btn_add', 'system:item:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('35', '20', '修改', 'btn_mod', 'system:item:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('36', '20', '删除', 'btn_del', 'system:item:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('37', '20', '排序', 'btn_sort', 'system:item:sort', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('38', '21', '保存权限', 'btn_permission', 'system:per:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('39', '21', '数据权限', 'btn_data_per', 'system:per:data', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('40', '6', '清空日志', 'btn_clear', 'monitor:log:clear', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('41', '6', '导出Excel', 'btn_excel', 'monitor:log:excel', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('42', '24', '类型新增', 'btn_type_add', 'system:code:type:save', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('43', '24', '类型修改', 'btn_type_mod', 'system:code:type:save', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('44', '24', '类型删除', 'btn_type_del', 'system:code:type:del', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('45', '24', '类型排序', 'btn_type_sort', 'system:code:type:sort', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('46', '24', '编码新增', 'btn_code_add', 'system:code:save', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('47', '24', '编码修改', 'btn_code_mod', 'system:code:save', '6', '0');
INSERT INTO `sys_menu_fun` VALUES ('48', '24', '编码删除', 'btn_code_del', 'system:code:del', '7', '0');
INSERT INTO `sys_menu_fun` VALUES ('49', '24', '编码排序', 'btn_code_sort', 'system:code:sort', '8', '0');
INSERT INTO `sys_menu_fun` VALUES ('50', '16', '成员分配', 'btn_member', 'system:role:member', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('51', '20', '栏目功能设置', 'btn_setup', 'system:item:setup', '6', '0');
INSERT INTO `sys_menu_fun` VALUES ('52', '25', '数据源排序', 'btn_data_sort', 'system:datasource:sort', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('53', '25', '编码值编辑', 'btn_data_mod', 'system:datasource:mod', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('54', '14', '管理员分配', 'btn_manager', 'system:org:manager', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('55', '20', '常用功能', 'btn_fun', 'system:item:fun', '5', '0');
INSERT INTO `sys_menu_fun` VALUES ('56', '21', '清除权限', 'btn_clear_per', 'system:per:clear', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('57', '21', '剔除权限', 'btn_remove_per', 'system:per:remove', '4', '0');
INSERT INTO `sys_menu_fun` VALUES ('58', '7', '强制退出', 'btn_kickout', 'monitor:online:kickout', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('59', '18', '新增', 'btn_add', 'system:config:add', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('60', '18', '删除', 'btn_del', 'system:config:delete', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('61', '32', '数据导入', 'btn_import', 'system:gen:data:import', '3', '0');
INSERT INTO `sys_menu_fun` VALUES ('62', '33', '编辑', 'btn_mod', 'sys:gen:field:editing', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('63', '33', '排序', 'btn_sort', 'sys:gen:field:sort', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('64', '32', '对象删除', 'btn_del', 'system:gen:data:delete', '2', '0');
INSERT INTO `sys_menu_fun` VALUES ('65', '32', '对象修改', 'btn_mod', 'system:gen:data:mod', '1', '0');
INSERT INTO `sys_menu_fun` VALUES ('66', '0', '审批修改', 'btn_trial_mod', 'trialmod', '8', '1');
INSERT INTO `sys_menu_fun` VALUES ('67', '20', '上传附件类型', 'btn_attach', 'system:item:attach', '7', '0');
INSERT INTO `sys_menu_fun` VALUES ('68', '15', '成员分配', 'btn_member', 'system:member:save', '5', '0');

-- ----------------------------
-- Table structure for sys_organize
-- ----------------------------
DROP TABLE IF EXISTS `sys_organize`;
CREATE TABLE `sys_organize` (
  `org_id`            int(11)           NOT NULL AUTO_INCREMENT       COMMENT '机构号Id',
  `pid`               int(11)           DEFAULT NULL                  COMMENT '父结点Id',
  `company_name`      varchar(200)      DEFAULT NULL                  COMMENT '机构名称',
  `nature`            varchar(50)       DEFAULT NULL                  COMMENT '机构性质',
  `short_name`        varchar(100)      DEFAULT NULL                  COMMENT '机构简称',
  `build_time`        varchar(30)       DEFAULT NULL                  COMMENT '成立时间',
  `siteurl`           varchar(100)      DEFAULT NULL                  COMMENT '官网',
  `email`             varchar(100)      DEFAULT NULL                  COMMENT '电子邮箱',
  `phone`             varchar(30)       DEFAULT NULL                  COMMENT '电话',
  `fax`               varchar(50)       DEFAULT NULL                  COMMENT '传真',
  `leader`            varchar(50)       DEFAULT NULL                  COMMENT '负责人',
  `postal_code`       varchar(20)       DEFAULT NULL                  COMMENT '邮编',
  `province_id`       varchar(50)       DEFAULT NULL                  COMMENT '所在省',
  `city_id`           varchar(50)       DEFAULT NULL                  COMMENT '所在市',
  `county_id`         varchar(50)       DEFAULT NULL                  COMMENT '所在县',
  `address`           varchar(200)      DEFAULT NULL                  COMMENT '详细地址',
  `remark`            varchar(500)      DEFAULT NULL                  COMMENT '备注',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time`       datetime          NOT NULL                      COMMENT '修改时间',
  `create_uid`        int(11)           DEFAULT NULL                  COMMENT '创建用户',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`org_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='组织机构表';

-- ----------------------------
-- Records of sys_organize
-- ----------------------------
INSERT INTO `sys_organize` VALUES ('1', '0', '上海公司', '4', '上海', '2017-12-06', '', '', '', '', '2', '', '310000', '310100', '310115', '', '', '2', '2017-12-20 00:44:02', '2019-11-29 16:51:27', null, '0');
INSERT INTO `sys_organize` VALUES ('2', '1', '深圳分公司', '4', '深圳分公司', '2019-02-05', '', '', '', '', '3', '', '', '', '', '', '', '1', '2019-02-25 17:21:24', '2020-02-21 17:42:46', null, '0');

-- ----------------------------
-- Table structure for sys_power_fun
-- ----------------------------
DROP TABLE IF EXISTS `sys_power_fun`;
CREATE TABLE `sys_power_fun` (
  `pm_id`             int(11)           NOT NULL                      COMMENT 'sys_power_menu表关联主键',
  `fun_id`            int(11)           NOT NULL                      COMMENT 'sys_menu_fun表关联主键',
  PRIMARY KEY (`pm_id`,`fun_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限菜单功能表';

-- ----------------------------
-- Records of sys_power_fun
-- ----------------------------

-- ----------------------------
-- Table structure for sys_power_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_power_menu`;
CREATE TABLE `sys_power_menu` (
  `pm_id`             int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `tb_type`           int(11)           NOT NULL                      COMMENT '1：用户权限；2：角色权限；3：剔除权限',
  `tb_id`             int(11)           NOT NULL                      COMMENT 'objtype=1:用户Id;2:角色Id;3:用户Id',
  `item_id`           int(11)           NOT NULL                      COMMENT '栏目Id',
  `del_item`          int(11)           DEFAULT NULL                  COMMENT '剔除权限时，checked=1，选择去除栏目；checked=0，只是选择了该栏目；',
  `data_per`          int(11)           DEFAULT NULL                  COMMENT '数据权限',
  `data_ids`          varchar(1000)     DEFAULT NULL                  COMMENT 'data_per=5:部门Ids；data_per=6:用户Ids',
  `create_time`       datetime          DEFAULT NULL                  COMMENT '创建时间',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`pm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限菜单表';

-- ----------------------------
-- Records of sys_power_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`           int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '机构号',
  `role_name`         varchar(100)      DEFAULT NULL                  COMMENT '角色名称',
  `role_remark`       varchar(300)      DEFAULT NULL                  COMMENT '角色备注',
  `role_per_value`    varchar(50)       DEFAULT NULL                  COMMENT '角色权限值',
  `role_state`        int(11)           DEFAULT NULL                  COMMENT '是否有效',
  `issys`             int(11)           DEFAULT '0'                   COMMENT '是否系统内置，1：是不能删除',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time`       datetime          NOT NULL                      COMMENT '修改时间',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '1', '超级管理员', '', 'admin', '1', '1', '1', '2017-12-23 00:57:43', '2017-12-23 01:28:14', '0');
INSERT INTO `sys_role` VALUES ('2', '1', '系统管理员', '', 'sys_admin', '1', '1', '2', '2017-12-23 00:57:43', '2020-03-22 03:43:18', '0');
INSERT INTO `sys_role` VALUES ('3', '1', '普通角色', '', 'common', '1', '0', '7', '2018-10-27 23:50:00', '2019-11-20 01:21:38', '0');
INSERT INTO `sys_role` VALUES ('4', '2', '系统管理员', null, 'administrator', '1', '1', '-3', '2019-10-21 16:44:41', '2019-10-21 16:44:41', '0');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`           int(11)           NOT NULL AUTO_INCREMENT       COMMENT '用户Id',
  `user_name`         varchar(50)       DEFAULT NULL                  COMMENT '用户姓名',
  `login_name`        varchar(100)      DEFAULT NULL                  COMMENT '登录名',
  `pwd_salt`          varchar(40)       DEFAULT NULL                  COMMENT '密码盐，设置成唯一',
  `password`          varchar(100)      DEFAULT NULL                  COMMENT '密码',
  `user_sex`          varchar(50)       DEFAULT NULL                  COMMENT '用户性别',
  `birthday`          varchar(20)       DEFAULT NULL                  COMMENT '出生日期',
  `email`             varchar(100)      DEFAULT NULL                  COMMENT '电子邮箱',
  `phone`             varchar(30)       DEFAULT NULL                  COMMENT '个人手机号',
  `workphone`         varchar(30)       DEFAULT NULL                  COMMENT '工作手机号',
  `subtelephone`      varchar(30)       DEFAULT NULL                  COMMENT '分机号',
  `head_photo`        varchar(100)      DEFAULT NULL                  COMMENT '头像地址',
  `oicq`              varchar(20)       DEFAULT NULL                  COMMENT 'QQ',
  `wechat`            varchar(40)       DEFAULT NULL                  COMMENT '微信',
  `user_state`        int(11)           DEFAULT '1'                   COMMENT '启用状态',
  `remark`            varchar(200)      DEFAULT NULL                  COMMENT '备注',
  `org_id`            int(11)           DEFAULT '1'                   COMMENT '机构号',
  `role_id`           int(11)           DEFAULT NULL                  COMMENT '用户角色',
  `dept_id`           int(11)           DEFAULT NULL                  COMMENT '用户部门',
  `post_id`           int(11)           DEFAULT NULL                  COMMENT '用户职位',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '排序号',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time`       datetime          DEFAULT NULL                  COMMENT '修改时间',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，0：未删除；1：删除',
  `issys`             int(11)           DEFAULT '0'                   COMMENT '是否系统内置，1：是不能删除',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `unique_login_name` (`login_name`) COMMENT '登录名唯一索引',
  UNIQUE KEY `unique_pwd_salt` (`pwd_salt`) COMMENT '密码盐唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '系统管理员', 'admin', 'c0634cd6774f4da49b1cf90fe7b60731', '87ee547f41ed59770f89351a1b57dae7', '男', '', 'admin@qq.com', '', '', '', '', '', '', '1', '', '1', '1', '1', null, '98', '2018-09-05 23:12:09', '2020-04-14 02:16:53', '0', '1');
INSERT INTO `sys_user` VALUES ('2', '苏如瑶', 'suruyao', '436e0c6acfaa4c50bab54d84d51f5f8d', '9358ada4a6cec92288b39472fef5fdca', '女', '', 'suruyao@qq.com', '', '', '', null, '', '', '1', '', '1', '3', '5', null, '83', '2018-10-28 02:08:01', '2020-04-14 02:25:55', '0', '0');
INSERT INTO `sys_user` VALUES ('3', '朱德华', 'zhudehua', '567995fb59bc46cca10693adfe1acd0b', '83d9eec187a7c36a0a040d152ef9d356', '男', '', 'zhudehua@qq.com', '', '', '', null, '', '', '1', '', '1', '2', '1', '1', '97', '2018-10-28 02:08:01', '2020-02-16 21:09:07', '0', '0');
INSERT INTO `sys_user` VALUES ('4', '江胜', 'jiangsheng', '757d350fc55346e0b1fa126714c98e04', 'e0c9ddea06f61d4bfc7f98ef195b286b', '男', '', 'jiangsheng@qq.com', '', '', '', null, '', '', '1', '', '1', '3', '3', '2', '96', '2018-10-28 02:08:01', '2019-11-29 23:23:30', '0', '0');
INSERT INTO `sys_user` VALUES ('5', '蔡月霖', 'caiyuelin', 'bc37565ddd7e4bfe8fbc18e64123ec96', '85974b0b93012f0edfb702857fb1d4f6', '男', null, 'caiyuelin@qq.com', '', '', '', null, null, null, '1', null, '1', '3', '3', '2', '95', '2018-10-28 02:08:01', null, '0', '0');
INSERT INTO `sys_user` VALUES ('6', '徐西', 'xuxi', 'ed3dd6b5607b460cb7f21105649efd6b', '8f23181bd815477bed53454b49eda3d8', '女', null, 'xuxi@qq.com', '', '', '', null, null, null, '1', null, '1', '3', '3', '2', '94', '2018-10-28 02:08:01', null, '0', '0');
INSERT INTO `sys_user` VALUES ('7', '苏英翠', 'suyingcui', 'e18d8e0d99134862b7f50401fc489609', '9eef0e6fcc1d307413394a8ac7159c19', '女', null, 'suyingcui@qq.com', '', '', '', null, null, null, '1', null, '1', '3', '2', '3', '93', '2018-10-28 02:08:01', null, '0', '0');
INSERT INTO `sys_user` VALUES ('8', '黄初春', 'huangchuchun', '7d05dd308f524cccb8f26c265744e459', '779a6b00d9890ed23b1ccfae3bbe9025', '女', null, 'huangchuchun@qq.com', '', '', '', null, null, null, '1', null, '1', '3', '2', '4', '92', '2018-10-28 02:08:01', null, '0', '0');

-- ----------------------------
-- Table structure for sys_user_online
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online` (
  `sessionId`         varchar(50)       NOT NULL                      COMMENT '用户会话id',
  `user_id`           int(11)           DEFAULT '0'                   COMMENT '登录账号',
  `dept_name`         varchar(50)       DEFAULT NULL                  COMMENT '部门名称',
  `ip_address`        varchar(50)       DEFAULT NULL                  COMMENT '登录IP地址',
  `login_location`    varchar(200)      DEFAULT NULL                  COMMENT '登录地点',
  `browser`           varchar(50)       DEFAULT NULL                  COMMENT '浏览器名称',
  `version`           varchar(50)       DEFAULT NULL                  COMMENT '浏览器版本号',
  `device`            varchar(50)       DEFAULT NULL                  COMMENT '操作系统',
  `status`            varchar(10)       DEFAULT NULL                  COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp`   datetime          DEFAULT NULL                  COMMENT 'session创建时间',
  `last_access_time`  datetime          DEFAULT NULL                  COMMENT 'session最后访问时间',
  `time_out`          bigint(5)         DEFAULT '0'                   COMMENT '超时时间，单位为毫秒',
  PRIMARY KEY (`sessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='在线用户记录表';

-- ----------------------------
-- Records of sys_user_online
-- ----------------------------
INSERT INTO `sys_user_online` VALUES ('4e52fd63-6e9e-4d11-8b41-84edf0f26509', '2', '其他部门', '192.168.1.101', '无法获知', 'Chrome', '70.0.3538.77', 'Windows 10', 'on_line', '2020-04-14 01:35:49', '2020-04-14 02:31:54', '36000000');

-- ----------------------------
-- Table structure for tb_configvalue
-- ----------------------------
DROP TABLE IF EXISTS `tb_configvalue`;
CREATE TABLE `tb_configvalue` (
  `id`                int(11)           NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `item_id`           int(11)           NOT NULL                      COMMENT '菜单栏目Id',
  `config_key`        varchar(50)       NOT NULL                      COMMENT '配置参数Key',
  `config_value`      text              DEFAULT NULL                  COMMENT '配置参数Value值',
  `config_text`       text              DEFAULT NULL                  COMMENT '配置参数Value值的中文文本，下拉菜单用到',
  `modify_time`       datetime          DEFAULT NULL                  COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统配置编辑对象存值表';

-- ----------------------------
-- Records of tb_configvalue
-- ----------------------------

-- ----------------------------
-- Table structure for tb_export_excel
-- ----------------------------
DROP TABLE IF EXISTS `tb_export_excel`;
CREATE TABLE `tb_export_excel` (
  `user_id`           int(11)           NOT NULL                      COMMENT '用户id',
  `oid`               int(11)           NOT NULL                      COMMENT '数据对象Id',
  `fid`               int(11)           NOT NULL                      COMMENT '字段Id',
  `serialcode`        int(11)           DEFAULT NULL                  COMMENT '导出字段排序',
  `isexport`          int(11)           DEFAULT NULL                  COMMENT '1：导出字段；0：不是',
  PRIMARY KEY (`user_id`,`oid`,`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='导出Excel字段设置表';

-- ----------------------------
-- Records of tb_export_excel
-- ----------------------------

-- ----------------------------
-- Table structure for tb_extend_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_extend_info`;
CREATE TABLE `tb_extend_info` (
  `table_name`        varchar(30)       NOT NULL                      COMMENT '数据库表名',
  `table_id`          int(11)           DEFAULT NULL                  COMMENT '表的主键值',
  `field_extend`      varchar(30)       DEFAULT NULL                  COMMENT '表扩展字段',
  `field_value`       varchar(100)      DEFAULT NULL                  COMMENT '扩展字段值，只限100字节内',
  `flag_1`            varchar(20)       DEFAULT NULL                  COMMENT '标记1，特殊作用',
  `flag_2`            varchar(10)       DEFAULT NULL                  COMMENT '标记2，特殊作用',
  `serialcode`        int(11)           DEFAULT '0'                   COMMENT '排序号',
  `create_time`       datetime          DEFAULT CURRENT_TIMESTAMP     COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='特殊字段扩展记录表';

-- ----------------------------
-- Records of tb_extend_info
-- ----------------------------
INSERT INTO `tb_extend_info` VALUES ('design_operator', '1', 'textfield', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '2', 'textfield', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '3', 'textfield', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '4', 'textfield', 'left_like', '左包含', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '5', 'textfield', 'right_like', '右包含', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '6', 'textfield', 'is_null', '为空', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '7', 'textfield', 'is_not_null', '不为空', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '8', 'numberfield', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '9', 'numberfield', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '10', 'numberfield', '>', '大于', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '11', 'numberfield', '>=', '大于等于', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '12', 'numberfield', '<', '小于', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '13', 'numberfield', '<=', '小于等于', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '14', 'numberfield', 'between', '区间', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '15', 'numberfield', 'is_null', '为空', '', '8', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '16', 'numberfield', 'is_not_null', '不为空', '', '9', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '19', 'textareafield', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '20', 'textareafield', 'left_like', '左包含', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '21', 'textareafield', 'right_like', '右包含', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '22', 'textareafield', 'is_null', '为空', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '23', 'textareafield', 'is_not_null', '不为空', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '26', 'htmleditor', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '27', 'htmleditor', 'left_like', '左包含', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '28', 'htmleditor', 'right_like', '右包含', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '29', 'htmleditor', 'is_null', '为空', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '30', 'htmleditor', 'is_not_null', '不为空', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '31', 'datefield', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '32', 'datefield', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '33', 'datefield', '>', '大于', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '34', 'datefield', '>=', '大于等于', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '35', 'datefield', '<', '小于', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '36', 'datefield', '<=', '小于等于', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '37', 'datefield', 'between', '区间', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '41', 'datefield', 'is_null', '为空', '', '11', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '42', 'datefield', 'is_not_null', '不为空', '', '12', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '43', 'datetimefield', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '44', 'datetimefield', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '45', 'datetimefield', '>', '大于', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '46', 'datetimefield', '>=', '大于等于', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '47', 'datetimefield', '<', '小于', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '48', 'datetimefield', '<=', '小于等于', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '49', 'datetimefield', 'between', '区间', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '53', 'datetimefield', 'is_null', '为空', '', '11', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '54', 'datetimefield', 'is_not_null', '不为空', '', '12', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '55', 'timefield', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '56', 'timefield', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '57', 'timefield', '>', '大于', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '58', 'timefield', '>=', '大于等于', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '59', 'timefield', '<', '小于', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '60', 'timefield', '<=', '小于等于', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '61', 'timefield', 'between', '区间', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '65', 'timefield', 'is_null', '为空', '', '8', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '66', 'timefield', 'is_not_null', '不为空', '', '9', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '67', 'trigger', '=', '等于', 'string', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '68', 'trigger', '!=', '不等于', 'string', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '69', 'trigger', 'like', '包含', 'string', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '70', 'trigger', 'left_like', '左包含', 'string', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '71', 'trigger', 'right_like', '右包含', 'string', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '72', 'trigger', 'is_null', '为空', 'string', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '73', 'trigger', 'is_not_null', '不为空', 'string', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '74', 'trigger', '=', '等于', 'int', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '75', 'trigger', '!=', '不等于', 'int', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '76', 'trigger', 'is_null', '为空', 'int', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '77', 'trigger', 'is_not_null', '不为空', 'int', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '78', 'panelpicker', '=', '等于', 'string', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '79', 'panelpicker', '!=', '不等于', 'string', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '80', 'panelpicker', 'like', '包含', 'string', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '81', 'panelpicker', 'left_like', '左包含', 'string', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '82', 'panelpicker', 'right_like', '右包含', 'string', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '83', 'panelpicker', 'is_null', '为空', 'string', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '84', 'panelpicker', 'is_not_null', '不为空', 'string', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '85', 'panelpicker', '=', '等于', 'int', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '86', 'panelpicker', '!=', '不等于', 'int', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '87', 'panelpicker', 'is_null', '为空', 'int', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '88', 'panelpicker', 'is_not_null', '不为空', 'int', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '89', 'checkboxgroup', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '90', 'checkboxgroup', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '91', 'checkboxgroup', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '92', 'checkboxgroup', 'is_null', '为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '93', 'checkboxgroup', 'is_not_null', '不为空', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '94', 'multitreepicker', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '95', 'multitreepicker', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '96', 'multitreepicker', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '97', 'multitreepicker', 'is_null', '为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '98', 'multitreepicker', 'is_not_null', '不为空', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '99', 'multicombobox', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '100', 'multicombobox', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '101', 'multicombobox', 'like', '包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '102', 'multicombobox', 'is_null', '为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '103', 'multicombobox', 'is_not_null', '不为空', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '104', 'radiogroup', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '105', 'radiogroup', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '106', 'radiogroup', 'is_null', '为空', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '107', 'radiogroup', 'is_not_null', '不为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '108', 'treepicker', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '109', 'treepicker', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '110', 'treepicker', 'is_null', '为空', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '111', 'treepicker', 'is_not_null', '不为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '112', 'singlecombobox', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '113', 'singlecombobox', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '114', 'singlecombobox', 'is_null', '为空', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '115', 'singlecombobox', 'is_not_null', '不为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '116', 'checkbox', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '117', 'checkbox', 'is_null', '为空', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '118', 'checkbox', 'is_not_null', '不为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '119', 'ueditor', 'like', '包含', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '120', 'ueditor', 'left_like', '左包含', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '121', 'ueditor', 'right_like', '右包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '122', 'ueditor', 'is_null', '为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '123', 'ueditor', 'is_not_null', '不为空', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '124', 'kindeditor', 'like', '包含', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '125', 'kindeditor', 'left_like', '左包含', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '126', 'kindeditor', 'right_like', '右包含', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '127', 'kindeditor', 'is_null', '为空', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '128', 'kindeditor', 'is_not_null', '不为空', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '129', 'my97date', '=', '等于', '', '1', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '130', 'my97date', '!=', '不等于', '', '2', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '131', 'my97date', '>', '大于', '', '3', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '132', 'my97date', '>=', '大于等于', '', '4', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '133', 'my97date', '<', '小于', '', '5', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '134', 'my97date', '<=', '小于等于', '', '6', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '135', 'my97date', 'between', '区间', '', '7', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '136', 'my97date', 'is_null', '为空', '', '11', null);
INSERT INTO `tb_extend_info` VALUES ('design_operator', '137', 'my97date', 'is_not_null', '不为空', '', '12', null);
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '1', 'xtype', 'hiddenfield', '隐藏域', '', '1', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '2', 'xtype', 'textfield', '文本输入框', '', '2', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '3', 'xtype', 'numberfield', '数值输入框', '', '3', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '4', 'xtype', 'singlecombobox', '下拉单选框', '', '4', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '5', 'xtype', 'multicombobox', '下拉多选框', '', '5', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '11', 'xtype', 'treepicker', '下拉树选择框', '', '6', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '6', 'xtype', 'datefield', '日期输入框', '', '7', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '7', 'xtype', 'my97date', 'my97日期框', '', '8', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '8', 'xtype', 'textareafield', '多行文本框', '', '9', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '9', 'xtype', 'datetimefield', '日期时间输入框', '', '10', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '10', 'xtype', 'timefield', '时间输入框', '', '11', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '12', 'xtype', 'checkbox', '复选框', '', '12', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '13', 'xtype', 'trigger', '触发弹出框', '', '13', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '14', 'xtype', 'definepicker', '自定义选择器', '', '14', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '15', 'xtype', 'checkboxgroup', '复选组选择框', '', '15', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '16', 'xtype', 'radiogroup', '单选组选择框', '', '16', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '17', 'xtype', 'htmleditor', 'Ext富文本框', '', '17', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '18', 'xtype', 'ueditor', '百度编辑器', '', '18', '2020-01-16 04:27:14');
INSERT INTO `tb_extend_info` VALUES ('input_xtype', '19', 'xtype', 'kindeditor', 'KindEditor编辑器', '', '19', '2020-01-16 04:27:14');

-- ----------------------------
-- Table structure for tb_gen_field
-- ----------------------------
DROP TABLE IF EXISTS `tb_gen_field`;
CREATE TABLE `tb_gen_field` (
  `fid`               int(11)           NOT NULL                      COMMENT '',
  `tid`               int(50)           DEFAULT NULL                  COMMENT '数据表Key',
  `extend_suf`        varchar(10)       NOT NULL                      COMMENT 'text：扩展表后缀',
  `field_name`        varchar(50)       DEFAULT NULL                  COMMENT '数据库字段名',
  `data_type`         varchar(50)       DEFAULT NULL                  COMMENT '字段类型',
  `xtype`             varchar(50)       DEFAULT NULL                  COMMENT '输入框类型',
  `field_tag`         varchar(30)       DEFAULT NULL                  COMMENT '特殊字段标记',
  `field_explain`     varchar(50)       DEFAULT NULL                  COMMENT '编辑界面字段名称（字段中文说明）',
  `serialcode`        int(11)           DEFAULT '0'                   COMMENT '排序号',
  `issearchfield`     int(11)           DEFAULT '1'                   COMMENT '是否查询字段，1：是，0：否',
  `iscolumns`         int(11)           DEFAULT NULL                  COMMENT '该字段是否显示列表，1：是，0：不是',
  `isdefine`          int(11)           DEFAULT '1'                   COMMENT '是否用户定义的字段，1：用户定义，0：系统生成的',
  `save_value`        int(11)           DEFAULT '0'                   COMMENT '对于 下拉单选、下拉多选、下拉树 该字段是否存值，1：存值，0：存文本。如果是存值，在显示列表时，要计算文本值',
  `default_value`     varchar(200)      DEFAULT NULL                  COMMENT '新增默认值，审批表单生成',
  `data_key`          varchar(50)       DEFAULT NULL                  COMMENT 'store：配置的编辑类型ID，远程加载时赋值',
  `store_datas`       text              DEFAULT NULL                  COMMENT 'store：配置的本地数据源，以数组的形式存储',
  `store_type`        varchar(100)      DEFAULT NULL                  COMMENT '数据源类型，编码表与系统数据源',
  `field_type`        varchar(10)       DEFAULT NULL                  COMMENT '前台创建字段时的类型：auto(默认)、string、int、float、bool、date',
  `editor_search`     text              DEFAULT NULL                  COMMENT '查询编辑器配置',
  `is_form_input`     int(11)           DEFAULT '0'                   COMMENT '是否表单输入框',
  `build_type`        varchar(50)       DEFAULT '按比'                COMMENT 'form中items生成方法的比例，审批表单生成',
  `create_time`       datetime          DEFAULT NULL                  COMMENT '创建时间',
  `create_uid`        int(11)           DEFAULT NULL                  COMMENT '创建用户',
  PRIMARY KEY (`fid`),
  UNIQUE KEY `fid` (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='导入数据字段表';

-- ----------------------------
-- Records of tb_gen_field
-- ----------------------------

-- ----------------------------
-- Table structure for tb_gen_object
-- ----------------------------
DROP TABLE IF EXISTS `tb_gen_object`;
CREATE TABLE `tb_gen_object` (
  `oid`               int(11)           NOT NULL                      COMMENT '对象Id',
  `pid`               int(11)           NOT NULL DEFAULT '0'          COMMENT '父结点Id',
  `object_type`       varchar(20)       DEFAULT NULL                  COMMENT '对象类型',
  `object_name`       varchar(100)      DEFAULT NULL                  COMMENT '对象名',
  `object_key`        varchar(50)       DEFAULT NULL                  COMMENT '对象源标识，唯一码',
  `main_table`        varchar(50)       DEFAULT NULL                  COMMENT ' 对象主表名称',
  `is_attgrid`        int(11)           DEFAULT '0'                   COMMENT '是否有附件列表，1：有附件；0：没有',
  `layout_type`       varchar(50)       DEFAULT NULL                  COMMENT '表单编辑类型',
  `item_method`       varchar(50)       DEFAULT NULL                  COMMENT '页面打开JS方法：自动生成方法；自定义方法',
  `config_tables`     text              DEFAULT NULL                  COMMENT '对象配置信息',
  `expanded`          varchar(10)       DEFAULT NULL                  COMMENT 'true：树加载父目录默认打开；false：父目录不打开',
  `serialcode`        int(11)           DEFAULT '0'                   COMMENT '排序号',
  `create_time`       datetime          DEFAULT NULL                  COMMENT '创建时间',
  `create_uid`        int(11)           DEFAULT NULL                  COMMENT '创建用户',
  PRIMARY KEY (`oid`),
  UNIQUE KEY `oid` (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='导入设计对象表';

-- ----------------------------
-- Records of tb_gen_object
-- ----------------------------

-- ----------------------------
-- Table structure for tb_gen_table
-- ----------------------------
DROP TABLE IF EXISTS `tb_gen_table`;
CREATE TABLE `tb_gen_table` (
  `tid`               int(11)           NOT NULL                      COMMENT '数据表Id',
  `pid`               int(11)           DEFAULT '0'                   COMMENT '父结点Id',
  `oid`               int(50)           DEFAULT NULL                  COMMENT '数据对象Id，0时为自定义数据库表',
  `table_key`         varchar(50)       DEFAULT NULL                  COMMENT '数据表Key',
  `table_name`        varchar(50)       DEFAULT NULL                  COMMENT '数据库表名',
  `table_explain`     varchar(100)      DEFAULT NULL                  COMMENT '表的中文名称',
  `extend_name`       varchar(50)       DEFAULT NULL                  COMMENT '扩展表名，当前表有Text字段时(只有主表与1:1关系的表有扩展表)',
  `table_type`        varchar(50)       DEFAULT 'table'               COMMENT '列表类型，table：普通列表；tree：树形列表',
  `tb_relation`       varchar(50)       DEFAULT NULL                  COMMENT '与主表单关系，1：一对一关系；N：一对多关系',
  `layout_type`       varchar(50)       DEFAULT 'single_winediting'   COMMENT '单表单：单元格编辑；行编辑；窗口编辑；选项卡编辑。多表单：窗口编辑；选项卡编辑。',
  `serialcode`        int(11)           DEFAULT '0'                   COMMENT '排序号',
  `create_time`       datetime          DEFAULT NULL                  COMMENT '创建时间',
  `create_uid`        int(11)           DEFAULT NULL                  COMMENT '创建用户',
  PRIMARY KEY (`tid`),
  UNIQUE KEY `tid` (`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='导入数据设计表';

-- ----------------------------
-- Records of tb_gen_table
-- ----------------------------
INSERT INTO `tb_gen_table` VALUES ('-12', '0', '0', 'key_per', 'sys_power', '权限管理', '', 'table', '主表', 'no_layout_type', '12', '2020-01-31 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-11', '0', '0', 'key_organize', 'sys_organize', '机构管理', '', 'table', '主表', 'no_layout_type', '10', '2020-01-29 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-10', '0', '0', 'key_role', 'sys_role', '角色管理', '', 'table', '主表', 'no_layout_type', '14', '2020-02-02 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-9', '0', '0', 'key_user', 'sys_user', '系统用户', '', 'table', '主表', 'no_layout_type', '15', '2020-02-03 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-8', '0', '0', 'key_menu_fun', 'sys_menu_fun', '菜单功能表', '', 'table', '主表', 'no_layout_type', '9', '2020-01-28 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-7', '0', '0', 'key_menu', 'sys_menu', '菜单管理', '', 'table', '主表', 'no_layout_type', '8', '2020-01-27 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-6', '0', '0', 'key_log', 'sys_log', '系统日志', '', 'table', '主表', 'no_layout_type', '7', '2020-01-26 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-5', '0', '0', 'key_dept', 'sys_dept', '部门管理', '', 'table', '主表', 'no_layout_type', '4', '2020-01-23 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-4', '0', '0', 'key_config', 'sys_config', '系统参数配置表', '', 'table', '主表', 'no_layout_type', '3', '2020-01-22 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-3', '0', '0', 'key_online', 'sys_user_online', '在线用户', '', 'table', '主表', 'no_layout_type', '16', '2020-02-04 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-2', '0', '0', 'key_field_gen', 'tb_gen_field', '生成数据字段表', '', 'table', '主表', 'no_layout_type', '17', '2020-02-05 07:10:00', '2');
INSERT INTO `tb_gen_table` VALUES ('-1', '0', '0', 'key_info_share', 'tb_info_share', '信息共享表', '', 'table', '主表', 'no_layout_type', '18', '2020-02-06 07:10:00', '2');

-- ----------------------------
-- Table structure for tb_info_share
-- ----------------------------
DROP TABLE IF EXISTS `tb_info_share`;
CREATE TABLE `tb_info_share` (
  `share_uuid`        varchar(50)       DEFAULT NULL                  COMMENT '主键',
  `item_id`           int(11)           NOT NULL                      COMMENT '信息所属栏目',
  `idleaf`            bigint(11)        NOT NULL                      COMMENT '信息主键号',
  `title`             varchar(200)      DEFAULT NULL                  COMMENT '信息共有标题',
  `tid`               int(50)           DEFAULT NULL                  COMMENT '数据库主表Id，tb_gen_table主键',
  `isdel`             int(11)           DEFAULT '0'                   COMMENT '是否删除，1：删除；0：未删除',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '信息创建时间',
  `modify_time`       datetime          DEFAULT NULL                  COMMENT '信息修改时间',
  `delete_time`       datetime          DEFAULT NULL                  COMMENT '信息删除时间',
  `create_uid`        int(11)           DEFAULT NULL                  COMMENT '新增用户Id',
  `modify_uid`        int(11)           DEFAULT NULL                  COMMENT '修改用户Id',
  `delete_uid`        int(11)           DEFAULT NULL                  COMMENT '删除用户Id',
  `org_id`            int(11)           DEFAULT NULL                  COMMENT '所属机构',
  `sub_table_num`     int(11)           DEFAULT NULL                  COMMENT 'sys_info_store分表号',
  `modify_records`    int(11)           DEFAULT '0'                   COMMENT '修改记录次数(新增、修改、恢复）',
  PRIMARY KEY (`item_id`,`idleaf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设计表信息共享表(回收站)';

-- ----------------------------
-- Records of tb_info_share
-- ----------------------------

-- ----------------------------
-- Table structure for tb_info_store1
-- ----------------------------
DROP TABLE IF EXISTS `tb_info_store1`;
CREATE TABLE `tb_info_store1` (
  `store_id`          bigint(11)        NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `share_uuid`        varchar(50)       DEFAULT NULL                  COMMENT '信息共享表主键',
  `store_data`        longtext          DEFAULT NULL                  COMMENT '存储内容',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `opertype`          varchar(50)       DEFAULT NULL                  COMMENT '操作类型：新增、修改',
  `user_id`           int(11)           DEFAULT NULL                  COMMENT '操作用户',
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史记录存储表';

-- ----------------------------
-- Records of tb_info_store1
-- ----------------------------

-- ----------------------------
-- Table structure for tb_object_att
-- ----------------------------
DROP TABLE IF EXISTS `tb_object_att`;
CREATE TABLE `tb_object_att` (
  `attach_id`         bigint(20)        NOT NULL AUTO_INCREMENT       COMMENT '主键',
  `item_id`           int(11)           DEFAULT NULL                  COMMENT '栏目号',
  `idleaf`            int(11)           DEFAULT NULL                  COMMENT '信息主键',
  `serialcode`        bigint(20)        DEFAULT NULL                  COMMENT '排序号',
  `attach_name`       varchar(100)      DEFAULT NULL                  COMMENT '附件名称',
  `attach_add`        varchar(400)      DEFAULT NULL                  COMMENT '附件地址',
  `attach_size`       varchar(50)       DEFAULT NULL                  COMMENT '附件大小',
  `attach_type`       varchar(10)       DEFAULT NULL                  COMMENT '附件扩展名',
  `create_time`       datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `attach_state`      int(11)           DEFAULT '0'                   COMMENT '启用状态',
  PRIMARY KEY (`attach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设计表附件上传表';

-- ----------------------------
-- Records of tb_object_att
-- ----------------------------

-- ----------------------------
-- View structure for view_flow_item
-- ----------------------------
DROP VIEW IF EXISTS `view_flow_item`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_flow_item` AS select `tb_extend_info`.`table_id` AS `item_id`,`tb_extend_info`.`field_value` AS `grid_item_id` from `tb_extend_info` where (`tb_extend_info`.`table_name` = 'flow_menu') ;

-- ----------------------------
-- View structure for view_gen_operator
-- ----------------------------
DROP VIEW IF EXISTS `view_gen_operator`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_gen_operator` AS select `tb_extend_info`.`table_id` AS `id`,`tb_extend_info`.`field_extend` AS `field_type`,`tb_extend_info`.`flag_2` AS `value_type`,`tb_extend_info`.`field_value` AS `operator`,`tb_extend_info`.`flag_1` AS `remark`,`tb_extend_info`.`serialcode` AS `serialcode` from `tb_extend_info` where (`tb_extend_info`.`table_name` = 'design_operator') order by `tb_extend_info`.`serialcode` ;

-- ----------------------------
-- View structure for view_gen_xtype
-- ----------------------------
DROP VIEW IF EXISTS `view_gen_xtype`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_gen_xtype` AS select `tb_extend_info`.`table_id` AS `id`,`tb_extend_info`.`field_value` AS `xtype`,`tb_extend_info`.`flag_1` AS `xtype_name`,`tb_extend_info`.`serialcode` AS `serialcode` from `tb_extend_info` where (`tb_extend_info`.`table_name` = 'input_xtype') order by `tb_extend_info`.`serialcode` ;

-- ----------------------------
-- View structure for view_item_fun
-- ----------------------------
DROP VIEW IF EXISTS `view_item_fun`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_item_fun` AS select `c`.`item_id` AS `item_id`,group_concat(`c`.`name` order by `c`.`serialcode` ASC separator ',') AS `fun_names` from `sys_menu_fun` `c` where (`c`.`isdel` = 0) group by `c`.`item_id` ;

-- ----------------------------
-- View structure for view_menu_fun
-- ----------------------------
DROP VIEW IF EXISTS `view_menu_fun`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_menu_fun` AS select `a1`.`item_id` AS `item_id`,`b1`.`fun_id` AS `fun_id`,`b1`.`itemid` AS `itemid`,`b1`.`name` AS `fun_name`,`a1`.`per_value` AS `grid_per_value`,`b1`.`per_value` AS `per_value` from (`sys_menu` `a1` left join `sys_menu_fun` `b1` on(((`a1`.`item_id` = `b1`.`item_id`) and (`b1`.`isdel` = 0)))) where ((`a1`.`isdel` = 0) and (`a1`.`isused` = 1)) ;

-- ----------------------------
-- View structure for view_power_fun
-- ----------------------------
DROP VIEW IF EXISTS `view_power_fun`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_power_fun` AS select `a1`.`tb_type` AS `tb_type`,`a1`.`tb_id` AS `tb_id`,`a1`.`item_id` AS `item_id`,`a1`.`del_item` AS `del_item`,`a1`.`data_per` AS `data_per`,`a1`.`data_ids` AS `data_ids`,`b1`.`fun_id` AS `fun_id` from (`sys_power_menu` `a1` left join `sys_power_fun` `b1` on((`a1`.`pm_id` = `b1`.`pm_id`))) where (`a1`.`isdel` = 0) ;

-- ----------------------------
-- Function structure for getGenObjectPids
-- ----------------------------
DROP FUNCTION IF EXISTS `getGenObjectPids`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getGenObjectPids`(`rootId` int) RETURNS varchar(1000) CHARSET utf8
    COMMENT 'tb_gen_object某一结点所有父结点'
BEGIN   
DECLARE str varchar(1000);  
DECLARE cid varchar(1000);   
SET str = '$';   
SET cid = rootId;   
WHILE cid is not null DO   
    SET str = concat(str, ',', cid);   
		select group_concat(oid) into cid from tb_gen_object a where FIND_IN_SET(pid, cid) > 0
		and exists(select 1 from tb_gen_object b where a.oid=b.pid);   
END WHILE;   
RETURN str;   
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for getObjectPids
-- ----------------------------
DROP FUNCTION IF EXISTS `getObjectPids`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getObjectPids`(`rootId` int) RETURNS varchar(1000) CHARSET utf8
    COMMENT 'design_object某一结点所有父结点'
BEGIN   
DECLARE str varchar(1000);  
DECLARE cid varchar(1000);   
SET str = '$';   
SET cid = rootId;   
WHILE cid is not null DO   
    SET str = concat(str, ',', cid);   
		select group_concat(oid) into cid from design_object a where FIND_IN_SET(pid, cid) > 0
		and exists(select 1 from design_object b where a.oid=b.pid);   
END WHILE;   
RETURN str;   
END
;;
DELIMITER ;
