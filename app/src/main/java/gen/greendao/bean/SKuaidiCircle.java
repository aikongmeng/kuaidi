package gen.greendao.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by lgg on 2016/8/17 17:21.
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
@Entity
public class SKuaidiCircle implements Serializable{

    @Transient
    private static final long serialVersionUID = -7573883805458389174L;
    @NotNull
    @Id
    @Index
    private String id;
    private String wduser_id;
    private String shop;
    private String brand;
    private String county;
    private String address;
    private String content;
    private String update_time;
    private String channel;
    private String lat;
    private String lng;
    private String hash;
    private String pic;
    private String huifu;
    private String good;
    private String fenxiang;
    private String hot;
    private String top;
    private boolean is_good;
    private String message;
    private String imageurls;
    private String imageurlsbig;
    @Generated(hash = 739751785)
    public SKuaidiCircle(@NotNull String id, String wduser_id, String shop,
            String brand, String county, String address, String content,
            String update_time, String channel, String lat, String lng,
            String hash, String pic, String huifu, String good, String fenxiang,
            String hot, String top, boolean is_good, String message,
            String imageurls, String imageurlsbig) {
        this.id = id;
        this.wduser_id = wduser_id;
        this.shop = shop;
        this.brand = brand;
        this.county = county;
        this.address = address;
        this.content = content;
        this.update_time = update_time;
        this.channel = channel;
        this.lat = lat;
        this.lng = lng;
        this.hash = hash;
        this.pic = pic;
        this.huifu = huifu;
        this.good = good;
        this.fenxiang = fenxiang;
        this.hot = hot;
        this.top = top;
        this.is_good = is_good;
        this.message = message;
        this.imageurls = imageurls;
        this.imageurlsbig = imageurlsbig;
    }

    @Generated(hash = 281027041)
    public SKuaidiCircle() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWduser_id() {
        return wduser_id;
    }

    public void setWduser_id(String wduser_id) {
        this.wduser_id = wduser_id;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getHuifu() {
        return huifu;
    }

    public void setHuifu(String huifu) {
        this.huifu = huifu;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public String getFenxiang() {
        return fenxiang;
    }

    public void setFenxiang(String fenxiang) {
        this.fenxiang = fenxiang;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public boolean is_good() {
        return is_good;
    }

    public void setIs_good(boolean is_good) {
        this.is_good = is_good;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIs_good() {
        return this.is_good;
    }

    public String getImageurlsbig() {
        return this.imageurlsbig;
    }

    public void setImageurlsbig(String imageurlsbig) {
        this.imageurlsbig = imageurlsbig;
    }

    public String getImageurls() {
        return this.imageurls;
    }

    public void setImageurls(String imageurls) {
        this.imageurls = imageurls;
    }
}
