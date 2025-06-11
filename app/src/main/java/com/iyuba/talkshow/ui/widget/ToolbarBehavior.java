//package com.iyuba.talkshow.ui.widget;
//
//import android.support.design.widget.CoordinatorLayout;
//
//public class ToolbarBehavior extends CoordinatorLayout.Behavior {
//
//    /**
//     * 判断child的布局是否依赖dependency
//     */
//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, T child, View dependency) {
//        boolean rs;
//        //根据逻辑判断rs的取值
//        //返回false表示child不依赖dependency，ture表示依赖
//        return rs;
//    }
//
//    /**
//     * 当dependency发生改变时（位置、宽高等），执行这个函数
//     * 返回true表示child的位置或者是宽高要发生改变，否则就返回false
//     */
//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, T child, View dependency) {
//        //child要执行的具体动作
//        return true;
//    }
//
//}
