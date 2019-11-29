package com.yhwch.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/image")
public class ImageAgent {

    @GetMapping("/mm")
    public void get(HttpServletResponse response){
        String path = "/Users/zhiaiyahong/Upload/product/pictures/63ec65d9ae56499cb47f0bb0a0afda1c_original.png";
        //设置ContentType的类型
        String imageType = path.substring(path.lastIndexOf(".") + 1);
        // id safari等浏览器不支持jpg媒体格式，因此将jpg映射为jpeg
        if("jpg".equals(imageType)){
            imageType = "jpeg";
        }
        String type = "image/" + imageType ;
        FileInputStream inputStream = null;
        OutputStream stream = null;

        try {
            File file = new File(path);
            inputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            response.setContentType(type);
            stream = response.getOutputStream();
            stream.write(data);
            stream.flush();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
