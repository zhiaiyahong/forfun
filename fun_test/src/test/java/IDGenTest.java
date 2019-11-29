
import com.yhwch.idgen.IDGen;
import com.yhwch.idgen.enums.IDGenResultCodeEnum;
import com.yhwch.idgen.model.IDGenResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class IDGenTest extends BaseTest {
    @Autowired
    private IDGen idGenService;

    private ExecutorService service = new ThreadPoolExecutor(20, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new IDGenTest.UpdateThreadFactory());

    private AtomicLong atomicLong = new AtomicLong(0);

    private AtomicLong atomicLongSuc = new AtomicLong(0);

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-IDGen-GET" + nextThreadNum());
        }
    }
    @Test
    public void test(){
        long start =System.currentTimeMillis();
        for(int i = 0;i<20;i++){
            service.execute(()->{
                for(int j = 0;j<100000;j++) {
                    IDGenResult idGenResult = idGenService.getId("fun_test");
                    atomicLongSuc.incrementAndGet();
                    if(idGenResult.getCode() != IDGenResultCodeEnum.SUC.getCode()) {
                        atomicLong.incrementAndGet();
                    }
                }
            });
        }
        service.shutdown();
        while (true){
            if(service.isTerminated()){
                long end =System.currentTimeMillis();
                log.info("执行完毕耗时:{}",(end-start));
                log.info("失败数量为:{}",atomicLong.get());
                log.info("执行总次数:{}",atomicLongSuc.get());
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
