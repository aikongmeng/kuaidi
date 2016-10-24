package gen.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Entity mapped to table "KBACCOUNT".
 */
@Entity
public class KBAccount {

    /** Not-null value. */
    @NotNull
    @Id
    @Index
    private String phoneNumber;
    private String password;
    /** Not-null value. */
    @NotNull
    @Unique
    private String userId;
    private java.util.Date lastUpdateTime;
    private String nickName;
    private String headImgUrl;
    private Boolean currentUser;
    private String codeId;
    @Generated(hash = 1547199359)
    public KBAccount() {
    }

    @Generated(hash = 1357026683)
    public KBAccount(@NotNull String phoneNumber, String password, @NotNull String userId, java.util.Date lastUpdateTime, String nickName, String headImgUrl,
            Boolean currentUser, String codeId) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.userId = userId;
        this.lastUpdateTime = lastUpdateTime;
        this.nickName = nickName;
        this.headImgUrl = headImgUrl;
        this.currentUser = currentUser;
        this.codeId = codeId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public boolean equals(Object obj) {
        if(this ==obj){//如果是引用同一个实例
            return true;
        } if (obj!=null && obj instanceof KBAccount) {
            KBAccount u = (KBAccount) obj;
            return this.getPhoneNumber().equals(u.getPhoneNumber()) && this.getUserId().equals(u.getUserId());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getPhoneNumber().hashCode()+this.getUserId().hashCode();
    }

    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

}
