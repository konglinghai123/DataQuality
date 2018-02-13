package com.jollychic.holmes.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.property.Property;
import com.jollychic.holmes.common.utils.HttpsClientUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/17.
 */
@Slf4j
public class WechatAlarm {
    public static String token = getToken();

    public synchronized static void sendMessage(String user, String title, String content){
        List<String> userList = JSON.parseObject(user, new TypeReference<List<String>>(){});
        user = "";
        int size = userList.size();
        for(int i=0; i<size-1; i++) {
            user += userList.get(i) + "|";
        }
        if(size>0) {
            user += userList.get(size-1);
        }

        log.info("send wechat message, to "+user+", content: "+title+"</br>"+content);
        String sendMessageUrl = Property.WECHAT_URL_SENDMESSAGE + "?access_token=" + token;
        String msg = title+"</br>"+content;
        String params = "{" +
                "\"touser\": \""+user+"\"," +
                "\"toparty\": \"\"," +
                "\"totag\": \"\"," +
                "\"msgtype\": \"text\"," +
                "\"agentid\": 1000005," +
                "\"text\": {" +
                "       \"content\": \""+msg+"\"" +
                "   }," +
                "\"safe\":\"0\"" +
                "}";
        String result;
        try {
            result = HttpsClientUtils.doPost(sendMessageUrl, params);
            log.info("send wechat message result: "+result);
            Map<String, String> resultMap = JSON.parseObject(result, new TypeReference<Map<String, String>>(){});
            if(!Integer.valueOf(resultMap.get("errcode")).equals(0)) {
                log.error("send wechat message error, "+resultMap.get("errmsg"));
                token = getToken();
                sendMessageUrl = Property.WECHAT_URL_SENDMESSAGE + "?access_token=" + token;
                result = HttpsClientUtils.doPost(sendMessageUrl, params);
                log.info("send wechat message result: "+result);
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.SEND_MESSAGE_ERROR, "send wechat message error, "+e.getMessage());
        }

        Map<String, String> resultMap = JSON.parseObject(result, new TypeReference<Map<String, String>>(){});
        String invalidUser = resultMap.get("invaliduser");
        if(invalidUser!=null && invalidUser.trim().length()>0) {
            throw new ServiceException(ErrorCode.SEND_MESSAGE_ERROR,
                    "user: "+resultMap.get("invaliduser")+" is invalid, send wechat message error");
        }
    }

    public static String getToken(){
        String tokenUrl = Property.WECHAT_URL_GETTOKEN + "?corpid=" + Property.WECHAT_CORPID + "&corpsecret=" + Property.WECHAT_CORPSECRET;
        String result;
        log.info("get wechat token");
        try {
            result = HttpsClientUtils.doGet(tokenUrl);
        } catch (Exception e) {
            log.error("get token error, "+e.getMessage());
            return null;
        }
        Map<String, String> resultMap = JSON.parseObject(result, new TypeReference<Map<String, String>>(){});
        return resultMap.get("access_token");
    }

}
