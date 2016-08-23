package com.linkloving.rtring_new.logic.UI.main.materialmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zkx on 2016/3/7.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> list;

    protected LayoutInflater inflater;

    protected Context mContext;

    private List<Integer> selectList = new ArrayList<Integer>();

    private int selectItem = -1;

    protected boolean selectEnable = false;

    public void enableSelect()
    {
        selectEnable = true;
        notifyDataSetChanged();
    }

    public void disEnable()
    {
        selectEnable = false;
        notifyDataSetChanged();
    }

    public int getSelectItem() {
        return selectItem;
    }

    public boolean isSelect(int index)
    {
        for(Integer select:selectList)
        {
            if(select.intValue() == index)
            {
                return true;
            }
        }
        return false;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        int i;
        for(i = 0;i < selectList.size();i++)
        {
            if(selectList.get(i).intValue() == selectItem)
            {
                break;
            }
        }

        if(i >= selectList.size())
        {
            selectList.add(selectItem);
        }
        else
        {
            selectList.remove(i);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<Integer> selectList) {
        this.selectList = selectList;
    }

    public CommonAdapter(Context context,List<T> list)
    {
        mContext = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = noConvertView(position,convertView,parent);
        }
        else
        {
            convertView = hasConvertView(position,convertView,parent);
        }
        return initConvertView(position, convertView, parent);
    }

    protected abstract View noConvertView(int position, View convertView, ViewGroup parent);

    protected abstract View hasConvertView(int position, View convertView, ViewGroup parent);

    protected abstract View initConvertView(int position, View convertView, ViewGroup parent);
}
