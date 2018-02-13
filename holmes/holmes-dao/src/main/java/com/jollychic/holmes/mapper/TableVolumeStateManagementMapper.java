package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.TableVolumeStateManagement;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/26.
 */
public interface TableVolumeStateManagementMapper {
    @Select("SELECT * FROM table_volume_state_management WHERE " +
            "table_volume_state_management_id  = #{tableVolumeStateManagementId}")
    public TableVolumeStateManagement get(Integer tableVolumeStateManagementId);

    @Select("SELECT * FROM table_volume_state_management WHERE " +
            " rule_id = #{ruleId} AND version = #{version}")
    public TableVolumeStateManagement getByRuleIdAndVersion(TableVolumeStateManagement tableVolumeStateManagement);

    @Select("SELECT * FROM table_volume_state_management WHERE " +
            "version IN (SELECT MAX(version) FROM table_volume_state_management WHERE rule_id = #{ruleId})")
    public TableVolumeStateManagement getByRuleIdAndMaxVersion(String ruleId);

    @Select("SELECT * FROM table_volume_state_management WHERE rule_id = #{ruleId}")
    public List<TableVolumeStateManagement> getByRuleId(String ruleId);

    @Select("SELECT * FROM table_volume_state_management ")
    public List<TableVolumeStateManagement> getAll();

    @Insert("INSERT INTO table_volume_state_management(rule_id, table_names, version) " +
            "values( #{ruleId}, #{tableNames}, #{version})")
    @Options(useGeneratedKeys=true, keyProperty="tableVolumeStateManagementId")
    public void insert(TableVolumeStateManagement tableVolumeStateManagement);

    @Delete("DELETE FROM table_volume_state_management WHERE " +
            "table_volume_state_management_id  = #{tableVolumeStateManagementId}")
    public void delete(String tableVolumeStateManagementId);
}
