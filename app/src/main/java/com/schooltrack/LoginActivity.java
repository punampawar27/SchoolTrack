package com.schooltrack;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.schooltrack.map.MapActivity;
import com.schooltrack.network.WebServiceUtils;
import com.schooltrack.utils.Util;


/**
 * Created by Mojib on 10/27/2015.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private Button mSignInBtn=null;
    private  String mUserName=null;
    private  String mPassword=null;
    private  String mAccount=null;
    private EditText mAccountET =null;
    private EditText mPasswordET =null;
    private EditText mUserNameET =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_screen);
        initializeView();
    }

    /**
     * Initialize the views
     */
   public void initializeView(){
    mSignInBtn=(Button)findViewById(R.id.btnSingIn);
    mSignInBtn.setOnClickListener(this);
    mAccountET =(EditText)findViewById(R.id.accountET);
    mUserNameET =(EditText)findViewById(R.id.userEt);
    mPasswordET =(EditText)findViewById(R.id.passwordET);

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
    if (validateInfo()) {
        new LoginAsyncTask().execute("");
    }else{

    }
    }

    /**
     * Validate the user's credentials
     * @return true if credentials are not empty
     */
public boolean validateInfo(){
    boolean isValidated=true;
   try {
       mAccount=mAccountET.getText().toString();
       mUserName=mUserNameET.getText().toString();
    mPassword=mPasswordET.getText().toString();

    if (!Util.getInstance().isOnline(this)){
        Util.getInstance().showToast(this,getResources().getString(R.string.internet_alert_message));
        isValidated=false;
    }else if( mAccount==null || mAccount.equalsIgnoreCase("")){
        isValidated=false;
        Util.getInstance().showAlertDialog(this,getResources().getString(R.string.account_empty_alert),getResources().getString(R.string.alert_message_header));
    }else if (mUserName==null || mUserName.equalsIgnoreCase("")){
        isValidated=false;
        Util.getInstance().showAlertDialog(this,getResources().getString(R.string.email_empty_alert),getResources().getString(R.string.alert_message_header));
    }else if (mPassword==null || mPassword.equalsIgnoreCase("")){
        isValidated=false;
        Util.getInstance().showAlertDialog(this,getResources().getString(R.string.password_empty_alert),getResources().getString(R.string.alert_message_header));
    }
   }catch (Exception e){
       e.printStackTrace();
      return  false;}
    return  isValidated;
}

    /**
     * Login AsyncTask call login api and authenticate user's credentials
     */
    private class LoginAsyncTask extends AsyncTask<String, String, String> {

        private String response;

        @Override
        protected String doInBackground(String... params) {
          try {
                String  url = LoginActivity.this.getResources().getString(R.string.login_url);
                 url = url+"account="+mAccount+"&user=" + mUserName + "&password=" + mPassword;
                response = WebServiceUtils.getInstance().callLoginWebAPI(LoginActivity.this, url);
            }catch (Exception  e) {
                e.printStackTrace();
                response = e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
           try{
            findViewById(R.id.screener).setVisibility(View.GONE);
            if(result!=null && result.equalsIgnoreCase("successful")){
               startActivity(new Intent(LoginActivity.this,MapActivity.class));
                LoginActivity.this.finish();
           }else if (result!=null && !result.equalsIgnoreCase("")){
               Util.getInstance().showToast(LoginActivity.this,result);
           }else{
                Util.getInstance().showToast(LoginActivity.this,getResources().getString(R.string.technical_error_message));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

        @Override
        protected void onPreExecute() {
               try{
                findViewById(R.id.screener).setVisibility(View.VISIBLE);
               }catch (Exception e){
                   e.printStackTrace();
               }
               }

        }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
