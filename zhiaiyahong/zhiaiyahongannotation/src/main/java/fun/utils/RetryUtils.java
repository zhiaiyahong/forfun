package fun.utils;

import fun.function.RetryFunction;
import fun.exception.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author pengweichao
 * @date 2019/8/13
 *
 * rpc或http请求可能会超时，此时期望程序可进行有限次数的重试以减少网络波动对系统的影响，因此提供此工具用于
 * 对需要重试的方法做自动重试。目标方法需要在内部逻辑中判定方法是否需要重试如需重试则抛出 RetryException 异常
 * util方法会捕获此异常进而对方法进行重试，如方法正常返回则直接返回结果，如遇到其他异常则终止执行直接抛出异常
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
                throw e;
            }

        }
        return null;
    }

}
