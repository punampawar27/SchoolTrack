package com.schooltrack.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.schooltrack.R;

import com.schooltrack.network.WebServiceUtils;
import com.schooltrack.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Gracie on 11/24/2015.
 */
public class CameraActivity extends Activity implements  View.OnClickListener {
    private SharedPreferences mPrefernce;
  public static ArrayList<String> imageList;
    GridView mGridView;
    private ImageView backBtnGridView;
    int columnWidth;
    private TextView cameraHeader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.grid_view_activity);
        mGridView=(GridView)findViewById(R.id.imageGridView);
        backBtnGridView=(ImageView)findViewById(R.id.backBtnGridView);
        cameraHeader=(TextView)findViewById(R.id.cameraHeader);
        backBtnGridView.setOnClickListener(CameraActivity.this);

        InitilizeGridLayout();

        mPrefernce = getSharedPreferences("GEOTRACKDATA", 0);
        cameraHeader.setText(mPrefernce.getString(Util.SELECTED_ROUTE_NAME,""));
        new AsyncTaskImageList().execute("http://45.40.137.142:8080/webservices/getImages?JSESSIONID="+mPrefernce.getString("SESSION_ID","")+"&routeId="+mPrefernce.getString(Util.SELECTED_ROUTE_ID,""));

   mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           Intent intent = new Intent(CameraActivity.this, FullScreenViewActivity.class);

           intent.putExtra("position", position);
           startActivity(intent);
       }
   });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtnGridView:
                finish();
                break;
            default:
                break;

        }
    }

    private class AsyncTaskImageList extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                resp = WebServiceUtils.getInstance().callRouteListWebAPI(CameraActivity.this, params[0]);

            }catch (Exception  e) {
                e.printStackTrace();
                resp = e.getMessage();
            }

            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.screener).setVisibility(View.GONE);
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.optJSONArray("imageLst");
                for (int i=0;i<jsonArray.length();i++){
                String imageUrl=jsonArray.optString(i);
                imageList.add(imageUrl);}
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GridAdapter adapter = new GridAdapter(CameraActivity.this, imageList,columnWidth);
            mGridView.setAdapter(adapter);
        }
        @Override
        protected void onPreExecute() {
            imageList=new ArrayList<>();
            findViewById(R.id.screener).setVisibility(View.VISIBLE);

        }
    }
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Util.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() - ((Util.NUM_OF_COLUMNS + 1) * padding)) / Util.NUM_OF_COLUMNS);

        mGridView.setNumColumns(Util.NUM_OF_COLUMNS);
        mGridView.setColumnWidth(columnWidth);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        mGridView.setHorizontalSpacing((int) padding);
        mGridView.setVerticalSpacing((int) padding);
    }


}
