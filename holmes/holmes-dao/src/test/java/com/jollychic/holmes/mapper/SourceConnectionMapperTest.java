package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.SourceConnection;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by WIN7 on 2018/1/4.
 */
public class SourceConnectionMapperTest {
    SqlSession session;
    String id;

    @Before
    public void insert() throws Exception {
        String resource= "mybatis-config-test.xml";
        InputStream is = SourceConnectionMapperTest.class.getClassLoader().getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        session = sqlSessionFactory.openSession(true);

        String statement = "com.jollychic.holmes.mapper.SourceConnectionMapper.insert";
        SourceConnection sourceConnection = new SourceConnection();
        sourceConnection.setConnectionName("testname");
        sourceConnection.setSourceType("mysql");
        sourceConnection.setConnectionInfo("host=...");
        sourceConnection.setAuthor("admin");
        session.insert(statement, sourceConnection);
        id = sourceConnection.getConnectionId();
    }

    @Test
    public void get() throws Exception {
        String statement = "com.jollychic.holmes.mapper.SourceConnectionMapper.get";
        SourceConnection sourceConnection = session.selectOne(statement, id);
        assertEquals("testname", sourceConnection.getConnectionName());
    }

    @Test
    public void getAll() throws Exception {
        String statement = "com.jollychic.holmes.mapper.SourceConnectionMapper.getAll";
        List<SourceConnection> sourceConnections = session.selectList(statement);
        assertNotEquals(0, sourceConnections.size());
    }

    @Test
    public void update() throws Exception {
        String statement = "com.jollychic.holmes.mapper.SourceConnectionMapper.update";
        SourceConnection sourceConnection = new SourceConnection();
        sourceConnection.setConnectionId(id);
        sourceConnection.setConnectionName("testname1");
        sourceConnection.setSourceType("hive");
        sourceConnection.setConnectionInfo("host=...&port=...");
        sourceConnection.setAuthor("admin");
        sourceConnection.setUpdatedAt(new Date());
        session.update(statement, sourceConnection);
    }

    @After
    public void delete() throws Exception {
        String statement3 = "com.jollychic.holmes.mapper.SourceConnectionMapper.delete";
        session.delete(statement3, id);
        session.close();
    }

}