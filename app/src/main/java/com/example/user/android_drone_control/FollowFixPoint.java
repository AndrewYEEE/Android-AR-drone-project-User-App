package com.example.user.android_drone_control;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/20.
 */
public class FollowFixPoint extends AppCompatActivity {
    private String Account;
    DrawerLayout drawerLayout;
    private GoogleMap map;
    LatLng latLng = new LatLng(23.696136, 120.534142);
    private GoogleApiClient googleApiClient = null;
    Location location;
    final List<LatLng> latLng2=new ArrayList(); //此陣列是為了將之後路線規劃所有的點存起來並用setPoints()畫出
    PolylineOptions polylineOptions;
    Polyline polyline= null;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_of_fix_point);
        //======================取得由SplashActivity傳過來的帳號密碼=======================
        Intent intent=this.getIntent();
        Account=intent.getStringExtra("Account");
        latLng=new LatLng(intent.getDoubleExtra("Lat",23.696136),intent.getDoubleExtra("Lng",120.534142));
        Toast.makeText(this,Account+""+latLng.latitude+""+latLng.longitude,Toast.LENGTH_SHORT).show();
        //==========================此部分是在處理NavigationView的item被執行時要做的動作===========================
        //contentView = (TextView) findViewById(R.id.content_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.follow_of_fix_point_drawer_layout); //此物件可以控制整個DrawLayout
        NavigationView view = (NavigationView) findViewById(R.id.follow_of_fix_point_navigation_view); //此物件用於控制NavigationView部分


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*
                            因為不是使用 onCreateOptionsMenu() 來載入導覽菜單，所以不能用 onOptionsItemSelected() 來設定反應，要加上 OnNavigationItemSelectedListener 來操作。
                         */
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());
                Toolbar toolbar=(Toolbar)findViewById(R.id.follow_of_fix_point_toolbar);
                //Snackbar.make(fab,menuItem.getItemId()+"",Snackbar.LENGTH_SHORT).show();
                int index=menuItem.getItemId();
                Intent intent;
                switch(index) {
                    case R.id.Drone_Control:
                        Snackbar.make(toolbar,"Drone_Control",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(FollowFixPoint.this,DroneControl.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);
                        FollowFixPoint.this.finish();
                        break;
                    case R.id.Follow_Me:
                        Snackbar.make(toolbar,"Follow_Me",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(FollowFixPoint.this,FollowMe.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);
                        FollowFixPoint.this.finish();
                        break;
                    case R.id.Follow_Of_Fix_Point:
                        Snackbar.make(toolbar,"Follow_Of_Fix_Point",Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.Live_Platform:
                        Snackbar.make(toolbar,"Live_Platform",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(FollowFixPoint.this,LivePlatform.class);
                        intent.putExtra("Account",Account);
                        startActivity(intent);
                        FollowFixPoint.this.finish();
                        break;
                    case R.id.Logout:
                        //===========以alert的方式讓使用者確認是否要登出================
                        AlertDialog.Builder dialog = new AlertDialog.Builder(FollowFixPoint.this);
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
                                intent = new Intent(FollowFixPoint.this,SplashActivity.class);
                                startActivity(intent);
                                FollowFixPoint.this.finish();
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.follow_of_fix_point_toolbar);
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
        //======================map=================================
        //===================依照螢幕尺寸調整layout大小==================
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);  //先取得螢幕尺寸(pixel)

        int vWidth = metrics.widthPixels;
        int vHeight = metrics.heightPixels;

        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.fix_map_linear);
        relativeLayout.getLayoutParams().height=(int)(vHeight*0.7);  //針對map進行尺寸調整
        //========================GoogleMap============================
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fix_direct_map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(Account);
        map.addMarker(markerOptions);
        //==================set map draw line======================

        latLng2.add(new LatLng(latLng.latitude,latLng.longitude)); //先加入起始點
        polylineOptions=new PolylineOptions() //畫線的細部設定
                .width(10)
                .color(Color.RED)
                .zIndex(2);//
        polyline=map.addPolyline(polylineOptions); //將畫線設定套用至map上
        //===========此map mark觸發設定是為了讓路線規畫能回到原點=============
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
            //private final View infoWindow=getLayoutInflater().inflate(R.layout.user_position,null);
            @Override
            public View getInfoWindow(Marker marker) {
                latLng2.add(new LatLng(latLng.latitude,latLng.longitude)); //設定當原點的mark被按下時，將座標存入
                polyline.setPoints(latLng2);
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        //===================set map click mark==========================
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() { //每點擊一次map就將點擊的座標加入latLng2，並畫線
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(FollowFixPoint.this,latLng.latitude+" "+latLng.longitude,Toast.LENGTH_SHORT).show();
                latLng2.add(new LatLng(latLng.latitude,latLng.longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng2.get(latLng2.size()-1));
                int temp=latLng2.size()-1;
                switch(temp){
                    case 1:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_1)); //設定圖示
                        break;
                    case 2:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_2)); //設定圖示
                        break;
                    case 3:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_3)); //設定圖示
                        break;
                    case 4:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_4)); //設定圖示
                        break;
                    case 5:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_5)); //設定圖示
                        break;
                    case 6:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_6)); //設定圖示
                        break;
                    case 7:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_7)); //設定圖示
                        break;
                    case 8:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_8)); //設定圖示
                        break;
                    case 9:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_9)); //設定圖示
                        break;
                    default:
                        break;
                }

                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.station));
                polyline.setPoints(latLng2);
                map.addMarker(markerOptions);
            }
        });
        //==================map clear==================================
        Button button=((Button)findViewById(R.id.btclear));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.addMarker(markerOptions);
                latLng2.clear();
                latLng2.add(new LatLng(latLng.latitude,latLng.longitude)); //先加入起始點
                polyline=map.addPolyline(polylineOptions); //將畫線設定套用至map上
            }
        });
        Button button1=((Button)findViewById(R.id.btupdatedirection));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequest();
            }
        });

    }

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

                final TextView outputView = (TextView) findViewById(R.id.direct_uptx);
                URL url = new URL("http://140.125.45.200:2226/hbase/Drone_web/auto_Direction_update.jsp");

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

                for(int i=0;i<latLng2.size();i++){
                    Lat=Lat+latLng2.get(i).latitude+",";
                    Lng=Lng+latLng2.get(i).longitude+",";
                }

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

                int responseCode = connection.getResponseCode();

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

                FollowFixPoint.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        outputView.setText(output);
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
