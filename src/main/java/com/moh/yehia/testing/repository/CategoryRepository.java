package com.moh.yehia.testing.repository;

import com.moh.yehia.testing.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Repository相比于半自动的Mybatis,可以通过识别方法命名来全自动地完成SQL语句(findByName→SELECT * FROM category WHERE name = ?)
// 只需要保证接口继承相应的操作类, 然后按命名规范写方法的名称即可
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

}
