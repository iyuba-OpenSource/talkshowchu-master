package com.iyuba.talkshow.lil.help_fix.data.library;

/**
 * @desction: 接口库
 * @date: 2023/4/10 18:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class UrlLibrary {

    public static final String BASE_URL = "http://ai.iyuba.cn/";
    //珠穆朗玛的url
    public static final String QOMLAMA_URL = "qomolama.cn";

    /*************************prefix*******************************/
    public static final String HTTP_AI = "http://ai.";
    public static final String HTTP_API = "http://api.";
    public static final String HTTP_APIS = "http://apis.";
    public static final String HTTP_APP = "http://app.";
    public static final String HTTP_APPS = "http://apps.";
    public static final String HTTP_CMS = "http://cms.";
    public static final String HTTP_IUSERSPEECH = "http://iuserspeech.";
    public static final String HTTP_STATIC = "http://static.";
    public static final String HTTP_STATIC2 = "http://static2.";
    public static final String HTTP_DAXUE = "http://daxue.";
    public static final String HTTP_USERSPEECH = "http://userspeech.";
    public static final String HTTP_VOA = "http://voa.";
    public static final String HTTP_VIP = "http://vip.";
    public static final String HTTP_WORD = "http://word.";
    public static final String HTTP_DEV = "http://dev.";

    public static final String HTTPS_AI = "https://ai.";
    public static final String HTTPS_API = "https://api.";
    public static final String HTTPS_APIS = "https://apis.";
    public static final String HTTPS_APP = "https://app.";
    public static final String HTTPS_APPS = "https://apps.";
    public static final String HTTPS_IUSERSPEECH = "https://iuserspeech.";

    /*************************suffix******************************/
    public static final String SUFFIX_9001 = ":9001";

    /**************************隐私政策和用户协议****************************/
    //统一的url(目前暂不使用，不够全面和安全)
    //http://iuserspeech.iyuba.cn：:9001/api/protocolpri.jsp?apptype=%E7%88%B1%E8%AF%AD%E5%90%A7%E8%8B%B1%E8%AF%AD&company=%E5%8C%97%E4%BA%AC%E7%88%B1%E8%AF%AD%E5%90%A7
    //北京爱语吧科技有限公司(开业)
    public static String BJIYUBA_PRIVACY = "/api/protocolpri.jsp?company=1&apptype=";
    public static String BJIYUBA_TERM = "/api/protocoluse.jsp?company=1&apptype=";
    //爱语言(北京)科技有限公司(开业)
    public static String BJIYUYAN_PRIVACY = "/api/protocolpri.jsp?company=3&apptype=";
    public static String BJIYUYAN_TERM = "/api/protocoluse.jsp?company=3&apptype=";
    //山东爱语吧信息科技有限公司(开业)
    public static String SDIYUBA_PRIVACY = "/api/protocolpri.jsp?company=4&apptype=";
    public static String SDIYUBA_TERM = "/api/protocoluse.jsp?company=4&apptype=";
    //苏州爱语吧科技有限公司(开业)
    public static String SZIYUBA_PRIVACY = "/api/protocolpri.jsp?company=5&apptype=";
    public static String SZIYUBA_TERM = "/api/protocoluse.jsp?company=5&apptype=";
    //济南万云天教育科技有限公司(注销)
    public static String JNWYT_PRIVACY = "/api/protocolpri.jsp?company=6&apptype=";
    public static String JNWYT_TERM = "/api/protocoluse.jsp?company=6&apptype=";
    //日照国东电子信息科技有限责任公司(注销)
    public static String RZGUODONG_PRIVACY = "/api/protocolpri.jsp?company=7&apptype=";
    public static String RZGUODONG_TERM = "/api/protocoluse.jsp?company=7&apptype=";
    //沈阳一方科技有限公司(开业)
    public static String SYYIFANG_PRIVACY = "/api/protocolpri.jsp?company=8&apptype=";
    public static String SYYIFANG_TERM = "/api/protocoluse.jsp?company=8&apptype=";
    //济南珠穆朗玛人工智能有限公司(开业)
    public static final String JNZMLM_PRIVACY = "http://www.qomolama.com.cn/protocolpri.jsp?company=5&apptype=";
    public static final String JNZMLM_TERM = "http://www.qomolama.com.cn/protocoluse.jsp?company=5&apptype=";

    /************************功能的链接***********************/
    /***********广告**********/
    public static final String ad_show = "/getAdEntryAll.jsp";
    /***********审核**********/
    public static final String verify_ability = "/getRegisterAll.jsp";
    /***********登录*********/
    //邮箱登录
    public static final String login_email = "/v2/api.iyuba";
    //获取用户账号信息
    public static final String userInfo = login_email;
    //注销账号
    public static final String login_out = login_email;
    //判断手机号是否存在
    public static final String phone_exist = "/sendMessage3.jsp";
    //手机注册-信息完善
    public static final String phone_register = "/v2/api.iyuba";
    /***************支付*********/
    //支付宝支付
    public static final String aliPay = "/alipay.jsp";
    //微信支付
    public static final String wxPay = "/weixinPay.jsp";
    /**************口语秀***********/
    //发布口语秀预览内容
    public static final String talk_preview = "/voa/UnicomApi2";
    //口语秀排行数据
    public static final String talk_rank = "/voa/UnicomApi";
    /****************积分***********/
    //分享后获取积分
    public static final String integral_share = "/credits/updateScore.jsp";
    //更新积分(用于下载pdf文件扣除积分)
    public static final String update_score = "/credits/updateScore.jsp";
    /*************单词查询**********/
    //查询单词详情
    public static final String word_search = "/words/apiWord.jsp";
    //插入/删除单词
    public static final String word_insert = "/words/updateWord.jsp";
    /***************提交学习报告*********/
    public static final String update_listen_study_report = "/ecollege/updateStudyRecordNew.jsp";
    public static final String update_word_study_report = "/ecollege/updateExamRecordNew.jsp";
    /*****************评测相关*****************/
    //评测
    public static final String EVAL_SUBMIT = "/test/concept/";
    //获取排行数据
    public static final String COURSE_RANKING = "/ecollege/getTopicRanking.jsp";
    //获取排行榜详情数据
    public static final String COURSE_RANK_DETAIL = "/voa/getWorksByUserId.jsp";
    //点赞评测排行的数据
    public static final String EVAL_RANK_AGREE = "/voa/UnicomApi";
    //评测发布
    public static final String EVAL_PUBLISH = "/voa/UnicomApi";
    //评测合成
    public static final String EVAL_MARGE = "/test/merge/";
    /****************收藏文章********************/
    //收藏文章
    public static final String COLLECT_ARTICLE = "/iyuba/updateCollect.jsp";
    //获取收藏的文章信息
    public static final String COURSE_COLLECT = "/dataapi/jsp/getCollect.jsp";
    /*******************学习报告*****************/
    //阅读报告
    public static final String REPORT_READ = "/ecollege/updateNewsStudyRecord.jsp";
    /*******************奖励信息*****************/
    //奖励的历史记录
    public static final String REWARD_HISTORY = "/credits/getuseractionrecord.jsp";

    /******************书籍***********************/
    /****小说****/
    //获取小说的书籍数据
    public static final String novel_book = "/book/getStroryInfo.jsp";
    //获取小说的章节数据
    public static final String novel_chapter = novel_book;
    //获取小说的章节详情数据
    public static final String novel_chapter_detail = novel_book;
    //小说-获取原文pdf下载链接
    public static final String novel_pdf_url = "/book/getBookWormPdf.jsp";
    //小说-课程部分评测
    public static final String novel_lesson_eval = "/test/ai/";
    //小说-提交评测数据
    public static final String novel_eval_publish = "/voa/UnicomApi";
    /****新概念****/
    //获取青少版的书籍数据
    public static final String concept_junior_book = "/iyuba/getTitleBySeries.jsp";
    //获取全四册的章节数据
    public static final String concept_four_chapter = "/concept/getConceptTitle.jsp";
    //获取青少版的章节数据
    public static final String concept_junior_chapter = "/iyuba/getTitleBySeries.jsp";
    //获取全四册的章节详情数据
    public static final String concept_four_chapter_detail = "/concept/getConceptSentence.jsp";
    //获取青少版的章节详情数据
    public static final String concept_junior_chapter_detail = "/iyuba/textExamApi.jsp";
    //获取全四册的单词数据
    public static final String concept_four_word = "/concept/getConceptWord.jsp";
    //获取青少版的单词数据
    public static final String concept_junior_word = "/iyuba/getWordByUnit.jsp";
    //新概念-单词部分评测
    public static final String concept_word_eval = "/test/concept/";
    //新概念-获取原文pdf下载链接(英文)
    public static final String concept_pdf_url_eg = "/iyuba/getConceptPdfFile_eg.jsp";
    //新概念-获取原文pdf下载链接(双语)
    public static final String concept_pdf_url = "/iyuba/getConceptPdfFile.jsp";
    /****中小学****/
    //获取中小学的出版社数据
    public static final String junior_type = "/iyuba/chooseLessonNew.jsp";
    //获取中小学的书籍数据
    public static final String junior_book = "/iyuba/getTitleBySeries.jsp";
    //获取中小学的书籍章节数据
    public static final String junior_chapter = junior_book;
    //获取中小学的书籍章节的详情数据
    public static final String junior_chapter_detail = "/iyuba/textExamApi.jsp";
    //中小学-单词数据
    public static final String junior_word = "/iyuba/getWordByUnit.jsp";
    //中小学-单词部分评测
    public static final String junior_word_eval = "/test/ai/";
    //中小学-原文pdf下载链接
    public static final String junior_pdf_url = "/iyuba/getVoapdfFile_new.jsp";
    //中小学-配音预览发布
    public static final String junior_dubbing_publish = "/voa/UnicomApi2";
}
