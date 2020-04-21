package com.xinsite.dal.dbhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.datasource.DataSourceHolder;
import com.xinsite.dal.datasource.DynamicDataSource;
import com.xinsite.dal.properties.ReadProperties;
import com.xinsite.dal.uitls.Utils_Context;
import com.xinsite.dal.uitls.Utils_Gson;
import com.xinsite.dal.uitls.Utils_String;
import com.xinsite.dal.uitls.Utils_Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DruidUtils {

    private static final Logger log = LoggerFactory.getLogger(DruidUtils.class);

    private static DynamicDataSource dataSource = null;

    private static ThreadLocal<Connection> container = new ThreadLocal<Connection>(); //声明线程共享变量

    static {
        if (dataSource == null) {
            dataSource = Utils_Context.getBean(DynamicDataSource.class);
            if (dataSource != null) dataSource.setLenientFallback(false);
        }
        if (dataSource == null) ReadProperties.addDataSource(DataSourceHolder.getMasterKey()); //以文件读取形式加载主数据库
    }

    /**
     * 获取数据连接
     */
    protected static Connection getConnection() throws Exception {
        Connection conn = container.get();//首先获取当前线程的连接
        try {
            //log.info("当前数据源{}", DataSourceHolder.getDBKey());
            if (conn == null || conn.isClosed()) {
                if (dataSource != null)
                    conn = dataSource.getConnection();
                else
                    conn = ReadProperties.getConnection();

                if (conn == null) throw new RuntimeException("获取数据源连接失败！");
                //System.out.println(Thread.currentThread().getName() + "连接已经开启......");
                container.set(conn);
            }
        } catch (Exception e) {
            //数据源没有配置，再以文件的形式获取一遍
            if (dataSource != null) conn = ReadProperties.getConnection();
            if (conn == null) {
                log.error(e.getMessage());
                throw e;
            }
        }
        return conn;
    }

    /***获取当前线程上的连接开启事务*/
    protected static void startTransaction(String... data_source) {
        if (data_source != null && data_source.length > 0) {
            DataSourceHolder.setDataSourceType(data_source[0]); //指定数据源
        } else {
            DataSourceHolder.setDataSourceType(DataSourceHolder.getDBKey()); //设定的数据源
        }

        try {
            Connection conn = getConnection();//从连接池中获取连接
            conn.setAutoCommit(false);//开启事务
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    //提交事务
    protected static void commit() {
        try {
            Connection conn = container.get();//从当前线程上获取连接if(conn!=null){//如果连接为空，则不做处理
            if (conn != null) {
                conn.commit();//提交事务
                conn.close();  //关闭连接
                //System.out.println(Thread.currentThread().getName() + "事务已经提交......");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                DataSourceHolder.clearDataSourceType();
                container.remove();//从当前线程移除连接切记
            } catch (Exception e2) {
                log.error(e2.getMessage());
            }
        }
    }

    /***回滚事务*/
    protected static void rollback() {
        try {
            Connection conn = container.get();//检查当前线程是否存在连接
            if (conn != null) {
                conn.rollback();//回滚事务
                conn.close();   //关闭连接
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                DataSourceHolder.clearDataSourceType();
                container.remove();//从当前线程移除连接切记
            } catch (Exception e2) {
                log.error(e2.getMessage());
            }
        }
    }

    /***关闭连接*/
    protected static void close() {
        Connection conn = container.get();
        boolean autoCommit = true;
        try {
            if (conn != null) {
                autoCommit = conn.getAutoCommit();
                if (autoCommit) conn.close(); //不是事务操作
                //System.out.println(Thread.currentThread().getName() + "连接关闭");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (autoCommit) {
                    //1、不是事务操作,有可能在同一方法中操作多个同数据源sql,
                    //2、注释3后，请确保新的请求前是主数据源，其他请求手动切换
                    //3、加DataSourceHolder.clearDataSourceType();
                    //不加3，是因为1，不然每次Sql操作都要设置数据源
                    container.remove();//从当前线程移除连接切记
                }
            } catch (Exception e2) {
                log.error(e2.getMessage());
            }
        }
    }

    /**
     * 关闭数据库连接对象
     */
    private static void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 判断连接是否可用
    private boolean isValid(Connection conn) {
        try {
            if (conn == null || conn.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return true;
    }

    /**
     * 关闭数据库操作对象
     */
    protected static void closeStmt(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 关闭数据库操作对象
     */
    protected static void closePstmt(PreparedStatement pstmt) {
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 关闭数据库操作对象
     */
    protected static void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void printErrorLog(Exception e, String sql) {
        log.error(e.getMessage());
        log.error("==================error sql========================");
        log.error(sql);
    }

    /**
     * 获取表的记录数
     */
    protected static long getRowCount(String sql, DBParameter... params) throws Exception {
        int rowcount = 0;
        Connection conn = DruidUtils.getConnection();
        NSQLUtils dbsql = NSQLUtils.get(sql);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            rset = pstmt.executeQuery();
            rset.last();    //光标在最后一行
            rowcount = rset.getRow();    //获得当前行号，即总记录数
        } catch (Exception e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return rowcount;
    }

    /**
     * 根据Sql语句，获取1*1的字段值
     */
    protected static String executeScalar(String sql, DBParameter... params) throws Exception {
        Connection conn = DruidUtils.getConnection();
        String value = "";
        NSQLUtils dbsql = NSQLUtils.get(sql);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            rset = pstmt.executeQuery();
//            Statement stmt = conn.createStatement();
//            ResultSet obj = stmt.executeQuery(sql);
            if (rset.next()) {
                value = rset.getString(1);
            }
        } catch (Exception e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return value;
    }

    /**
     * 更新Sql，返回更新的行数
     */
    protected static int executeNonQuery(String sql, DBParameter... params) throws Exception {
        Connection conn = DruidUtils.getConnection();
        NSQLUtils dbsql = NSQLUtils.get(sql);
        PreparedStatement pstmt = null;
        Statement statement = null;
        try {
            String[] sqls = sql.split(";");
            if ((params == null || params.length == 0) && sqls.length > 1) {
                statement = conn.createStatement();
                for (String ss : sqls) {
                    statement.addBatch(ss);
                }
                int[] count = statement.executeBatch();
                int sum = 0;
                for (int i : count) sum += i;
                return sum;
            } else {
                pstmt = conn.prepareStatement(dbsql.getSql());
                dbsql.setParameters(sql, pstmt, params); //添加查询参数
                return pstmt.executeUpdate();
            }
//            Statement stmt = conn.createStatement();
//            return stmt.executeUpdate(sql);
        } catch (Exception e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeStmt(statement);
        }
    }

    /**
     * 新增数据表，返回新增的主键Id
     */
    protected static <T> T insertTable(String sql, T id, DBParameter... params) throws Exception {
        Connection conn = DruidUtils.getConnection();
        NSQLUtils dbsql = NSQLUtils.get(sql);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            pstmt = conn.prepareStatement(dbsql.getSql(), Statement.RETURN_GENERATED_KEYS);
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            id = Utils_Value.tryParse(pstmt.executeUpdate(), id);
            try {
                if (sql.toLowerCase().indexOf("insert") >= 0) {
                    rset = pstmt.getGeneratedKeys(); //对于有自增的返回自增值
                    if (rset.next() && rset.getMetaData().getColumnCount() >= 1) {
                        id = Utils_Value.tryParse(rset.getString(1), id);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return id;
    }

    /**
     * 新增数据表，返回新增的主键Id
     */
    protected static int executeAddTable(String sql, DBParameter... params) throws Exception {
        Connection conn = DruidUtils.getConnection();
        NSQLUtils dbsql = NSQLUtils.get(sql);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int id = 0;
        try {
            pstmt = conn.prepareStatement(dbsql.getSql(), Statement.RETURN_GENERATED_KEYS);
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            id = pstmt.executeUpdate();
            try {
                if (sql.toLowerCase().indexOf("insert") >= 0) {
                    rset = pstmt.getGeneratedKeys(); //对于有自增的返回自增值
                    if (rset.next() && rset.getMetaData().getColumnCount() >= 1) {
                        id = rset.getInt(1);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return id;
    }

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成JsonArray数组
     */
    protected static JsonArray executeJsonArray(String sql, DBParameter... params) throws Exception {
        return executeJsonArray(sql, "", params);
    }

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成JsonArray数组
     */
    protected static JsonArray executeJsonArray(String sql, Map<String, Object> params) throws Exception {
        return executeJsonArray(sql, "", params);
    }

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成JsonArray数组
     */
    protected static JsonArray executeJsonArray(String sql, String columns, Map<String, Object> params) throws Exception {
        return executeJsonArray(sql, columns, DBParameter.getParameter(params));
    }

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成对象数组
     */
    protected static List executeList(String sql, Class clz, DBParameter... params) throws Exception {
        List<Object> list = new ArrayList<Object>();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        NSQLUtils dbsql = NSQLUtils.get(sql);
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            rset = pstmt.executeQuery();
            ResultSetMetaData metaData = rset.getMetaData();
            int columnCount = metaData.getColumnCount();  // 获取列数
            // 遍历ResultSet中的每条数据
            while (rset.next()) {
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
        } catch (SQLException e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return list;
    }

    /**
     * 根据Sql语句，获取转换成对象数组
     */
    protected static List executeList(String sql, Map<String, Object> params, Class clz) throws Exception {
        return executeList(sql, clz, DBParameter.getParameter(params));
    }

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成ListMaps数组
     */
    protected static List<Map<String, Object>> executeListMaps(String sql, String columns, DBParameter... params) throws Exception {
        List<Map<String, Object>> array = new ArrayList<>();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        NSQLUtils dbsql = NSQLUtils.get(sql);
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            rset = pstmt.executeQuery();
            ResultSetMetaData metaData = rset.getMetaData();
            int columnCount = metaData.getColumnCount();  // 获取列数
            String[] cols = new String[0];
            if (Utils_String.isNotEmpty(columns)) {
                cols = columns.split(",");
            }

            // 遍历ResultSet中的每条数据
            while (rset.next()) {
                int rowcount = rset.getRow();    //获得当前行号，即总记录数
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {  // 遍历每一列
                    String columnName = metaData.getColumnLabel(i);
                    if (cols.length == 0 || Utils_String.contains(cols, columnName, true)) {
                        String value = rset.getString(columnName);
                        map.put(columnName, value);
                    }
                }
                array.add(map);
            }
        } catch (SQLException e) {
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return array;
    }

    //region 按指定数据源获取数据

    /**
     * 根据Sql语句(where中的参数已经在Sql中)，获取转换成JsonArray数组
     */
    protected static JsonArray executeJsonArray(String sql, String columns, DBParameter... params) throws Exception {
        JsonArray array = new JsonArray();
        Connection conn = DruidUtils.getConnection();

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        NSQLUtils dbsql = NSQLUtils.get(sql);
        try {
            pstmt = conn.prepareStatement(dbsql.getSql());
            dbsql.setParameters(sql, pstmt, params); //添加查询参数
            rset = pstmt.executeQuery();
            ResultSetMetaData metaData = rset.getMetaData();
            int columnCount = metaData.getColumnCount();  // 获取列数
            String[] cols = new String[0];
            if (Utils_String.isNotEmpty(columns)) {
                cols = columns.split(",");
            }

            // 遍历ResultSet中的每条数据
            while (rset.next()) {
                int rowcount = rset.getRow();    //获得当前行号，即总记录数
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
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            printErrorLog(e, sql);
            throw e;
        } finally {
            DruidUtils.close();
            DruidUtils.closePstmt(pstmt);
            DruidUtils.closeRs(rset);
        }
        return array;
    }
    //endregion
}
