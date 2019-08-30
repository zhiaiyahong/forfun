package fun.utils;

import fun.model.AdjustSegmentFilePojo;
import fun.model.DoSegmentPojo;
import fun.model.FastSegmentPojo;
import fun.service.DoSegmentFileByThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FastSegmentFile {
    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private FileUtil fileUtil;
    /**
     * 分割文件入口
     * @param fastSegmentPojo
     * @return 返回子文件列表
     */
    public List<String> doSegmentFile(FastSegmentPojo fastSegmentPojo){
        List<String> backList = new ArrayList<>();
        long begin = System.currentTimeMillis();
        File file = new File(fastSegmentPojo.getFilePath());
        if(file.exists()){
            //检查写入路径是否存在不存在则创建
            File fileWrite = new File(fastSegmentPojo.getSegmentFilePath());
            if(!fileWrite.exists()){
                fileWrite.mkdirs();
            }
            //设置分割个数
            fastSegmentPojo.setSegmentSize((int)Math.ceil(file.length()/(double)fastSegmentPojo.getBlockFileSize()));
            log.info("即将开始执行文件分割,参数:{}",fastSegmentPojo);
            ExecutorService pool = Executors.newFixedThreadPool(fastSegmentPojo.getPoolSize());
            for(int i = 0;i<fastSegmentPojo.getSegmentSize();i++){
                DoSegmentPojo doSegmentPojo = new DoSegmentPojo();
                doSegmentPojo.setOriginFile(file);
                doSegmentPojo.setNewFilePath(fastSegmentPojo.getSegmentFilePreName()+String.valueOf(i)+
                        fastSegmentPojo.getSegmetnFileSuffix());
                doSegmentPojo.setStartPos((long)fastSegmentPojo.getBlockFileSize()*i);
                //判断是否达到末尾，最后一部分可能不满足单文件大小 需要计算
                if(i == (fastSegmentPojo.getSegmentSize() - 1)){
                    doSegmentPojo.setReadSize((int)(file.length() - (long)fastSegmentPojo.getBlockFileSize()*i));
                }else {
                    doSegmentPojo.setReadSize(fastSegmentPojo.getBlockFileSize());
                }
                pool.execute(new DoSegmentFileByThread(doSegmentPojo));
                backList.add(doSegmentPojo.getNewFilePath());
            }
            pool.shutdown();
            while(true){
                if(pool.isTerminated()){
                    long end = System.currentTimeMillis();
                    log.info("快速分割完毕耗时:{},分割个数:{},子文件大小:{}",end-begin,fastSegmentPojo.getSegmentSize(),fastSegmentPojo.getBlockFileSize()/(1024*1024));
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //等待分割完毕进入后续补差流程
            log.info("文件列表:{}",backList);
            adjustSegmentFile(backList,fastSegmentPojo.getRegStrForRow());
        }else{
            log.info("分割文件不存在,参数:{}",fastSegmentPojo);
        }
        long end_true = System.currentTimeMillis();
        log.info("文件快速分割完毕，总耗时:{}参数,:{}",end_true-begin,fastSegmentPojo);
        return backList;
    }

    /**
     * 调整分割文件，使子文件内容完整
     * @param list 文件列表
     * @param regStr 单行数据正则表达式
     */
    public void adjustSegmentFile(List<String> list,String regStr){
        if(list!=null && list.size() > 0){
            long begin = System.currentTimeMillis();
            List<AdjustSegmentFilePojo> adjustSegmentFilePojoList = new ArrayList<>();
            for(String s:list){
                AdjustSegmentFilePojo  adjustSegmentFilePojo = new AdjustSegmentFilePojo();
                String firstRow = fileUtil.readFirstRow(s);
                String lastRow = fileUtil.readLastRow(s);
                adjustSegmentFilePojo.setFilePath(s);
                adjustSegmentFilePojo.setFirstRow(firstRow);
                adjustSegmentFilePojo.setLastRow(lastRow);
                adjustSegmentFilePojo.setFirstComplete(firstRow.matches(regStr));
                adjustSegmentFilePojo.setLastComplete(lastRow.matches(regStr));
                adjustSegmentFilePojoList.add(adjustSegmentFilePojo);
            }
            long end1 = System.currentTimeMillis();
            for(int i = 0;i<adjustSegmentFilePojoList.size();i++){
                log.info("开始修复序列:{},数据:{}",i,adjustSegmentFilePojoList.get(i));
                AdjustSegmentFilePojo  adPre = adjustSegmentFilePojoList.get(i);
                if(!adPre.isFirstComplete()){
                    fileUtil.modifyFirstRow(adPre.getFilePath(),adPre.getFirstRow());
                }else{
                    log.info("第一行完整,无需调整,数据:{}",adPre);
                }
                //当前文件最后一行不完整 用下一文件首行数据补充 最后一个文件 不对尾行进行处理
                if(!adPre.isLastComplete() && i+1 < adjustSegmentFilePojoList.size()){
                    AdjustSegmentFilePojo  adNext = adjustSegmentFilePojoList.get(i+1);
                    fileUtil.modifyLastRow(adPre.getFilePath(),adNext.getFirstRow());
                }else{
                    log.info("最后一行完整,无需调整,数据:{}",adPre);
                }
            }
            long end2 = System.currentTimeMillis();
            log.info("调整文件耗时:获取首位行数据耗时:[{}],调整文件内容耗时:[{}]",end1-begin,end2-begin);
        }else{
            log.info("文件列表为空无法合并");
        }
    }
}
