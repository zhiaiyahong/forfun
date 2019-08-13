package fun.service;

import fun.annotation.Crypto;
import fun.annotation.MethodLog;
import fun.constant.CommonConstant;
import fun.domain.FunDo;
import org.springframework.stereotype.Service;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
@Service
public class AnnotationTestService {

    @MethodLog
    @Crypto(cryptList = "phone,cardNo",secretKey = CommonConstant.COMMON_SECRET_KEY,encrypt = true)
    public void handleFunDo(FunDo funDo){
        // do something
        System.out.println(funDo);
    }
    @MethodLog
    @Crypto(cryptList = "phone,cardNo",secretKey = CommonConstant.COMMON_SECRET_KEY,decrypt = true)
    public FunDo handleOutput(){
        FunDo funDo = new FunDo();
        funDo.setCardNo("f03504f6fd99a80015b21eda3de8adb9492efbe2dd33d6b2652c1cdb00ec1e53");
        funDo.setPhone("c808d263d43276854de85b0e3525f7a9");
        funDo.setName("我是测试");
        return funDo;
    }
}
