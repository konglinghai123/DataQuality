package com.jollychic.holmes.alarm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by WIN7 on 2018/1/18.
 */
public class EmailAlarmTest {
    @Test
    public void sendMessage() {
        String email = "evan@jollycorp.com";
        String content = "触发规则："+"evan_rule_app_volumn"+"</br>监控表："+"zydb.app_gs_top200_new"+"</br>详细信息："+
                "volumn < 1000；volumn < 1000；volumn < 1000；volumn < 1000；volumn < 1000；";
        String subject = "evan_rule_app_volumn"+"报警";
        EmailAlarm.sendMessage(email, subject, content);
    }

}