package fun.model;

import java.lang.reflect.Field;

public class FilePathPojo {
    private String path = "";
    private String fileName = "";
    private String fileType = "";
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
