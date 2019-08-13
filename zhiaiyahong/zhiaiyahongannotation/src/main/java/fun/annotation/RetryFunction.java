package fun.annotation;

import fun.exception.RetryException;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
@FunctionalInterface
public interface RetryFunction<T> {
    T runFunction() throws RetryException;
}
