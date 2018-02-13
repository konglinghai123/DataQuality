package com.jollychic.holmes.mapper;

import com.jollychic.holmes.model.UserEmail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserEmailMapper {
    @Select("SELECT * FROM user_email WHERE user_email_id=#{userEmailId}")
    public UserEmail get(String userEmailId);

    @Select("SELECT * FROM user_email ")
    public List<UserEmail> getAll();

    @Select("SELECT * FROM user_email WHERE user_chinese_name=#{userChineseName} AND user_english_name = #{userEnglishName}")
    public UserEmail getByName(UserEmail userEmail);

    @Insert("INSERT INTO user_email(user_email_id,user_chinese_name,user_english_name,email) VALUES " +
            "(#{userEmailId}, #{userChineseName},#{userEnglishName},#{email})")
    @Options(useGeneratedKeys = true, keyProperty = "userEmailId")
    public void insert(UserEmail userEmail);

    @Delete("DELETE FROM user_email WHERE user_email_id=#{userEmailId}")
    public void delete(String userEmailId);

    @Update("UPDATE user_email SET user_chinese_name = #{userChineseName}" +
            ", user_english_name=#{userEnglishName}, email=#{email} "+
            "WHERE user_email_id= #{userEmailId}")
    public void update(UserEmail userEmail);
}
