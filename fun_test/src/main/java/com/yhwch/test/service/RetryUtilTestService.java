package com.yhwch.test.service;

import com.yhwch.retry.function.RetryUtil;
import com.yhwch.retry.function.exception.RetryException;
import org.springframework.stereotype.Service;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
@Service
public class RetryUtilTestService {

    public void retryPrivateMethod(){

        RetryUtil.doRetry(()->doSomething(1));

        RetryUtil.doRetry(()->doSomething(2));

        RetryUtil.doRetry(()->doSomething(3));
    }

    private int doSomething(int in) throws RetryException {
        System.out.println(in);
        if(in == 1) {
            throw new RetryException("赶紧的重试");
        }
        if(in == 2){
           throw new RuntimeException("其他异常");
        }
        return in;
    }
}
