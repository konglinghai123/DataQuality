package com.jollychic.holmes.result;

import com.jollychic.holmes.model.UserEmail;
import lombok.Data;

@Data
public class UserEmailResult {
    private boolean success;
    private UserEmail userEmail;
    private int code;
    private String msg;

    public static UserEmailResult successResult(UserEmail userEmail) {
        UserEmailResult userEmailResult = new UserEmailResult();
        userEmailResult.setSuccess(true);
        userEmailResult.setUserEmail(userEmail);
        return userEmailResult;
    }

    public static UserEmailResult errorResult(int errCode, String errMsg) {
        UserEmailResult userEmailResult = new UserEmailResult();
        userEmailResult.setSuccess(false);
        userEmailResult.setCode(errCode);
        userEmailResult.setMsg(errMsg);
        return userEmailResult;
    }
}
