package com.linkloving.rtring_new.logic.UI.launch.register;

import java.util.ArrayList;

/**
 * Created by Mr_Wang on 2016/5/23.
 */
public interface MyAutoCompleteTextViewHelperListener {

    /** 搜索匹配 **/
    public abstract ArrayList<String> refreshPostData(String key );

    /**
     *  设置重新在下拉提示文本框选择的标志量
     *  这里为什么设置这么一个方法，是有原因的
     *  这个方法一般在下拉列表的数据发生了变化重新填装但是用户还没有选择的阶段
     *  通知Activity方面修改标志量，我这里是重置了选中项的position，即置为-1，防止用户没有选择，而导致position保存的是上一次选中的位置
     *  造成一些错误，其实就是相当于了OnItemSelectedListener.onNothingSelected(parent);方法，只不过当时图省事就这么处理了
     *  当然大家也可以自己在MyAutoCompleteTextView类里面监听，即在里面设置标志量，当dismiss的时候如果标志量为-1，表明用户未选择，即可调用
     *  OnItemSelectedListener.onNothingSelected(parent);方法来通知即可
     */
    public abstract void resetMarkNum();
}
