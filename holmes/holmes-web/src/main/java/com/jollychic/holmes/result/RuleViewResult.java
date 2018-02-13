package com.jollychic.holmes.result;

import com.jollychic.holmes.view.RuleView;
import lombok.Data;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class RuleViewResult {
    private boolean success;
    private RuleView rule;
    private int code;
    private String msg;

    public static RuleViewResult successResult(RuleView ruleView) {
        RuleViewResult ruleViewResult = new RuleViewResult();
        ruleViewResult.setSuccess(true);
        ruleViewResult.setRule(ruleView);
        return ruleViewResult;
    }

    public static RuleViewResult errorResult(int errCode, String errMsg) {
        RuleViewResult ruleViewResult = new RuleViewResult();
        ruleViewResult.setSuccess(false);
        ruleViewResult.setCode(errCode);
        ruleViewResult.setMsg(errMsg);
        return ruleViewResult;
    }
}
