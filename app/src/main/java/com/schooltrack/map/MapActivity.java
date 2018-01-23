package com.schooltrack.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.schooltrack.gallery.CameraActivity;
import com.schooltrack.network.WebServiceUtils;
import com.schooltrack.utils.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.schooltrack.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Gracie on 11/5/2015.
 */


public class MapActivity extends FragmentActivity implements View.OnClickListener {

    private  MapRoutListAdapter mAdapter=null;
    private  ListView mListview=null;
    private  RelativeLayout mLeftLayout=null;
    boolean mIsLeftLayoutVisible=false;
    private String mRouteListURL="http://45.40.137.142:8080/webservices/routeList?JSESSIONID=71BE65978B6C105BCADF839668C562B9";
    private String  mRouteDetailURL="http://45.40.137.142:8080/webservices/routeLocation?JSESSIONID=E9CA3F43FFA8D4AC3A94C47752F981CF&routeId=geocorridor1";
    private  GoogleMap mMap=null;
    private  List<LatLng> mDirectionPoint;
    private  LatLng mCurrentLatLng =null;
    private String mLastUpdateDate=null;
    private LatLng mStartLatLng;
    private LatLng mEndLatLng;
     private LatLng mLatLng;
    private ArrayList<Object> mapRouteItemBeansList=new ArrayList<Object>();
   // private  String[] trackedRouteArray=new String[]{"ROUTE 245 NORTH","ROUTE 14 WEST"};
   // private  String[] availableRouteArray=new String[]{"ROUTE 245 NORTH","ROUTE 14 WEST","ROUTE 35","ROUTE 35"};
    private String[]  mSectionArray=new String[]{"NOW TRACKING ROUTES","ROUTES AVAILABLE FOR TRACK"};
    private  RelativeLayout mTransperentLayout=null;
    private TextView mBackToMenuTxt=null;
    private  List<Object> mTrackedRouteList =new ArrayList<Object>();
    private  List<Object> mAvailableRouteList =new ArrayList<Object>();
    //boolean first=true;
    private SharedPreferences mPrefernce;
    private  ArrayList<RouteDetailsItemBean> mRouteDetailsItemBeansList=new ArrayList<RouteDetailsItemBean>();
    private  ImageView mRefreshBtn=null,mCameraBtn;
    private TextView mLastUpdatedTxt;
    private  ImageView mBackBtn=null;
    private  ImageView mMenuBtn=null;
    private  GPSTracker mGpsTracker;
    private  double mLatitude =0.0, mLongitude =0.0;
    private boolean mIsFromGps =false;
    private  Marker mMarker;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurrentLocation();
        }
    };
    private LatLng fromPosition;
    private LatLng toPosition;
    private MarkerOptions markerOptions;

    @Override
    public void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_screen);
        mGpsTracker =new GPSTracker(this);
        try{
        mListview = (ListView) findViewById(R.id.routeListview);
        mLeftLayout=(RelativeLayout)findViewById(R.id.leftLayout);
        mMenuBtn=(ImageView)findViewById(R.id.menuBtn);
        mMenuBtn.setOnClickListener(this);
            mBackBtn=(ImageView)findViewById(R.id.backBtn);
            mBackBtn.setOnClickListener(this);
        TextView mBackTOMapBtn=(TextView)findViewById(R.id.backToMenuTxt);
        mBackTOMapBtn.setOnClickListener(this);
            mRefreshBtn=(ImageView)findViewById(R.id.refreshTxt);
            mCameraBtn=(ImageView)findViewById(R.id.cameraBtn);
            mRefreshBtn.setOnClickListener(this);
            mCameraBtn.setOnClickListener(this);
            mLastUpdatedTxt=(TextView)findViewById(R.id.LastUpdatedTxt);
         mTransperentLayout=(RelativeLayout)findViewById(R.id.transperent_layout);
        mTransperentLayout.setOnClickListener(this);
            mPrefernce = getSharedPreferences("GEOTRACKDATA", 0);
            new AsyncTaskRouteList().execute("http://45.40.137.142:8080/webservices/routeList?JSESSIONID="+mPrefernce.getString("SESSION_ID",""));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
  public void setUpListView(){
    try{
    mAdapter = new MapRoutListAdapter(this,mapRouteItemBeansList);
    mListview.setAdapter(mAdapter);
    }catch (Exception e){
        e.printStackTrace();
    }
}

   public void  initializeMap(){
  try{
      for(int j=0; j<mRouteDetailsItemBeansList.size();j++){
          RouteDetailsItemBean routeDetailsItemBean=mRouteDetailsItemBeansList.get(j);
          mCurrentLatLng=routeDetailsItemBean.getCurrentLatLng();
          mStartLatLng=routeDetailsItemBean.getStartLatLng();
          mEndLatLng=routeDetailsItemBean.getEndLatLng();
          mDirectionPoint=routeDetailsItemBean.getmDirectionPoint();
          mLastUpdateDate=routeDetailsItemBean.getLastUpdateDate();
          List<String> stopList=routeDetailsItemBean.getStopAddressList();
          final PolylineOptions rectLine;
          rectLine = new PolylineOptions().width(3).color(Color.BLUE);
        for (int i = 0; i < mDirectionPoint.size()&& i<20; i++) {
            rectLine.add(mDirectionPoint.get(i));
            if (i!=0 && i!=mDirectionPoint.size()-1) {
                mMap.addMarker(new MarkerOptions().position(mDirectionPoint.get(i)).title(stopList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_btn_current)));
            }
       }
       mMap.addPolyline(rectLine);
          if (mPrefernce.getString(Util.SELECTED_ROUTE_ID,"").equalsIgnoreCase(routeDetailsItemBean.getRouteId())){
              CameraUpdate camera= CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 14);
              mMap.animateCamera(camera);
              mLastUpdatedTxt.setText(mLastUpdateDate);
          }
       mMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon_red)));
       mMap.addMarker(new MarkerOptions().position(mStartLatLng).title(stopList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_bubuls_icon)));
       mMap.addMarker(new MarkerOptions().position(mEndLatLng).title(stopList.get(stopList.size() - 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_bubuls_icon)));
      }
      }catch (Exception e){
        e.printStackTrace();
     }
   }
    private void setUpMapIfNeeded() {
     try{
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                //initializeMap();
            }
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    }
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.menuBtn:
                    if (!mIsLeftLayoutVisible) {
                        mLeftLayout.setVisibility(View.VISIBLE);
                        mMenuBtn.setVisibility(View.GONE);
                        Animation RightSwipe = AnimationUtils.loadAnimation(MapActivity.this, R.anim.right_slide);
                        mLeftLayout.startAnimation(RightSwipe);
                        mIsLeftLayoutVisible = true;
                        mTransperentLayout.setVisibility(View.VISIBLE);
                        mBackBtn.setVisibility(View.VISIBLE);
                        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
                        rotation.setRepeatCount(1);
                        mBackBtn.startAnimation(rotation);
                    } else {
                        closeSlidingDrawer();
                    }
                    break;
                case R.id.backToMenuTxt:
                   // closeSlidingDrawer();
                    if (Util.getInstance().isOnline(this)) {
                        Util.getInstance().logout(this, findViewById(R.id.screener));
                    }else{
                        Util.getInstance().showToast(this,getResources().getString(R.string.internet_alert_message));
                    }
                    break;
                case R.id.transperent_layout:
                    if (mIsLeftLayoutVisible) {
                       closeSlidingDrawer();
                    }
                    break;
                case R.id.cameraBtn:
                    String selectedRouteId=(mPrefernce.getString(Util.SELECTED_ROUTE_ID,""));
                    if(selectedRouteId.length()!=0){
                        Intent intent=new Intent(MapActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }else{
                        Util.getInstance().showAlertDialog(this,"Please select route to view photos","Alert");
                    }
                    break;
                case R.id.refreshTxt:
                    if (mTrackedRouteList!=null && mTrackedRouteList.size()>0) {
                        AsyncTaskROuteDetails execute = new AsyncTaskROuteDetails();
                        execute.isLoaderShow="True";
                        execute.execute("");
                    }else{
                        Util.getInstance().showToast(this,"Please select atleast one route for trak...");
                    }
                    break;
                case R.id.backBtn:
               // startActivity(new Intent(this, DashBoardActivity.class));
                 //   MapActivity.this.finish();
                    closeSlidingDrawer();
                break;
                default:
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class AsyncTaskROuteDetails extends AsyncTask<String, String, String> {
        private String resp;
        public String isLoaderShow="false";
        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                mRouteDetailsItemBeansList.clear();
                for (int j=0; j<mTrackedRouteList.size();j++) {
                    RouteDetailsItemBean routeDetailsItemBean = new RouteDetailsItemBean();
                    ArrayList<String> stopAddressList=new ArrayList<String>();
                    ArrayList<String> ETAList=new ArrayList<String>();
                    ArrayList<String> visitedList=new ArrayList<String>();
                            String routeID=((TrackingItemBean)mTrackedRouteList.get(j)).getRouteId();
                    resp = WebServiceUtils.getInstance().callRouteDetailsWebAPI(MapActivity.this, routeID);
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getJSONArray("LocationList") != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray("LocationList");
                        mDirectionPoint = new ArrayList<LatLng>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonLatLong = jsonArray.getJSONObject(i);
                            mDirectionPoint.add(new LatLng(jsonLatLong.getDouble("Lat"), jsonLatLong.getDouble("Lon")));
                            stopAddressList.add(jsonLatLong.getString("Stop"));
                            ETAList.add(jsonLatLong.getString("distance"));
                            visitedList.add(jsonLatLong.getString("isVisited"));
                            if (i == 0) {
                                mStartLatLng = new LatLng(jsonLatLong.getDouble("Lat"), jsonLatLong.getDouble("Lon"));
                            } else if (i == jsonArray.length() - 1) {
                                mEndLatLng = new LatLng(jsonLatLong.getDouble("Lat"), jsonLatLong.getDouble("Lon"));
                            }
                        }
                    }
                  //  String url = getDirectionsUrl(mStartLatLng, mEndLatLng);
                   // getDirectionPath(url);
                    if (jsonObject.has("CurrentValue")) {
                        mCurrentLatLng = new LatLng(jsonObject.getJSONObject("CurrentValue").getDouble("Lat"), jsonObject.getJSONObject("CurrentValue").getDouble("Lon"));
                    }
                    if (jsonObject.has("lastUpdatedDate")) {
                        mLastUpdateDate = jsonObject.getString("lastUpdatedDate");
                    }
                    routeDetailsItemBean.setStopAddressList(stopAddressList);
                    routeDetailsItemBean.setVisitedList(visitedList);
                    routeDetailsItemBean.setETAList(ETAList);
                    routeDetailsItemBean.setCurrentLatLng(mCurrentLatLng);
                    routeDetailsItemBean.setEndLatLng(mEndLatLng);
                    routeDetailsItemBean.setLastUpdateDate(mLastUpdateDate);
                    routeDetailsItemBean.setmDirectionPoint(mDirectionPoint);
                    routeDetailsItemBean.setRouteId(routeID);
                    routeDetailsItemBean.setStartLatLng(mStartLatLng);
                    mRouteDetailsItemBeansList.add(routeDetailsItemBean);
                 }
            }catch (Exception  e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            try{

                if (result!=null && !result.equalsIgnoreCase("")){
                    setUpMapIfNeeded();
                   // initializeMap();
                    markerOptions = new MarkerOptions();
                    fromPosition = mStartLatLng;//new LatLng(11.663837, 78.147297);
                    toPosition = mEndLatLng;//new LatLng(11.723512, 78.466287);
                    GetRouteTask getRoute = new GetRouteTask();
                    getRoute.isShowLoader=false;
                    getRoute.execute();
                    }else{
                    findViewById(R.id.screener).setVisibility(View.GONE);
                    Util.getInstance().showToast(MapActivity.this,"Opps something went wrong please try after some times...");
                }
            }catch (Exception e){
                e.printStackTrace();;}
        }

        @Override
        protected void onPreExecute() {
         try{
             if (isLoaderShow!=null && isLoaderShow.equalsIgnoreCase("true")) {
                 findViewById(R.id.screener).setVisibility(View.VISIBLE);
             }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    }

    private class AsyncTaskRouteList extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                resp = WebServiceUtils.getInstance().callRouteListWebAPI(MapActivity.this, params[0]);
                JSONObject jsonObject=new JSONObject(resp);
                if (jsonObject.getJSONArray("RouteList") !=null){

                    JSONArray jsonArray= jsonObject.getJSONArray("RouteList");
                    mTrackedRouteList.clear();
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonLatLong= jsonArray.getJSONObject(i);
                       if (jsonLatLong.getString("Status").equalsIgnoreCase("ON")){
                        TrackingItemBean trackingItemBean=new TrackingItemBean();
                           trackingItemBean.setStatus(jsonLatLong.getString("Status"));
                           trackingItemBean.setRouteName(jsonLatLong.getString("Route Name"));
                           trackingItemBean.setRouteId(jsonLatLong.getString("RouteId"));
                           mTrackedRouteList.add(trackingItemBean);
                       }
                    }
                    mAvailableRouteList.clear();
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonLatLong= jsonArray.getJSONObject(i);
                            AvailableRouteItemBean availableRouteItemBean=new AvailableRouteItemBean();
                        availableRouteItemBean.setStatus(jsonLatLong.getString("Status"));
                        availableRouteItemBean.setRouteName(jsonLatLong.getString("Route Name"));
                        availableRouteItemBean.setRouteId(jsonLatLong.getString("RouteId"));
                        mAvailableRouteList.add(availableRouteItemBean);
                    }
                    prepareRouteList();
                }

            }catch (Exception  e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                if (mTrackedRouteList!=null && mTrackedRouteList.size()>0) {
                    AsyncTaskROuteDetails execute = new AsyncTaskROuteDetails();
                    execute.isLoaderShow="False";
                    execute.execute("");
                }else {
                     findViewById(R.id.screener).setVisibility(View.GONE);
                    mPrefernce.edit().putString(Util.SELECTED_ROUTE_ID, "").commit();
                    try{
                        mGpsTracker =new GPSTracker(MapActivity.this);
                  if(mGpsTracker.isGPSEnabled){
                     mHandler.sendEmptyMessage(1);
                    }else{
                      showAlertDialog2(MapActivity.this,"Please Enable Gps To View Current Location","Alert");
}
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    if(mMap!=null) {
                        mMap.clear();
                    }
                }
                setUpListView();
            }catch (Exception e){
                e.printStackTrace();;}
        }
        @Override
        protected void onPreExecute() {
            findViewById(R.id.screener).setVisibility(View.VISIBLE);
        }
    }
public void prepareRouteList(){
    try{
    mapRouteItemBeansList.clear();
    HeaderItem item = new HeaderItem();
    if (mTrackedRouteList!=null && mTrackedRouteList.size()>0){

        item.setRouteName(mSectionArray[0]);
        mapRouteItemBeansList.add(item);
        mapRouteItemBeansList.addAll(mTrackedRouteList);
        if(mPrefernce.getString(Util.SELECTED_ROUTE_ID,"").equalsIgnoreCase("")){
            mPrefernce.edit().putString(Util.SELECTED_ROUTE_ID, ((TrackingItemBean)mTrackedRouteList.get(0)).getRouteId()).commit();
            mPrefernce.edit().putString(Util.SELECTED_ROUTE_NAME, ((TrackingItemBean) mTrackedRouteList.get(0)).getRouteName()).commit();
        }
    }
    item = new HeaderItem();
    item.setRouteName(mSectionArray[1]);
    mapRouteItemBeansList.add(item);
    mapRouteItemBeansList.addAll(mAvailableRouteList);
}catch (Exception e){
        e.printStackTrace();
    }
}
    public void updateRouteList(Object item){
     try{
        String api="";
        if(mPrefernce==null){
            mPrefernce = getSharedPreferences("GEOTRACKDATA", 0);
        }
        if (((AvailableRouteItemBean) item).getStatus().equalsIgnoreCase("ON")) {
            api="http://45.40.137.142:8080/webservices/routeStatusChange?JSESSIONID="+mPrefernce.getString("SESSION_ID","")+"&routeId="+((AvailableRouteItemBean) item).getRouteId()+"&status=OFF";
        }else{
            api="http://45.40.137.142:8080/webservices/routeStatusChange?JSESSIONID="+mPrefernce.getString("SESSION_ID","")+"&routeId="+((AvailableRouteItemBean) item).getRouteId()+"&status=ON";
            if(mPrefernce.getString(Util.SELECTED_ROUTE_ID,"").equalsIgnoreCase(((AvailableRouteItemBean) item).getRouteId())){
                mPrefernce.edit().putString(Util.SELECTED_ROUTE_ID, "").commit();
            }
        }
        mPrefernce = getSharedPreferences("GEOTRACKDATA", 0);
        new AsyncTaskRouteList().execute(api);
    }catch (Exception e){
        e.printStackTrace();
    }
    }

    public void changeCurrentRoute(Object item){
        try {
            closeSlidingDrawer();
            String routeID = ((TrackingItemBean) item).getRouteId();
            for (int i = 0; i < mRouteDetailsItemBeansList.size(); i++) {
                if (mRouteDetailsItemBeansList.get(i).getRouteId().equalsIgnoreCase(routeID)) {
                    CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(mRouteDetailsItemBeansList.get(i).getCurrentLatLng(), 14);
                    mMap.animateCamera(camera);
                    mLastUpdatedTxt.setText(mRouteDetailsItemBeansList.get(i).getLastUpdateDate());
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

  public void closeSlidingDrawer(){
   try{
    mTransperentLayout.setVisibility(View.GONE);
    Animation RightSwipe = AnimationUtils.loadAnimation(MapActivity.this, R.anim.left_slide);
    mLeftLayout.startAnimation(RightSwipe);
    mLeftLayout.setVisibility(View.GONE);
    mIsLeftLayoutVisible = false;
       mBackBtn.setVisibility(View.GONE);
       mMenuBtn.setVisibility(View.VISIBLE);
       Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
       rotation.setRepeatCount(1);
       mMenuBtn.startAnimation(rotation);
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
                            turnGPSOn();
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!mIsLeftLayoutVisible) {

                            } else {
                                closeSlidingDrawer();
                            }
                            dialog.cancel();
                            {
                                setCurrentLocation();
                            }
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void turnGPSOn()
    {
        mIsFromGps =true;
        if (!mIsLeftLayoutVisible) {

        } else {
            closeSlidingDrawer();
        }
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsFromGps){
            mIsFromGps =false;
        if (mTrackedRouteList==null || mTrackedRouteList.size()<=0) {
            setCurrentLocation();
        }else {}
        }
    }

    public void setCurrentLocation(){
        try{
        mLatitude = mGpsTracker.getLatitude();
        mLongitude = mGpsTracker.getLongitude();
        mLatLng = new LatLng(mLatitude, mLongitude);
            if(mMap==null) {
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }
            if (mMap != null) {
                CameraUpdate camera= CameraUpdateFactory.newLatLngZoom(mLatLng, 14);
                mMap.animateCamera(camera);
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(mLatLng)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_btn_current)));
                mMarker.showInfoWindow();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void getDirectionPath(String url){

        try {
            String  data = "";//downloadUrl(url);
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            jObject = new JSONObject(data);
            DirectionJSONParser parser= new DirectionJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<routes.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = routes.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    mDirectionPoint.add(position);
                }
            System.out.print("");
                // Adding all the points in the route to LineOptions
                /*lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);*/
            }

            // Drawing polyline in the Google Map for the i-th route
            //map.addPolyline(lineOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";
        GMapVGetRouteDirection v2GetRouteDirection=new GMapVGetRouteDirection();
        Document document;
        public boolean isShowLoader=false;
        ArrayList<Document> documentList=new ArrayList<Document>();
       /* MarkerOptions markerOptions;
        private LatLng fromPosition;
        private LatLng toPosition;
*/
        @Override
        protected void onPreExecute() {
           /* Dialog = new ProgressDialog(MapActivity.this);
            Dialog.setMessage("Loading route...");
            Dialog.show();
           */
            v2GetRouteDirection = new GMapVGetRouteDirection();
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            try {
                for (int i=0 ;i<mRouteDetailsItemBeansList.size();i++){
                    document = v2GetRouteDirection.getDocument(mRouteDetailsItemBeansList.get(i).getStartLatLng(),mRouteDetailsItemBeansList.get(i).getEndLatLng(), GMapVGetRouteDirection.MODE_DRIVING);
                    documentList.add(document);
                }

                response = "Success";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                mMap.clear();
                if(response.equalsIgnoreCase("Success")){
                    for (int i=0; i<documentList.size();i++) {
                        document=documentList.get(i);
                        RouteDetailsItemBean routeDetailsItemBean= mRouteDetailsItemBeansList.get(i);

                        ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                                R.color.purple
                        );

                        for (int j = 0; j < directionPoint.size(); j++) {
                            rectLine.add(directionPoint.get(j));
                        }
                        // Adding route on the map
                        mMap.addPolyline(rectLine);
                       /* markerOptions.position(toPosition);
                        markerOptions.draggable(true);
                        mMap.addMarker(markerOptions);*/
                        if (mPrefernce.getString(Util.SELECTED_ROUTE_ID, "").equalsIgnoreCase(routeDetailsItemBean.getRouteId())) {
                            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 14);
                            mMap.animateCamera(camera);
                            mLastUpdatedTxt.setText(mLastUpdateDate);
                        }
                        mCurrentLatLng = mCurrentLatLng;//new LatLng(11.663837, 78.147297);
                        mCurrentLatLng=routeDetailsItemBean.getCurrentLatLng();
                        mStartLatLng=routeDetailsItemBean.getStartLatLng();
                        mEndLatLng=routeDetailsItemBean.getEndLatLng();
                        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 14);
                        mMap.animateCamera(camera);
                        mMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon_red)));
                        mMap.addMarker(new MarkerOptions().position(mStartLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_bubuls_icon)));
                        mMap.addMarker(new MarkerOptions().position(mEndLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_bubuls_icon)));
                    }
                }
                findViewById(R.id.screener).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Dialog.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}




