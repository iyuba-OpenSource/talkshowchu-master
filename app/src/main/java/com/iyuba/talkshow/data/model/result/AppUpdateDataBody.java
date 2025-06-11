package com.iyuba.talkshow.data.model.result;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Administrator on 2016/12/16/016.
 */

@Root(name = "data", strict = false)
public class AppUpdateDataBody {
    @Element(name = "versiuon", required = false)
    private String version;
    @Element(name = "url", required = false)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
