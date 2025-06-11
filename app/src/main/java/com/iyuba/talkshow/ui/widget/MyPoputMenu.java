package com.iyuba.talkshow.ui.widget;

import android.content.Context;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import android.view.View;
import android.widget.PopupMenu;

public class MyPoputMenu extends PopupMenu {
    public MyPoputMenu(Context context, View anchor) {
        super(context, anchor);
    }

    public MyPoputMenu(Context context, View anchor, int gravity) {
        super(context, anchor, gravity);
    }

    public MyPoputMenu(Context context, View anchor, int gravity, int popupStyleAttr, int popupStyleRes) {
        super(context, anchor, gravity, popupStyleAttr, popupStyleRes);
    }

//    MenuBuilder  mMenu ;
//    View mAnchor ;
//    MenuPopupHelper mPopup;
//    Context mContext ;
//    public MyPoputMenu(Context context, View anchor) {
//        init(context,anchor);
//    }
//
//
//    @SuppressLint("RestrictedApi")
//    private void init(Context context, View anchor){
//        mContext = context;
//        mMenu = new MenuBuilder(context);
//        mMenu.setCallback(this);
//        mAnchor = anchor;
//        mPopup = new MenuPopupHelper(context, mMenu, anchor);
//        mPopup.setCallback(this);
//        mPopup.setForceShowIcon(true); //ADD THIS LINE
//    }
}
