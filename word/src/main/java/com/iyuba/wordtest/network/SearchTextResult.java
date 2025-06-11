package com.iyuba.wordtest.network;

import com.iyuba.wordtest.entity.TalkshowTexts;

import java.util.List;

public class SearchTextResult {


    /**
     * total : 15
     * voatext : [{"ImgPath":"","EndTiming":5.6,"ParaId":"1","IdIndex":"1","sentence_cn":"嗨，我是格雷格。","ImgWords":"","Timing":3.6,"Sentence":"Hi, I\u2019m Greg. "},{"ImgPath":"","EndTiming":6.9,"ParaId":"2","IdIndex":"1","sentence_cn":"我是新来的。","ImgWords":"","Timing":5.6,"Sentence":"I\u2019m new in town."},{"ImgPath":"","EndTiming":9.4,"ParaId":"3","IdIndex":"1","sentence_cn":"嗨，我是海伦。","ImgWords":"","Timing":7.2,"Sentence":"Hi, I\u2019m Helen. "},{"ImgPath":"","EndTiming":11.3,"ParaId":"4","IdIndex":"1","sentence_cn":"欢迎来到社区！","ImgWords":"","Timing":9.4,"Sentence":"Welcome to the neighborhood! "},{"ImgPath":"","EndTiming":13.5,"ParaId":"5","IdIndex":"1","sentence_cn":"到目前为止你觉得怎么样？","ImgWords":"","Timing":11.3,"Sentence":"How do you like it so far?"},{"ImgPath":"","EndTiming":18.6,"ParaId":"6","IdIndex":"1","sentence_cn":"这太棒了，但我还是不知道有些路该怎么走。","ImgWords":"","Timing":13.7,"Sentence":"It\u2019s fantastic, but I still don\u2019t really know my way around."},{"ImgPath":"","EndTiming":22.9,"ParaId":"7","IdIndex":"1","sentence_cn":"好吧，最好的超市在中央大街。","ImgWords":"","Timing":18.7,"Sentence":"Well, the best supermarket is on Center Street. "},{"ImgPath":"","EndTiming":25.8,"ParaId":"8","IdIndex":"1","sentence_cn":"你可以在那里买最新鲜的食物。","ImgWords":"","Timing":23.1,"Sentence":"You can buy the freshest food there."},{"ImgPath":"","EndTiming":27.7,"ParaId":"9","IdIndex":"1","sentence_cn":"哦，太好了。","ImgWords":"","Timing":26.1,"Sentence":"Oh, great. "},{"ImgPath":"","EndTiming":30.2,"ParaId":"10","IdIndex":"1","sentence_cn":"这附近有电影院吗？","ImgWords":"","Timing":27.8,"Sentence":"Is there a cinema around here? "},{"ImgPath":"","EndTiming":32.3,"ParaId":"11","IdIndex":"1","sentence_cn":"我喜欢看电影。","ImgWords":"","Timing":30.4,"Sentence":"I love watching movies."},{"ImgPath":"","EndTiming":36.4,"ParaId":"12","IdIndex":"1","sentence_cn":"是的，太阳电影院是最新的。","ImgWords":"","Timing":32.4,"Sentence":"Yes, Sun Cinema is the newest one. "},{"ImgPath":"","EndTiming":41.4,"ParaId":"13","IdIndex":"1","sentence_cn":"你可以坐得最舒服，因为他们有最大的座位。","ImgWords":"","Timing":36.7,"Sentence":"You can sit the most comfortably because they have the biggest seats."},{"ImgPath":"","EndTiming":42.9,"ParaId":"14","IdIndex":"1","sentence_cn":"谢谢你告诉我这些。","ImgWords":"","Timing":41.5,"Sentence":"Thanks for telling me."},{"ImgPath":"","EndTiming":44.8,"ParaId":"15","IdIndex":"1","sentence_cn":"没事。","ImgWords":"","Timing":43.1,"Sentence":"No problem."}]
     */

    private String total;
    private List<TalkshowTexts> voatext;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<TalkshowTexts> getVoatext() {
        return voatext;
    }

    public void setVoatext(List<TalkshowTexts> voatext) {
        this.voatext = voatext;
    }

    public static class VoatextBean {
    }
}
