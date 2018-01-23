package com.schooltrack.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import com.schooltrack.R;
import com.schooltrack.utils.Util;

/**
 * Created by Gracie on 11/5/2015.
 */
public class MapRoutListAdapter extends BaseAdapter{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private LayoutInflater mInflater;
    private  MapActivity mContext=null;
    private int mSelectedPosition=0;
    SharedPreferences mPrefernce;

    public MapRoutListAdapter(MapActivity context, ArrayList<Object> item) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData=item;
        mContext=context;
        mPrefernce = context.getSharedPreferences("GEOTRACKDATA", 0);

    }

   /* public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }
*/
    public void addSectionHeaderItem(int position) {
        sectionHeader.add(position);
        notifyDataSetChanged();
    }

    /*@Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }*/

    /*@Override
    public int getViewTypeCount() {
        return 2;
    }
*/
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //int rowType = getItemViewType(position);
        try {
            final Object item = mData.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                // if (item instanceof TrackingItemBean || item instanceof  AvailableRouteItemBean) {
                convertView = mInflater.inflate(R.layout.snippet_item1, null);
                holder.routeName = (TextView) convertView.findViewById(R.id.routeNameTxt);
                holder.routeIcon = (ImageView) convertView.findViewById(R.id.routeIcon);
                holder.seperatorView = (View) convertView.findViewById(R.id.seperatorView);
                holder.routeToggleBtn = (ImageView) convertView.findViewById(R.id.toggleBtn);
                holder.rowContainerLayout = (RelativeLayout) convertView.findViewById(R.id.firstLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.routeToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.updateRouteList(item);
                    if (((AvailableRouteItemBean) item).getStatus().equalsIgnoreCase("ON")) {
                        ((ImageView)view).setImageResource(R.drawable.on_btn_g);
                    }else{
                        ((ImageView)view).setImageResource(R.drawable.on_btn_g);
                    }
                }
            });
            if (item instanceof TrackingItemBean) {
                holder.routeIcon.setVisibility(View.VISIBLE);
                holder.routeToggleBtn.setVisibility(View.GONE);
                holder.seperatorView.setBackgroundResource(R.drawable.sap_line);
              final  TrackingItemBean trackingItem = (TrackingItemBean) item;
                holder.routeIcon.setImageResource(R.drawable.routes_track_icon);
                holder.routeName.setText(trackingItem.getRouteName());
                if(mPrefernce.getString(Util.SELECTED_ROUTE_ID,"").equalsIgnoreCase(trackingItem.getRouteId())){
                    holder.routeName.setTextColor((mContext.getResources().getColor(R.color.tracked_route_text)));
                }else{
                    holder.routeName.setTextColor((mContext.getResources().getColor(R.color.white)));
                }
                holder.rowContainerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.changeCurrentRoute(item);
                        mPrefernce.edit().putString(Util.SELECTED_ROUTE_ID, trackingItem.getRouteId()).commit();
                        MapRoutListAdapter.this.notifyDataSetChanged();
                    }
                });
            } else if (item instanceof AvailableRouteItemBean) {
                holder.routeIcon.setVisibility(View.VISIBLE);
                holder.routeToggleBtn.setVisibility(View.VISIBLE);
                holder.seperatorView.setBackgroundResource(R.drawable.sap_line);
                AvailableRouteItemBean availableItem = (AvailableRouteItemBean) item;
                if (availableItem.getStatus().equalsIgnoreCase("ON")) {
                    holder.routeIcon.setImageResource(R.drawable.bus_icon_red);
                   holder.routeToggleBtn.setImageResource(R.drawable.on_btn_g);
                } else {
                    holder.routeIcon.setImageResource(R.drawable.bus_icon);
                    holder.routeToggleBtn.setImageResource(R.drawable.track_off);
                  //
                  //  holder.routeToggleBtn.setChecked(false);
                }
                holder.routeName.setText(availableItem.getRouteName());
                holder.routeName.setTextColor((mContext.getResources().getColor(R.color.white)));
            } else if (item instanceof HeaderItem) {
                HeaderItem headerItem = (HeaderItem) item;
                holder.routeName.setText(headerItem.getRouteName());
                holder.routeIcon.setVisibility(View.GONE);
                holder.routeToggleBtn.setVisibility(View.GONE);
                holder.seperatorView.setBackgroundResource(R.drawable.sap_dotted);
                holder.routeName.setTextColor((mContext.getResources().getColor(R.color.white)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView routeName;
        public ImageView routeToggleBtn;
        public ImageView routeIcon;
        private  View seperatorView=null;
        public RelativeLayout rowContainerLayout;
    }
}
