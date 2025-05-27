package com.moh.yehia.testing.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

// 这里是一个@around切片, 切片织入到controller下面的所有包类方法中,用于打印方法获得的参数及运行的时间,放在controller上可以监控到应用从接到指令到执行完指令花费的时间
// 作为一个标准的应用程序log打印的切片可以适用到所有应用上, 可以在本类上扩充更多的打印内容
// @Component告诉容器将此类管理. 切片类放入spring容器后才能作为切片织入其它代码
@Component
// 标记为切片类,作为切片进行织入
@Aspect
@Log4j2
public class AppLogger {
	// around切片 其中指定切片表达式
	// execution代表切片表达式, xxx.controller指定到包名,
	// controller..表示controller包及所有子包 , controller.则表示仅controller包之下
	// .*.* 中, 第一个.*表示包中的所有类, 第二个.*指定的是方法名
	// (**)指定的是方法参数为任意
	@Around("execution(* com.moh.yehia.testing.controller..*.*(..))")
	// ProceedingJoinPoint是aop的连接点的一种, 可以帮@around切片方法拿到正在执行的方法的签名和参数
	// 相比于JoinPoint可用于所有切片,ProceedingJoinPoint专用于around切片,
	// 因为它可以通过proceed()方法来继续执行原方法
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		// 记录方法开始执行log
		logMethodStatus(joinPoint, "start");
		// 记录方法传入的参数
		logMethodArguments(joinPoint);
		// stopWatch类用于测量方法运行的时间
		StopWatch stopWatch = new StopWatch();
		// start()开始计时
		stopWatch.start();
		// 切片开始调用原方法运行
		Object returnedValue = joinPoint.proceed();
		// stop()停止计时
		stopWatch.stop();
		// 记录方法运行消耗的时间
		logMethodExecutionTime(joinPoint, stopWatch.getTotalTimeMillis());
		// 打印log结束说明
		logMethodStatus(joinPoint, "end");
		return returnedValue;
	}

	// 一个方法指定一个输出log的格式,用于调用进行log打印
	private void logMethodStatus(ProceedingJoinPoint joinPoint, String status) {
		// log.info方法是在log中输出info等级的log
		// 参数1 日志模板, 用于指定打印的log格式, 用{}作为占位符, {}的数量和内容与后面的其它参数对其
		// 参数2.... 后续参数都是{}占位符填入的内容,这里指定了3个, 分别用joinPoint获取了原执行方法的类名, 方法名,
		// 传入的string状态说明文
		log.info("{} :: {} :: {}", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName(),
				status);
	}

	private void logMethodArguments(ProceedingJoinPoint joinPoint) {
		log.info("executing function {} with arguments = {}", joinPoint.getSignature().getName(),
				// 获取参数,即传入方法的参数
				Arrays.toString(joinPoint.getArgs()));
	}

	private void logMethodExecutionTime(ProceedingJoinPoint joinPoint, long executionTime) {
		log.info("{} :: {} :: execution time is =>{} ms", joinPoint.getTarget().getClass().getName(),
				joinPoint.getSignature().getName(), executionTime);
	}
}
