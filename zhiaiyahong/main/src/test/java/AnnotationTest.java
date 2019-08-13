import fun.domain.FunDo;
import fun.service.AnnotationTestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
public class AnnotationTest extends BaseTest {
    @Autowired
    private AnnotationTestService annotationTestService;

    @Test
    public void testEncrypt(){
        FunDo funDo = new FunDo();
        funDo.setCardNo("6222020402033789109");
        funDo.setPhone("18188888888");
        funDo.setName("我是测试");
        annotationTestService.handleFunDo(funDo);
    }

    @Test
    public void testDecrypt(){
        FunDo funDo = annotationTestService.handleOutput();
        System.out.println(funDo);
    }
}
