package lltan.jikexueyuan.com.lego.adapter;

import java.io.Serializable;

/**
 * Created by legoer on 2016/4/17.
 */
public class BaseBean implements Serializable {
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
