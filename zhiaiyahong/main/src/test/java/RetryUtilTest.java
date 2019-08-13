import fun.service.RetryUtilTestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author pengweichao
 * @date 2019/8/13
 */
public class RetryUtilTest extends BaseTest {
    @Autowired
    private RetryUtilTestService retryUtilTestService;

    @Test
    public void test(){
        retryUtilTestService.retryPrivateMethod();
    }
}
