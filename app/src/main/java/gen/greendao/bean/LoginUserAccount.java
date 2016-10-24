package gen.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "LOGIN_USER_ACCOUNT".
 */
@Entity
public class LoginUserAccount {

    /** Not-null value. */
    @NotNull
    @Id
    @Index
    private String phoneNumber;
    @NotNull
    /** Not-null value. */
    private String userId;
    private java.util.Date lastUpdateTime;
    private String nickName;
    private String headImgUrl;
    private Boolean currentUser;
    private String codeId;
    private String idImg;
    private String realnameAuthStatus;
    @Generated(hash = 1833425848)
    public LoginUserAccount() {
    }
    @Generated(hash = 2091398548)
    public LoginUserAccount(@NotNull String phoneNumber, @NotNull String userId,
            java.util.Date lastUpdateTime, String nickName, String headImgUrl,
            Boolean currentUser, String codeId, String idImg, String realnameAuthStatus) {
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.lastUpdateTime = lastUpdateTime;
        this.nickName = nickName;
        this.headImgUrl = headImgUrl;
        this.currentUser = currentUser;
        this.codeId = codeId;
        this.idImg = idImg;
        this.realnameAuthStatus = realnameAuthStatus;
    }
    /** Not-null value. */
    @NotNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    @NotNull
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** Not-null value. */
    @NotNull
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    @NotNull
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public java.util.Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(java.util.Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
    public String getRealnameAuthStatus() {
        return this.realnameAuthStatus;
    }
    public void setRealnameAuthStatus(String realnameAuthStatus) {
        this.realnameAuthStatus = realnameAuthStatus;
    }
    public String getIdImg() {
        return this.idImg;
    }
    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

}
