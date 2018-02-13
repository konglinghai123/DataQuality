package com.jollychic.holmes.result;

import com.jollychic.holmes.model.UserEmail;
import lombok.Data;

import java.util.List;
@Data
public class UserEmailListResult {
    private boolean success;
    private List<UserEmail> userEmails;
    private int code;
    private String msg;

    public static UserEmailListResult successResult(List<UserEmail> userEmails) {
        UserEmailListResult userEmailListResult = new UserEmailListResult();
        userEmailListResult.setSuccess(true);
        userEmailListResult.setUserEmails(userEmails);
        return userEmailListResult;
    }

    public static UserEmailListResult errorResult(int errCode, String errMsg) {
        UserEmailListResult userEmailListResult = new UserEmailListResult();
        userEmailListResult.setSuccess(false);
        userEmailListResult.setCode(errCode);
        userEmailListResult.setMsg(errMsg);
        return userEmailListResult;
    }
}
