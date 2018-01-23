package com.schooltrack.gallery;

import android.app.Activity;
import android.content.Context;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.schooltrack.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dell on 11/26/15.
 */
public class SwipeAdapter extends PagerAdapter {
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    int positionToShow;

    // constructor
    public SwipeAdapter(Activity activity,
                        ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.swipe_adapter, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        Picasso.with(_activity).load(_imagePaths.get(position)).into(imgDisplay);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getScreenWidth(), (getScreenHeight()/2)+30);
        imgDisplay.setLayoutParams(layoutParams);
        imgDisplay.setScaleType(ImageView.ScaleType.FIT_XY);
        //imgDisplay.setMaxZoom(2f);
        imgDisplay.setMinimumWidth(getScreenWidth()+30);
        layoutParams.setMargins(1, 1, 1, 1);

        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);



        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
         });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
    @SuppressWarnings("deprecation")
    public int getScreenHeight() {
        int height = 0;
        WindowManager wm = (WindowManager)_activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

       height=display.getHeight();
        return height;
    }
    public int getScreenWidth() {
        int width = 0;
        WindowManager wm = (WindowManager)_activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        width=display.getHeight();
        return width;
    }

}