package com.example.user.android_drone_control;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 2016/9/18.
 */
public class DroneSelection extends AppCompatActivity {
    private GoogleMap map;
    LatLng latLng = new LatLng(23.696136, 120.534142);
    private GoogleApiClient googleApiClient = null;
    Location location;
    private Handler mThreadHandler;
    private String Account;
    Boolean User_Station_used=false; //用來判別使用者是否已經租用了一個租借站
    String Station="";//使用者選擇的租借站
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drone_selection);
        Intent intent=this.getIntent();
        Account=intent.getStringExtra("Account");
        //=======================GoogleMap=====================
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.droneselect_map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(Account);
        markerOptions.snippet(Account);
        map.addMarker(markerOptions);
        //=========================GoogleMap mark資訊========================
        map.setInfoWindowAdapter(new Myinfo());
        //========================GoogleMap設定===========================
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(latLng)
                .bearing(0)
                .tilt(85f)
                .zoom(map.getCameraPosition().zoom)
                .build();
        //added a tilt[.tilt(65.5f)] value so the map will rotate in 3D.
        map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

        UiSettings uiSetting=map.getUiSettings();
        uiSetting.setZoomControlsEnabled(false);
        //=====================初始化drone租借站========================
        mThreadHandler=new Handler();
        mThreadHandler.postDelayed(runnable1,3000);

        Button button=(Button)findViewById(R.id.reset_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                mThreadHandler=new Handler();
                mThreadHandler.postDelayed(runnable1,3000);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.addMarker(markerOptions);
            }
        });
        Button button1=(Button)findViewById(R.id.reset_all);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostResetRequest();
                User_Station_used=false;

                map.clear();
                mThreadHandler=new Handler();
                mThreadHandler.postDelayed(runnable1,3000);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.addMarker(markerOptions);
            }
        });

    }

    private Runnable runnable1=new Runnable () {
        public void run() {
            // TODO Auto-generated method stub
            sendPostRequest();

        }

    };
    //===================Reset Station network 連線====================
    public void sendPostResetRequest() {
        new PostResetClass(this).execute();
    }


    private class PostResetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostResetClass(Context c){

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
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/Drone_Station_Reset.jsp");

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
                String Lat="lat=";
                String Lng="lng=";
                /*
                for(int i=0;i<latLng2.size();i++){
                    Lat=Lat+latLng2.get(i).latitude+",";
                    Lng=Lng+latLng2.get(i).longitude+",";
                }
                */
                /*
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(Lat+"&"+Lng);
                dStream.flush();
                dStream.close();
                */
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

                DroneSelection.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

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
            //progress.dismiss();
        }

    }

    //===================Map network 連線====================
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
            /*
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
            */
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/Drone_Station_Get.jsp");

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
                String Lat="lat=";
                String Lng="lng=";
                /*
                for(int i=0;i<latLng2.size();i++){
                    Lat=Lat+latLng2.get(i).latitude+",";
                    Lng=Lng+latLng2.get(i).longitude+",";
                }
                */
                /*
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(Lat+"&"+Lng);
                dStream.flush();
                dStream.close();
                */
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

                DroneSelection.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        String tmp = responseOutput.toString();
                        //tmp=tmp.substring(tmp.indexOf("["), tmp.lastIndexOf("]") + 1);
                        //tmp=tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
                        JSONObject json_read = null;    //將資料丟進JSONObject
                        JSONArray list=null;
                        String Station="";
                        String User="";
                        String Lat="";
                        String Lng="";
                        String Enable="";

                        try {
                            list=new JSONArray(tmp);  //取得jsonArray字串
                            for(int i=0;i<list.length();i++){ //依照Array長度決定次數
                                json_read=list.getJSONObject(i);  //將Array中的元素轉成JSON物件
                                /*
                                if(json_read.getString("Enable").equals("false")){ //若此Station已被占用，則另外做決定
                                    continue;
                                }*/
                                if(json_read.getString("User").equals(Account)){
                                    User_Station_used=true;
                                }
                                LatLng latLng=new LatLng(json_read.getDouble("Lat"),json_read.getDouble("Lng"));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);                  //設定座標
                                markerOptions.title(json_read.getString("Station")); //設定Station名稱
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.station)); //設定Station站圖示
                                markerOptions.snippet(json_read.getString("User")); //原本是用來設定簡短說明的，現在當作UserName
                                map.addMarker(markerOptions);
                                //Toast.makeText(DroneSelection.this,json_read.getString("Station")+" "+json_read.getString("Lat")+" "+json_read.getString("Lng")+" "+json_read.getString("Enable"),Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //progress.dismiss();
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
            //progress.dismiss();
        }

    }

    //=================GoogleGPS=====================
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener= new GoogleApiClient.OnConnectionFailedListener(){

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(DroneSelection.this,"Connect failed.",Toast.LENGTH_SHORT).show();
            if(!connectionResult.hasResolution()){
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),DroneSelection.this,0).show();
                return;
            }
            try{
                connectionResult.startResolutionForResult(DroneSelection.this,1);
            }catch(IntentSender.SendIntentException e){
                Log.e("MainActivity","Exception while start resolution activity");
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (ActivityCompat.checkSelfPermission(DroneSelection.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DroneSelection.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);



                    LocationRequest locationRequest=LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(1000)
                            .setSmallestDisplacement(1);

                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, locationListener);

                }

                @Override
                public void onConnectionSuspended(int i) {
                    Toast.makeText(DroneSelection.this,"暫時無連結",Toast.LENGTH_SHORT).show();
                }
            };

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(DroneSelection.this,"GPS reset: "+location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            map.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            markerOptions.position(latLng);
            markerOptions.title(Account);
            map.addMarker(markerOptions);
            sendPostRequest(); //防止GPS更新後所有station因map.clear都不見了
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(onConnectionFailedListener)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(googleApiClient!=null){
            googleApiClient.disconnect();
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                googleApiClient.connect();
            }
        }
    }
    //===============GoogleMapMark==================================================
    private class Myinfo implements GoogleMap.InfoWindowAdapter{
        private final View infoWindow=getLayoutInflater().inflate(R.layout.drone_station,null);
        @Override
        public View getInfoWindow(final Marker marker) {
            TextView user_title=((TextView)infoWindow.findViewById(R.id.drone_station));
            TextView user_position = ((TextView)infoWindow.findViewById(R.id.drone_station_position));
            TextView user_name = ((TextView)infoWindow.findViewById(R.id.drone_station_User));
            ImageView imageView=(ImageView)infoWindow.findViewById(R.id.drone_station_img) ;
            //Button user_button=(Button)infoWindow.findViewById(R.id.drone_station_button);
            user_position.setText("Lat: "+marker.getPosition().latitude+"\nLng: "+marker.getPosition().longitude);
            user_title.setText(marker.getTitle());
            imageView.setImageResource(R.drawable.drone);

            if(marker.getSnippet().equals("null")){  //若此租借站吾人租借"username=null"，則開放button租借
                user_name.setText("");
                //user_button.setEnabled(true);


                final String temp=marker.getTitle();
                Toast.makeText(DroneSelection.this,"ya",Toast.LENGTH_SHORT).show();

                AlertDialog.Builder dialog = new AlertDialog.Builder(DroneSelection.this);
                dialog.setTitle("Alert");
                dialog.setMessage("您確定要租借"+temp);
                dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        if((User_Station_used)){ //若使用者本身已經租借了一個租借站，則不允許傳送資料
                            Toast.makeText(DroneSelection.this,"須歸還租借站才能租借其他站",Toast.LENGTH_SHORT).show();
                        }else{
                            Station=marker.getTitle(); //取得使用者要租借的站名
                            StationsendPostRequest(); //送出使用者租借要求以及資訊
                        }

                    }

                });
                dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //do nothing.
                    }

                });
                dialog.show();


            }else{ //若已有人占用，則disable button
                user_name.setText("Now User: "+marker.getSnippet());
                //user_button.setEnabled(false);
            }


            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    //===================Station network 連線====================
    //===================Map network 連線====================
    public void StationsendPostRequest() {
        new StationPostClass(this).execute();
    }


    private class StationPostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public StationPostClass(Context c){

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
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/Drone_Station_User_update.jsp");

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
                String Username="Username="+Account;
                String Station_num="StationNum="+Station;
                String Bool="Bool=false";
                Station="";//清除數據
                /*
                for(int i=0;i<latLng2.size();i++){
                    Lat=Lat+latLng2.get(i).latitude+",";
                    Lng=Lng+latLng2.get(i).longitude+",";
                }
                */

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(Username+"&"+Station_num+"&"+Bool);
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

                DroneSelection.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
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


}
