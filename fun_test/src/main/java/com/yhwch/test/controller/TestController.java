package com.yhwch.test.controller;

import com.yhwch.cache.config.ConfigManagement;
import com.yhwch.idgen.IDGen;
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
    @Autowired
    private IDGen idGen;

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

    @RequestMapping("/getID")
    @ResponseBody
    public String getID(@RequestParam("key") String key){

        return String.valueOf(idGen.getId(key));
    }
}
