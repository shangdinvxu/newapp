package com.linkloving.rtring_new.logic.UI.friend.group;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.group.groupmodel.Group;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;
import com.yolanda.nohttp.Response;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linkloving on 2016/4/8.
 */
public class SearchGroupAdapter extends RecyclerView.Adapter {
    public final static String TAG = SearchGroupAdapter.class.getSimpleName();
    List<Group> newDatas = new ArrayList<Group>();
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private LayoutInflater mInflater;
    private List<Group> list = new ArrayList<Group>();
    Context context;
    AlertDialog alertDialog;
    public SearchGroupAdapter(Context context, List<Group> list) {
        this.context = context;
        this.list = list;
        newDatas = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.list_item_search_group, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        } else if (viewType == TYPE_FOOTER) {

            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);

            FootViewHolder footViewHolder = new FootViewHolder(foot_view);

            footViewHolder.setIsRecyclable(false);

            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {

            MyLog.e(TAG, "ItemViewHolder=" + position);

            ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);

            itemViewHolder.search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Group ent = list.get(position);
                    final String ent_id = ent.getEnt_id();
                    String group_name = ent.getEnt_name();
                    final String my_ent_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_id();
                    final String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id() + "";
                    final String pasword = ent.getJoin_psw();
                    MyLog.e(TAG, "ent_url:" + ent_id + "  my_ent_id:" + my_ent_id);
                    //|| my_ent_id.equals("0")
                    if (CommonUtils.isStringEmpty(my_ent_id) || my_ent_id.equals("9999999999") || my_ent_id.equals("0"))  //从未加入过群组  第三个是从设备独到的deviceid为0的时候 其实是没加入群组
                    {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.general_tip))
                                .setMessage(MessageFormat.format(context.getString(R.string.relationship_search_join_group_message), group_name))
                                .setPositiveButton(context.getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(pasword)) {
                                            //密码是空 直接进入
                                            CallServer.getRequestInstance().add(context, false, CommParams.REQUEST_GET_GROUP, NoHttpRuquestFactory.create_FindQun_JoinGroup_Request(user_id, ent_id), httpCallback);
                                        MyLog.e(TAG,"密码为空时的请求");
                                        } else {
                                            //else 输入密码再进入
                                            final View layout = mInflater.inflate(R.layout.user_info_update_user_nickname, null);
                                            final EditText nicknameView = (EditText) layout.findViewById(R.id.user_info_update_user_nicknameView);
                                            new AlertDialog.Builder(context)
                                                    .setTitle(context.getString(R.string.relationship_search_group_features))
                                                    .setMessage(context.getString(R.string.relationship_search_group_nopasword))
                                                    .setView(layout)
                                                    .setPositiveButton(context.getString(R.string.general_ok), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            if (!CommonUtils.isStringEmpty(nicknameView.getText().toString())) {

                                                                if (pasword.equals(nicknameView.getText().toString().trim())) {
                                                                    //密码正确
                                                                    if (MyApplication.getInstance(context).isLocalDeviceNetworkOk())
                                                                        CallServer.getRequestInstance().add(context, false, CommParams.REQUEST_GET_GROUP, NoHttpRuquestFactory.create_FindQun_JoinGroup_Request(user_id, ent_id), httpCallback);
                                                                    else
                                                                        MyToast.show(context, "还未连接网络！", Toast.LENGTH_LONG);
                                                                } else {
                                                                    //密码错误
                                                                    new AlertDialog.Builder(context)  //
                                                                            .setTitle(context.getString(R.string.relationship_search_group_psderror))
                                                                            .setMessage(context.getString(R.string.relationship_search_group_errpasword))
                                                                            .setPositiveButton(context.getString(R.string.general_yes), null)
                                                                            .show();
                                                                }
                                                            } else {
                                                                Toast.makeText(context, R.string.relationship_search_group_nopasword, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton(context.getString(R.string.general_cancel), null)
                                                    .show();
                                        }

                                    }
                                })
                                .setNegativeButton(context.getString(R.string.general_cancel), null)
                                .show();
                    } else {
                        String my_ent_name = MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_name();
                        if (ent_id.equals(my_ent_id))   //已经加入过
                        {

                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.general_tip))
                                    .setMessage(MessageFormat.format(context.getString(R.string.relationship_search_already_group_message), my_ent_name))
                                    .setPositiveButton(context.getString(R.string.general_yes), null)
                                    .setNegativeButton(null, null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(context)  //
                                    .setTitle(context.getString(R.string.general_tip))
                                    .setMessage(MessageFormat.format(context.getString(R.string.relationship_search_already_group_message_other), my_ent_name))
                                    .setPositiveButton(context.getString(R.string.general_yes), null)
                                    .setNegativeButton(null, null)
                                    .show();
                        }
                    }
                }


            });


            itemViewHolder.nickName.setText(list.get(position).getEnt_name());
            itemViewHolder.count.setText(MessageFormat.format(context.getString(R.string.relationship_search_people_count), list.get(position).getEnt_count()));
        } else if (holder instanceof FootViewHolder) {

            FootViewHolder footViewHolder = (FootViewHolder) holder;

            int i = footViewHolder.getAdapterPosition();

            if (newDatas.size() != 50) {

                MyLog.e(TAG, "int i=" + i + "new=" + newDatas.size());
                footViewHolder.foot_view_item.setVisibility(View.GONE);

            }

        }
    }

    @Override
    public int getItemCount() {
        // 最后一个item设置为footerView
        return list.size() + 1;
    }

    public void addMoreItem(List<Group> newDatas) {

        this.newDatas = newDatas;

        MyLog.e(TAG, newDatas.size() + ">>>>>>>>>>>>>>>");
        list.addAll(newDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {


        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout search;
        public TextView nickName;
        public TextView count;

        public ItemViewHolder(View itemView) {
            super(itemView);
            search = (FrameLayout) itemView.findViewById(R.id.find_group);
            count = (TextView) itemView.findViewById(R.id.search_group_people_count);
            nickName = (TextView) itemView.findViewById(R.id.search_group_name);

        }
    }

    /**
     * 底部FootView布局
     */
    public class FootViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout foot_view_item;

        public FootViewHolder(View view) {
            super(view);
            foot_view_item = (LinearLayout) view.findViewById(R.id.my_foot_linearlayout);
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.e(TAG, "onSucceed");
            //不为空开始解析
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            MyLog.e(TAG, "返回值response=" + response.get()+"===value===="+value);
            if (!com.linkloving.rtring_new.utils.CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                ToolKits.showCommonTosat(context, true, context.getString(R.string.relationship_search_join_succeed), Toast.LENGTH_SHORT);
                UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                MyApplication.getInstance(context).setLocalUserInfoProvider(userAuthedInfo);
                alertDialog=new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.relationship_search_group_features))
                        .setMessage(context.getString(R.string.relationship_search_group_success2))
                        .setPositiveButton(context.getString(R.string.relationship_search_group_continue), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                CallServer.getRequestInstance().add(context,false,CommParams.REQUEST_GET_GROUP,NoHttpRuquestFactory.create_FindQun_JoinGroup_Request1(MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()+""),httpCallback1);
                                //加入群组成功 弹出继续对话框  点击继续时返回主界面  更换群组logo(未实现)：
                               /* context.startActivity(IntentFactory.createPortalActivityIntent((Activity)context));
                                ((Activity) context).finish();*/
                                //加入群组成功 弹出继续对话框 返回朋友圈页面
                                ((Activity) context).finish();
                            }
                        }).show();
            } else {
                ToolKits.showCommonTosat(context, true, context.getString(R.string.relationship_search_join_failure), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {


            MyLog.e(TAG, "onFailed" + "==message==" + message + "responseCode" + responseCode);

        }
    };
    private HttpCallback<String> httpCallback1 = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.e(TAG, "onSucceed");

            //不为空开始解析
            JSONObject object = JSON.parseObject(response.get());

            String value = object.getString("returnValue");
            MyLog.e(TAG, "====httpCallback1====returnValue=" + value);
            UserEntity userEntity = new Gson().fromJson(value, UserEntity.class);
            MyApplication.getInstance(context).setLocalUserInfoProvider(userEntity);

        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "===httpCallback1==onFailed1"+"==message=="+message+"responseCode"+responseCode);

        }
    };
}
