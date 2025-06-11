package com.iyuba.talkshow.newview;

import com.iyuba.talkshow.R;
import com.mob.secverify.datatype.UiSettings;

public class CustomizeUtils {

	public static UiSettings customizeUi(){
		return new UiSettings.Builder()
				.setAgreementUncheckHintType(0)
				// 标题栏背景色资源ID
				.setNavColorId(R.color.sec_verify_demo_text_color_blue)
				//标题栏是否透明
				.setNavTransparent(true)
				//标题栏是否隐藏
				.setNavHidden(false)
				//设置背景图片
//				.setBackgroundImgId(R.drawable.sec_verify_background_demo)
				//设置背景是否点击关闭页面
//				.setBackgroundClickClose(false)
				// 标题栏标题文字资源ID
//				.setNavTextId(R.string.sec_verify_demo_verify)
				// 标题栏文字颜色资源ID
//				.setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
				// 标题栏左侧关闭按钮图片资源ID
				.setNavCloseImgId(R.drawable.sec_verify_demo_back)
				//标题栏返回按钮是否隐藏
				.setNavCloseImgHidden(false)
				// Logo图片资源ID，默认使用应用图标
				.setLogoImgId(R.drawable.ic_launcher)
				//logo是否隐藏
				.setLogoHidden(false)
//				//logo宽度
				.setLogoWidth(R.dimen.sec_verify_demo_common_80)
				//logo高度
				.setLogoHeight(R.dimen.sec_verify_demo_common_80)
				//logo x轴偏移量
//				.setLogoOffsetX(R.dimen.sec_verify_demo_common_30)
				//logo y轴偏移量
//				.setLogoOffsetY(R.dimen.sec_verify_demo_common_300)
				// 脱敏手机号字体颜色资源ID
				.setNumberColorId(R.color.light_black)
				// 脱敏手机号字体大小资源ID
				.setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
				//脱敏手机号 x轴偏移量
//				.setNumberOffsetX(R.dimen.sec_verify_demo_common_150)
				//脱敏手机号 y轴偏移量
//				.setNumberOffsetY(R.dimen.sec_verify_demo_common_300)
				// 切换账号字体颜色资源ID
				.setSwitchAccColorId(R.color.sec_verify_demo_text_color_blue)
				//切换账号 字体大小
				.setSwitchAccTextSize(R.dimen.sec_verify_demo_text_size_s)
				// 切换账号是否显示，默认显示
				.setSwitchAccHidden(false)
				.setSwitchAccText(R.string.sec_verify_demo_other_login)
				.setSwitchAccOffsetBottomY(50)
				//切换账号 x轴偏移量
//				.setSwitchAccOffsetX(R.dimen.sec_verify_demo_common_180)
				//切换账号 y轴偏移量
//				.setSwitchAccOffsetY(R.dimen.sec_verify_demo_switch_acc_offset_y)
				// 登录按钮背景图资源ID，建议使用shape
				.setLoginBtnImgId(R.drawable.sec_verify_demo_shape_rectangle)
				// 登录按钮文字资源ID
				.setLoginBtnTextId(R.string.sec_verify_demo_one_key_login)
				// 登录按钮字体颜色资源ID
				.setLoginBtnTextColorId(R.color.sec_verify_demo_text_color_common_white)
				//登录按钮字体大小
				.setLoginBtnTextSize(R.dimen.sec_verify_demo_text_size_m)
				//登录按钮 width
				.setLoginBtnWidth(320)
				//登录按钮 height
				.setLoginBtnHeight(60)
				//登录按钮 x轴偏移
//				.setLoginBtnOffsetX(R.dimen.sec_verify_demo_common_20)
				//登录按钮 y轴偏移
//				.setLoginBtnOffsetY(R.dimen.sec_verify_demo_common_400)
				//是否隐藏复选框(设置此属性true时setCheckboxDefaultState不会生效)
				.setCheckboxHidden(true)
				// 隐私协议复选框背景图资源ID，建议使用selector
				.setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
				// 隐私协议复选框默认状态，默认为“选中”
				.setCheckboxDefaultState(true)
				// 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
//				.setAgreementColorId(R.color.sec_verify_demo_main_color)
				// 自定义隐私协议一文字资源ID
				.setAgreementHidden(false)
				.setCusAgreementNameId1(R.string.sec_verify_demo_mob_service)
//				// 自定义隐私协议一URL
				.setCusAgreementUrl1("http://www.mob.com/policy/zh")
////				自定义隐私协议一颜色
				.setCusAgreementColor1(R.color.sec_verify_demo_main_color)
//				// 自定义隐私协议二文字资源ID
//				.setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
//				// 自定义隐私协议二URL
//				.setCusAgreementUrl2("https://www.jianshu.com")
////				自定义隐私协议二颜色
				.setCusAgreementColor2(R.color.sec_verify_demo_main_color)
				.setCusAgreementNameId3("自有服务策略")
//				.setCusAgreementUrl3("http://www.baidu.com")
//				.setCusAgreementColor3(R.color.sec_verify_demo_main_color)
//				.setAgreementTextAnd3("&")
				//隐私协议是否左对齐，默认居中
				.setAgreementGravityLeft(true)
				//隐私协议其他文字颜色
//				.setAgreementBaseTextColorId(R.color.sec_verify_demo_text_color_common_black)
				//隐私协议 x轴偏移量，默认30dp
				.setAgreementOffsetX(R.dimen.sec_verify_demo_common_50)
				//隐私协议 rightMargin右偏移量，默认30dp
				.setAgreementOffsetRightX(R.dimen.sec_verify_demo_common_50)
				//隐私协议 y轴偏移量
//				.setAgreementOffsetY(R.dimen.sec_verify_demo_common_50)
				//隐私协议 底部y轴偏移量
//				.setAgreementOffsetBottomY(R.dimen.sec_verify_demo_agreement_offset_bottom_y)
				.setSloganHidden(true)
				//slogan文字大小
				.setSloganTextSize(R.dimen.sec_verify_demo_text_size_xs)
				.setSloganOffsetY(180)
				//slogan文字颜色
				.setSloganTextColor(R.color.sec_verify_demo_text_color_common_gray)
				//slogan x轴偏移量
//				.setSloganOffsetX(R.dimen.sec_verify_demo_common_200)
				//slogan y轴偏移量
//				.setSloganOffsetY(R.dimen.sec_verify_demo_common_180)
				//slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
//				.setSloganOffsetBottomY(R.dimen.sec_verify_demo_common_20)
				//设置状态栏为透明状态栏，5.0以上生效
				.setImmersiveTheme(true)
				//设置状态栏文字颜色为黑色，只在6.0以上生效
				.setImmersiveStatusTextColorBlack(true)
				//使用平移动画
//				.setTranslateAnim(true)
				.setStartActivityTransitionAnim(R.anim.translate_in, R.anim.translate_out)
				.setFinishActivityTransitionAnim(R.anim.translate_in, R.anim.translate_out)
				//设置隐私协议文字起始
				.setAgreementTextStart(R.string.sec_verify_demo_agreement_text_start)
//				//设置隐私协议文字连接
//				.setAgreementTextAnd1(R.string.sec_verify_demo_agreement_text_and1)
//				//设置隐私协议文字连接
//				.setAgreementTextAnd2(R.string.sec_verify_demo_agreement_text_and2)
//				//设置隐私协议文字结束
				.setAgreementTextEnd(R.string.sec_verify_demo_agreement_text_end)
//				//设置移动隐私协议文字
				.setAgreementCmccText(R.string.sec_verify_demo_agreement_text_all)
//				//设置联通隐私协议文字
				.setAgreementCuccText(R.string.sec_verify_demo_agreement_text_all)
//				//设置电信隐私协议文字
				.setAgreementCtccText(R.string.sec_verify_demo_agreement_text_all)
//				//设置自定义隐私协议SpannableString
//				.setAgreementText(buildSpanString())
//				//设置自定义隐私协议未勾选时提示资源文件
//				.setAgreementUncheckHintText(R.string.ct_account_brand_text)
				//设置自定义隐私协议未勾选时提示
				.setAgreementUncheckHintText("请阅读并勾选隐私协议")
				//设置隐私协议文字颜色
				.setAgreementColorId(R.color.sec_verify_demo_main_color)
				//设置隐私协议左间距
				.setAgreementOffsetX(80)
				//设置隐私协议右间距
				.setAgreementOffsetRightX(80)
				.build();
	}

}
