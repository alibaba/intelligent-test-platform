package com.alibaba.markovdemo.mapper;

import com.alibaba.markovdemo.entity.GotMenu;
import org.springframework.stereotype.Repository;


@Repository
public interface GotMenuMapper {
    GotMenu getMenu();

}


