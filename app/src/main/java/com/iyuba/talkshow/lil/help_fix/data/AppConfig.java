package com.iyuba.talkshow.lil.help_fix.data;

import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;

/**
 * @title:
 * @date: 2023/5/19 13:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AppConfig {

    /****************************中小学内容*********************/
    /****选书****/
    //类型
    public static final String JUNIOR_TYPE = TypeLibrary.BookType.junior_primary;
    //教材大类型
    public static final String JUNIOR_BIG_TYPE = "人教版";
    //教材小类型id
    public static final String JUNIOR_SMALL_TYPE_ID = "313";
    //书籍id
    public static final String JUNIOR_BOOK_ID = "205";
    //书籍名称
    public static final String JUNIOR_BOOK_NAME = "1年级上(新起点)";

    /*****************************小说***************************/
    /****选书****/
    //小说类型
    public static final String novel_type = TypeLibrary.BookType.newCamstoryColor;
    //小说等级
    public static final int novel_level = 0;
    //小说id
    public static final String novel_id = "0";
    //小说名称
    public static final String novel_name = "快速变身";
}
