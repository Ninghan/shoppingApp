package lltan.jikexueyuan.com.lego.bean;

import java.util.List;

/**
 * Created by legoer on 2016/5/19.
 */
public class ChargeRefundCollection {

    String object;
    String url;
    Boolean hasMore;
    List<?> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
