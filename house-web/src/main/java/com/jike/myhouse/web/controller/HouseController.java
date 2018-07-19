package com.jike.myhouse.web.controller;

import com.jike.myhouse.biz.service.IHouseService;
import com.jike.myhouse.common.model.House;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class HouseController {

    @Resource
    private IHouseService iHouseService;

    /**
     * 1、实现分页
     * 2、支持小区搜索（模糊查询）、类型（出租或者售卖）搜索
     * 3、支持排序
     * 4、支持展示图片、价格、标题、地址等信息
     * @param pageSize 每页显示的数量
     * @param pageNum 第几页
     * @param query 房产查询条件
     * @param modelMap 返回前端需要的数据
     * @return
     */
    @RequestMapping("/house/list")
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap) {
//       iHouseService.queryHouse(query);
        //1、构建分页组件

        return "";
    }

}
