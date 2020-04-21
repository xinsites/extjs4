package com.xinsite.dal.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBParameter {
    private String key;        //字段
    private String operator;  //查询操作符
    private Object value;     //字段值，如果值形式：1,2,3并且操作符不空，说明是多参数，参数分成：key1,key2,key3

    public DBParameter() {
    }

    public DBParameter(String key, Object value) {
        if (key.indexOf("@") != 0) key = "@" + key;
        this.key = key;
        //this.operator = "=";
        this.value = value;
    }

    public DBParameter(String key, String operator, Object value) {
        if (key.indexOf("@") != 0) key = "@" + key;
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (key.indexOf("@") != 0) key = "@" + key;
        this.key = key;
    }

    public String getValue() {
        if (value == null) return null;
        return value.toString();
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public static DBParameter[] getParameter(Map<String, Object> params) {
        List<DBParameter> list = new ArrayList<DBParameter>();
        if (params != null) {
            for (String key : params.keySet()) {
                list.add(new DBParameter(key, params.get(key)));
            }
        }
        DBParameter[] parameters = new DBParameter[list.size()];
        return list.toArray(parameters);
    }

    public static DBParameter[] getParameter(List<DBParameter> list) {
        if (list != null) {
            DBParameter[] parameters = new DBParameter[list.size()];
            return list.toArray(parameters);
        } else {
            return new DBParameter[0];
        }
    }

    public static String getSqlWhere(List<DBParameter> list) {
        return getSqlWhere(list, "");
    }

    public static String getSqlWhere(List<DBParameter> list, String prefix) {
        if (list != null) {
            return DBParameter.getSqlWhere(getParameter(list), prefix);
        } else {
            return "";
        }
    }

    public static String getSqlWhere(DBParameter[] params, String... prefix) {
        if (params != null) {
            String pre = "";
            if (prefix != null && prefix.length > 0) pre = prefix[0];
            StringBuilder sb = new StringBuilder();
            for (DBParameter para : params) {
                String param_key = para.getKey().replace("@", "");
                if (para.getOperator() == null) {
                    sb.append(String.format(" and %s = @%s", param_key, pre + param_key));
                } else {
                    switch (para.getOperator()) {
                        case "=":
                        case "!=":
                        case ">":
                        case ">=":
                        case "<":
                        case "<=":
                            sb.append(String.format(" and %s %s @%s", param_key, para.getOperator(), pre + param_key));
                            break;
                        case "in":
                            sb.append(String.format(" and %s in(%s)", param_key, para.getValue()));
                            break;
                        case "like":
                            para.setValue("%" + para.getValue().toString() + "%");
                            sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                            break;
                        case "left_like":
                            para.setValue("%" + para.getValue().toString());
                            sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                            break;
                        case "right_like":
                            para.setValue(para.getValue().toString() + "%");
                            sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                            break;
                        case "between":
                            sb.append(String.format(" and %s between @%s1 and @%s2", param_key, pre + param_key, pre + param_key));
                            break;
                        case "is_null":
                            sb.append(String.format(" and (%s is null or length(%s)=0)", param_key, param_key));
                            break;
                        case "is_not_null":
                            sb.append(String.format(" and (%s is not null and length(%s)>0)", param_key, param_key));
                            break;
                        case "late":  //按时间，最近几天
                            sb.append(String.format(" and DATEDIFF(now(),%s)<=@%s", param_key, pre + param_key));
                            break;
                    }
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static String getSqlWhere(String sql, DBParameter[] params, String... prefix) {
        if (params != null) {
            String pre = "";
            if (prefix != null && prefix.length > 0) pre = prefix[0];
            StringBuilder sb = new StringBuilder();
            for (DBParameter para : params) {
                if (sql.indexOf(para.getKey()) >= 0) continue;
                if (para.getOperator() == null) continue;
                String param_key = para.getKey().replace("@", "");
                switch (para.getOperator()) {
                    case "=":
                    case "!=":
                    case ">":
                    case ">=":
                    case "<":
                    case "<=":
                        sb.append(String.format(" and %s %s @%s", param_key, para.getOperator(), pre + param_key));
                        break;
                    case "in":
                        sb.append(String.format(" and %s in(%s)", param_key, para.getValue()));
                        break;
                    case "like":
                        para.setValue("%" + para.getValue().toString() + "%");
                        sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                        break;
                    case "left_like":
                        para.setValue("%" + para.getValue().toString());
                        sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                        break;
                    case "right_like":
                        para.setValue(para.getValue().toString() + "%");
                        sb.append(String.format(" and %s like @%s", param_key, pre + param_key));
                        break;
                    case "between":
                        sb.append(String.format(" and %s between @%s1 and @%s2", param_key, pre + param_key, pre + param_key));
                        break;
                    case "is_null":
                        sb.append(String.format(" and (%s is null or length(%s)=0)", param_key, param_key));
                        break;
                    case "is_not_null":
                        sb.append(String.format(" and (%s is not null and length(%s)>0)", param_key, param_key));
                        break;
                    case "late":  //按时间，最近几天
                        sb.append(String.format(" and DATEDIFF(now(),%s)<=@%s", param_key, pre + param_key));
                        break;
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
