package com.moh.yehia.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moh.yehia.testing.model.ValidationError;
import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Map;
// 写法2
// 这是一个辅助断言判断的工具类, 写了一些自定义的ResultMatcher,用于andExpect()方法做断言判断
public class ResponseBodyMatchers {
	// 将对象转换为json来进行对比的方法
	// 本方法还需要两个参数, 一个是用来断言比较的对象(测试生成的对象), 及对象的class类
    public <T> ResultMatcher containsObjectAsJson(Object expected, Class<T> target) {
    	// mvcResult是调用andExpect()的对象, 是本ResultMatcher会用到的判断对象,使用lambda表达式写对其的处理逻辑
        return mvcResult -> {
        	// 使用MvcResult的方法获得其响应体的字符串(json)
            String jsonResponseAsString = mvcResult.getResponse().getContentAsString();
            // 对json进行反序列化, 转化成对象. T actualObject将actualObject的类型定义为作为参数传入的class
            T actualObject = new ObjectMapper().readValue(jsonResponseAsString, target);
            // 利用AssertJ的方法对从响应体转化出来的对象及传入的对象进行断言比较
            Assertions.assertThat(actualObject).usingRecursiveComparison().isEqualTo(expected);
        };
    }

    public ResultMatcher containsError(String expectedFieldName, String expectedMessage) {
        return mvcResult -> {
            String jsonResponseAsString = mvcResult.getResponse().getContentAsString();
            ValidationError validationError = new ObjectMapper().readValue(jsonResponseAsString, ValidationError.class);
            Map<String, String> errors = validationError.getErrors();
            String errorMessage = errors.getOrDefault(expectedFieldName, null);
            Assertions.assertThat(errorMessage)
                    .withFailMessage("expecting exactly 1 error message with field name {%s} and message {%s}", expectedFieldName, expectedMessage)
                    .isNotNull()
                    .isEqualTo(expectedMessage);
        };
    }
    // responseBody()这个方法用于获得本类的实例,以便调用本类的方法.如此设计方便做缓存或单例或注入
    static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }
}
