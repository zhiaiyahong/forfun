package fun.utils;

import fun.model.FileMapPojo;
import fun.model.FilePathPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class FileUtil {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 获取文件列表_更具正则匹配
     * @param dirName 文件子目录 主目录统一配置
     * @param regStr 正则表达式
     * @return 返回文件名称(不包含路径)
     */
    public List<String> getFileList(String dirName, String regStr){
        List<String> list = new ArrayList<String>();
        try {
            String filePath =  dirName;
            File file = new File(filePath);
            if(file.exists()){
                File fa[] = file.listFiles();
                for (int i = 0; i < fa.length; i++) {
                    File fs = fa[i];
                    if (!fs.isDirectory()) {
                        if(fs.getName().matches(regStr)){
                            list.add(fs.getName());
                        }
                    }
                }
                Collections.sort(list);
            }else{
                log.error("读取文件列表失败，路径{}", filePath);
            }
        } catch (Exception e) {
            log.error("读取文件列表异常，路径{},错误原因:{}", dirName,e);
        }
        return list;
    }

    /**
     * 获取文件夹目录下符合规则的文件列表 包含文件大小
     * @param dirName
     * @param regStr
     * @return
     */
    public List<FileMapPojo> getFileListDetail(String dirName, String regStr){
        List<FileMapPojo> list = new ArrayList<FileMapPojo>();
        try {
            String filePath =  dirName;
            File file = new File(filePath);
            if(file.exists()){
                File fa[] = file.listFiles();
                for (int i = 0; i < fa.length; i++) {
                    File fs = fa[i];
                    if (!fs.isDirectory()) {
                        if(fs.getName().matches(regStr)){
                            FileMapPojo fileMapPojo = new FileMapPojo();
                            fileMapPojo.setShortFileName(fs.getName());
                            fileMapPojo.setFileSize(fs.length());
                            fileMapPojo.setFullFileName(fs.getAbsolutePath());
                            fileMapPojo.setFilePath(fs.getParent());
                            list.add(fileMapPojo);
                        }
                    }
                }
                //对列表进行排序
                Collections.sort(list, new Comparator<FileMapPojo>() {
                    @Override
                    public int compare(FileMapPojo o1, FileMapPojo o2) {
                        return o1.getFullFileName().compareTo(o2.getFullFileName());
                    }
                });
            }else{
                log.error("读取文件列表失败，路径{}", filePath);
            }
        } catch (Exception e) {
            log.error("读取文件列表异常，路径{},错误原因:{}", dirName,e);
        }
        return list;
    }
    /**
     * 读取文件 并将结果放入map中
     * @param filePath 文件绝对路径
     * @param verifyBitReg 验证位正则 表示获取报文中第几位值
     * @return
     */
    public Map<String,String> readFile(String filePath,String verifyBitReg){
        Map<String,String> map = new HashMap<>();
        BufferedReader buffer = null;
        try {
            File file=new File(filePath);
            if(file.exists()) {
                FileInputStream in = new FileInputStream(file);
                InputStreamReader read = new InputStreamReader(in, "UTF-8");//ANSI 可以不转码或者用GBK转码，其他需要相应格式转码
                buffer = new BufferedReader(read);
                String lineTxt = "";
                while ((lineTxt = buffer.readLine()) != null) {
                    String[] strArray = lineTxt.split("\\|");
                    if (strArray.length > 1) {
                        StringBuilder sbu = new StringBuilder();
                        for (int j = 0; j < strArray.length; j++) {
                            if (String.valueOf(j).matches(verifyBitReg)) {
                                sbu.append(strArray[j]);
                            }
                        }
                        //验证重复 规则
                        if (map.containsKey(sbu.toString())) {
                            log.info("验证位存在重复值,验证位:{},重复key值:{},重复原始值:{}", verifyBitReg, sbu.toString(), lineTxt);
                        } else {
                            map.put(sbu.toString(), lineTxt);
                        }
                    } else {
//                        map.put(lineTxt, lineTxt);
                        log.info("报文格式错误:{}", lineTxt);
                    }
                }
            }else{
                log.error("文件不存在{}", filePath);
            }
        } catch (Exception e) {
            log.error("读取文件报错{}", e);
        }finally{
            try {
                if(buffer!=null) {
                    buffer.close();
                }
            } catch (Exception e2) {
                log.error("buffer.close报错{}", e2);
            }
        }
        return map;
    }

    /**
     * 删除文件见 或文件
     * @param filePath  文件夹路径
     * @return
     */
    public boolean deleteHistory(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return deleteDir(file);
        }else{
            log.info("历史文件删除失败，文件夹不存在,文件名称{}",filePath);
            return false;
        }
    }

    /**
     * 递归删除文件夹及其目录下文件
     * @param file
     * @return
     */
    private static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return file.delete();
    }

    /**
     * 读取首行内容
     * @param filePath 文件绝对路径
     * @return
     */
    public String readFirstRow(String filePath){
        BufferedReader buffer = null;
        String back ;
        try{
            File file=new File(filePath);
            if(file.exists()){
                FileInputStream in = new FileInputStream(file);
                InputStreamReader read = new InputStreamReader(in, "UTF-8");//ANSI 可以不转码或者用GBK转码，其他需要相应格式转码
                buffer = new BufferedReader(read);
                while ((back = buffer.readLine()) != null){
                    //只读取一次即返回
                    log.info("首行读取成功,内容:【{}】",back);
                    break;
                }
            }else{
                back = "noFile";
            }
        }catch (Exception e){
            log.error("首行内容获取失败,文件:{},异常信息{}",filePath,e);
            back = "exception";
        }finally {
            if(buffer!=null){
                try {
                    buffer.close();
                }catch (IOException e) {
                    log.error("关闭流异常,文件:{},异常信息{}",filePath,e);
                }
            }
        }
        return back;
    }

    /**
     * 读取文件尾行内容
     * @param filePath 文件绝对路径
     * @return
     */
    public String readLastRow(String filePath){
        RandomAccessFile raf = null;
        String lastLine = "";
        try {
            raf = new RandomAccessFile(filePath, "r");
            long len = raf.length();
            if (len != 0L) {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        lastLine = raf.readLine();
                        lastLine = new String(lastLine.getBytes("8859_1"), "UTF-8");
                        log.info("尾行读取成功,内容:【{}】",lastLine);
                        break;
                    }
                }
            }
        }catch (Exception  e){
            log.error("读取最后一行异常,文件:{},原因:{}",filePath,e);
        }finally {
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    log.error("关闭流异常,文件:{},原因:{}",filePath,e);
                }
            }
        }
        return lastLine;
    }

    /**
     * 将首行修改为 空格
     * @param filePath
     * @param info
     * @return
     */
    public boolean modifyFirstRow(String filePath,String info){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            long len = raf.length();

            StringBuilder sbu = new StringBuilder();
            for(int i=0;i<info.length();i++){
                sbu.append("A");
            }
            byte[] b = sbu.toString().getBytes();
            raf.seek(0);
            raf.write(b,0,b.length);
            return true;
        }catch (Exception  e){
            log.error("修改文件内容异常,文件:{},内容:{},原因:{}",filePath,info,e);
        }finally {
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    log.error("关闭流异常,文件:{},原因:{}",filePath,e);
                }
            }
        }
        return false;
    }
    /**
     * 末尾追加内容
     * @param filePath
     * @param info
     * @return
     */
    public boolean modifyLastRow(String filePath,String info){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            long len = raf.length();
            raf.seek(len);
            raf.write(info.getBytes());
            return true;
        }catch (Exception  e){
            log.error("修改文件内容异常,文件:{},内容:{},原因:{}",filePath,info,e);
        }finally {
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    log.error("关闭流异常,文件:{},原因:{}",filePath,e);
                }
            }
        }
        return false;
    }

    /**
     * 分割绝对路径地址返回 路径 文件名(不含后缀) 文件类型(如 : .txt)
     * @param filePath
     * @return
     */
    public FilePathPojo splitFilePath(String filePath){
        FilePathPojo filePathPojo = new FilePathPojo();
        if(filePath.indexOf("/") > -1) {
            if(filePath.indexOf(".")>-1 ) {
                filePathPojo.setFileType(filePath.substring(filePath.indexOf(".")));
                filePathPojo.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf(".")));
            }else{
                filePathPojo.setFileType("");
                filePathPojo.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
            }
            filePathPojo.setPath(filePath.substring(0, filePath.lastIndexOf("/") + 1));
        }else{
            filePathPojo.setFileType(filePath);
            filePathPojo.setFileName(filePath);
            filePathPojo.setPath(filePath);
        }
        return filePathPojo;
    }
}