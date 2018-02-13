package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.TableRuleTmp;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/8.
 */
public interface TableRuleTmpMapper {
    @Select("SELECT * FROM table_rule_tmp WHERE tmp_id = #{tmpId}")
    public TableRuleTmp get(int tmpId);

    @Select("SELECT * FROM table_rule_tmp")
    public List<TableRuleTmp> getAll();

    @Select("SELECT * FROM table_rule_tmp WHERE table_id = #{tableId} AND rule_id = #{ruleId}")
    public TableRuleTmp getByRuleIdAndTableId(TableRuleTmp tableRuleTmp);

    @Select("SELECT * FROM table_rule_tmp WHERE table_id = #{tableId}")
    public List<TableRuleTmp> getByTableId(String tableId);

    @Insert("INSERT INTO table_rule_tmp(table_id, rule_id, author) " +
            "VALUES(#{tableId}, #{ruleId}, #{author})")
    @Options(useGeneratedKeys = true, keyProperty = "tmpId")
    public void insert(TableRuleTmp tableRuleTmp);

    @Select("SELECT tmp_id FROM table_rule_tmp WHERE table_id = #{tableId} AND rule_id= #{ruleId}")
    public Integer getTmpId(TableRuleTmp tableRuleTmp);

    @Delete("DELETE FROM table_rule_tmp WHERE tmp_id = #{tmpId}")
    public void delete(Integer tmpId);
    @Delete("DELETE FROM table_rule_tmp WHERE rule_id = #{ruleId}")
    public void deleteByRuleId(String ruleId);
    @Delete("DELETE FROM table_rule_tmp WHERE rule_id IN (" +
            "SELECT rule_id FROM rule WHERE rule_name= #{ruleName})")
    public void deleteByRuleName(String ruleName);


    @Delete("DELETE FROM table_rule_tmp WHERE table_id = #{tableId}")
    public void deleteByTableId(String tableId);
    @Delete("DELETE FROM table_rule_tmp WHERE table_id IN (" +
            "SELECT table_id FROM source_table WHERE table_name= #{tableName})")
    public void deleteByTableName(String tableName);

    @Update("UPDATE table_rule_tmp SET table_id=#{tableId}, rule_id=#{ruleId}, " +
            "author=#{author}, updated_at=current_timestamp() "+
            "WHERE tmp_id=#{tmpId}")
    public void update(TableRuleTmp tableRuleTmp);
}
