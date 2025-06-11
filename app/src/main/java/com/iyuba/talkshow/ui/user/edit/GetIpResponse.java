package com.iyuba.talkshow.ui.user.edit;

/**
 * Created by carl shen on 2020/12/2.
 */
public class GetIpResponse {
	public int result;
	public int status;
	public String province;
	public String city;
	public String adcode;
	public String infocode;
	public String rectangle;
	public String info;

	@Override
	public String toString() {
		return "GetIpResponse{" +
				"result=" + result +
				"status=" + status +
				", province='" + province + '\'' +
				", city='" + city + '\'' +
				", adcode='" + adcode + '\'' +
				", infocode='" + infocode + '\'' +
				", rectangle='" + rectangle + '\'' +
				", info='" + info + '\'' +
				'}';
	}
}
