package com.jollychic.holmes.alarm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by WIN7 on 2018/1/18.
 */
public class WechatAlarmTest {
    @Test
    public void sendMessage() {
        String user = "1199";
        String title = "Holmes 报警";
        String content = "触发规则："+"evan_rule_app_volumn"+"</br>监控表："+"zydb.app_gs_top200_new"+"</br>详细信息："+
                "volumn < 1000；volumn < 1000；volumn < 1000；volumn < 1000；volumn < 1000；";
        WechatAlarm.sendMessage(user, title, content);
    }

}