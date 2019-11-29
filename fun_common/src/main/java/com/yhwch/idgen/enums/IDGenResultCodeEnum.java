package com.yhwch.idgen.enums;

import lombok.Getter;

@Getter
public enum IDGenResultCodeEnum {
    SUC(1,"ok"),
    FAIL(-1,"获取失败"),
    UN_FOUND_BIZ_NAME(404,"业务未配置,无法发号"),
    EXCEPTION(500,"出现异常,无法发号,请使用日志进行异常排查"),
    PARAM_INVALID(2,"参数无效"),
    INIT_FAIL(3,"发号器初始化失败"),
    BOTH_BUFFER_NOT_READY(4,"buffer均为就绪,无法发号"),
    ;

    IDGenResultCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    private int code;
    private String msg;

}
