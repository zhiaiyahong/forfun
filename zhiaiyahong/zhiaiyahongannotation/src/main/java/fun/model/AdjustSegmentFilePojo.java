package fun.model;

import java.lang.reflect.Field;

public class AdjustSegmentFilePojo {
    /**
     * 当前文件路径
     */
    private String filePath = "";
    /**
     * 本文件第一行内容
     */
    private String firstRow = "";
    /**
     * 本文件最后一行内容
     */
    private String lastRow = "";
    /**
     * 本文件第一行内容 是否完整
     */
    private boolean firstComplete = false;
    /**
     * 本文件最后一行内容 是否完整
     */
    private boolean lastComplete = false;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFirstComplete() {
        return firstComplete;
    }

    public boolean isLastComplete() {
        return lastComplete;
    }

    public String getFirstRow() {
        return firstRow;
    }

    public String getLastRow() {
        return lastRow;
    }

    public void setFirstComplete(boolean firstComplete) {
        this.firstComplete = firstComplete;
    }

    public void setFirstRow(String firstRow) {
        this.firstRow = firstRow;
    }

    public void setLastComplete(boolean lastComplete) {
        this.lastComplete = lastComplete;
    }

    public void setLastRow(String lastRow) {
        this.lastRow = lastRow;
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
