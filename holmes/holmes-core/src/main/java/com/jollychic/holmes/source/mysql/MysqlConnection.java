package com.jollychic.holmes.source.mysql;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * Created by WIN7 on 2018/1/15.
 */
@Slf4j
public class MysqlConnection {
    private String url;
    private String user;
    private String passwd;
    private Connection connection;

    public MysqlConnection(String url, String user, String passwd) throws SQLException {
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        connect();
    }

    public boolean validate() {
        try {
            return connection.createStatement().executeQuery("select 1").next();
        } catch (SQLException e) {
            log.error("", e);
            return false;
        }
    }

    public void connect() throws SQLException {
        if (connection == null || !validate()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, passwd);
            } catch (ClassNotFoundException e1) {
                log.error("", e1);
            } catch (SQLException e2) {
                throw e2;
            }
        }
    }

    public boolean execute(String sql) throws SQLException {
        connect();
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("execute sql["+sql+"] error, ", e);
            return false;
        }
        return true;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        connect();
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("", e);
        }
    }
}
