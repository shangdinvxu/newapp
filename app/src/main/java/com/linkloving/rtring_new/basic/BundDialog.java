package com.linkloving.rtring_new.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.linkloving.rtring_new.R;

/**
 * Created by zkx on 2016/4/13.
 */
public class BundDialog extends android.support.v7.app.AlertDialog.Builder {

    public BundDialog(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.modify_sex_dialog,null );//(ViewGroup)layoutsex.findViewById(R.id.linear_modify_sex)
        final RadioButton left= (RadioButton) view.findViewById(R.id.rb_left);
        left.setText(context.getString(R.string.bound_link_band));
        final RadioButton right=(RadioButton) view.findViewById(R.id.rb_right);
        right.setText(context.getString(R.string.bound_link_watch));
        setView(view);
    }

    public BundDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

}
