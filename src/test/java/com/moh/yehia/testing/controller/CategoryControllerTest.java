package com.moh.yehia.testing.controller;

import com.github.javafaker.Faker;
import com.moh.yehia.testing.asserts.ApiErrorAssert;
import com.moh.yehia.testing.asserts.CategoryAssert;
import com.moh.yehia.testing.model.ApiError;
import com.moh.yehia.testing.model.Category;
import com.moh.yehia.testing.model.CategoryRequest;
import com.moh.yehia.testing.service.design.CategoryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

//继承了编写的GlobalSpringContext对象, 以获得MockMvc与ObjectMapper单例
class CategoryControllerTest extends GlobalSpringContext {
	// @MockBean注解使spring容器中的categoryService改成由Mockito创建的mock实例, 而非真实实现, 使得测试时不会调用到真实实现,使得mock测试可以不用依赖真实环境中的service,不用创建相关对象
	// web层(集成)测试中对于类需要依赖的实例都可以通过@MockBean创建mock测试实例
    @MockBean
    private CategoryService categoryService;
    // 将controller定义的@RequestMapping路径写死方便后续调用
    private final String API_URL = "/api/v1/categories";
    // faker用于生成伪数据, 在测试中很常用. 是个单例对象
    private static Faker faker;
    //@BeforeAll是一个生命周期注解, 表示在所有测试方法执行之前, 先执行这个方法
    // 通常用于打印日志,初始化静态变量, 搭建测试环境等
    @BeforeAll
    // 初始化Faker变量方法, 语言环境定位英语环境(locale)
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }
    // @Test标记下面的方法为测试方法, 告诉Junit在测试时运行此方法 
    @Test
    // 本测试对应controller的findAll() 
    void shouldReturnAllCategories() throws Exception {
        // 调用私有的populateRandomCategories()方法创建一个模拟List数据出来
        List<Category> expectedCategories = populateRandomCategories();
        // BDDMockito是将Mockito的方法改写得更符合自然语义,更直观易懂的工具类(如when→given,thenReturn→willReturn)
        // 使用BDDMockito写模拟service动作,更加易懂. 这一句设定模拟了service的findall(),return一个mock的expectedCategories数据
        BDDMockito.given(categoryService.findAll()).willReturn(expectedCategories);
        // 使用MockMvc模拟浏览器请求,perform()表示发起一个请求,参数为请求格式
        // perform()这个方法运行时会模拟将请求发给spring的servlet,然后根据url走到指定的controller方法, 完整调用方法, 但service的调用使用上面定义的模拟service直接返回值
        // perform()执行完后获得返回值,后续的andDo()与andExpect()都是将controller的返回值拿来使用与做判断
        MvcResult mvcResult = mockMvc.perform(
        				// 用MockMvcRequestBuilders构造一个get请求,请求地址为controller的接收地址
                        MockMvcRequestBuilders.get(API_URL)
                        		// 链式编程配置请求的请求体contentType为json
                        		// 如果测的是一个get请求的api, 是可以不设置这个的,因为get请求没有请求体
                                .contentType(MediaType.APPLICATION_JSON)
                // 要求执行请求后在控制台打印请求与响应日志用于debug分析
                // andDo()是在MockMvc模拟执行请求的过程中,附加一个Handler使得可以对执行的结果进行处理, 最常用的方式就是打印请求与响应
                ).andDo(MockMvcResultHandlers.print())
        		// 断言响应码,是否为200 OK
        		// andExpect()方法接收各种ResultMatcher来做断言判断.本质上是个函数式接口, 用参数(ResultMatcher)写的逻辑对调用其的对象进行断言判断
        		// isOK()就是一个针对result(调用)写的lambda表达式, 判断result的status是否为200
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 最后返回MvcResult对象, 包括响应头响应内容和状态码. 不写的话perform().andDo().andExpect()执行完后没有返回值MvcResult
                .andReturn();
        // 方法1 直接在测试结尾手写断言比较逻辑
        // 抽取响应体为字符串出来,可能是个json对象
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        // Assertions.assertThat()是AssertJ断言库的入口, 将传入的参数包装成Assertion object对象, 以便调用各种断言方法进行对比
        // isEqualToIgnoringWhitespace()比较参数与调用对象在忽略空格、换行、制表符差异下，内容是否相等
        Assertions.assertThat(actualResponseAsString).isEqualToIgnoringWhitespace(
        		// 使用objectMapper将随机生成的list对象序列化以便断言比较
                objectMapper.writeValueAsString(expectedCategories)
        );
    }

    @Test
    void shouldReturnCategoryWhenValidId() throws Exception {
        // mock
        Category category = new Category(UUID.randomUUID().toString(), "random name", "random category description");
        // given
        BDDMockito.given(categoryService.findById(ArgumentMatchers.anyString())).willReturn(category);
        // when, perform & assert
        mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 使用手写的ResultMatcher类对category类做深度比较, 确认返回的category对象符合预期
                .andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(category, Category.class));
    }
    // 测试get(API_URL + "/{id}对应的controller方法在service返回null的时候应该给前端返回的异常是否正确/符合预期
    @Test
    void shouldThrowAnExceptionWhenInvalidId() throws Exception {
        // given
        BDDMockito.given(categoryService.findById(ArgumentMatchers.anyString())).willReturn(null);
        // when, perform & assert
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        //用ObjectMapper的readValue()将响应体反序列化成ApiError对象
        ApiError actualApiError = objectMapper.readValue(actualResponseAsString, ApiError.class);
        // 调用下面手写的方法, 定义为手动编辑的报错字符串
        ApiError expectedApiError = populateError();
        
        // 利用我们人为编写的APIError断言类来判断返回的这个ApiError对象与我们预期的(下面手动定义的)对象是否一致
        ApiErrorAssert.assertThat(actualApiError)
                .isNotNull()
                // 判断ApiError的statusCode属性是否一致的方法
                .hasStatusCode(expectedApiError.getStatusCode())
                // 同上
                .hasMessage(expectedApiError.getMessage())
                .hasPath(expectedApiError.getPath());
    }

    @Test
    void shouldSaveCategoryWhenValidData() throws Exception {
        // mock
        CategoryRequest categoryRequest = new CategoryRequest("name", "some random description");
        Category category = new Category(UUID.randomUUID().toString(), categoryRequest.getName(), categoryRequest.getDescription());
        // given
        BDDMockito.given(categoryService.save(ArgumentMatchers.any(CategoryRequest.class))).willReturn(category);
        // when, verify & assertions
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        Category actualCategory = objectMapper.readValue(actualResponseAsString, Category.class);
        CategoryAssert.assertThat(actualCategory)
                .hasId()
                .hasName(category.getName())
                .hasDescription(category.getDescription());
    }

    @Test
    void shouldThrowAnExceptionWhenInvalidData() throws Exception {
        // mock
        CategoryRequest categoryRequest = new CategoryRequest("", "");
        // when, verify & assertions
        mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody().containsError("name", "must not be blank"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("description", "must not be blank"))
                .andReturn();
    }

    // 内部方法用于生成数个随机categories返回需要的List对象
    private List<Category> populateRandomCategories() {
    	// 使用Category方法的builder链式调用创建对象, id使用UUID的randomUUID()生成, name使用faker的commerce()(商品相关假数据).department()(随机商品分类名)生成,
    	// funnyName()(搞笑类).name()(搞笑的人名字符串)生成一个任意字符串
        return Arrays.asList(
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build()
        );
    }

    private ApiError populateError() {
        return new ApiError("INVALID_REQUEST", "Category not found with this id: 123456", "uri=" + API_URL + "/123456");
    }

}