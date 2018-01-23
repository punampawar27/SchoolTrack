package com.schooltrack.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.schooltrack.utils.Util;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gracie on 10/31/2015.
 */
public class WebServiceUtils {


    public static WebServiceUtils mContextt=null;
    public static WebServiceUtils getInstance(){
        if (mContextt==null){
            mContextt=new WebServiceUtils();
        }
        return  mContextt;
    }

    public String callLoginWebAPI(Context context,String url){
        final ParserUtil parUtil = new ParserUtil(context);
        String respStr = null;
        try {
            if (Util.getInstance().isOnline(context)) {
                SharedPreferences prefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
                respStr = parUtil.fetchJSONResponse(url);
                if (respStr != null) {
                    JSONObject jsonObject=new JSONObject(respStr);
                    if (jsonObject.getString("login")!=null && (jsonObject.getString("login").equalsIgnoreCase("successfull")||jsonObject.getString("login").equalsIgnoreCase("successful"))){
                        prefernce.edit().putString("SESSION_ID",jsonObject.getString("sessionId")).commit();
                        return jsonObject.getString("login");
                    }else{
                        return  jsonObject.getString("login");
                    }
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public String callLogoutWebAPI(Context context,String url){
        final ParserUtil parUtil = new ParserUtil(context);
        String respStr = null;
        try {
            if (Util.getInstance().isOnline(context)) {
                SharedPreferences prefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
                respStr = parUtil.fetchJSONResponse(url);
                if (respStr != null) {
                    JSONObject jsonObject=new JSONObject(respStr);
                    if (jsonObject.getString("logout")!=null && (jsonObject.getString("logout").equalsIgnoreCase("successful")||jsonObject.getString("logout").equalsIgnoreCase("successfull"))){
                        prefernce.edit().putString("SESSION_ID","").commit();
                        return jsonObject.getString("logout");
                    }else{
                        return  null;
                    }
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public String callRouteDetailsWebAPI(Context context,String routeID){
        final ParserUtil parUtil = new ParserUtil(context);
        String respStr = null;
        final List<LatLng> directionPoint =new ArrayList<LatLng>();

        directionPoint.add(new LatLng(22.7000, 75.9000));

        try {
            if (Util.getInstance().isOnline(context)) {
                SharedPreferences prefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
                String  url = "http://45.40.137.142:8080/webservices/routeLocation?JSESSIONID="+prefernce.getString("SESSION_ID","")+"&routeId="+routeID;
                respStr = parUtil.fetchJSONResponse(url);
                if (respStr != null) {
                   return respStr;
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public String callRouteListWebAPI(Context context,String url){
        final ParserUtil parUtil = new ParserUtil(context);
        String respStr = null;
        try {
            if (Util.getInstance().isOnline(context)) {
                respStr = parUtil.fetchJSONResponse(url);
                if (respStr != null) {
                    return respStr;
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public String callWebAPI(Context context,String url){
        final ParserUtil parUtil = new ParserUtil(context);
        String respStr = null;
        try {
            if (Util.getInstance().isOnline(context)) {
                SharedPreferences prefernce = context.getSharedPreferences("GEOTRACKDATA", 0);
                respStr = parUtil.fetchJSONResponse(url);
                if (respStr != null) {
                    return respStr;
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
