package com.jollychic.holmes.result;

import com.jollychic.holmes.view.RuleView;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class RuleViewListResult {
    private boolean success;
    private List<RuleView> rules;
    private int code;
    private String msg;

    public static RuleViewListResult successResult(List<RuleView> ruleViewList) {
        RuleViewListResult ruleViewListResult = new RuleViewListResult();
        ruleViewListResult.setSuccess(true);
        ruleViewListResult.setRules(ruleViewList);
        return ruleViewListResult;
    }

    public static RuleViewListResult errorResult(int errCode, String errMsg) {
        RuleViewListResult ruleViewListResult = new RuleViewListResult();
        ruleViewListResult.setSuccess(false);
        ruleViewListResult.setCode(errCode);
        ruleViewListResult.setMsg(errMsg);
        return ruleViewListResult;
    }
}
