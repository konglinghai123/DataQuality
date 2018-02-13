package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/8.
 */
public interface SourceTableMapper {
    @Select("SELECT * FROM source_table WHERE table_id = #{tableId}")
    public SourceTable get(String tableId);
    @Select("SELECT * FROM source_table WHERE table_name = #{tableName} AND connection_id=#{connectionId}")
    public SourceTable getByTableName(SourceTable sourceTable);

    @Select("SELECT * FROM source_table")
    public List<SourceTable> getAll();
    @Select("SELECT * FROM source_table WHERE connection_id IN(" +
            "SELECT connection_id FROM source_connection WHERE connection_name=#{connName}) ")
    public List<SourceTable> getByConnName(String connName);
    @Select("SELECT * FROM source_table WHERE table_id IN (" +
            "SELECT table_id FROM table_rule_tmp WHERE rule_id=#{ruleId})")
    public SourceTable getByRuleId(String ruleId);
    @Select("SELECT * FROM source_table WHERE table_id IN (" +
            "SELECT table_id FROM table_rule_tmp WHERE rule_id IN (" +
            "SELECT rule_id FROM rule WHERE rule_name=#{ruleName}))")
    public List<SourceTable> getByRuleName(String ruleName);


    @Insert("INSERT INTO source_table(table_id, table_name, connection_id, table_schema, author) " +
            "VALUES(UUID(),#{tableName}, #{connectionId}, #{tableSchema}, #{author})")
    @Options(useGeneratedKeys = true, keyProperty = "tableId")
    public void insert(SourceTable sourceTable);

    @Delete("DELETE FROM source_table WHERE table_id = #{tableId}")
    public void delete(String tableId);


    @Update("UPDATE source_table SET table_schema=#{tableSchema}, author=#{author}, updated_at=current_timestamp() " +
            "WHERE table_id=#{tableId}")
    public void update(SourceTable sourceTable);
}
