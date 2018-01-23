package com.schooltrack;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;

import com.schooltrack.fragments.RouteDetailsFragment;
import com.schooltrack.notification.GCMRegistration;

import java.util.ArrayList;

/**
 * Created by Gracie on 10/27/2015.
 */
public class DashBoardActivity extends BaseActivity {

    private String[] some_array;
    private ListView mDrawerList;
    private String mTitle="DashBoard";
    private ArrayList<DrawerItemBean> mItemList=new ArrayList<DrawerItemBean>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_screen);
        initializeViews();
        new GCMRegistration().setRegistrationId(this, "");
    }

    public void initializeViews(){
        setMenuClick(findViewById(R.id.menuBtn));
         selectItem(0);
    }
    public  void selectItem(int position){
        Fragment fragment = new RouteDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle =(String) title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getOrientation();
        switch(orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }

    }
}
