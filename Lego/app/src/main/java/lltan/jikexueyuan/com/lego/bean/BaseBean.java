package lltan.jikexueyuan.com.lego.bean;

import java.io.Serializable;

/**
 * Created by legoer
 */
public class BaseBean implements Serializable {


    protected   long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
