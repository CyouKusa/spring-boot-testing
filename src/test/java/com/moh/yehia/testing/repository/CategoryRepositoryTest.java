package com.moh.yehia.testing.repository;

import com.moh.yehia.testing.model.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Arrays;
import java.util.List;
// @DataMongoTest是springboot提供的专门用于mongoDB测试的注解
// 此注解下springboot会自动扫描相关的repository/template, 只启动数据访问相关部分的容器,	轻量测试. 
// 还会根据项目配置文件连接数据库, 如果类继承了BaseMongoContainer,就会转而连接到BaseMongoContainer配置的临时mongoDB容器上, 防止数据污染
@DataMongoTest
class CategoryRepositoryTest extends BaseMongoContainer {
	// RepositoryTest不需要mock service或controller
	// 因为这里是集成测试, 需要用到spring容器, 所以使用@Autowired注入容器中的对象, 而没必要用@InjectMocks强行注入依赖(没有经过容器)
    @Autowired
    private CategoryRepository categoryRepository;
    // 手写每次测试之后的数据清理
    @AfterEach
    void clearUp() {
    	// deleteAll()方法是Repository 接口的方法, 不需要手写在categoryRepository接口中也可以直接调用.
        categoryRepository.deleteAll();
    }

    @Test
    void shouldReturnCategories() {
        List<Category> categories = Arrays.asList(
                Category.builder().name("category 01").description("category 01 description").build(),
                Category.builder().name("category 02").description("category 02 description").build()
        );
        categoryRepository.saveAll(categories);

        Assertions.assertThat(categoryRepository.findAll())
                .isNotNull()
                .hasSameSizeAs(categories);
    }

    @Test
    void shouldSaveCategoryWithValidData() {
        Category category = Category.builder().name("category 01").description("category 01 description").build();
        Category savedCategory = categoryRepository.save(category);
        Assertions.assertThat(savedCategory)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(category);
    }

    @Test
    void shouldReturnCategoryWhenValidId() {
        Category savedCategory = categoryRepository.save(Category.builder().name("category 01").description("category 01 description").build());

        Assertions.assertThat(savedCategory)
                .isNotNull();
        Assertions.assertThat(categoryRepository.findById(savedCategory.getId()))
                .isNotNull()
                .containsInstanceOf(Category.class)
                .get()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("name")
                .hasFieldOrProperty("description");
    }

    @Test
    void shouldReturnNullWhenNotFoundId() {
        Assertions.assertThat(categoryRepository.findById("123465"))
                .isNotPresent()
                .isEmpty();
    }
}