package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

/**
 * @title: 小说系列的回调数据()
 * @date: 2023/4/27 10:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Novel_book {

    /**
     * types : newCamstory
     * orderNumber : 0
     * level : 0
     * bookname_en : What a Lottery
     * author : Colin Campbell
     * about_book : 里克是个穷困潦倒的歌手，他的妻子离开了他。他买了一张彩票，结果中了巨奖。他想要告诉妻子这个好消息，却在去找妻子的路上遇到了逃犯巴里。巴里和里克之间发生了怎样的争抢？里克能否顺利兑奖？之后又发生了什么离奇风波？彩票给里克带来的是麻烦还是好运？
     * bookname_cn : 彩票风波
     * about_interpreter : 河北保定市第十七中学英语一级教师。荣获过第六届全国中学英语教师教学技能大赛一等奖、第二届保定市中学英语教师技能大赛一等奖。著有多篇论文，其辅导的学生在国家级、省级英语竞赛中多次获奖。
     * wordcounts : 37千字
     * interpreter : 吴如妹
     * pic : /newCamstory/images/0_0.jpg
     * about_author : 英语语言教育专家，拥有近三十年的一线英语教学经历，在欧洲各国担任英语教师、教师培训师和顾问。他在意大利和波兰创办语言学校，在意大利一家电视台主持英语学习节目，创作过多部英语学习小说。在“剑桥双语分级阅读•小说馆”系列中，他创作了《彩票风波》《平行世界》等读本。
     */

    private String types;
    private String orderNumber;
    private String level;
    private String bookname_en;
    private String author;
    private String about_book;
    private String bookname_cn;
    private String about_interpreter;
    private String wordcounts;
    private String interpreter;
    private String pic;
    private String about_author;

    public String getTypes() {
        return types;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getLevel() {
        return level;
    }

    public String getBookname_en() {
        return bookname_en;
    }

    public String getAuthor() {
        return author;
    }

    public String getAbout_book() {
        return about_book;
    }

    public String getBookname_cn() {
        return bookname_cn;
    }

    public String getAbout_interpreter() {
        return about_interpreter;
    }

    public String getWordcounts() {
        return wordcounts;
    }

    public String getInterpreter() {
        return interpreter;
    }

    public String getPic() {
        return pic;
    }

    public String getAbout_author() {
        return about_author;
    }
}
