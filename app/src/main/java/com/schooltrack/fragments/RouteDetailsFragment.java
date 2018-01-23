package com.schooltrack.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.schooltrack.R;
import com.schooltrack.map.MapActivity;
import com.schooltrack.utils.Util;


/**
 * Created by Gracie on 10/27/2015.
 */
public class RouteDetailsFragment extends Fragment implements View.OnClickListener {
    private  View rootView;
    private TextView mDetailTxt=null;
    private TextView mTitleTxt=null;
    private TextView mSubTitleTxt=null;
    private  TextView mReportTxt=null;
    private  ImageButton mRouteDetailBtn=null;
    private  ImageButton mLogoutBtn=null;

    public RouteDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.route_details_screen, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      try{
        mTitleTxt= (TextView) rootView.findViewById(R.id.titleTxt);
        mDetailTxt= (TextView) rootView.findViewById(R.id.descriptionTxt);
        mSubTitleTxt= (TextView) rootView.findViewById(R.id.bannerTxt);
        mReportTxt= (TextView) rootView.findViewById(R.id.reportTxt);
mLogoutBtn=(ImageButton)rootView.findViewById(R.id.logoutImgBtn);
mLogoutBtn.setOnClickListener(this);
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "open_sans_regular.ttf");
        mDetailTxt.setTypeface(font);
        mSubTitleTxt.setTypeface(font);
        mTitleTxt.setTypeface(font);
        mReportTxt.setTypeface(font);
        mRouteDetailBtn=(ImageButton)rootView.findViewById(R.id.routeDetailImgBtn);
        mRouteDetailBtn.setOnClickListener(this);
    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void onClick(View v) {
      try {
          switch (v.getId()) {
              case R.id.routeDetailImgBtn:
                  startActivity(new Intent(this.getActivity(), MapActivity.class));
                  break;
              case R.id.logoutImgBtn:
                  showAlertDialog(RouteDetailsFragment.this.getActivity());
                  break;
              default:
                  break;
          }
      }catch (Exception e){e.printStackTrace();
      }
    }
    public void showAlertDialog(Context context){
       try {
           AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
           builder1.setTitle("Alert Message");
           builder1.setMessage("Do you really want to logout");
           builder1.setCancelable(true);
           builder1.setPositiveButton("No",
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.cancel();
                       }
                   });
           builder1.setNegativeButton("Yes",
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.cancel();
                           if (Util.getInstance().isOnline(RouteDetailsFragment.this.getActivity())) {
                               Util.getInstance().logout(RouteDetailsFragment.this.getActivity(), rootView.findViewById(R.id.screener));
                           } else {
                               Util.getInstance().showToast(RouteDetailsFragment.this.getActivity(), "Please connect to internet...");
                           }
                       }
                   });

           AlertDialog alert11 = builder1.create();
           alert11.show();
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
