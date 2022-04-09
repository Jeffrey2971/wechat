package com.jeffrey.wechat.dao;

import com.jeffrey.wechat.entity.FeedBack;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionServiceDao {

    /**
     * 存储反馈表单到数据库中
     * @param feedBack 反馈表单项
     * @return true / false
     */
    @Insert("INSERT INTO `user_feedback` (`title`, `message`, `way`, `details`, `openid`, `ctime`) VALUE (#{title}, #{message}, #{way}, #{details}, #{openid}, #{ctime})")
    boolean saveFeedback(FeedBack feedBack);

    @Select("SELECT COUNT(openid) FROM `user_feedback` WHERE openid = #{openid}")
    int feedBackIsExists(String openid);

}
