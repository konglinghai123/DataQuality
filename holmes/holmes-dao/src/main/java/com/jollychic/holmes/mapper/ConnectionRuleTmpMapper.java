package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.ConnectionRuleTmp;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/26.
 */
public interface ConnectionRuleTmpMapper {
    @Select("SELECT * FROM connection_rule_tmp WHERE connection_rule_tmp_id = #{connectionRuleTmpId}")
    public ConnectionRuleTmp get(int connectionRuleTmpId);

    @Select("SELECT * FROM connection_rule_tmp")
    public List<ConnectionRuleTmp> getAll();

    @Select("SELECT * FROM connection_rule_tmp WHERE connection_rule_tmp_id = #{connectionRuleTmpId} " +
            "AND connection_id = #{connectionId}")
    public ConnectionRuleTmp getByRuleIdAndConnectionId(ConnectionRuleTmp connectionRuleTmp);

    @Select("SELECT * FROM connection_rule_tmp WHERE connection_id = #{connectionId}")
    public List<ConnectionRuleTmp> getByConnectionId(String connectionId);

    @Insert("INSERT INTO connection_rule_tmp(rule_id, connection_id,author) " +
            "VALUES(#{ruleId}, #{connectionId}, #{author})")
    @Options(useGeneratedKeys = true, keyProperty = "connectionRuleTmpId")
    public void insert(ConnectionRuleTmp connectionRuleTmp);

    @Delete("DELETE FROM connection_rule_tmp WHERE connection_rule_tmp_id = #{connectionRuleTmpId}")
    public void delete(Integer connectionRuleTmpId);
    @Delete("DELETE FROM connection_rule_tmp WHERE rule_id = #{ruleId}")
    public void deleteByRuleId(String ruleId);
    @Delete("DELETE FROM connection_rule_tmp WHERE rule_id IN (" +
            "SELECT rule_id FROM rule WHERE rule_name= #{ruleName})")
    public void deleteByRuleName(String ruleName);


    @Delete("DELETE FROM connection_rule_tmp WHERE connection_id = #{connectionId}")
    public void deleteByConnectionId(String connectionId);
    @Delete("DELETE FROM connection_rule_tmp WHERE connection_id IN (" +
            "SELECT connection_id FROM source_connection WHERE connection_name= #{connectionName})")
    public void deleteByConnectionName(String connectionName);

    @Update("UPDATE connection_rule_tmp SET connection_id=#{connectionId}, rule_id=#{ruleId}, " +
            "author=#{author}, updated_at=current_timestamp() "+
            "WHERE connection_rule_tmp_id = #{connectionRuleTmpId}")
    public void update(ConnectionRuleTmp connectionRuleTmp);
}
