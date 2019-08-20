package fun.controller;

import fun.service.ConfigManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ConfigManagement configManagementGen;

    @RequestMapping("/ok")
    @ResponseBody
    public String test(){
        return "ok";
    }

    @RequestMapping("/getConfigByKey")
    @ResponseBody
    public String getConfigByKey(@RequestParam("key") String key){

        return configManagementGen.get(key);
    }
}
