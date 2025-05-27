package com.moh.yehia.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// 这是一个测试基类, 让别的测试类继承以用于测试
// 为的是统一提供MockMVC和objectMapper给测试类用.以避免重复注入
// @SpringBootTest启用完整的springboot上下文,是为了做集成测试, spring容器的所有Bean类都会被注入进来, 继承此类做测试的话测试的负担会重很多
// 如果只做controller的测试, 加载所有Bean类是没有必要的, 不如@WebMvcTest(只加载Web相关的Bean如controller,Filter,json等)
@SpringBootTest
// 自动配置MockMVC,以便用MockMVC模拟http请求,测试API
@AutoConfigureMockMvc
// 只是一个基类, 定义为abstract交给其他测试类进行继承
public abstract class GlobalSpringContext {
	// 自动注入MockMvc
    @Autowired
    protected MockMvc mockMvc;
    // 自动注入ObjectMapper
    // 测试类在进行测试的时候会自动生成json字符串, 最后需要将输出的对象转换成json一遍与最开始的json字符串进行比较, 所以要手动使用到ObjectMapper, 因此需要在Spring容器自动管理外额外注入ObjectMapper
    @Autowired
    protected ObjectMapper objectMapper;
}
