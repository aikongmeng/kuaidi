package gen.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Entity mapped to table "USER_BIND".
 */
@Entity
public class UserBind {

    @Id(autoincrement = true)
    private Long id;
    private String master;
    private String guest;

    @Generated(hash = 270675018)
    public UserBind() {
    }

    @Generated(hash = 1948786604)
    public UserBind(Long id, String master, String guest) {
        this.id = id;
        this.master = master;
        this.guest = guest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

}
