package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.Alarm;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/10.
 */
public interface AlarmMapper {
    @Select("SELECT * FROM alarm WHERE execution_id = #{executionId}")
    public List<Alarm> getByExecutionId(Integer executionId);

    @Select("SELECT * FROM alarm WHERE execution_id = #{executionId} and alarm = #{alarm}")
    public List<Alarm> getByExecutionIdAndAlarm(Alarm alarm);

    @Select("SELECT * FROM alarm WHERE rule_id = #{ruleId}")
    public List<Alarm> getByRuleId(String ruleId);

    @Select("SELECT * FROM alarm WHERE rule_id = #{ruleId} and alarm = #{alarm}")
    public List<Alarm> getByRuleIdAndAlarm(Alarm alarm);

    @Select("SELECT * FROM alarm ")
    public List<Alarm> getAll();

    @Insert("INSERT INTO alarm(execution_id, rule_id, alarm, alarm_info) values(#{executionId}, #{ruleId}, #{alarm}, #{alarmInfo})")
    @Options(useGeneratedKeys=true, keyProperty="alarmId")
    public void insert(Alarm alarm);
}
