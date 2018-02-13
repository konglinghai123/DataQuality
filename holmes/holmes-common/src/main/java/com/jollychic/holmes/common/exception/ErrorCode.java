package com.jollychic.holmes.common.exception;

/**
 * @DESCRIPTION: controller 返回码
 * Created by WIN7 on 2018/1/5.
 */
public class ErrorCode {
    public static final int ACCESS_TOKEN_ERROR = 40001; // 请求参数access_token错误
    public static final int FILE_SIZE_ERROR = 40002; // 不合法的文件大小
    public static final int FILE_TYPE_ERROR = 40003; // 不支持的文件类型
    public static final int PARAMETER_MISSING_ERROR = 40004; // 请求参数缺失
    public static final int PARAMETER_ERROR = 40005;    //请求参数错误
    public static final int NOT_AUTHORIZED = 40005;         //没有权限
    public static final int REQUEST_FORBIDDEN = 40300; // 请求被拒绝
    public static final int REQUEST_NOT_FOUND = 40400; // 请求不存在或错误
    public static final int REQUEST_TIMEOUT = 40800; // 请求超时
    public static final int FAIL = 50000; // 请求失败
    public static final int SERVICE_UNAVAILABLE = 50300; // 服务不可用

    public static final int RULE_ERROR = 60000;
    public static final int RULE_LOAD_ERROR = 60001;    //规则加载错误
    public static final int RULE_PARSE_ERROR = 60002;   //规则解析错误
    public static final int RULE_EXECUTION_ERROR = 60003;//规则执行错误
    public static final int SOURCE_LOAD_ERROR = 60101;  //数据源加载错误
    public static final int SOURCE_CONNECTION_ERROR = 60102;  //数据源连接错误
    public static final int SOURCE_READ_ERROR = 60103;  //数据源读取错误
    public static final int SEND_MESSAGE_ERROR = 60300;  //发送微信or邮件异常
}
