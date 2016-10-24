package com.kuaibao.skuaidi.activity.picksendmapmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by lgg on 2016/6/29 11:10.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMRangePoint implements Serializable {
    private static final long serialVersionUID = 2687755509757430420L;
    private String id;
    private String address;
    private String amap_lng;
    private String amap_lat;
    private String area;
    private String point;
    private String coord_address;

    public String getCoord_address() {
        return coord_address;
    }

    public void setCoord_address(String coord_address) {
        this.coord_address = coord_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmap_lng() {
        return amap_lng;
    }

    public void setAmap_lng(String amap_lng) {
        this.amap_lng = amap_lng;
    }

    public String getAmap_lat() {
        return amap_lat;
    }

    public void setAmap_lat(String amap_lat) {
        this.amap_lat = amap_lat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "CMRangePoint{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", amap_lng='" + amap_lng + '\'' +
                ", amap_lat='" + amap_lat + '\'' +
                ", area='" + area + '\'' +
                ", point='" + point + '\'' +
                ", coord_address='" + coord_address + '\'' +
                '}';
    }
}
