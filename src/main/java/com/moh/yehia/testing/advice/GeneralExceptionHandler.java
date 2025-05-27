// advice中放AOP(面向切片编程)中的通知相关代码, 比如统一异常捕获, 日志拦截, 权限认证的切片判断,统一修改返回值结构等
// GlobalExceptionHandler是全局异常统一捕获的方法, 将exception以统一的格式返回给前端
package com.moh.yehia.testing.advice;

import com.moh.yehia.testing.exception.InvalidRequestException;
import com.moh.yehia.testing.model.ApiError;
import com.moh.yehia.testing.model.ValidationError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

// controller层的切片, advice通知类
// 这个注解与@ExceptionHandler构成一组, spring会扫描出这两个注解,并将@ExceptionHandler标注的方法用于当controller接收到请求但在正确返回前抛出异常的时候执行这个方法向前端返回返回值
@ControllerAdvice
// 用于自动生成lombok标准格式的log,这里是注入了Log4j2的实例,以便后面使用log.info()、log.error()、log.debug()方法来手动打印log,
@Log4j2
//继承了Springmvc框架自带的ResponseEntityExceptionHandler异常处理基类, 用于进行全局异常处理
// ResponseEntityExceptionHandler自带很多标准的异常处理, 继承即可自动对相关异常进行处理,不写@ExceptionHandler都可以. 只对新追加的全局异常处理方法写@ExceptionHandler即可
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
	// @ExceptionHandler注解用于spring识别为全局异常处理的方法, 传入要进行处理的类,表示针对这个类使用注解下方的方法进行处理
    @ExceptionHandler(InvalidRequestException.class)
    // 手写了一个处理无效请求的新统一异常返回,参数是上面传入的类的对象, 和web链接
    // 返回值定义为ResponseEntity, 是spring封装的返回给前端的http响应,包含状态码,响应头(不需要作为参数定义), 响应体. 传入的泛型即是定义响应体的格式,可以将响应体定义为字符串,user等类, 或者自定义格式的错误对象
    // ResponseEntity的状态码与响应头应该是协议层面的, 决定协议上通信的状态; 而响应体中再定义一个带状态码响应头响应体的对象, 是为了定义业务层的通信状态.
    public ResponseEntity<ApiError> handleInvalidRequest(InvalidRequestException e, WebRequest webRequest) {
    	// 出error先打印到log中. 异常的log打印推荐是在全局异常处理中统一处理(此处), 但继承extends ResponseEntityExceptionHandler的方法不会打印log, 只有手写/override加上了log打印的异常处理才会打印log
    	// 想要打印什么异常的log就需要写对应异常的全局处理方法, 如果想打印所有异常的log,就写一个exception.class的全局处理方法进行log输出
        log.error(e.getMessage(), e);
        // return的时候不需再写一遍泛型了, 编辑器会自动推断出来
        return new ResponseEntity<>(
        		//响应体定义为一个ApiError对象, 手写状态码, 将异常信息作为message, 最后用webRequest的getDescription方法获得请求的地址(false代表不带客户端IP),便于定位异常的接口是哪个
                new ApiError("INVALID_REQUEST", e.getMessage(), webRequest.getDescription(false)),
                // HttpStatus是spring提供的枚举类, 里面枚举了http的各种状态. BAD_REQUEST是状态码400, Bad Request
                // ResponseEntity要求的状态码类型就是HttpStatus
                HttpStatus.BAD_REQUEST
        );
    }

    // handleMethodArgumentNotValid()是spring在ResponseEntityExceptionHandler中预置的一个全局异常处理方法, 负责处理@Valid相关注解做数据校验时失败的异常
    // 重写这个方法, 将返回值改造成与上个异常处理相同功能的类型,而不是返回spring默认的http相应
    // 这是个数据校验异常时必须调用的钩子,所以我们只能改造这个方法来匹配之前的返回内容
    @Override
    // 参数中的@NunNull注解可写可不写, 取决于项目需求要不要求这个规范(不写在此方法调用时也是必须要传入各个参数的)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode httpStatusCode, WebRequest request) {
        // 对validationError写好和APIError同样的地址 异常信息
    	ValidationError validationError = new ValidationError(request.getDescription(false), "Invalid Request Data, Your request is either missing required data or contains invalid values");
        // 取出spring送来的校验报错信息, 逐个装到validationError的map中
    	// FieldError 表示**某个具体字段（属性）**的校验失败。
    	List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    	// GlobalError 表示整个对象级别的校验失败，如订单对象的“开始日期”不能晚于“结束日期”
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        fieldErrors.forEach(fieldError -> validationError.addError(fieldError.getField(), fieldError.getDefaultMessage()));
        globalErrors.forEach(globalError -> validationError.addError(globalError.getObjectName(), globalError.getDefaultMessage()));
        // 最终return的类与上个方法相同
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }
}
