package com.xinsite.dal.dbhelper;


import com.xinsite.dal.bean.DBParameter;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL语句抽象，提供基于命名参数的SQL语句功能
 *
 * @author zhangxiaxin
 */
public class NSQLUtils {
    // 静态集合缓存使用过的NSQL
    private static Map<String, NSQLUtils> caches = new HashMap<String, NSQLUtils>();

    private String sql_naming;
    private String sql_execute;
    private String[] names;
    private static char prefix = '@';

    private NSQLUtils() {
        // 用户不能实例化对象
        // 通过get方法获取可用实例
    }

    public boolean hasName() {
        return names.length > 0;
    }

    public void setParameter(PreparedStatement ps, String name, Object value) throws SQLException {
        for (int a = 0, b = names.length - 1; a <= b; a++, b--) {
            if (names[a].equals(name)) {
                //ps.setNull(1, Types.NULL);
                ps.setObject(a + 1, value);
                //break;
            }
            if (a != b && names[b].equals(name)) {
                ps.setObject(b + 1, value);
                //break;
            }
        }
    }

    public void setParameters(String sql, PreparedStatement pstmt, DBParameter[] params) throws SQLException {
        if (params != null) {
            for (DBParameter p : params) {
                if (StringUtils.isNotEmpty(p.getOperator())) {
                    String[] strs = p.getValue().split(",");
                    if (strs.length > 1) {
                        if (sql.indexOf(p.getKey() + 1) >= 0) {
                            for (int i = 0; i < strs.length; i++) {
                                setParameter(pstmt, p.getKey() + (i + 1), strs[i]);
                            }
                        } else {
                            setParameter(pstmt, p.getKey(), p.getValue());
                        }
                    } else {
                        setParameter(pstmt, p.getKey(), p.getValue());
                    }
                } else {
                    setParameter(pstmt, p.getKey(), p.getValue());
                }
            }
        }
    }

    public void setParameters(PreparedStatement ps, Map<String, Object> values) throws SQLException {
        int count = 1;
        for (int index = 0; index < names.length; index++) {
            if (values.containsKey(names[index])) {
                ps.setObject(count++, values.get(names[index]));
            }
            //ps.setObject(index + 1, values.get(names[index]));
        }
    }

    /**
     * 获取用于数据库执行的SQL语句
     *
     * @return
     */
    public String getSql() {
        return sql_execute;
    }

    /**
     * 获取用户定义的命名SQL语句
     *
     * @return
     */
    public String getNamingSql() {
        return sql_naming;
    }

    /**
     * 获取对象实例，此方法将缓存分析过的SQL语句以提高性能
     *
     * @param sql
     * @return
     */
    public static NSQLUtils get(String sql) {
        NSQLUtils nsql = caches.get(sql);
        if (nsql == null) {
            nsql = NSQLUtils.parse(sql);
            caches.put(sql, nsql);
        }
        return nsql;
    }

    /**
     * 分析命名SQL语句获取抽象NSQl实例；java(JDBC)提供SQL语句命名参数而是通过?标识参数位置，
     * 通过此对象可以命名参数方式使用SQL语句，命名参数以?开始后跟名称?name。
     * 例如：select * from table where name = ?key and email = ?key;
     *
     * @param sql
     * @return
     */
    public static NSQLUtils parse(String sql) {
        // select * from table where name = ?key and email = ?key;
        // A~Z a~z 01~9 _
        if (sql == null) {
            throw new NullPointerException("SQL String is null");
        }

        char c;
        List<String> names = new ArrayList<String>();
        StringBuilder sql_builder = new StringBuilder();
        StringBuilder name_builder = new StringBuilder();
        for (int index = 0; index < sql.length(); index++) {
            c = sql.charAt(index);
            if (prefix == c) {
                sql_builder.append("?");
                while (++index < sql.length()) {
                    c = sql.charAt(index);
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= '0' && c <= '9')) {
                        name_builder.append(c);
                    } else {
                        sql_builder.append(c);
                        break;
                    }
                }
                names.add(prefix + name_builder.toString());
                name_builder.setLength(0);
            } else {
                sql_builder.append(c);
            }
        }
        NSQLUtils dbsql = new NSQLUtils();
        dbsql.sql_naming = sql;
        dbsql.sql_execute = sql_builder.toString();
        dbsql.names = names.toArray(dbsql.names = new String[names.size()]);
        return dbsql;
    }

    @Override
    public String toString() {
        return "NAMING: " + sql_naming + "\nEXECUTE: " + sql_execute;
    }

}
