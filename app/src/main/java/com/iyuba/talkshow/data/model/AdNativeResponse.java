package com.iyuba.talkshow.data.model;


import com.youdao.sdk.nativeads.NativeResponse;

/**
 * AdNativeResponse
 *
 * @author wayne
 * @date 2018/2/7
 */
public class AdNativeResponse  implements RecyclerItem {
  private NativeResponse nativeResponse;

    public void setNativeResponse(NativeResponse nativeResponse) {
        this.nativeResponse = nativeResponse;
    }

    public NativeResponse getNativeResponse() {
        return nativeResponse;
    }
}
