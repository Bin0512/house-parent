package com.jike.myhouse.biz.service.impl;

import com.jike.myhouse.biz.mapper.HouseMapper;
import com.jike.myhouse.biz.service.IHouseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HouseServiceImpl implements IHouseService {

    @Resource
    private HouseMapper houseMapper;

}
