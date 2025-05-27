package com.moh.yehia.testing.controller;

import com.moh.yehia.testing.exception.InvalidRequestException;
import com.moh.yehia.testing.model.Category;
import com.moh.yehia.testing.model.CategoryRequest;
import com.moh.yehia.testing.service.design.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
// 生成一个包含所有finel与@NonNull字段的构造方法
// 如果变量中有final字段或NonNull字段的变量, 就必须要加这个注解(这两个标注的变量需要在new对象的时候保证有传入, 使用这个注解可以防止方法的构造方法漏掉相关字段)
@RequiredArgsConstructor
// 自动给类生成一个 private static final Logger log 实例. 总之是让本类走slf4j的log日志输出.
// 与GeneralExceptionHandler方法使用了不一样的日志门面(log4j2), 使得项目日志门面开始混乱起来了, 但可能是因为GeneralExceptionHandler需要调用更底层的方法做处理, 而log4j2才能做到
@Slf4j
public class CategoryController {
	// service对象是单例对象, 要使用final修饰从编译层面保证service的单例属性
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category findById(@PathVariable("id") String id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            throw new InvalidRequestException("Category not found with this id: " + id);
        }
        return category;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category save(@Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryRequest);
    }
}
