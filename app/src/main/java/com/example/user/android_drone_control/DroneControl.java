package com.example.user.android_drone_control;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * Created by user on 2016/9/9.
 */
public class DroneControl extends AppCompatActivity {
    DrawerLayout drone_control_drawerLayout;
    private String Account;
    private String Password;
    private int navItemId;
    private static final String NAV_ITEM_ID = "nav_index";
    FloatingActionButton fab ;
    private GoogleMap map;
    LatLng latLng = new LatLng(23.696136, 120.534142);
    private GoogleApiClient googleApiClient = null;
    Location location;
    private ProgressDialog progress;
    private String Command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drone_control_layout);

        final Intent intent=this.getIntent();
        Account=intent.getStringExtra("Account");
        Password=intent.getStringExtra("Password");
        //==========================此部分是在處理NavigationView的item被執行時要做的動作===========================
        //contentView = (TextView) findViewById(R.id.content_view);
        drone_control_drawerLayout = (DrawerLayout) findViewById(R.id.drone_control_drawer_layout); //此物件可以控制整個DrawLayout
        NavigationView view = (NavigationView) findViewById(R.id.drone_control_navigation_view); //此物件用於控制NavigationView部分

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*
                            因為不是使用 onCreateOptionsMenu() 來載入導覽菜單，所以不能用 onOptionsItemSelected() 來設定反應，要加上 OnNavigationItemSelectedListener 來操作。
                         */
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.drone_control_fabL);
                //Snackbar.make(fab,menuItem.getItemId()+"",Snackbar.LENGTH_SHORT).show();
                int index=menuItem.getItemId();
                Intent intent;
                switch(index) {
                    case R.id.Drone_Control:
                        Snackbar.make(fab,"Drone_Control",Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.Follow_Me:
                        Snackbar.make(fab,"Follow_Me",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(DroneControl.this,FollowFixPoint.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        break;
                    case R.id.Follow_Of_Fix_Point:
                        Snackbar.make(fab,"Follow_Of_Fix_Point",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(DroneControl.this,FollowFixPoint.class);
                        Toast.makeText(DroneControl.this,"請先回Home頁面",Toast.LENGTH_SHORT).show();
                        DroneControl.this.finish();
                        break;
                    case R.id.Live_Platform:
                        Snackbar.make(fab,"Live_Platform",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(DroneControl.this,LivePlatform.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        DroneControl.this.finish();
                        break;
                    case R.id.Logout:
                        //===========以alert的方式讓使用者確認是否要登出================
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DroneControl.this);
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
                                intent = new Intent(DroneControl.this,SplashActivity.class);
                                startActivity(intent);
                                DroneControl.this.finish();
                            }

                        });
                        dialog.show();
                        break;
                    default:
                        return true;
                }
                menuItem.setChecked(true);//若Navigation表單的item被按下去，則在案下的item設定為按下的模式(true)
                drone_control_drawerLayout.closeDrawers(); //按下後關閉NavigationView
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.drone_control_toolbar);
        setSupportActionBar(toolbar); //將原本只是ToolBar的工具列轉換成ActionBar

        /*此段code有問題，但正確(問題在於沒在String.xml裡加入<string name="openDrawer">Open Drawer</string><string name="closeDrawer">Close Drawer</string>)*/
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drone_control_drawerLayout, toolbar,R.string.openDrawer , R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        drone_control_drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        /*
                ActionBar actionBar = getSupportActionBar(); //取得剛剛的Toolbar
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                actionBar.setDisplayHomeAsUpEnabled(true);//在ToolBar上建立一個"三"的圖示方便叫出NavigationView*/
        /*一定要加入底下兩個函式onCreateOptionsMenu和onOptionsItemSelected，不然會叫不出NavigationView*/
        //=================FloatButton=========================
        FloatingActionButton fabL = (FloatingActionButton)findViewById(R.id.drone_control_fabL);
        fabL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CoordinatorLayout 是一個 FrameLayout 的強化版。透過定義 Behavior，
                                它能協調自身各 child view 間的互動。由於 Google 的 FAB 已內建支援 CoordinatorLayout，
                                我們只需將包著 contentView 的 Layout 改為 android.support.design.widget.CoordinatorLayout便可解決問題。
                                現在 Snackbar 出現時 FAB 會跟著動了。*/
                Command="startNavigation";
                sendGetRequest();
                FloatingActionButton fabL=(FloatingActionButton)findViewById(R.id.drone_control_fabL);
                Snackbar.make(fabL, "startNavigation", Snackbar.LENGTH_SHORT).show();
                //測試用Snackbar代替Toast

            }
        });
        FloatingActionButton fabR = (FloatingActionButton)findViewById(R.id.drone_control_fabR);
        fabR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CoordinatorLayout 是一個 FrameLayout 的強化版。透過定義 Behavior，
                                它能協調自身各 child view 間的互動。由於 Google 的 FAB 已內建支援 CoordinatorLayout，
                                我們只需將包著 contentView 的 Layout 改為 android.support.design.widget.CoordinatorLayout便可解決問題。
                                現在 Snackbar 出現時 FAB 會跟著動了。*/

                Command="LiveStart";
                sendGetRequest();
                FloatingActionButton fabR=(FloatingActionButton)findViewById(R.id.drone_control_fabL);
                Snackbar.make(fabR, "LiveStart", Snackbar.LENGTH_SHORT).show();

            }
        });
        //========================GoogleMap============================
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.drone_control_map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        map.addMarker(markerOptions);
        //========================GoogleMap設定===========================
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(latLng)
                .bearing(0)
                .tilt(75f)
                .zoom(map.getCameraPosition().zoom)
                .build();
        //added a tilt[.tilt(65.5f)] value so the map will rotate in 3D.
        map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

        //===================Left JoyStick功能設定==================
        final JoystickView Leftjoystick = (JoystickView) findViewById(R.id.joystickView1);
        final JoystickView Rightjoystick = (JoystickView) findViewById(R.id.joystickView2);
        final TextView textleftangle=(TextView)findViewById(R.id.leftangle);
        final TextView textleftlength=(TextView)findViewById(R.id.leftlength);
        final TextView textleftcommand=(TextView)findViewById(R.id.leftcommand);

        Leftjoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                textleftangle.setText("Left Angle : "+angle);
                textleftlength.setText("Left Length : "+strength+"%");

                if(strength>50){
                    Rightjoystick.setEnabled(false);
                    if(angle<=45||angle>=316){
                        textleftcommand.setText("Command : Right");
                        if(!Command.equals("Right")){
                            Command="Right";
                            sendGetRequest();
                        }

                    }else if(angle<=135&&angle>=46){
                        textleftcommand.setText("Command : Front");
                        if(!Command.equals("Front")){
                            Command="Front";
                            sendGetRequest();
                        }
                    }else if(angle<=225&&angle>=136){
                        textleftcommand.setText("Command : Left");
                        if(!Command.equals("Left")){
                            Command="Left";
                            sendGetRequest();
                        }
                    }else if(angle<=315&&angle>=226){
                        textleftcommand.setText("Command : Back");
                        if(!Command.equals("Back")){
                            Command="Back";
                            sendGetRequest();
                        }
                    }
                }else{
                    textleftcommand.setText("Command : ");
                    Rightjoystick.setEnabled(true);
                    Command="Stop";
                }

            }
        });
        //===================Right JoyStick功能設定==================
        final TextView textrightangle=(TextView)findViewById(R.id.rightangle);
        final TextView textrightlength=(TextView)findViewById(R.id.rightlength);
        final TextView textrightcommand=(TextView)findViewById(R.id.rightcommand);


        Rightjoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                textrightangle.setText("Right Angle : "+angle);
                textrightlength.setText("Right Length : "+strength+"%");

                if(strength>50){
                    Leftjoystick.setEnabled(false);
                    if(angle<=45||angle>=316){
                        textrightcommand.setText("Command : Clockwise");
                        if(!Command.equals("Clockwise")){
                            Command="Clockwise";
                            sendGetRequest();
                        }
                    }else if(angle<=135&&angle>=46){
                        textrightcommand.setText("Command : Up");
                        if(!Command.equals("Up")){
                            Command="Up";
                            sendGetRequest();
                        }
                    }else if(angle<=225&&angle>=136){
                        textrightcommand.setText("Command : counter\nClockwise");
                        if(!Command.equals("counterClockwise")){
                            Command="counterClockwise";
                            sendGetRequest();
                        }
                    }else if(angle<=315&&angle>=226){
                        textrightcommand.setText("Command : Down");
                        if(!Command.equals("Down")){
                            Command="Down";
                            sendGetRequest();
                        }
                    }

                }else{
                    Leftjoystick.setEnabled(true);
                    textrightcommand.setText("Command :");
                    Command="Stop";
                }

            }
        });
        //====================按鈕設定========================
        Button buttonTakeoff=(Button)findViewById(R.id.takeoff);
       buttonTakeoff.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //Toast.makeText(DroneControl.this, "takeoff", Toast.LENGTH_SHORT).show();
               Command="Takeoff";
               sendGetRequest();
           }
       });

        Button buttonLand=(Button)findViewById(R.id.land);
        buttonLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(DroneControl.this, "land", Toast.LENGTH_SHORT).show();
                Command="Land";
                sendGetRequest();
            }
        });
        Button buttonreset=(Button)findViewById(R.id.reset);
        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(DroneControl.this, "reset", Toast.LENGTH_SHORT).show();
                Command="Reset";
                sendGetRequest();
            }
        });
        Button buttonstart=(Button)findViewById(R.id.Start);
        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command="Start";
                sendGetRequest();
            }
        });
        //===================依照螢幕尺寸調整layout大小==================
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);  //先取得螢幕尺寸(pixel)

        int vWidth = metrics.widthPixels;
        int vHeight = metrics.heightPixels;

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.map_linear);
        linearLayout.getLayoutParams().height=(int)(vHeight*0.4);  //針對map進行尺寸調整


    }
    //=================GoogleGPS=====================
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener= new GoogleApiClient.OnConnectionFailedListener(){

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(DroneControl.this,"Connect failed.",Toast.LENGTH_SHORT).show();
            if(!connectionResult.hasResolution()){
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),DroneControl.this,0).show();
                return;
            }
            try{
                connectionResult.startResolutionForResult(DroneControl.this,1);
            }catch(IntentSender.SendIntentException e){
                Log.e("DroneControl","Exception while start resolution activity");
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (ActivityCompat.checkSelfPermission(DroneControl.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DroneControl.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(DroneControl.this,"暫時無連結",Toast.LENGTH_SHORT).show();
                }
            };

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(DroneControl.this,"GPS reset: "+location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            map.clear();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
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
    //=========================飛控指令上傳==========================
    public void sendGetRequest() {
        new GetClass(this).execute();
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

                DroneControl.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        Toast.makeText(DroneControl.this,Command+"",Toast.LENGTH_SHORT).show();


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
}
