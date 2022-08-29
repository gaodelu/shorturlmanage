package com.mochi.shorturl.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrCodeConstants {

   PARAM_CAN_NOT_BE_NULL("0001","参数不能为空"),
   LONG_URL_IS_NOT_EXISTS("0002","删除失败"),
   URL_IS_INCORRECT("0003","url格式不正确"),
   OPERATION_FAST("0004","操作频繁，10分钟后重试"),
   SYS_ERR("9999","系统异常"),
   ;

   private String errCode;

   private String errMsg;
}
