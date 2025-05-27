package com.moh.yehia.testing.asserts;

import com.moh.yehia.testing.model.ApiError;
import org.assertj.core.api.AbstractAssert;
// 方法3 手写Assert类自定义一个类的断言比较逻辑,交给Test调用
// 自定义的断言类需要实现AbstractAssert, 定义泛型. 分别为本类的类型, 及进行断言的对象的类型
public class ApiErrorAssert extends AbstractAssert<ApiErrorAssert, ApiError> {
	// 构造方法定义为调用父类的构造方法(suer()就是父类构造方法) 传入断言的对象及本类类名
	// 传入本类是为了保证使用AbstractAssert()提供的isNotNull()之类的方法时,可以保证方法返回的类型为本类(ApiErrorAssert), 保证返回类型的正确以便其他方法的链式调用
    protected ApiErrorAssert(ApiError actual) {
        super(actual, ApiErrorAssert.class);
    }

    public static ApiErrorAssert assertThat(ApiError apiError) {
        return new ApiErrorAssert(apiError);
    }
    //抽出对象属性逐个比较的方法
    public ApiErrorAssert hasStatusCode(String statusCode) {
        isNotNull();
        if (!actual.getStatusCode().equals(statusCode)) {
            failWithMessage("Expected apiError statusCode to be {%s} but it was {%s}", statusCode, actual.getStatusCode());
        }
        return this;
    }

    public ApiErrorAssert hasMessage(String message) {
        isNotNull();
        if (!actual.getMessage().equals(message)) {
            failWithMessage("Expected apiError message to be {%s} but it was {%s}", message, actual.getMessage());
        }
        return this;
    }

    public ApiErrorAssert hasPath(String path) {
        isNotNull();
        if (!actual.getPath().equals(path)) {
            failWithMessage("Expected apiError path to be {%s} but it was {%s}", path, actual.getPath());
        }
        return this;
    }
}
