package com.iyuba.talkshow.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import personal.iyuba.personalhomelibrary.data.model.GroupInfoBean;
import personal.iyuba.personalhomelibrary.data.model.HostInfoBean;
import personal.iyuba.personalhomelibrary.data.model.MemberInfoBean;

public class EnterGroup {

  /**
   * groupinfo : {"groupimage":null,"grouptitle":"VOA官方群","groupdesc":"这是一款致力于英语爱好者的精品APP，在这里学习英语将会体验到前所未有的快感","createtime":1567164182,"status":1,"groupid":"10001","uid":"4729911"}
   * hostnum : 1
   * hostinfo : [{"uid":928,"status":3,"groupuname":"我是群主","groupuimage":"http://m.iyuba.cn/imagesdtl/iyubalogo.png"}]
   * membernum : 3
   * memberinfo : [{"uid":928,"status":3,"groupuname":"我是群主","groupuimage":"http://m.iyuba.cn/imagesdtl/iyubalogo.png"},{"uid":4729911,"status":1,"groupuname":"我是成员","groupuimage":"http://m.iyuba.cn/imagesdtl/iyubalogo.png"},{"uid":6919886,"status":1,"groupuname":"VOA官方群","groupuimage":"http://www.iyuba.cn/image/android/iyubaClient/icon.png"}]
   */

  @SerializedName("result")
  public int result;
  @SerializedName("gpinfo")
  public List<GroupInfoBean> groupinfo;
  @SerializedName("hostnum")
  public int hostnum;
  @SerializedName("membernum")
  public int membernum;
  @SerializedName("hostinfo")
  public List<HostInfoBean> hostinfo;
  @SerializedName("memberinfo")
  public List<MemberInfoBean> memberinfo;

}
