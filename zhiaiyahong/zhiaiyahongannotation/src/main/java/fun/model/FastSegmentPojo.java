package fun.model;

import java.lang.reflect.Field;

public class FastSegmentPojo {
    /**
     * 分割文件地址，绝对路径
     */
    private String filePath = "";
    /**
     * 分割后子文件名前缀 一般为 原始文件名_
     */
    private String segmentFilePreName = "";
    /**
     * 分割文件的保存路径
     */
    private String segmentFilePath = "";
    /**
     * 子文件后缀 即文件类型 如 .txt .avi 等
     */
    private String segmetnFileSuffix = "";
    /**
     * 分割子文件大小 默认30MB
     */
    private int blockFileSize = 1024*1024*30;
    /**
     * 分割子文件个数 0为不分割
     */
    private int segmentSize = 0;
    /**
     * 快速分割线程池大小
     */
    private int poolSize = 5;
    /**
     * 行数据正则表达式 用于验证首位行是否被截断
     */
    private String regStrForRow = "";

    public int getBlockFileSize() {
        return blockFileSize;
    }

    public int getSegmentSize() {
        return segmentSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setBlockFileSize(int blockFileSize) {
        this.blockFileSize = blockFileSize;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSegmentSize(int segmentSize) {
        this.segmentSize = segmentSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getSegmentFilePreName() {
        return segmentFilePreName;
    }

    public String getSegmetnFileSuffix() {
        return segmetnFileSuffix;
    }

    public void setSegmentFilePreName(String segmentFilePreName) {
        this.segmentFilePreName = segmentFilePreName;
        this.segmentFilePath = segmentFilePreName.substring(0,segmentFilePreName.lastIndexOf("/")+1);
    }

    public void setSegmetnFileSuffix(String segmetnFileSuffix) {
        this.segmetnFileSuffix = segmetnFileSuffix;
    }

    public void setRegStrForRow(String regStrForRow) {
        this.regStrForRow = regStrForRow;
    }

    public String getRegStrForRow() {
        return regStrForRow;
    }

    public String getSegmentFilePath() {
        return segmentFilePath;
    }



    @Override
    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            Class cls = this.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                sb.append(f.getName()).append(":").append(f.get(this)).append(",");
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e){
            return  "转化异常";
        }
    }
}
