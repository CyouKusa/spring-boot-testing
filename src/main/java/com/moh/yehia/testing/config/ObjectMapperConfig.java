package com.moh.yehia.testing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//这是一个配置ObjectMapper类为单例对象的方法, 注册一个单例对象交给spring托管,将ObjectMapper的时间戳配置为JavaTimeModule的ISO字符串格式
//将这里配置过的ObjectMapper交给spring托管后, spring就不会使用默认配置的ObjectMapper来作序列化/反序列化了,而是使用本配置文件配置过的ObjectMapper进行序列化/反
//可以针对性地对默认配置的ObjectMapper在序列化/反时处理错误/无法处理的部分进行操作定义,保证spring可以正常地序列化/反 程序中的对象

//配置类标注, 如果类中有@Bean的方法(说明类中有方法是Bean配置方法,这些方法应该统一写到配置类中), 就需要给类标注@Configuration,告知spring将此类定义为配置类
// 如果不加configuration,只对方法写Bean的话, spring会在每次调用@Bean的方法时,都new出一个新的返回值对象, 没有实现单例的效果
@Configuration
public class ObjectMapperConfig {
    @Bean
    // 这是一个Bean配置方法, 返回值为生成的Bean类, 方法名也为生成的Bean类, 方法的作用是通过自定义的方式生成一个对象,然后通过@Bean将此方法生成的对象交给spring作为对象管理
    // 要想spring将此对象作为单例对象管理, 就必须给类加上@configuration
    public ObjectMapper objectMapper() {
    	// 创建一个ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        // objectMapper原生不认识java8加入的新时间类, 所以提供了registerModule方法来注册这些类(让objectMapper按类定义的方式序列化与反序列化),使得objectMapper可以认识与使用这些新的日期格式
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用了object默认的data格式(纯数字),改为UTF-8的时间字符串格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
