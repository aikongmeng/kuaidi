package gen.greendao.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "USER_TELE_PREFER".
 */
@Entity
public class UserTelePrefer {

    /** Not-null value. */
    @NotNull
    @Id
    @Index
    private String phoneNumber;
    /** Not-null value. */
    @NotNull
    private String teleType;
    private Boolean showWarn;

    @Generated(hash = 677881073)
    public UserTelePrefer() {
    }

    @Generated(hash = 990249778)
    public UserTelePrefer(@NotNull String phoneNumber, @NotNull String teleType,
            Boolean showWarn) {
        this.phoneNumber = phoneNumber;
        this.teleType = teleType;
        this.showWarn = showWarn;
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
    public String getTeleType() {
        return teleType;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    @NotNull
    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }

    public Boolean getShowWarn() {
        return showWarn;
    }

    public void setShowWarn(Boolean showWarn) {
        this.showWarn = showWarn;
    }

}
