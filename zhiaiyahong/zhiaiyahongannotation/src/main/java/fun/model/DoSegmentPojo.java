package fun.model;

import java.io.File;
import java.lang.reflect.Field;

public class DoSegmentPojo {

    private File originFile ;
    private String newFilePath = "";
    private long startPos = 0l;
    private int readSize = 0;

    public File getOriginFile() {
        return originFile;
    }

    public int getReadSize() {
        return readSize;
    }

    public long getStartPos() {
        return startPos;
    }

    public String getNewFilePath() {
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }

    public void setOriginFile(File originFile) {
        this.originFile = originFile;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
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
