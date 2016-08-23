package com.linkloving.rtring_new.logic.UI.main.materialmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.logic.UI.customerservice.serviceItem.Feedback;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.youzan.sdk.Callback;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;

import java.util.List;

/**
 * Created by zkx on 2016/3/11.
 */
public class MenuNewAdapter extends RecyclerView.Adapter {
    private static final String TAG = MenuNewAdapter.class.getSimpleName();
    public  static int JUMP_FRIEND_TAG=1;
    UserEntity userEntity;
    private Context mContext;
    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private List<MenuVO> list;

    public MenuNewAdapter(Context context, List<MenuVO> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_menu, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        holder.itemImg.setImageResource(list.get(position).getImgID());
        holder.itemName.setText(list.get(position).getTextID());
        holder.itemName.setTextColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //还可以添加长点击事件
    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout layout;
        public TextView itemName;
        public ImageView itemImg;
        public TextView unread;

        public MenuViewHolder(View convertView) {
            super(convertView);
            itemImg = (ImageView) convertView.findViewById(R.id.fragment_item_img);
            itemName = (TextView) convertView.findViewById(R.id.fragment_item_text);
            unread = (TextView) convertView.findViewById(R.id.fragment_item_unread_text);
            layout = (LinearLayout) convertView.findViewById(R.id.Layout);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
                switch (this.getPosition()){

                    case Left_viewVO.FRIENDS:
                        IntentFactory.start_FriendActivity((Activity) mContext,JUMP_FRIEND_TAG);
                        break;

                    case Left_viewVO.RANKING:
                        IntentFactory.start_RankingActivityIntent((Activity) mContext);
                        break;

                    case Left_viewVO.GOAL:
                        IntentFactory.startGoalActivityIntent((Activity) mContext, MyApplication.getInstance((Activity) mContext).getLocalUserInfoProvider());
                        break;

                    case Left_viewVO.KEFU:
                        IntentFactory.start_CustomerService_ActivityIntent((Activity) mContext, Feedback.PAGE_INDEX_ONE);
                        break;

                    case Left_viewVO.SHOP:
                        //        跳转到更多界面
                        registerYouzanUserForWeb();
                        break;

                    case Left_viewVO.MORE:
                        //        跳转到更多界面
                        mContext.startActivity(IntentFactory.start_MoreActivityIntent((Activity) mContext));
                        break;
                    default :
                }
            }
        }

        private void registerYouzanUserForWeb() {
            /**
             * 打开有赞入口网页需先注册有赞用户
             * <pre>
             * 如果你们App的用户这个时候还没有登录, 请先跳转你们的登录页面, 然后再回来同步用户信息
             *
             * 或者参考{@link LoginWebActivity}
             * </pre>
             */
            UserEntity userEntity = MyApplication.getInstance(mContext).getLocalUserInfoProvider();
            YouzanUser user = new YouzanUser();
            user.setUserId(userEntity.getUser_id()+"");
            user.setGender(userEntity.getUserBase().getUser_sex());
            user.setNickName(userEntity.getUserBase().getNickname());
            user.setUserName(userEntity.getUserBase().getNickname());
            if(!CommonUtils.isStringEmpty(userEntity.getUserBase().getUser_mobile())){
                user.setTelephone(userEntity.getUserBase().getUser_mobile());
            }
            YouzanSDK.asyncRegisterUser(user, new Callback() {
                @Override
                public void onCallback() {
                    IntentFactory.startH5Activity((Activity) mContext);
                }
            });
        }
    }
}
