package com.xinsite.dal.dbhelper;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.uitls.Utils_Gson;
import com.xinsite.dal.uitls.Utils_String;
import com.xinsite.dal.uitls.Utils_Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBPager {
    private int pageStart;       //分页数开始位置,从0开始
    private int pageSize;        //页大小
    private long itemCount;      //总的记录数(根据分页Sql以及给定的主键)
    private String primaryKey;   //查询表的主键字段
    private String querySQL;     //查询的sql语句,形如:"*  from tableName where 1=1"
    private String countSQL;     //总记录数语句,形如:"select count(1) from tableName where 1=1"
    private String orderColumn;  //查询sql排序表达式,形如:id desc,time asc
    private String showColumn;   //显示的字段
    private List<DBParameter> params;

    public DBPager() {
        this.params = new ArrayList<DBParameter>();
    }

    public DBPager(String querySQL, String orderColumn, int pageStart, int pageSize) {
        this.params = new ArrayList<DBParameter>();
        this.querySQL = querySQL;
        this.orderColumn = orderColumn;
        this.pageStart = pageStart;
        if (pageSize == 0) pageSize = 10;
        this.pageSize = pageSize;
    }

    /**
     * 获取表的记录数
     */
    public long getItemCount() {
        if (itemCount == 0 && StringUtils.isNotEmpty(countSQL)) {
            String str = DBFunction.executeScalar(countSQL, DBParameter.getParameter(params));
            itemCount = Utils_Value.tryParse(str, 0L);
        }
        return itemCount;
    }

    /**
     * 获取当前页的记录(JsonArray)
     */
    public JsonArray getCurPageArray() throws Exception {
        String sql = String.format("select %s %s", querySQL, getOrderColumn());
        if (querySQL.trim().toLowerCase().indexOf("select") == 0)
            sql = String.format("%s %s", querySQL, getOrderColumn());
        JsonArray array = new JsonArray();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        NSQLUtils dbsql = NSQLUtils.get(sql);
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, DBParameter.getParameter(params)); //添加查询参数
            rset = pstmt.executeQuery();
            ResultSetMetaData metaData = rset.getMetaData();
            int columnCount = metaData.getColumnCount();  // 获取列数
            String[] cols = new String[0];
            if (Utils_String.isNotEmpty(showColumn)) {
                cols = showColumn.split(",");
            }
            int index = 0;
            int lastRow = pageStart + pageSize - 1; //最后一行
            // 遍历ResultSet中的每条数据
            while (rset.next()) {
                //int rowcount = rset.getRow();    //获得当前行号，即总记录数
                if (index >= pageStart) {
                    JsonObject jsonObj = new JsonObject();
                    for (int i = 1; i <= columnCount; i++) {  // 遍历每一列
                        String columnName = metaData.getColumnLabel(i);
                        if (cols.length == 0 || Utils_String.contains(cols, columnName, true)) {
                            String value = rset.getString(columnName);
                            jsonObj.addProperty(columnName, value);
                        }
                    }
                    array.add(jsonObj);
                }
                if (index >= lastRow) break;
                index++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return array;
    }

    /**
     * 获取当前页的记录(List)
     */
    public List getCurPageList(Class clz) throws Exception {
        String sql = String.format("select %s %s", querySQL, getOrderColumn());
        List<Object> list = new ArrayList<Object>();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        NSQLUtils dbsql = NSQLUtils.get(sql);
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, DBParameter.getParameter(params)); //添加查询参数
            rset = pstmt.executeQuery();
            ResultSetMetaData metaData = rset.getMetaData();
            int columnCount = metaData.getColumnCount();  // 获取列数
            String[] cols = new String[0];
            if (Utils_String.isNotEmpty(showColumn)) {
                cols = showColumn.split(",");
            }
            int index = 0;
            int lastRow = pageStart + pageSize - 1; //最后一行
            // 遍历ResultSet中的每条数据
            while (rset.next()) {
                //int rowcount = rset.getRow();    //获得当前行号，即总记录数
                if (index >= pageStart) {
                    BeanWrapperImpl bw = new BeanWrapperImpl(clz);
                    for (int i = 1; i <= columnCount; i++) {  // 遍历每一列
                        String name = metaData.getColumnLabel(i);
                        String value = rset.getString(name);
                        try {
                            Utils_Gson.setProperty(bw, Utils_String.transferToCamel(name, false), value);
                        } catch (Exception e) {
                        }
                    }
                    list.add(bw.getWrappedInstance());
                }
                if (index >= lastRow) break;
                index++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return list;
    }

    /**
     * Mysql数据库分页语句
     */
    public String mysqlToString() {
        return String.format("select %s %s limit %d,%d", querySQL, getOrderColumn(), pageStart, pageSize);
    }

    /**
     * oracle数据库分页语句
     */
    public String oracleToString() {
        int firstNum = pageStart;
        int lastNum = pageStart + pageSize - 1;
        String sql = String.format("select %s %s", querySQL, getOrderColumn());
        String page_sql = "select * from (select rownum r,a.* from(%s) a where rownum <=%d) b where b.r >=%d";
        return String.format(page_sql, sql, lastNum, firstNum);
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getOrderColumn() {
        String str = "";
        if (Utils_String.isNotEmpty(orderColumn)) {
            if (orderColumn.toLowerCase().indexOf("order by") >= 0) return orderColumn;
            str = " order by " + orderColumn;
        }
        return str;
    }

    public List<DBParameter> getParams() {
        return params;
    }

    public void setParams(List<DBParameter> params) {
        this.params = params;
    }

    public void setShowColumn(String showColumn) {
        this.showColumn = showColumn;
    }
}
