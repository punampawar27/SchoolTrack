package com.schooltrack.gallery;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.schooltrack.R;
import com.schooltrack.utils.Util;

public class FullScreenViewActivity extends Activity implements ViewPager.OnClickListener{
	private SharedPreferences mPrefernce;
	private SwipeAdapter adapter;
	private ViewPager viewPager;
	private ImageView backBtnFullScreen,leftArrow,rightArrow;
	private TextView cameraHeader;
	int positionToShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.swipe_screen);
		backBtnFullScreen=(ImageView)findViewById(R.id.backBtnFullScreen);
		cameraHeader=(TextView)findViewById(R.id.cameraHeader);
		leftArrow=(ImageView)findViewById(R.id.leftArrow);
		rightArrow=(ImageView)findViewById(R.id.rightArrow);
		viewPager = (ViewPager) findViewById(R.id.pager);
		backBtnFullScreen.setOnClickListener(this);
		mPrefernce = getSharedPreferences("GEOTRACKDATA", 0);
		cameraHeader.setText(mPrefernce.getString(Util.SELECTED_ROUTE_NAME, ""));

		Intent i = getIntent();
		positionToShow = i.getIntExtra("position", 0);

		adapter = new SwipeAdapter(FullScreenViewActivity.this,CameraActivity.imageList
				);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(positionToShow);
		leftArrow.setOnClickListener(this);
		rightArrow.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.backBtnFullScreen:
				finish();
				break;
			case  R.id.leftArrow:
				/*adapter = new SwipeAdapter(FullScreenViewActivity.this,CameraActivity.imageList
				);

				viewPager.setAdapter(adapter);

				// displaying selected image first

				viewPager.setCurrentItem(positionToShow-1);*/
				if(positionToShow>0){
				viewPager.setCurrentItem(positionToShow-1);
				positionToShow=positionToShow-1;
				}
				break;
			case  R.id.rightArrow:
				/*adapter = new SwipeAdapter(FullScreenViewActivity.this,CameraActivity.imageList
				);

				viewPager.setAdapter(adapter);

				// displaying selected image first*/
				if(positionToShow<(CameraActivity.imageList.size()-1)){
				viewPager.setCurrentItem(positionToShow+1);
				positionToShow=positionToShow+1;
				}
				break;
			default:
				break;
		}
	}
}
