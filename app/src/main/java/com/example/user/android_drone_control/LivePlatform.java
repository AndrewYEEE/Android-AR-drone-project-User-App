package com.example.user.android_drone_control;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2016/9/12.
 */
public class LivePlatform extends AppCompatActivity {
    DrawerLayout live_platform_drawerLayout;
    private int navItemId;
    private static final String NAV_ITEM_ID = "nav_index";
    FloatingActionButton fab ;
    private String path;
    //private HashMap<String, String> options;
    private VideoView mVideoView;
    private String Account;
    private String Password;
    ProgressDialog progress;
    private List<String> sessionid=new ArrayList<>();
    private String sessionid2;


    private Socket socket; //設定Socket路徑
    {
        try{
            socket = IO.socket("http://140.125.45.187:8087/chat");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_platform_layout);
        //===================================================================================================
        Intent intent=this.getIntent();
        Account=intent.getStringExtra("Account");
        Password=intent.getStringExtra("Password");
        Toast.makeText(this,Account+""+Password,Toast.LENGTH_SHORT).show();
        //==========================此部分是在處理NavigationView的item被執行時要做的動作===========================
        //==========================此部分是在處理NavigationView的item被執行時要做的動作===========================
        //contentView = (TextView) findViewById(R.id.content_view);
        live_platform_drawerLayout = (DrawerLayout) findViewById(R.id.live_platform_drawer_layout); //此物件可以控制整個DrawLayout
        NavigationView view = (NavigationView) findViewById(R.id.live_platform_navigation_view); //此物件用於控制NavigationView部分

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*
                            因為不是使用 onCreateOptionsMenu() 來載入導覽菜單，所以不能用 onOptionsItemSelected() 來設定反應，要加上 OnNavigationItemSelectedListener 來操作。
                         */
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());
                Toolbar toolbar=(Toolbar)findViewById(R.id.live_platform_toolbar);
                Intent intent;
                //Snackbar.make(fab,menuItem.getItemId()+"",Snackbar.LENGTH_SHORT).show();
                int index=menuItem.getItemId();
                switch(index) {
                    case R.id.Drone_Control:
                        Snackbar.make(toolbar,"Drone_Control",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(LivePlatform.this,DroneControl.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        LivePlatform.this.finish();
                        break;
                    case R.id.Follow_Me:
                        Snackbar.make(toolbar,"Follow_Me",Snackbar.LENGTH_SHORT).show();
                        intent = new Intent(LivePlatform.this,FollowMe.class);
                        intent.putExtra("Account",Account);
                        intent.putExtra("Password",Password);
                        startActivity(intent);
                        LivePlatform.this.finish();
                        break;
                    case R.id.Follow_Of_Fix_Point:
                        Snackbar.make(toolbar,"Follow_Of_Fix_Point",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(LivePlatform.this,"請先回Home頁面",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Live_Platform:
                        Snackbar.make(toolbar,"Live_Platform",Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                menuItem.setChecked(true);//若Navigation表單的item被按下去，則在案下的item設定為按下的模式(true)
                live_platform_drawerLayout.closeDrawers(); //按下後關閉NavigationView
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.live_platform_toolbar);
        setSupportActionBar(toolbar); //將原本只是ToolBar的工具列轉換成ActionBar

        /*此段code有問題，但正確(問題在於沒在String.xml裡加入<string name="openDrawer">Open Drawer</string><string name="closeDrawer">Close Drawer</string>)*/
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, live_platform_drawerLayout, toolbar,R.string.openDrawer , R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        live_platform_drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        /*
                ActionBar actionBar = getSupportActionBar(); //取得剛剛的Toolbar
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                actionBar.setDisplayHomeAsUpEnabled(true);//在ToolBar上建立一個"三"的圖示方便叫出NavigationView*/
        /*一定要加入底下兩個函式onCreateOptionsMenu和onOptionsItemSelected，不然會叫不出NavigationView*/
        //=================FloatButton=========================

        //================將 RecyclerView 和 ContactsAdapter 結合=================
        /*作法一
                RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
                ContactsAdapter adapter = new ContactsAdapter(Contact.generateSampleList());
                rvContacts.setAdapter(adapter);
                rvContacts.setLayoutManager(new GridLayoutManager(this, 2));
                */
        /*作法二*/
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.live_platform_recyclerView);
        final List<AnswerMember> memberList=new ArrayList<>();
        /*
        memberList.add(new AnswerMember(1, R.drawable.user,"User","testtest"));
        memberList.add(new AnswerMember(2, R.drawable.user,"User","testtest"));
        memberList.add(new AnswerMember(3, R.drawable.user,"User","testtest"));
        memberList.add(new AnswerMember(4, R.drawable.user,"User","testtest"));
        memberList.add(new AnswerMember(5, R.drawable.user,"User","testtest"));
        */
        final AnswerMemberAdapter memberAdapter=new AnswerMemberAdapter(this,memberList);
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));//設定為自訂網格layout，設定為1以模擬linearlayout
        recyclerView.setFocusable(true); //設定當item被點擊時螢幕會focus在點擊的地方

        //===================================================
        /*
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);  //先取得螢幕尺寸(pixel)

                    int vWidth = metrics.widthPixels;
                    int vHeight = metrics.heightPixels;

                    LinearLayout linearLayout=(LinearLayout)findViewById(R.id.answer_line);
                    linearLayout.getLayoutParams().width=(int)(vWidth*0.8);

                */
        //===========================================
        //=====================RTMP STREAM======================
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        path="http://140.125.45.187:8090/hls/12345678.m3u8";
        /*options = new HashMap<>();
                   options.put("rtmp_playpath", "");
                   options.put("rtmp_swfurl", "");
                   options.put("rtmp_live", "1");
                   options.put("rtmp_pageurl", "");*/
        mVideoView.setVideoPath(path);
        //mVideoView.setVideoURI(Uri.parse(path), options);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        //=============實作下拉更新功能==================
        final SwipeRefreshLayout mSwipeRefreshLayout;
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //設定下拉被觸發時要做的動作
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);

                new Handler().postDelayed(new Runnable() { //更新動作寫在這，設定3秒
                    @Override
                    public void run() {
                        //do something

                        mSwipeRefreshLayout.setRefreshing(false); //關閉更新符號
                        Toast.makeText(getApplicationContext(), "Refresh done!", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });
        //==================Socketio=====================
        //sendPostRequest();

        /*
        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                        // modify request headers
                        headers.put("Cookie", Arrays.asList(""));
                    }
                });

                transport.on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                        // access response headers
                        //String cookie = headers.get("Set-Cookie").get(0);
                    }
                });
            }
        });
        */

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        String imageText;
                        try{
                            JSONObject data = (JSONObject) args[0];
                            message = data.getString("msg").toString();
                            ChatListItem(memberList,memberAdapter,message.substring(0,message.indexOf(":")),message.substring(message.indexOf(":")+1));
                        }catch(JSONException ex) {
                            message=new String(args[0].toString());
                            ChatListItem(memberList,memberAdapter,Account,message);
                        }

                    }
                });
            }
        });
        socket.on("status", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        String imageText;
                        try{
                            JSONObject data = (JSONObject) args[0];
                            message = data.getString("msg").toString();
                            ChatListItem(memberList,memberAdapter,Account,message);
                        }catch(JSONException ex) {
                            message=new String(args[0].toString());
                            ChatListItem(memberList,memberAdapter,Account,message);
                        }
                    }
                });
            }
        });
        socket.on("connect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        String imageText;
                        JSONObject data=new JSONObject();
                        try {
                            data.put("username",Account);
                            socket.emit("joined", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        //====================設定回復Button功能===========================

        ImageButton answer_button=(ImageButton)findViewById(R.id.live_platform_button);
        final EditText editText=(EditText)findViewById(R.id.live_platform_edittext);
        answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                int index=memberAdapter.getItemCount();                     //取得目前member數量(item數量)
                memberList.add(0,new AnswerMember(index+1, R.drawable.user,Account,editText.getText()+"")); //(新增新的留言資料)(id+1)
                memberAdapter.notifyItemInserted(0);                    //設定允許加入新資料到第一個位置，也就是最上層
                editText.setText("");
                */
                String message = editText.getText().toString().trim();
                editText.setText("");

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //強制關閉鍵盤

                //ChatListItem(memberList,memberAdapter,Account,message);
                JSONObject sendText = new JSONObject();
                try{
                    sendText.put("msg",message);
                    sendText.put("username",Account);
                    socket.emit("text", sendText);
                }catch(JSONException e){
                }
            }
        });

        //==================連線button=======================
        ImageButton buttonconnect=(ImageButton)findViewById(R.id.live_platform_setting);
        buttonconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequest();
            }
        });

    }


    public void ChatListItem(List<AnswerMember> member,AnswerMemberAdapter memberAdapter,String user,String str){
        int index=memberAdapter.getItemCount();                     //取得目前member數量(item數量)
        member.add(0,new AnswerMember(index+1, R.drawable.user,user,str)); //(新增新的留言資料)(id+1)
        memberAdapter.notifyItemInserted(0);                    //設定允許加入新資料到第一個位置，也就是最上層
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
    //=====================RecycleCardView===========================
    private class AnswerMemberAdapter extends RecyclerView.Adapter<AnswerMemberAdapter.ViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<AnswerMember> memberList;

        public AnswerMemberAdapter(Context context, List<AnswerMember> memberList) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.memberList = memberList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvId, tvName,tvAnswer;

            public ViewHolder(View itemView) {
                super(itemView);
                //ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
                tvName=(TextView)itemView.findViewById(R.id.answer_user);
                ivImage=(ImageView)itemView.findViewById(R.id.answer_imageview);
                tvAnswer=(TextView)itemView.findViewById(R.id.answer_answer);
            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.answer_line, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            // final Intent intent = new Intent();
            final AnswerMember member = memberList.get(position);
            //viewHolder.ivImage.setImageResource(member.getImage());
            viewHolder.tvName.setText(member.getName());
            viewHolder.tvAnswer.setText(member.getAnswer());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        }

    }
    //======================連線用===========================
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
                URL url = new URL("http://140.125.45.187:8087/login/api");
                String charset = "UTF-8";
                //URL url = new URL("http://140.125.45.200");
                final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                String Ac=new String(Account);
                String Ps=new String(Password);

                String query = String.format("buser=%s&bpass=%s", URLEncoder.encode(Ac, charset), URLEncoder.encode(Ps, charset));
                /*
                                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                                dStream.writeBytes(urlParameters);
                                dStream.flush();
                                dStream.close();
                                */
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.close();
                os.close();
                //======================取得所有定點巡航座標================================
                final String cookieval = connection.getHeaderField("set-cookie");
                final String[] session = new String[1];


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

                LivePlatform.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //outputView.setText(output);
                        if(responseCode !=200){

                        }else{
                            if(cookieval != null) {
                                socket.connect();
                                session[0] = cookieval.substring(0, cookieval.indexOf(";"));

                            }
                            Toast.makeText(LivePlatform.this,session[0],Toast.LENGTH_SHORT).show();
                            Toast.makeText(LivePlatform.this,responseOutput.toString()+"",Toast.LENGTH_SHORT).show();
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
            //progress.dismiss();
        }

    }

}
