package com.linkloving.rtring_new.logic.UI.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BundTypeActivity extends ToolBarActivity {
    private static final int TYPE_BAND = 0;
    private static final int TYPE_WATCH = 1;
    private static final int TYPE_BAND_VERSION_3 = 2; //纽扣手环 3.0

    public static final String KEY_TYPE = "device_type";
    public static final String KEY_TYPE_WATCH = "watch";
    public static final String KEY_TYPE_BAND = "band";
    public static final String KEY_TYPE_BAND_VERSION_3 = "band3.0";
    private TypeAdapter typeAdapeter;
    private ArrayList<TypeVo> typeList = new ArrayList<>();
    @InjectView(R.id.bundtype_recycler)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bund_type); //之后去调用initView（）
        typeList.add(TYPE_BAND,new TypeVo(R.mipmap.bound_band_on,R.string.bound_link_band));
        typeList.add(TYPE_WATCH,new TypeVo(R.mipmap.bound_watch_on,R.string.bound_link_watch));
        typeList.add(TYPE_BAND_VERSION_3,new TypeVo(R.mipmap.bound_band_on,R.string.bound_link_niukouband));
        ButterKnife.inject(this);
        typeAdapeter = new TypeAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(typeAdapeter);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        //default do nothing
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.bound_list));
    }

    @Override
    protected void initListeners() {

    }

    private  class  TypeAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bundtype, null);
//          不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            MenuViewHolder viewHolder = (MenuViewHolder) holder;
            viewHolder.image.setImageResource(typeList.get(position).getImgID());
            viewHolder.tv.setText(typeList.get(position).getTextID());
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == TYPE_BAND){
                        MyLog.i("绑定","点击了手环");
                        Intent intent=new Intent();
                        intent.putExtra(KEY_TYPE, KEY_TYPE_BAND);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else if(position == TYPE_WATCH){
                        MyLog.i("绑定","点击了手表");
                        Intent intent=new Intent();
                        intent.putExtra(KEY_TYPE, KEY_TYPE_WATCH);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else if(position == TYPE_BAND_VERSION_3){
                        MyLog.i("绑定","点击了手环3.0");
                        Intent intent=new Intent();
                        intent.putExtra(KEY_TYPE, KEY_TYPE_BAND_VERSION_3);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return  typeList.size();
        }

        class MenuViewHolder extends RecyclerView.ViewHolder{
            public LinearLayout layout;
            //图片
            public ImageView image;
            //描述
            public TextView tv;
            public MenuViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);
                image = (ImageView) itemView.findViewById(R.id.bund_img);
                tv = (TextView) itemView.findViewById(R.id.bund_txt);
            }
        }
    }


    private class TypeVo{

        public TypeVo(int imgID, int textID) {
            this.imgID = imgID;
            this.textID = textID;
        }
        //自定义数据类型
        private int textID;

        private int imgID;

        public int getTextID() {
            return textID;
        }

        public void setTextID(int textID) {
            this.textID = textID;
        }

        public int getImgID() {
            return imgID;
        }

        public void setImgID(int imgID) {
            this.imgID = imgID;
        }
    }

}
