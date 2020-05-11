package com.alibaba.markovdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.markovdemo.common.AjaxResult;
import com.alibaba.markovdemo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/api")
public class MenuController {
    @Autowired
    MenuService menuService;

    /**
     * 功能:获取菜单页
     * @return
     */
    @RequestMapping(value = "/getMenu", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getMenu(){
        try {
            JSONObject buinfo = JSONObject.parseObject(menuService.getMenu().getContent());
            return AjaxResult.succResult(buinfo);
        } catch (Exception e) {
            return AjaxResult.errorResult(e.getMessage());
        }
    }

}
