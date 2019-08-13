package fun.service;

import fun.exception.RetryException;
import fun.utils.RetryUtils;
import org.springframework.stereotype.Service;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
@Service
public class RetryUtilTestService {

    public void retryPrivateMethod(){

        RetryUtils.doRetry(()->doSomething(1));

        RetryUtils.doRetry(()->doSomething(2));

        RetryUtils.doRetry(()->doSomething(3));
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
