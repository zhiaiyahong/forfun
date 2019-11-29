package com.yhwch.annotation.util;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author pengweichao
 * @date 2019/7/29
 */
public class ReflectionUtil {

    private ReflectionUtil(){
        throw new IllegalAccessError("utils");
    }
    /**
     * 类型或其元素类型（包括集合和数组）是否是自定义POJO
     *
     * 判断逻辑：对象为基本类型、Number|Character|Character、String、数组类型、map、set、collection外认为是自定义pojo
     * @param object 对象
     * @return
     */
    public static boolean isPOJO(Object object) {
        if (object == null) {
            return false;
        }

        Class clazz = object.getClass();
        if (clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)) {
            return false;
        }

        if (object instanceof String) {
            return false;
        }
        if(object instanceof Array){
            return false;
        }
        if (object instanceof Collection) {
            return false;
        }
        if (object instanceof Map) {
            return false;
        }
        if(object instanceof Set){
            return false;
        }
        return true;
    }
}
