package com.moh.yehia.testing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok库中的一些降低编码量的注解
//将类作为Data/model类处理, 自动生成getter/setter/tostring等方法
@Data
// 生成无参构造函数
@NoArgsConstructor
// 生成全参构造函数
@AllArgsConstructor
// 生成Builder方法用于链式调用 ---> ApiError.uilder().statusCode("xxx").message("xxx").path("xxx")
@Builder
//定义了全局异常处理时返回的请求体的格式
public class ApiError {
    private String statusCode;
    private String message;
    // path定义的是发来的请求的path, 用于返回异常通知的时候告知前端出错的端口是哪个,便于排查问题
    private String path;
}
