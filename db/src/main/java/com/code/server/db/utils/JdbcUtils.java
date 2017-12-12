package com.code.server.db.utils;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.*;

/**
 * Created by sunxianping on 2017/4/12.
 */
public class JdbcUtils {

    private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/test1";
    private static String username = "root";
    private static String password = "root";

    static {
        try {
            // 加载数据库驱动
            Class.forName(driver);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {

//        if (username == null) {
//            Properties s = null;
//            try {
//                s = ProperitesUtil.loadProperties("local/admin_db.properties");
//                url = "jdbc:mysql://" + s.getProperty("ip") + ":3306/test";
//                username = s.getProperty("userName");
//                password = s.getProperty("password");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return DriverManager.getConnection(url, username, password);
    }


    public static void update(String sql, Object params[]) throws SQLException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            st.executeUpdate();

        } finally {
            release(conn, st, rs);
        }
    }

    public static Object query(String sql, Object params[], ResultSetHandler rsh) throws SQLException {

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            rs = st.executeQuery();

            return rsh.handle(rs);

        } finally {
            release(conn, st, rs);
        }
    }

    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                // 关闭存储查询结果的ResultSet对象
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                // 关闭负责执行SQL命令的Statement对象
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                // 关闭Connection数据库连接对象
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
