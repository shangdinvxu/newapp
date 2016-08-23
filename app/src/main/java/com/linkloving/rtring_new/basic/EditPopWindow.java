package com.linkloving.rtring_new.basic;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.linkloving.rtring_new.R;

/**
 * Created by Administrator on 2016/4/22.
 */
public class EditPopWindow extends PopupWindow {
    private Context mContext;
    private WindowManager mWindowManager;

    public EditPopWindow(View contentView, int width, int height, boolean focusable) {
        if (contentView != null) {
            mContext = contentView.getContext();
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
    }

    public static EditPopWindow createPopupWindow(Context context, int layoutID, int width, int height, boolean focus)
    {
        View contentView = View.inflate(context, layoutID, null);
        EditPopWindow pop = new EditPopWindow(contentView, width, height,focus);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        return pop;
    }

    public void setText(EditPopWindow popupWindow,String message){
        View view = popupWindow.getContentView();
        TextView error = (TextView) view.findViewById(R.id.textView);
        error.setText(message);
    }
}
