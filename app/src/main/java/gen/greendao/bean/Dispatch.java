package gen.greendao.bean;


import com.kuaibao.skuaidi.dispatch.bean.Notice;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Entity mapped to table "DISPATCH".
 */
@Entity
public class Dispatch implements Serializable {

    @Transient
    private static final long serialVersionUID = -8836861101828571173L;
    /**
     * Not-null value.
     */
    @NotNull
    @Id
    @Index
    private String wayBillNo;
    private String wayBillTime;
    private String status;
    /**
     * Not-null value.
     */
    @NotNull
    private String courierNO;
    private String address;
    private Boolean isSelected;
    private Boolean isDeleted;
    private String notes;
    private String noticeUpdateTime;
    private Integer isShow;
    private String province;
    private String city;
    private String area;
    private double latitude;
    private double longitude;
    private String name;
    private String mobile;

    @Transient
    private float distance;


    @Transient
    private Notice notice;


    @Generated(hash = 1373879691)
    public Dispatch(@NotNull String wayBillNo, String wayBillTime, String status,
                    @NotNull String courierNO, String address, Boolean isSelected, Boolean isDeleted,
                    String notes, String noticeUpdateTime, Integer isShow, String province, String city,
                    String area, double latitude, double longitude, String name, String mobile) {
        this.wayBillNo = wayBillNo;
        this.wayBillTime = wayBillTime;
        this.status = status;
        this.courierNO = courierNO;
        this.address = address;
        this.isSelected = isSelected;
        this.isDeleted = isDeleted;
        this.notes = notes;
        this.noticeUpdateTime = noticeUpdateTime;
        this.isShow = isShow;
        this.province = province;
        this.city = city;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.mobile = mobile;
    }

    @Generated(hash = 595609634)
    public Dispatch() {
    }


    /**
     * Not-null value.
     */
    @NotNull
    public String getWayBillNo() {
        return wayBillNo;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    @NotNull
    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public String getWayBillTime() {
        return wayBillTime;
    }

    public void setWayBillTime(String wayBillTime) {
        this.wayBillTime = wayBillTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Not-null value.
     */
    @NotNull
    public String getCourierNO() {
        return courierNO;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    @NotNull
    public void setCourierNO(String courierNO) {
        this.courierNO = courierNO;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNoticeUpdateTime() {
        return noticeUpdateTime;
    }

    public void setNoticeUpdateTime(String noticeUpdateTime) {
        this.noticeUpdateTime = noticeUpdateTime;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Dispatch dispatch = (Dispatch) o;

        return new EqualsBuilder()
                .append(wayBillNo, dispatch.wayBillNo)
                .append(courierNO, dispatch.courierNO)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(wayBillNo)
                .append(courierNO)
                .toHashCode();
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
