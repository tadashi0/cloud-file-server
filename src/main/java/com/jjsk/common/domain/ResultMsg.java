package com.jjsk.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码
 * <p>
 * 状态码规则：
 * 1xxx: 公共状态码
 */
@Getter
@AllArgsConstructor
public enum ResultMsg {

    /**
     * 公共状态码
     */
    SUCCESS(200, "操作成功"),
    FAIL(402, "请求失败"),
    ILLEGAL_REQUEST(400, "非法请求"),
    NOT_AUTHORIZATION(401, "未授权"),
    ILLEGAL_PARAM(403, "参数异常"),
    FALL_BACK(405, "断路返回"),
    SERVER_ERROR(500, "服务器异常"),
    ERROR(-1, "系统开小差了"),

    ARGUMENT_NOT_INVALID(1001, "参数无效"),
    DATA_NOT_FOUND(1002, "没有找到记录"),
    PARAM_IS_ILLEGAL(1003, "包含非法字符"),
    VALIDATOR_ERROR(1004, "数据校验异常"),
    REPEAT_OPERATION(1005, "亲，您已操作过，请勿重复操作"),
    ;

    private final Integer code;
    private final String msg;
}
