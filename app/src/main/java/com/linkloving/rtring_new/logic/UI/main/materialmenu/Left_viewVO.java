package com.linkloving.rtring_new.logic.UI.main.materialmenu;

import com.linkloving.rtring_new.R;

/**
 * Created by zkx on 2016/3/10.
 */
public class Left_viewVO {
    public static final int FRIENDS = 0;
    public static final int RANKING = 1;
    public static final int GOAL = 2;
    public static final int KEFU = 3;
    public static final int SHOP = 4;
    public static final int MORE = 5;

    public static int[] menuIcon = {
            R.mipmap.ic_menu_friends,
            R.mipmap.ic_menu_rank,
            R.mipmap.ic_menu_goal,
            R.mipmap.ic_menu_kefu,
            R.mipmap.ic_menu_mall,
            R.mipmap.ic_menu_more
    };
    public static int[] menuText = {
            R.string.relationship,
            R.string.ranking_title,
            R.string.menu_goal,
            R.string.service_center_title,
            R.string.shop,
            R.string.general_more
    };
}
