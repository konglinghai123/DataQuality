package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.Rule;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/8.
 */
public interface RuleMapper {
    @Select("SELECT * FROM rule WHERE rule_id = #{ruleId}")
    public Rule get(String ruleId);

    @Select("SELECT * FROM rule")
    public List<Rule> getAll();

    @Select("SELECT * FROM rule WHERE rule_type = #{ruleType}")
    public List<Rule> getByRuleType(String ruleType);

    @Select("SELECT * from rule where rule_id IN (" +
            "SELECT rule_id from table_rule_tmp where table_id=#{tableId})")
    public List<Rule> getByTableId(String tableId);

    @Select("SELECT * from rule where rule_name=#{ruleName}")
    public Rule getByRuleName(String ruleName);

    @Insert("INSERT INTO rule(rule_id, rule_name, rule_type, source_info,rule_expression, rule_description, alarm_type, alarm_user, author) " +
            "values(UUID(), #{ruleName}, #{ruleType},#{sourceInfo},#{ruleExpression},#{ruleDescription},#{alarmType},#{alarmUser},#{author})")
    @Options(useGeneratedKeys = true, keyProperty = "ruleId")
    public void insert(Rule rule);

    @Delete("DELETE FROM rule WHERE rule_id = #{ruleId}")
    public void delete(String ruleId);
    @Delete("DELETE FROM rule WHERE rule_name = #{ruleName}")
    public void deleteByRuleName(String ruleName);
    @Delete("DELETE FROM rule WHERE rule_type = #{ruleType}")
    public void deleteByRuleType(String ruleType);

    @Update("UPDATE rule set rule_name=#{ruleName}, rule_type=#{ruleType}, source_info=#{sourceInfo}, " +
            "rule_expression=#{ruleExpression}, rule_description=#{ruleDescription}, alarm_type=#{alarmType}, " +
            "alarm_user=#{alarmUser}, author=#{author}, updated_at=current_timestamp() "+
            "WHERE rule_id=#{ruleId}")
    public void update(Rule rule);
}
