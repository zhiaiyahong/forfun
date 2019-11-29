package com.yhwch.annotation;

import java.lang.annotation.*;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MethodLog {

}
