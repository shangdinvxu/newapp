package com.linkloving.rtring_new.logic.UI.launch.register;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.linkloving.rtring_new.R;

import java.util.ArrayList;

/**
 * Created by Mr_Wang on 2016/5/23.
 */
public class MyAutoCompleteTextView extends AppCompatEditText {

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private AutoCompleteTextViewDropDownItem dropDown = null;


    private ArrayList<String> mDataSet = null;
    /**
     * 自动完成文本框开始搜索的阈值，即输入的数量超过了这个阈值就开始搜索张贴栏，默认为1
     **/
    private int beginSearchThreshold = 1;
    /**
     * 状态标志量，true表示此次EditText的变化是由选中了下拉列表中的数据导致，结束此次监听事件；否则是由用户输入导致
     **/
    private boolean finishMark = false;
    private OnItemSelectedListener mOnItemSelectedListener = null;
    /**
     * 使用该控件的类务必实现该方法，否则无法刷新数据
     **/
    private MyAutoCompleteTextViewHelperListener mHelpListener = null;

    public MyAutoCompleteTextView(Context context) {
        super(context);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(this.mContext);

        /**为了避免空指针异常，提前初始化数据集 **/
        mDataSet=new ArrayList<>();

        /** 绑定可编辑文本框的字符变化监听器 **/
        addTextChangedListener(new EditTextInputTextWatcher());

    }

    /**
     * 设置自动完成文本框的选择监听器，用于回调，务必在显示下拉列表前载入
     * @param mOnItemSelectedListener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener mOnItemSelectedListener) {
        this.mOnItemSelectedListener = mOnItemSelectedListener;
    }

    /**
     * 设置自动完成文本框的助手类监听器，用于调用封装自动填补列表的数据
     * @param mHelpListener
     */
    public void setAutoCompleteTextViewHelpListener(MyAutoCompleteTextViewHelperListener mHelpListener) {
        this.mHelpListener = mHelpListener;
    }

    /**
     * 设置刷新阈值
     *
     * @param searchThreshold
     */
    public void setSearchThreshold(int searchThreshold) {
        beginSearchThreshold = searchThreshold;
    }

    /**
     * 可编辑文本框的输入动态监听器，其中的方法是依次调用
     */
    private class EditTextInputTextWatcher implements TextWatcher {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (finishMark) {
                finishMark = !finishMark;
                return;
            }
            if (mHelpListener != null) {
                mHelpListener.resetMarkNum();
            }
            if (dropDown == null) {
                dropDown = new AutoCompleteTextViewDropDownItem(mContext);
            }
            if (s.length() >= beginSearchThreshold) {
                if (mHelpListener != null) {
                    mDataSet = mHelpListener.refreshPostData(s.toString());
                    if (dropDown != null) {
                        dropDown.notifyDataSetChanged();
                    }
                }
                if (!dropDown.isShowing()) {
                    if (mDataSet.size() == 0) {
                        return;
                    }
                    dropDown.showAsDropDown(MyAutoCompleteTextView.this);
                } else {
                    //关闭popupwindow
                    if (mDataSet.size() == 0) {
                        dropDown.dismiss();
                    }
                }
            } else {
                if (dropDown.isShowing()) {
                    dropDown.dismiss();
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }

    }

    /**
     * 在弹出式窗口结束后修改了EditText的值
     * 此时要重置一遍该编辑框中的游标
     */
    public void setInputCursor() {
        setSelection(getText().toString().length());
    }

    private class AutoCompleteTextViewDropDownItem extends PopupWindow {

        private ListView listView = null;
        private AutoCompleteTextViewDropDownItemAdapter adapter = null;

        public AutoCompleteTextViewDropDownItem(Context context) {
            super(context);

            adapter = new AutoCompleteTextViewDropDownItemAdapter();

            View view = mInflater.inflate(R.layout.custom_auto_complete_text_view, null);

            listView = (ListView) view.findViewById(R.id.my_auto_complete_text_view_list);

            listView.setAdapter(adapter);

            setWidth(MyAutoCompleteTextView.this.getWidth());
            setHeight(LayoutParams.WRAP_CONTENT);

            /** 得到焦点，这句话务必注释掉，否则会阻塞可编辑文本框的输入 **/
//          setFocusable(true);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setOutsideTouchable(true);  //点击布局外失去焦点
            setFocusableInTouchMode(true);
            setContentView(view);

        }

        public void showAsDropDown(View view) {
            showAsDropDown(view, 0, 0); // 保证尺寸是根据屏幕像素密度来的
            update();       //刷新
        }

        public void notifyDataSetChanged() {
            adapter.notifyDataSetChanged();
        }

        /**
         * 内部类，自动完成文本框的下拉列表的适配器
         */
        private class AutoCompleteTextViewDropDownItemAdapter extends BaseAdapter {

            public int getCount() {
                return mDataSet.size();
            }

            public Object getItem(int position) {
                return mDataSet.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                AutoCompleteTextViewDropDownItemHolder holder = null;
                if (convertView == null) {
                    holder = new AutoCompleteTextViewDropDownItemHolder();
                    convertView = mInflater.inflate(R.layout.my_auto_complete_text_view_item, null);
                    holder.position = position;
                    holder.txt = (TextView) convertView.findViewById(R.id.my_auto_complete_text_view_item_text);
                    convertView.setTag(holder);
                } else {
                    holder = (AutoCompleteTextViewDropDownItemHolder) convertView.getTag();
                }

                convertView.setOnClickListener(new CustomAutoCompleteTextViewItemOnClickListener());
                holder.txt.setText(mDataSet.get(position));
                return convertView;
            }

            private final class AutoCompleteTextViewDropDownItemHolder {

                public int position;
                public TextView txt;
            }


            public class CustomAutoCompleteTextViewItemOnClickListener implements OnClickListener {
                @Override
                public void onClick(View v) {
                    /** 置标记变量为true，表示接下来给可编辑文制本框的赋值是我们控的，不是用户输入的，跳过此次监听事件 **/
                    finishMark = true;
                    TextView mTextView = (TextView) v.findViewById(R.id.my_auto_complete_text_view_item_text);
                    String content = mTextView.getText().toString();
                    MyAutoCompleteTextView.this.setText(content);
                    setInputCursor();
                    if(dropDown!=null&&dropDown.isShowing()){
                        dropDown.dismiss();
                    }
                    if (mOnItemSelectedListener != null) {
                        AutoCompleteTextViewDropDownItemHolder holder = (AutoCompleteTextViewDropDownItemHolder) v.getTag();
                        /** 注意第一个变量我用了null，所以在Activity中不可取值，这点要切记，尽管基本用不到它 **/
                        mOnItemSelectedListener.onItemSelected(null, holder.txt, holder.position, holder.position);
                    }

                }
            }

        }

    }

}