package com.schooltrack.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.schooltrack.network.WebServiceUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.schooltrack.LoginActivity;

/**
 * Created by Gracie on 10/30/2015.
 */
public class Util {

    private  static  Util mContext=null;
    public static String SELECTED_ROUTE_ID="SELECTED_ROUTE_ID";
    public static String SELECTED_ROUTE_NAME="SELECTED_ROUTE_NAME";

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

public static String SESSION_ID="SESSION_ID";

    public static final int NUM_OF_COLUMNS = 3;
    public static final int GRID_PADDING = 8; // in dp
    public static final String PHOTO_ALBUM = "grid";
    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");

    public static Util getInstance() {
        if (mContext == null) {
            mContext = new Util();
        }
        return mContext;
    }

    public boolean isOnline(Context _act) {
        try {
            final ConnectivityManager conManager = (ConnectivityManager) _act.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = conManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected())
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

       /**
     * Validate hex with regular expression
     *
     * @param hex
     *            hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

    public void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public void showAlertDialog(Context context, String message,String title){
     try{
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
       /* builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }catch (Exception e){
        e.printStackTrace();
    }
    }
    public void showAlertDialog2(Context context, String message,String title){
        try{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle(title);
            builder1.setMessage(message);
            builder1.setCancelable(true);
            builder1.setNeutralButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void logout(final Activity context, final View screener){
         class AsyncTaskLogout extends AsyncTask<String, String, String> {

            private String resp;

            @Override
            protected String doInBackground(String... params) {
                publishProgress("Sleeping..."); // Calls onProgressUpdate()
                try {
                    SharedPreferences prefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
                    String  url = "http://45.40.137.142:8080/webservices/logout?page=login&JSESSIONID="+prefernce.getString("SESSION_ID","");
                    resp = WebServiceUtils.getInstance().callLogoutWebAPI(context, url);
                }catch (Exception  e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
                return resp;
            }

            @Override
            protected void onPostExecute(String result) {
                try{
                    screener.setVisibility(View.GONE);
                    if (result!=null && (result.equalsIgnoreCase("successfull")|| result.equalsIgnoreCase("successful"))){Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        context.finish();
                    }else{
                        Util.getInstance().showToast(context,"Opps something went wrong please try after some times...");
                    }
                }catch (Exception e){
                    e.printStackTrace();;}
            }

            @Override
            protected void onPreExecute() {
                screener.setVisibility(View.VISIBLE);
            }

        }
        new AsyncTaskLogout().execute("");
    }


}
