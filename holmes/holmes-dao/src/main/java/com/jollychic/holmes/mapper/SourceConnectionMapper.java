package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.SourceConnection;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/4.
 */

public interface SourceConnectionMapper {
    @Select("SELECT * FROM source_connection WHERE connection_id = #{connectionId}")
    public SourceConnection get(String connectionId);
    @Select("SELECT * FROM source_connection WHERE connection_name = #{connectionName}")
    public SourceConnection getByConnName(String connectionName);

    @Select("SELECT * FROM source_connection")
    public List<SourceConnection> getAll();

    @Insert("INSERT INTO source_connection(connection_id, connection_name, source_type, connection_info, author) " +
            "VALUES (UUID(), #{connectionName}, #{sourceType}, #{connectionInfo}, #{author})")
    @Options(useGeneratedKeys = true, keyProperty = "connectionId")
    public void insert(SourceConnection sourceConnection);

    @Delete("DELETE FROM source_connection WHERE connection_id = #{connectionId}")
    public void delete(String connectionId);

    @Delete("DELETE FROM source_connection WHERE connection_name = #{connectionName}")
    public void deleteByConnName(String connectionName);

    @Update("UPDATE source_connection SET connection_name=#{connectionName}, source_type=#{sourceType}, " +
            "connection_info=#{connectionInfo}, author=#{author}, updated_at=current_timestamp() " +
            "WHERE connection_id=#{connectionId}")
    public void update(SourceConnection sourceConnection);
}