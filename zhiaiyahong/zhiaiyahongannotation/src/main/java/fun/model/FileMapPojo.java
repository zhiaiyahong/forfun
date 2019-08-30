package fun.model;

import java.lang.reflect.Field;

public class FileMapPojo {
    private String shortFileName;//文件名称
    private long fileSize;//文件大小
    private String fullFileName;//文件绝对路径 含文件名
    private String fileSuffix;//文件后缀
    private String filePreName;//文件名称 不含后缀
    private String filePath;//文件绝对路径 不含文件名

    public long getFileSize() {
        return fileSize;
    }

    public String getShortFileName() {
        return shortFileName;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
        String[] s = shortFileName.split("\\.");
        if(s.length>1) {
            this.filePreName = s[0];
            this.fileSuffix = "." + s[1];
        }
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath+"/";
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFilePreName() {
        return filePreName;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFilePreName(String filePreName) {
        this.filePreName = filePreName;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
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
