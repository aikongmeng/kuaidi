package com.kuaibao.skuaidi.activity.picksendmapmanager.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by lgg on 2016/7/7 17:01.
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
public class PointPoiItem implements Comparable<PointPoiItem>,Parcelable{
    private String serverId;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    private String poiId;

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    private double lat;
    private double lng;
    private boolean isAdded;
    private String poiDescription;
    private String snippet;
    private int distance;
    private String formatAddress;
    private String city;
    private String district;
    private String province;
    private String toweShip;
    private String neighborhood;
    private String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getToweShip() {
        return toweShip;
    }

    public void setToweShip(String toweShip) {
        this.toweShip = toweShip;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }


    public String getFormatAddress() {
        return formatAddress;
    }

    public void setFormatAddress(String formatAddress) {
        this.formatAddress = formatAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getPoiDescription() {
        return poiDescription;
    }

    public void setPoiDescription(String poiDescription) {
        this.poiDescription = poiDescription;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int compareTo(PointPoiItem p) {
        // 先排距离中心点的距离
        if (this.getDistance() != p.getDistance()) {
            return this.getDistance() - p.getDistance();
        }else if(!this.getPoiDescription().equals(p.getPoiDescription())) {
            // 距离相同按照地址名称排序
            return this.getPoiDescription().compareTo(p.getPoiDescription());
        }else{
            // 名称也相同则按ID排序
            return this.getPoiId().compareTo(p.getPoiId());
        }
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PointPoiItem){
            PointPoiItem p=(PointPoiItem)obj;
            return new LatLng(this.getLat(),this.getLng()).equals(new LatLng(p.getLat(),p.getLng()));
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new LatLng(this.getLat(),this.getLng()).hashCode();
    }

    @Override
    public String toString() {
        return "PointPoiItem{" +
                "serverId='" + serverId + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", isAdded=" + isAdded +
                ", poiDescription='" + poiDescription + '\'' +
                ", snippet='" + snippet + '\'';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serverId);
        dest.writeString(this.poiId);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeByte(isAdded ? (byte) 1 : (byte) 0);
        dest.writeString(this.poiDescription);
        dest.writeString(this.snippet);
        dest.writeInt(this.distance);
        dest.writeString(this.formatAddress);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.province);
        dest.writeString(this.toweShip);
        dest.writeString(this.neighborhood);
    }

    public PointPoiItem() {
    }

    protected PointPoiItem(Parcel in) {
        this.serverId = in.readString();
        this.poiId = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.isAdded = in.readByte() != 0;
        this.poiDescription = in.readString();
        this.snippet = in.readString();
        this.distance = in.readInt();
        this.formatAddress = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.province = in.readString();
        this.toweShip = in.readString();
        this.neighborhood = in.readString();
    }

    public static final Creator<PointPoiItem> CREATOR = new Creator<PointPoiItem>() {
        public PointPoiItem createFromParcel(Parcel source) {
            return new PointPoiItem(source);
        }

        public PointPoiItem[] newArray(int size) {
            return new PointPoiItem[size];
        }
    };
}
