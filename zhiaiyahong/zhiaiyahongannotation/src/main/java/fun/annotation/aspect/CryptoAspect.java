package fun.annotation.aspect;

import fun.annotation.handler.CryptoHandler;
import fun.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import fun.annotation.Crypto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author pengweichao
 * @date 2019/7/26
 */
@Aspect
@Component
public class CryptoAspect {

    private final Logger log = LoggerFactory.getLogger(CryptoAspect.class);

    private final String SPLIT_CHAR = ",";

    @Pointcut("@annotation(fun.annotation.Crypto)")
    public void annotationPointcut(){

    }

    @Around("annotationPointcut()")
    public Object aspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature sign =  (MethodSignature)proceedingJoinPoint.getSignature();
        Method method = sign.getMethod();
        Crypto crypto = method.getAnnotation(Crypto.class);
        if(crypto.encrypt() && !crypto.decrypt()){
            /* 做加密 */
            encrypt(crypto,proceedingJoinPoint.getArgs(),sign.getParameterNames());
        }
        Object back = proceedingJoinPoint.proceed();
        if(crypto.decrypt() && !crypto.encrypt()){
            /* 做解密 */
            back = decrypt(crypto,back);
        }
        return back;
    }

    private void encrypt(Crypto crypto,Object[] args,String[] parameterNames) throws IllegalAccessException {
        if(ArrayUtils.isEmpty(args) || ArrayUtils.isEmpty(parameterNames)){
            log.warn("注解无法生效,参数为空");
            return;
        }
        CryptoHandler cryptoHandler = crypto.handler().getCryptoHandler();
        if(cryptoHandler == null){
            log.warn("注解无法生效,未指定处理器或处理器为空,crypto:{}",crypto);
            return;
        }
        String cryptoList = crypto.cryptList();
        for(int i = 0;i < args.length; i++){
            Object object = args[i];
            if(!ReflectionUtils.isPOJO(object) && isContains(cryptoList,parameterNames[i])){
                 // 基本类型或字符串类型 不可进行操作 警告级别日志
                log.warn("基本类型无法通过本注解处理,p=属性名:{},对象:{}",parameterNames[i],object);
            }else {
                for (Field field : object.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (isDoCrypto(cryptoList,field)) {
                        field.set(object,cryptoHandler.crypto(crypto.encrypt(),crypto.decrypt(),String.valueOf(field.get(object)),crypto.strict(),crypto.secretKey()));
                    }
                }
            }
        }
    }

    private Object decrypt(Crypto crypto,Object object) throws IllegalAccessException {
        if(object == null){
            log.warn("注解无法生效,参数为空");
            return object;
        }
        CryptoHandler cryptoHandler = crypto.handler().getCryptoHandler();
        if(cryptoHandler == null){
            log.warn("注解无法生效,未指定处理器或处理器为空,crypto:{},object:{}",crypto,object);
            return object;
        }
        String cryptoList = crypto.cryptList();
        // 自定义pojo
        if(ReflectionUtils.isPOJO(object)){
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (isDoCrypto(cryptoList,field)) {
                    field.set(object,cryptoHandler.crypto(crypto.encrypt(),crypto.decrypt(),String.valueOf(field.get(object)),crypto.strict(),crypto.secretKey()));
                }
            }
            return object;
        }
        // string
        if(object instanceof String){
            return cryptoHandler.crypto(crypto.encrypt(),crypto.decrypt(),String.valueOf(object),crypto.strict(),crypto.secretKey());
        }
        // 其他
        log.warn("注解无法生效,返回值不为自定义pojo或string,crypto:{},object:{}",crypto,object);
        return object;
    }

    private boolean isContains(String stringObject,String inspectStr){
        if(StringUtils.isEmpty(inspectStr) || StringUtils.isEmpty(stringObject)){
            return false;
        }
        stringObject = SPLIT_CHAR+stringObject+SPLIT_CHAR;
        return stringObject.contains(SPLIT_CHAR+inspectStr+SPLIT_CHAR);
    }

    private boolean isDoCrypto(String cryptoList,Field field){
        if(!isContains(cryptoList,field.getName())){
            return false;
        }
        if(!field.getType().isAssignableFrom(String.class)){
            log.warn("属性不为string,无法进行加解密field:{}",field);
            return false;
        }
        return true;
    }
}
