package com.schooltrack;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.schooltrack.utils.Util;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;

/**
 * Created by Mojib on 10/27/2015.
 */
public class BaseActivity extends SlidingFragmentActivity implements AdapterView.OnItemClickListener, View.OnClickListener {


    private String[] mCategoryArray;
    private ListView mDrawerListView;
    private String mTitle="DashBoard";
    private ArrayList<DrawerItemBean> mItemList=new ArrayList<DrawerItemBean>();
    private SlidingMenu mSlidingMenu =null;
    private boolean mSetClickOnMainMenu = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        try{
        setBehindContentView(R.layout.category_layout);
        customizeSlidingMenu();
        setCategoryList();
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    protected void customizeSlidingMenu() {
      try {

       mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        setSlidingActionBarEnabled(false);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setBehindScrollScale(0.40f);
      }catch (Exception e){
          e.printStackTrace();;}
    }

    protected void disableSwipeOfMenu(){
        if(mSlidingMenu !=null)
            mSlidingMenu.setSlidingEnabled(false);
    }

    /**
     * Initialize the sliding menu setting and listener
     */
    protected void setSlidingMenus() {
        mSetClickOnMainMenu = false;
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);

        mSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {

            @Override
            public void onOpen() {

             if (findViewById(R.id.transperent_layout) != null) {
                 findViewById(R.id.transperent_layout).setVisibility(View.VISIBLE);
             }
            }
        });

        mSlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

            @Override
            public void onClose() {
                if (findViewById(R.id.transperent_layout) != null) {
                    findViewById(R.id.transperent_layout).setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * Set side menu listview
     */
    public void setCategoryList(){
       try {
        TypedArray iconsArray = getResources().obtainTypedArray(R.array.icons);
        Drawable drawable = iconsArray.getDrawable(0);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mCategoryArray = getResources().getStringArray(R.array.category_array);
        for(int i=0 ; i<9;i++) {
            mItemList.add(new DrawerItemBean(iconsArray.getDrawable(i), mCategoryArray[i]));
        }
        mDrawerListView.setAdapter(new CategoryAdapter(mItemList, this));
        mDrawerListView.setOnItemClickListener(this);
        setSlidingMenus();
        findViewById(R.id.textViewLogout).setOnClickListener(this);
       }catch (Exception e){
           e.printStackTrace();
    }
       }

    public void setMenuClick(View view){
        view.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
            case 1:
            default:
        }
    }

    @Override
    public void onClick(View v) {
       try{
        switch (v.getId()){
            case R.id.menuBtn:
                mSlidingMenu.toggle();
                break;
            case R.id.textViewLogout:
                if (Util.getInstance().isOnline(this)) {
                    Util.getInstance().logout(this, findViewById(R.id.screener));
                }else{
                    Util.getInstance().showToast(this,getResources().getString(R.string.internet_alert_message));
                }
                break;

        }
       }catch (Exception e){
           e.printStackTrace();
       }

    }

}
