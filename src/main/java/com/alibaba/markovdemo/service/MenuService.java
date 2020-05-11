package com.alibaba.markovdemo.service;

import com.alibaba.markovdemo.entity.GotMenu;
import com.alibaba.markovdemo.mapper.GotMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    @Autowired
    GotMenuMapper gotMenuMapper;

    public GotMenu getMenu(){
        return  gotMenuMapper.getMenu();
    }

}
