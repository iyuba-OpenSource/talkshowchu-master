package com.iyuba.talkshow.util;

/**
 * 用于判断一串数字是否是手机号
 * 
 * @author Administrator
 * 
 */
public class TelNumMatch {
	// 中国移动
	private final static String YD = "^[1]{1}(((([3]{1}[4-9]{1})|([4]{1}[478]{1})|([5]{1}[0-27-9]{1})|([6]{1}[5]{1})|([7]{1}[28]{1})|([8]{1}[23478]{1})|([9]{1}[578]{1}))[0-9]{1})|(([4]{1}[4]{1}[0]{1})|([7]{1}[0]{1}[356]{1})))[0-9]{7}$";
	private final static String YD_NAME = "China Mobile";

	// 中国联通
	private final static String LT = "^[1]{1}(((([3]{1}[0-2]{1})|([4]{1}[056]{1})|([5]{1}[56]{1})|([6]{1}[67]{1})|([7]{1}[56]{1})|([8]{1}[56]{1})|([9]{1}[6]{1}))[0-9]{1})|([7]{1}(([0]{1}[47-9]{1})|([1]{1}[0-9]{1}))))[0-9]{7}$";
	private final static String LT_NAME = "China Unicom";

	// 中国电信
	private final static String DX = "^[1]{1}(((([3]{1}[3]{1})|([4]{1}[19]{1})|([5]{1}[3]{1})|([6]{1}[2]{1})|([7]{1}[37]{1})|([8]{1}[019]{1})|([9]{1}[0139]{1}))[0-9]{1})|(([3]{1}[4]{1}[9]{1})|([7]{1}[4]{1}[0]{1})|([7]{1}[0]{1}[0-2]{1})))[0-9]{7}$";
	private final static String DX_NAME = "China Telecom";

	//中国广电
	private final static String GD = "^[1]{1}([9]{1}[2]{1})[0-9]{8}$";
	private static final String GD_NAME = "China Broadnet";

	public static boolean isPhonenumberLegal(String phone) {
        //			return (phone.matches(YD) || phone.matches(LT) || phone.matches(DX));
        return phone.length() == 11;
	}

	public static String getOperatorName(String phone) {
		String result;
		if (phone.matches(YD)) {
			result = YD_NAME;
		} else if (phone.matches(LT)) {
			result = LT_NAME;
		} else if (phone.matches(DX)) {
			result = DX_NAME;
		} else {
			result = "Unknown";
		}
		return result;
	}
}