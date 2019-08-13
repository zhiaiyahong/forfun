package fun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/ok")
    @ResponseBody
    public String test(){
        return "ok";
    }
}
