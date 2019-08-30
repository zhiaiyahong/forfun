package fun.service;

import fun.model.DoSegmentPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class DoSegmentFileByThread implements Runnable {
    private Logger log =  LoggerFactory.getLogger(getClass());
    private DoSegmentPojo doSegmentPojo;
    public DoSegmentFileByThread(DoSegmentPojo doSegmentPojo){
        this.doSegmentPojo = doSegmentPojo;
    }

    @Override
    public void run() {
        RandomAccessFile randomAccessFile = null;
        OutputStream outputStream = null;
        log.info("开始生成分割文件,参数:{}",doSegmentPojo);
        try {
            randomAccessFile = new RandomAccessFile(doSegmentPojo.getOriginFile(), "r");
            byte[] readyB = new byte[doSegmentPojo.getReadSize()];
            randomAccessFile.seek(doSegmentPojo.getStartPos());
            int r = randomAccessFile.read(readyB);
            outputStream = new FileOutputStream(doSegmentPojo.getNewFilePath());
            outputStream.write(readyB,0,r);
            outputStream.flush();
            log.info("生成成功,参数:{}",doSegmentPojo);
        }catch (Exception e){
            log.error("分割文件出现异常,参数:{},错误原因:{}",e,doSegmentPojo);
        }finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }catch (IOException ioe){
                log.error("关闭流异常,参数:{}错误原因:{}",ioe,doSegmentPojo);
            }
        }
    }
}
