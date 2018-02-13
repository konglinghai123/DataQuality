package com.jollychic.holmes.source.hive;

import org.junit.Test;

import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 * Created by WIN7 on 2018/2/7.
 */
public class HiveConnectionTest {
    @Test
    public void executeQuery() throws Exception {
        HiveConnection hiveConnection = new HiveConnection("jdbc:hive2://172.31.2.216:10000/default", "admin", "admin");
        ResultSet resultSet = hiveConnection.executeQuery("select * from aa limit 10");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

}