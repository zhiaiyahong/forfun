package fun.exception;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
public class RetryException extends Exception {

    public RetryException(){}

    public RetryException(String message){
        super(message);
    }

    public RetryException(String message, Throwable cause){
        super(message,cause);
    }

    public RetryException(Throwable cause){
        super(cause);
    }
}
