package com.moh.yehia.testing.service;

import com.github.javafaker.Faker;
import com.moh.yehia.testing.model.Category;
import com.moh.yehia.testing.model.CategoryRequest;
import com.moh.yehia.testing.repository.CategoryRepository;
import com.moh.yehia.testing.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.moh.yehia.testing.asserts.ProjectAssertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	// service层的测试因为脱离Spring容器, 所以可以不用@MockBean来模拟容器中的对象, 只需要使用@Mock将service依赖的Repository对象替换为Mock对象即可
    @Mock
    private CategoryRepository categoryRepository;
    // 依赖注入到准备测试的service类上(本CategoryTest测的就是CategoryServiceImpl类中的各方法的业务逻辑)以实现对对象类的单体测试
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Faker faker;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }
    
    @Test
    void shouldReturnCategories() {
        // 模拟一个DAO的返回结果
        List<Category> categories = populateCategoriesList();
        // 模拟repository的方法将生成的返回结果传回给service
        BDDMockito.given(categoryRepository.findAll()).willReturn(categories);
        // 调用service的方法,获得运行结果
        List<Category> retrievedCategories = categoryService.findAll();
        // 用Assertj的方法断言比较前后两个list
        // 确认运行结果不为null
        Assertions.assertThat(retrievedCategories).isNotNull()
        		// 确认运行结果与mock的对象容量相同
                .hasSameSizeAs(categories)
                // 确认运行结果中的每个对象不为null
                .doesNotContainNull();
    }

    @Test
    void shouldReturnCategoryByValidCategoryId() {
        // mock
        Category category = populateRandomCategory();
        // given
        BDDMockito.given(categoryRepository.findById(ArgumentMatchers.anyString())).willReturn(Optional.of(category));
        // when
        Category retrievedCategory = categoryService.findById("123456");
        // then or assertions
        Assertions.assertThat(retrievedCategory).isNotNull();
        Assertions.assertThat(retrievedCategory.getId()).isEqualTo(category.getId());
        Assertions.assertThat(retrievedCategory.getName()).isEqualTo(category.getName());
        Assertions.assertThat(retrievedCategory.getDescription()).isEqualTo(category.getDescription());
    }

    @Test
    void shouldSaveCategoryWithValidData() {
        // mock
        CategoryRequest categoryRequest = populateRandomCategoryRequest();
        Category category = populateSavedCategory(categoryRequest);
        // given
        BDDMockito.given(categoryRepository.save(ArgumentMatchers.any(Category.class))).willReturn(category);
        // when
        Category savedCategory = categoryService.save(categoryRequest);
        // then or assertions
        assertThat(savedCategory)
                .hasId()
                .hasName(categoryRequest.getName())
                .hasDescription(categoryRequest.getDescription());
    }

    private List<Category> populateCategoriesList() {
        return Arrays.asList(
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build()
        );
    }

    private Category populateRandomCategory() {
        return Category.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().department())
                .description(faker.funnyName().name())
                .build();
    }

    private CategoryRequest populateRandomCategoryRequest() {
        return new CategoryRequest(faker.commerce().department(), faker.funnyName().name());
    }

    private Category populateSavedCategory(CategoryRequest categoryRequest) {
        return Category.builder()
                .id(UUID.randomUUID().toString())
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
    }
}