package fun.utils;

import fun.annotation.RetryFunction;
import fun.exception.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author pengweichao
 * @date 2019/8/13
 */
public class RetryUtils {

    private static final Logger log = LoggerFactory.getLogger(RetryUtils.class);
    private static final int DEFAULT_RETRY = 3; /* 默认重试三次 */
    private static final int DEFAULT_WAIT = 2; /* 默认 2ms 后进行下一次重试 */
    private RetryUtils(){
        throw new IllegalAccessError("utils");
    }

    public static  <T> T doRetry(RetryFunction<T> retryFunction){
        return doRetry(retryFunction,DEFAULT_RETRY,DEFAULT_WAIT);
    }

    public static <T> T doRetry(RetryFunction<T> retryFunction, int retry, long wait) {
        if (retry <= 0) {
            retry = DEFAULT_RETRY;
        }
        if (wait < 0) {
            wait = DEFAULT_WAIT;
        }
        for (int i = 0; i < retry; i++) {
            try {
                return retryFunction.runFunction();
            }catch (RetryException retryException){
                log.info("方法需要重试,批次:{}retryE=",i,retryException);
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    break;
                }
            }catch (Exception e){
                log.error("执行重试出现异常e=",e);
                break;
            }

        }
        return null;
    }

}
