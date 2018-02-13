package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.KafkaMaxOffsetManagement;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by WIN7 on 2018/2/7.
 */
public interface KafkaMaxOffsetManagementMapper {
    @Select("SELECT * FROM kafka_max_offset_management WHERE " +
            "kafka_max_offset_management_id  = #{kafkaMaxOffsetManagement}")
    public KafkaMaxOffsetManagement get(Integer kafkaMaxOffsetManagement);

    @Select("SELECT * FROM kafka_max_offset_management WHERE " +
            " rule_id = #{ruleId} AND version = #{version}")
    public KafkaMaxOffsetManagement getByRuleIdAndVersion(KafkaMaxOffsetManagement kafkaMaxOffsetManagement);

    @Select("SELECT * FROM kafka_max_offset_management WHERE " +
            "version IN (SELECT MAX(version) FROM kafka_max_offset_management WHERE rule_id = #{ruleId})")
    public KafkaMaxOffsetManagement getByRuleIdAndMaxVersion(String ruleId);

    @Select("SELECT * FROM kafka_max_offset_management WHERE rule_id = #{ruleId}")
    public List<KafkaMaxOffsetManagement> getByRuleId(String ruleId);

    @Select("SELECT * FROM kafka_max_offset_management ")
    public List<KafkaMaxOffsetManagement> getAll();

    @Insert("INSERT INTO kafka_max_offset_management(rule_id, max_offset, version) " +
            "values( #{ruleId}, #{maxOffset}, #{version})")
    @Options(useGeneratedKeys=true, keyProperty="kafkaMaxOffsetManagementId")
    public void insert(KafkaMaxOffsetManagement kafkaMaxOffsetManagement);

    @Delete("DELETE FROM kafka_max_offset_management WHERE " +
            "kafka_max_offset_management_id  = #{kafkaMaxOffsetManagementId}")
    public void delete(String kafkaMaxOffsetManagementId);
    @Delete("DELETE FROM kafka_max_offset_management WHERE " +
            "datediff(NOW(),created_at)>=#{day}")
    public void deleteByDay(Integer day);
}
