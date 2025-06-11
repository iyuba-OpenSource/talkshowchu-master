package com.iyuba.talkshow.injection.component;

import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.junior.JuniorChapterCollectFragment;
import com.iyuba.talkshow.lil.junior.choose.JuniorChooseFragment;
import com.iyuba.talkshow.lil.junior.ui.JuniorFragment;
import com.iyuba.talkshow.lil.junior.ui.JuniorListFragment;
import com.iyuba.talkshow.lil.junior.ui.JuniorWordFragment;
import com.iyuba.talkshow.newce.MainFragment;
import com.iyuba.talkshow.newce.kouyu.KouyuFragment;
import com.iyuba.talkshow.newce.me.MeFragment;
import com.iyuba.talkshow.newce.study.dubbingNew.DubbingNewFragment;
import com.iyuba.talkshow.newce.study.eval.EvalFragment;
import com.iyuba.talkshow.newce.study.image.ImageFragment;
import com.iyuba.talkshow.newce.study.rank.EvalrankFragment;
import com.iyuba.talkshow.newce.study.read.newRead.ui.NewReadFragment;
import com.iyuba.talkshow.newce.study.section.SectionFragment;
import com.iyuba.talkshow.newce.study.word.VoaWordFragment;
import com.iyuba.talkshow.newce.wordstep.WordstepFragment;
import com.iyuba.talkshow.newview.StudyReportPage;
import com.iyuba.talkshow.ui.courses.coursechoose.ChooseCourseFragment;
import com.iyuba.talkshow.ui.courses.coursechoose.ChooseKouFragment;
import com.iyuba.talkshow.ui.detail.comment.CommentFragment;
import com.iyuba.talkshow.ui.detail.ranking.RankingFragment;
import com.iyuba.talkshow.ui.detail.recommend.RecommendFragment;
import com.iyuba.talkshow.ui.rank.listen.RankListenFragment;
import com.iyuba.talkshow.ui.rank.oral.RankOralFragment;
import com.iyuba.talkshow.ui.rank.study.RankStudyFragment;
import com.iyuba.talkshow.ui.rank.test.RankTestFragment;
import com.iyuba.talkshow.ui.user.download.DownloadFragment;
import com.iyuba.talkshow.ui.user.me.dubbing.draft.DraftFragment;
import com.iyuba.talkshow.ui.user.me.dubbing.released.ReleasedFragment;
import com.iyuba.talkshow.ui.user.me.dubbing.unreleased.UnreleasedFragment;
import com.iyuba.talkshow.ui.words.WordNoteFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent
public interface FragmentComponent {
    void inject(CommentFragment commentFragment);
//    void inject(ChooseCourseFragment courseFragment);
    void inject(ChooseCourseFragment courseFragmentFix);
    void inject(ChooseKouFragment kouFragment);
    void inject(MainFragment mainFragment);
    void inject(WordstepFragment wordFragment);
    void inject(WordNoteFragment noteFragment);
    void inject(KouyuFragment dubFragment);
    void inject(MeFragment meFragment);
    void inject(ImageFragment imageFragment);
//    void inject(ReadFragment readFragment);
    void inject(StudyReportPage studyReport);
    void inject(EvalFragment evalFragment);
    void inject(EvalrankFragment evalRankFragment);

    void inject(DownloadFragment downloadFragment);

    void inject(RankingFragment rankingFragment);

    void inject(RecommendFragment recommendFragment);

    void inject(DraftFragment draftFragment);

    void inject(ReleasedFragment releasedFragment);

    void inject(UnreleasedFragment unreleasedFragment);

    //增加VoaWordFragment
    void inject(VoaWordFragment voaWordFragment);
    //排行榜-听力
    void inject(RankListenFragment listenFragment);
    //排行榜-口语
    void inject(RankOralFragment oralFragment);
    //排行榜-学习
    void inject(RankStudyFragment studyFragment);
    //排行榜-测试
    void inject(RankTestFragment testFragment);
    //学习-配音
    void inject(DubbingNewFragment newFragment);
    //学习-阅读界面
    void inject(SectionFragment sectionFragment);

    /******************中小学内容*************/
    //主界面
    void inject(JuniorFragment fragment);
    //列表界面
    void inject(JuniorListFragment listFragment);
    //单词界面
    void inject(JuniorWordFragment wordFragment);
    //选书界面
    void inject(JuniorChooseFragment chooseFragment);
    //新的原文界面
    void inject(NewReadFragment newReadFragment);

    /********************课程收藏*****************/
    //中小学的课程收藏
    void inject(JuniorChapterCollectFragment collectFragment);
}
