package fun.exception;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
public class ConfigManagementException extends Exception {

    public ConfigManagementException(){}

    public ConfigManagementException(String message){
        super(message);
    }

    public ConfigManagementException(String message, Throwable cause){
        super(message,cause);
    }

    public ConfigManagementException(Throwable cause){
        super(cause);
    }
}
