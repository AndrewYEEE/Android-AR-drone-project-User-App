package com.example.user.android_drone_control;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 2016/9/28.
 */
public class FollowMe extends AppCompatActivity {
    private String Account;
    private String Password;
    DrawerLayout drawerLayout;
    private GoogleMap map;
    LatLng latLng = new LatLng(23.696136, 120.534142);
    private GoogleApiClient googleApiClient = null;
    Location location;
    String Command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_me_layout);
        Bundle bundle = this.getIntent().getExtras();
        Account=bundle.getString("Account");
        Password=bundle.getString("Password");
        drawerLayout = (DrawerLayout) findViewById(R.id.follow_me_layout); //此物件可以控制整個DrawLayout
        NavigationView view = (NavigationView) findViewById(R.id.follow_me_navigation_view); //此物件用於控制NavigationView部分


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*
                            因為不是使用 onCreateOptionsMenu() 來載入導覽菜單，所以不能用 onOptionsItemSelected() 來設定反應，要加上 OnNavigationItemSelectedListener 來操作。
                         */
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.follow_me_fab);
                //Snackbar.make(fab,menuItem.getItemId()+"",Snackbar.LENGTH_SHORT).show();
                int index=menuItem.getItemId();
                Intent intent;
                switch(index) {
                    case R.id.Drone_Control:
                        Snackbar.make(fab,"Drone_Control",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(FollowMe.this,DroneControl.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);
                        break;
                    case R.id.Follow_Me:
                        Snackbar.make(fab,"Follow_Me",Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.Follow_Of_Fix_Point:
                        Snackbar.make(fab,"Follow_Of_Fix_Point",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(FollowMe.this,"請先回Home頁面",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Live_Platform:
                        Snackbar.make(fab,"Live_Platform",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(FollowMe.this,LivePlatform.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);
                        break;
                    case R.id.Logout:
                        //===========以alert的方式讓使用者確認是否要登出================
                        AlertDialog.Builder dialog = new AlertDialog.Builder(FollowMe.this);
                        dialog.setTitle("Alert");
                        dialog.setMessage("您確定要登出嗎?");
                        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                //do nothing.
                            }

                        });
                        dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                Intent intent;
                                intent = new Intent(FollowMe.this,SplashActivity.class);
                                startActivity(intent);
                                FollowMe.this.finish();
                            }

                        });
                        dialog.show();
                        break;
                    default:
                        return true;
                }
                menuItem.setChecked(true);//若Navigation表單的item被按下去，則在案下的item設定為按下的模式(true)
                drawerLayout.closeDrawers(); //按下後關閉NavigationView
                return true;
            }
        });

        //===================此部分是為了解決旋轉螢幕會自動重設的問題(在此不採用)==========================
        /*
                    有沒有發覺旋轉螢幕後 menu 和 contentView 會被重設？這是因為旋轉螢幕等於 Configuration Change，
                    Configuration Change 後 Activity 會被消滅然後重生，等於執行了 onDestory() 後再 onCreate()。
                    除非有特別處理，否則所有 variable 都會被 reset。
               */
        /*
                if(null != savedInstanceState){
                    navItemId = savedInstanceState.getInt(NAV_ITEM_ID, R.id.navigation_item_1);
                }
                else{
                    navItemId = R.id.navigation_item_1;
                }
                navigateTo(view.getMenu().findItem(navItemId));
                */
        //====================================================================
        final Toolbar toolbar = (Toolbar) findViewById(R.id.follow_me_toolbar);
        setSupportActionBar(toolbar); //將原本只是ToolBar的工具列轉換成ActionBar

        /*此段code有問題，但正確(問題在於沒在String.xml裡加入<string name="openDrawer">Open Drawer</string><string name="closeDrawer">Close Drawer</string>)*/
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar,R.string.openDrawer , R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        /*
                ActionBar actionBar = getSupportActionBar(); //取得剛剛的Toolbar
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                actionBar.setDisplayHomeAsUpEnabled(true);//在ToolBar上建立一個"三"的圖示方便叫出NavigationView*/
        /*一定要加入底下兩個函式onCreateOptionsMenu和onOptionsItemSelected，不然會叫不出NavigationView*/
        //==============map====================
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.follow_me_map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(Account);
        map.addMarker(markerOptions);
        /*
        UiSettings uiSetting=map.getUiSettings();
        uiSetting.setCompassEnabled(true);
        uiSetting.setScrollGesturesEnabled(false);
        uiSetting.setZoomGesturesEnabled(false);
        uiSetting.setTiltGesturesEnabled(false);
        uiSetting.setZoomControlsEnabled(false);
        */
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
        //===================FollowMe========================
        Button buttonstart=(Button)findViewById(R.id.follow_me_start);
        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command="Start";
                sendGetRequest();
            }
        });

        Button button=(Button)findViewById(R.id.follow_me_on);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command="GO";
                sendGetRequest();
            }
        });



    }
    //=================GoogleGPS=====================
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener= new GoogleApiClient.OnConnectionFailedListener(){

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(FollowMe.this,"Connect failed.",Toast.LENGTH_SHORT).show();
            if(!connectionResult.hasResolution()){
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),FollowMe.this,0).show();
                return;
            }
            try{
                connectionResult.startResolutionForResult(FollowMe.this,1);
            }catch(IntentSender.SendIntentException e){
                Log.e("MainActivity","Exception while start resolution activity");
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (ActivityCompat.checkSelfPermission(FollowMe.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FollowMe.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(FollowMe.this,"GPS暫時無連結",Toast.LENGTH_SHORT).show();
                }
            };

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(FollowMe.this,"GPS reset: "+location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            map.clear();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(Account);
            map.addMarker(markerOptions);

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
        private final View infoWindow=getLayoutInflater().inflate(R.layout.mapgps_layout,null);
        @Override
        public View getInfoWindow(Marker marker) {
            TextView user_title=((TextView)infoWindow.findViewById(R.id.user_title));
            TextView user_position = ((TextView)infoWindow.findViewById(R.id.user_text1));
            user_position.setText("coordinate: "+latLng.latitude+" "+latLng.longitude);
            user_title.setText("User: "+marker.getTitle());
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
    //=========================飛控指令上傳==========================
    public void sendGetRequest() {
        new FollowMe.GetClass(this).execute();
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public GetClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                //final TextView outputView = (TextView) findViewById(R.id.drone_control_text1);
                //上傳座標的參數
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/User_Control_Update.jsp?" + "Command=" + Command);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "This Var never used when you use GET.";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                //output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());

                FollowMe.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        Toast.makeText(FollowMe.this,Command+"",Toast.LENGTH_SHORT).show();


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
            /*
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
            */
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/User_GPS_update.jsp");

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
                String Lat="Lat="+ latLng.latitude;
                String Lng="Lng="+latLng.longitude;
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
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + responseOutput.toString());

                FollowMe.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        if(responseCode !=200){
                            FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.sed_fab);
                            Snackbar.make(fab,"Connect lost",Snackbar.LENGTH_SHORT).show();
                        }else{
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

}
