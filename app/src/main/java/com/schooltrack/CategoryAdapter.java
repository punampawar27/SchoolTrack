package com.schooltrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gracie on 10/27/2015.
 */
public class CategoryAdapter extends BaseAdapter {
    private  Context mContext;
    private ArrayList<DrawerItemBean> mItemList;
    CategoryAdapter(ArrayList<DrawerItemBean> itemList, Context context){
        this.mContext=context;
        mItemList=itemList;
    }
    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.category_row, parent, false);
                holder.categoryImageView = (ImageView) convertView.findViewById(R.id.imageViewIcon);
                holder.categoryNameTxt = (TextView) convertView.findViewById(R.id.textViewName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DrawerItemBean item = mItemList.get(position);
            holder.categoryImageView.setImageDrawable(item.icon);
            holder.categoryNameTxt.setText(item.name);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "open_sans_regular.ttf");
            holder.categoryNameTxt.setTypeface(font);
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView categoryNameTxt=null;
        ImageView categoryImageView=null;
    }
}
