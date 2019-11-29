package com.yhwch.annotation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
@Aspect
@Component
public class MethodLogAspect {
    private static final Logger log = LoggerFactory.getLogger(MethodLogAspect.class);


    @Pointcut("@annotation(fun.annotation.MethodLog)")
    public void annotationPointCut(){

    }

    @Around("annotationPointCut()")
    public Object advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        log.info("切面日志 参数:{},类名:{},方法名:{},结果:{},执行时间:{}",args,className,methodName,result,end-start);
        return result;
    }
}
