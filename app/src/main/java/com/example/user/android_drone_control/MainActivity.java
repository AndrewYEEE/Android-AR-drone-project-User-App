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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //TextView contentView;
    private String Account;
    private String Password;
    DrawerLayout drawerLayout;
    private int navItemId;
    private static final String NAV_ITEM_ID = "nav_index";
    FloatingActionButton fab ;
    private GoogleMap map;
    LatLng latLng = new LatLng(23.696136, 120.534142);
    private GoogleApiClient googleApiClient = null;
    Location location;
    private ProgressDialog progress;

    private Handler mUI_Handler = new Handler();
    //宣告特約工人的經紀人
    private Handler mThreadHandler;
    private WifiManager wiFiManager;
    TextView textView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //======================取得由SplashActivity傳過來的帳號密碼=======================
        //Intent intent=this.getIntent();
        Bundle bundle = this.getIntent().getExtras();
        Account=bundle.getString("Account");
        Password=bundle.getString("Password");

        Toast.makeText(this,Account+" "+Password,Toast.LENGTH_SHORT).show();

        //==========================此部分是在處理NavigationView的item被執行時要做的動作===========================
        //contentView = (TextView) findViewById(R.id.content_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //此物件可以控制整個DrawLayout
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view); //此物件用於控制NavigationView部分


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*
                            因為不是使用 onCreateOptionsMenu() 來載入導覽菜單，所以不能用 onOptionsItemSelected() 來設定反應，要加上 OnNavigationItemSelectedListener 來操作。
                         */
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.sed_fab);
                //Snackbar.make(fab,menuItem.getItemId()+"",Snackbar.LENGTH_SHORT).show();
                int index=menuItem.getItemId();
                Intent intent;
                switch(index) {
                    case R.id.Drone_Control:
                        Snackbar.make(fab,"Drone_Control",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,DroneControl.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        break;
                    case R.id.Follow_Me:
                        Snackbar.make(fab,"Follow_Me",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,FollowMe.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        break;
                    case R.id.Follow_Of_Fix_Point:
                        Snackbar.make(fab,"Follow_Of_Fix_Point",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,FollowFixPoint.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        intent.putExtra("Lat",latLng.latitude);
                        intent.putExtra("Lng",latLng.longitude);
                        startActivity(intent);
                        break;
                    case R.id.Live_Platform:
                        Snackbar.make(fab,"Live_Platform",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,LivePlatform.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        break;
                    case R.id.Logout:
                        //===========以alert的方式讓使用者確認是否要登出================
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
                                intent = new Intent(MainActivity.this,SplashActivity.class);
                                startActivity(intent);
                                MainActivity.this.finish();
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        //=================FloatButton=========================
        fab = (FloatingActionButton)findViewById(R.id.sed_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CoordinatorLayout 是一個 FrameLayout 的強化版。透過定義 Behavior，
                                它能協調自身各 child view 間的互動。由於 Google 的 FAB 已內建支援 CoordinatorLayout，
                                我們只需將包著 contentView 的 Layout 改為 android.support.design.widget.CoordinatorLayout便可解決問題。
                                現在 Snackbar 出現時 FAB 會跟著動了。*/
                Intent intent;
                intent = new Intent(MainActivity.this,DroneSelection.class);
                intent.putExtra("Account",Account);
                startActivity(intent);
                /*
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.sed_fab);
                //測試用Snackbar代替Toast
                Snackbar.make(fab, "選擇租借站", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(MainActivity.this,DroneSelection.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);

                    }
                }).show();
                */
            }
        });
        //================將 RecyclerView 和 ContactsAdapter 結合=================
        /*作法一
                RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
                ContactsAdapter adapter = new ContactsAdapter(Contact.generateSampleList());
                rvContacts.setAdapter(adapter);
                rvContacts.setLayoutManager(new GridLayoutManager(this, 2));
                */
        /*作法二*/
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        List<Member> memberList=new ArrayList<>();

        memberList.add(new Member(1, R.drawable.drone_control,"drone_control"));
        memberList.add(new Member(2, R.drawable.follow_me,"follow_me"));
        memberList.add(new Member(3, R.drawable.follow_of_fix_point,"follow_of_fix_point"));
        memberList.add(new Member(4, R.drawable.live_platform,"live_platform"));
        memberList.add(new Member(5, R.drawable.coming_soon,"coming_soon"));

        recyclerView.setAdapter(new MemberAdapter(this,memberList));
        //====================================================
        /*AppBar 最初叫 ActionBar，後來改名為 Toolbar ，現在統稱叫 AppBar。
                AppBarLayout 即是控制內容元件滑動時 AppBar 的顯示，
                需要在 CoordinatorLayout 底下才能運作。*/
        /*在 RecyclerView 加上 app:layout_behavior="@string/appbar_scrolling_view_behavior"，
                    並在 Toolbar 加上 app:layout_scrollFlags="scroll|enterAlways"。
                    這樣 CoordinatorLayout 便會在 RecyclerView 捲動時，
                    去找自己當中受影響的 view ，在這裏即是 AppBarLayout，作用相關的反應。*/
        //========================GoogleMap============================
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(Account);
        map.addMarker(markerOptions);

        UiSettings uiSetting=map.getUiSettings();
        uiSetting.setCompassEnabled(true);

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
        //===================依照螢幕尺寸調整layout大小==================
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);  //先取得螢幕尺寸(pixel)

        int vWidth = metrics.widthPixels;
        int vHeight = metrics.heightPixels;

        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.master_map_linear);
        relativeLayout.getLayoutParams().height=(int)(vHeight*0.4);  //針對map進行尺寸調整
        //=====================多執行續(網路部分)=================================
        mThreadHandler=new Handler();
        mThreadHandler.postDelayed(runnable1,5000);
        //======================取得wifi權限======================
        wiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);


    }
    //====================Thread Runnable===================
    private Runnable runnable1=new Runnable () {
        public void run() {
            // TODO Auto-generated method stub
            //=======================確認網路是否開啟================================
            if (isConnected()) {

            }else{
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.sed_fab);
                //測試用Snackbar代替Toast
                Snackbar.make(fab, "Internet lost", Snackbar.LENGTH_SHORT).setAction("Open", new View.OnClickListener() {
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
            sendStationPostRequest();
            sendPostRequest();
            mThreadHandler.postDelayed(this,5000); //遞迴性呼叫自己
        }

    };
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

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(Lat+"&"+Lng);
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
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + responseOutput.toString());

                MainActivity.this.runOnUiThread(new Runnable() {

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

    //====================網路連線確認======================
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()&&(!networkInfo.isFailover())) {
            return true;
        }
        return false;
    }
    //=================GoogleGPS=====================
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener= new GoogleApiClient.OnConnectionFailedListener(){

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(MainActivity.this,"Connect failed.",Toast.LENGTH_SHORT).show();
            if(!connectionResult.hasResolution()){
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),MainActivity.this,0).show();
                return;
            }
            try{
                connectionResult.startResolutionForResult(MainActivity.this,1);
            }catch(IntentSender.SendIntentException e){
                Log.e("MainActivity","Exception while start resolution activity");
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(MainActivity.this,"暫時無連結",Toast.LENGTH_SHORT).show();
                }
            };

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this,"GPS reset: "+location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            map.clear();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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


    //=====================RecycleCardView===========================
    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<Member> memberList;

        public MemberAdapter(Context context, List<Member> memberList) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.memberList = memberList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvId, tvName;

            public ViewHolder(View itemView) {
                super(itemView);
                //ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
               // tvName=(TextView)itemView.findViewById(R.id.tv_name);
                ivImage=(ImageView)itemView.findViewById(R.id.iv_image);
            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_contact, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
           // final Intent intent = new Intent();
            final Member member = memberList.get(position);
            viewHolder.ivImage.setImageResource(member.getImage());
            //viewHolder.tvName.setText(member.getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerView);

                    int temp = member.getId(); //取得主畫面表單ID
                    if (temp == 1) {
                        Snackbar.make(recyclerView,"Drone Control",Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,DroneControl.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                    }else if(temp==2){
                        Snackbar.make(recyclerView,"Follow Me",Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,FollowMe.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                    }else if(temp==3){
                        Snackbar.make(recyclerView,"Follow Of Fix Point",Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,FollowFixPoint.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        intent.putExtra("Lat",latLng.latitude);
                        intent.putExtra("Lng",latLng.longitude);
                        startActivity(intent);
                    }else if(temp==4){
                        Snackbar.make(recyclerView,"Live Platform",Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LivePlatform.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                    }else{

                    }
                }
            });
        }

    }
    //=====返回鍵設計===================
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Do something.
            //===========以alert的方式讓使用者確認是否要登出或離開================
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Alert");
            dialog.setMessage("請選擇動作");
            dialog.setNegativeButton("登出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent intent;
                    intent = new Intent(MainActivity.this,SplashActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

            });
            dialog.setNeutralButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    //do nothing.
                }

            });
            dialog.setPositiveButton("離開",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    MainActivity.this.finish();
                }

            });
            dialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //===================Map network 連線====================
    public void sendStationPostRequest() {
        new MainActivity.StationPostClass(this).execute();
    }


    private class StationPostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public StationPostClass(Context c){

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

                MainActivity.this.runOnUiThread(new Runnable() {

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
                                LatLng latLng=new LatLng(json_read.getDouble("Lat"),json_read.getDouble("Lng"));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);                  //設定座標
                                markerOptions.title(json_read.getString("Station")); //設定Station名稱
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_station)); //設定Station站圖示
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

    /*
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.drawer, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                switch (id) {
                    case android.R.id.home:
                        drawerLayout.openDrawer(GravityCompat.START);
                        return true;
                }

                return super.onOptionsItemSelected(item);
            }
        */
    /*此部分是為了解決旋轉螢幕會自動重設的問題
        private void navigateTo(MenuItem menuItem){
            contentView.setText(menuItem.getTitle());

            navItemId = menuItem.getItemId();
            menuItem.setChecked(true);
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(NAV_ITEM_ID, navItemId);
        }
        */

}
