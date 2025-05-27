package com.moh.yehia.testing.repository;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

// 配置一个隔离的MongoDB容器 并将其交给spring容器接管
// 可以进行增删改查, 但数据都是放在容器中临时存在的, 没有做持久化
// Repository测试类需要继承本类以保证通过@DataMongoTest注入的是这里配置的mongoDB容器,而不会直接操作到application.yml中配置的真实数据库上导致数据污染
public abstract class BaseMongoContainer {
    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:4.4.29-focal"));
        MONGO_DB_CONTAINER.setPortBindings(List.of("27017:27017")); // used for only enforcing testcontainers to create the expose port 27017 for the mongo container
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }
}
