package com.example.user.android_drone_control;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//========開啟畫面===================
public class SplashActivity extends AppCompatActivity {
    private Handler mThreadHandler;
    private WifiManager wiFiManager;
    Button Login_button;
    private EditText editText_acount;
    private EditText editText_passwd;
    private TextView Notice;
    String Account=new String();
    String Password=new String();
    private ProgressDialog progress;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_layout);

        player = MediaPlayer.create(SplashActivity.this, R.raw.unturned_ost);
        player.setLooping(true);

        //=====================多執行續(網路部分)=================================
        mThreadHandler=new Handler();
        mThreadHandler.postDelayed(runnable1,1000);
        //======================取得wifi權限======================
        wiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //=======================================================

        editText_acount=(EditText)findViewById(R.id.account);
        editText_passwd=(EditText)findViewById(R.id.passwd);
        Notice=(TextView)findViewById(R.id.notice);
        Account=editText_acount.getText().toString().trim();
        Password=editText_passwd.getText().toString().trim();


        Login_button=(Button)findViewById(R.id.login);
        Login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequest();
            }
        });



    }

    //====================Thread Runnable===================
    private Runnable runnable1=new Runnable () {
        public void run() {
            // TODO Auto-generated method stub
            //=======================確認網路是否開啟================================
            if (isConnected()) {
                //==================確認是否有輸入帳號密碼===========================
                Account=editText_acount.getText().toString().trim();
                Password=editText_passwd.getText().toString().trim();
                if(Account.equals("")||Password.equals("")){
                    Login_button.setEnabled(false);
                }else{
                    Login_button.setEnabled(true);
                }

            }else{
                Button button=(Button)findViewById(R.id.login);
                Login_button.setEnabled(false);
                //測試用Snackbar代替Toast
                Snackbar.make(button, "Internet lost", Snackbar.LENGTH_LONG).setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // action triggered

                        if (!wiFiManager.isWifiEnabled()) {
                            wiFiManager.setWifiEnabled(true); //要加入<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
                        }
                        if(wiFiManager.isWifiEnabled()){
                            wiFiManager.setWifiEnabled(false);
                        }

                    }
                }).show();
            }

            mThreadHandler.postDelayed(this,500);
        }

    };

    //====================網路連線確認======================
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()&&(!networkInfo.isFailover())) {
            return true;
        }
        return false;
    }

    //===================network 連線====================
    public void sendPostRequest() {
        new PostClass(this).execute();
    }


    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c){

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute(){

            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/User_Login_check.jsp");

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                /*
                                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                                dStream.writeBytes(urlParameters);
                                dStream.flush();
                                dStream.close();
                                */
                //======================取得所有定點巡航座標================================


                /*
                for(int i=0;i<latLng2.size();i++){
                    Lat=Lat+latLng2.get(i).latitude+",";
                    Lng=Lng+latLng2.get(i).longitude+",";
                }
                */
                String account="Account="+Account;
                String passwd="Password="+Password;

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(account+"&"+passwd);
                dStream.flush();
                dStream.close();

                /*
                                   try{
                                       JSONObject jsonObject=new JSONObject();
                                       jsonObject.put("Command","test");
                                       OutputStream os = connection.getOutputStream();
                                       OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                                       osw.write(jsonObject.toString());
                                       osw.flush();
                                       osw.close();
                                   }catch(JSONException ex) {
                                       ex.printStackTrace();
                                        Toast.makeText(DroneFixedPointActivity.this,"error",Toast.LENGTH_SHORT).show();
                                   }
                                   */
                //==================================================================

                final int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);

                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator")  + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + responseOutput.toString());

                SplashActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        //若讀取網頁正確，則進行帳密的判斷，不然就顯示Connect error.
                        if(responseCode !=200){
                            FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.sed_fab);
                            Snackbar.make(fab,"Connect error",Snackbar.LENGTH_SHORT).show();
                        }else{

                            String temp=responseOutput.toString().trim();
                            if(temp.equals("true")){
                                Toast.makeText(SplashActivity.this,"true",Toast.LENGTH_SHORT).show();
                                Notice.setText("Login Successful");
                                //===跳至home頁面======
                                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("Account", Account);
                                bundle.putString("Password", Password);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                SplashActivity.this.finish();
                            }else{
                                Toast.makeText(SplashActivity.this,"false",Toast.LENGTH_SHORT).show();
                                Notice=(TextView)findViewById(R.id.notice);
                                Notice.setText("The username and password you entered did not match our records. \nPlease check and try again.");
                                editText_passwd.setText("");
                                editText_acount.setText("");
                            }

                        }
                        progress.dismiss();
                    }
                });


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();
        }

    }
    @Override
    protected void onPause() {
        try {
            player.stop();
        } catch (Exception io) {
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        try {
            if (player != null) {
                player.stop();
                player.prepare();
                player.start();
            }
        } catch (Exception io) {
        }
        super.onResume();
    }


}
