package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.Execution;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/8.
 */
public interface ExecutionMapper {
    @Select("SELECT * FROM execution WHERE execution_id = #{executionId}")
    public Execution get(int executionId);

    @Select("SELECT * FROM execution WHERE rule_id = #{ruleId}")
    public List<Execution> getByRuleId(String ruleId);

    @Select("SELECT * FROM execution WHERE rule_id = #{ruleId} AND status = #{status}")
    public Execution getByRuleIdAndStatus(Execution execution);

    @Select("SELECT * FROM execution")
    public List<Execution> getAll();

    @Delete("DELETE FROM execution WHERE rule_id = #{ruleId}")
    public void delete(String ruleId);

    @Insert("INSERT INTO execution(execution_name, rule_id, status) values(#{executionName}, #{ruleId}, #{status})")
    @Options(useGeneratedKeys=true, keyProperty="executionId")
    public void insert(Execution execution);

    @Update("UPDATE execution set status=#{status}, error_info=#{errorInfo}, updated_at=current_timestamp()"+
            "WHERE execution_id=#{executionId}")
    public void updateStatus(Execution execution);

    @Update("UPDATE execution set error_info=#{errorInfo}, updated_at=current_timestamp()"+
            "WHERE execution_id=#{executionId}")
    public void updateErrorInfo(Execution execution);

}
