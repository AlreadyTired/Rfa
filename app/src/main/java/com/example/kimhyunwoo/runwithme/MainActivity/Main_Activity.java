package com.example.kimhyunwoo.runwithme.MainActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.BackPressCloseHandler;
import com.example.kimhyunwoo.runwithme.MapUtil;
import com.example.kimhyunwoo.runwithme.R;
import com.example.kimhyunwoo.runwithme.UserInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kt.gigaiot_sdk.DeviceApi;
import com.kt.gigaiot_sdk.GigaIotOAuth;
import com.kt.gigaiot_sdk.PushApi;
import com.kt.gigaiot_sdk.TagStrmApi;
import com.kt.gigaiot_sdk.data.Device;
import com.kt.gigaiot_sdk.data.DeviceApiResponse;
import com.kt.gigaiot_sdk.data.GiGaIotOAuthResponse;
import com.kt.gigaiot_sdk.data.PushTypePair;
import com.kt.gigaiot_sdk.data.TagStrm;
import com.kt.gigaiot_sdk.data.TagStrmApiResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Math.abs;

public class Main_Activity extends AppCompatActivity {

    private String regId = null;
    private GoogleCloudMessaging mGcm;
    private ArrayList<Device> mDeivce;

    private LogPollingThread logPollingThread;

    TextView textTemp;
    TextView textHR;
    //  뒤로가기 버튼을 2번 누르면 종료시키는 클레스
    private BackPressCloseHandler backPressCloseHandler;
    private AlertDialog dialog;
    LocationManager manager;
    ViewPager pager;
    TabLayout tab;

    android.support.v4.app.Fragment historys, main, friends;

    MapUtil mapUtil;
    public LocationManager getLocationManager() {
        return manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textTemp = (TextView)findViewById(R.id.txt_temp);
        textHR = (TextView)findViewById(R.id.txt_hr);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        UserInfo.logStreams = new ArrayList<LogStream>();
        UserInfo.TemperateDataArray = new ArrayList<Double>();
        UserInfo.TemperateDateArray = new ArrayList<String>();
        UserInfo.No2DataArray = new ArrayList<Double>();
        UserInfo.No2DateArray = new ArrayList<String>();
        UserInfo.GasDataArray = new ArrayList<Double>();
        UserInfo.GasDateArray = new ArrayList<String>();



        mapUtil = new MapUtil();
        backPressCloseHandler = new BackPressCloseHandler(this);

        Configuration config = new Configuration();
        config = mapUtil.setLocaleResources();
        getBaseContext().getResources().updateConfiguration(
                config,getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main_);

        //  Context에 있는 location 상수 알려주기
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //  ViewPager 위젯 연결
        pager = (ViewPager) findViewById(R.id.vp_pager);
        //  TabLayout 연결
        tab = (TabLayout) findViewById(R.id.tl_tab);
        tab.addTab(tab.newTab().setText("History"));
        tab.addTab(tab.newTab().setText("Main"));
        tab.addTab(tab.newTab().setText("Friends"));

        //  각각 프래그먼트 생성
        historys = new HistoryFragment();
        main = new MainFragment();
        friends = new FriendsFragment();

        //  프래그먼트 리스트에 넣음
        //  Fragment를 v4로 사용함
        //  버그생기면 머리 아파짐 ㅠ
        List<Fragment> datas = new ArrayList<>();
        datas.add(historys);
        datas.add(main);
        datas.add(friends);

        //  프래그먼트 매니저, 어뎁터에 전달
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), datas);

        //  어뎁터를 페이저 위젯 연결
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapter.getCount()-2);
        //  페이저 변경 됬을 때 변경해주는 리스너
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        pager.setOffscreenPageLimit(3);

        //  탭이 변경 됬을 때 변경해주는 리스너
        tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

        //  권한 실행
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        }


        ApplicationPreference.init(Main_Activity.this);
        new GigaIotOAuthResponseTask().execute();
        Toast.makeText(Main_Activity.this, "Login Success", Toast.LENGTH_SHORT).show();



    }

    //  권한 체크
    private final int REQ_PERMISSION = 100;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        // 권한을 가지고 있는지 시스템에 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            //  권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            //  권한을 요구하는 팝업
            requestPermissions(permissions, REQ_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            //  사용자의 승인
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                //  거절했을 경우 띄움
                cancel();
            }
        }
    }

    public void cancel() {
        Toast.makeText(this, "The user rejects GPS rights.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "The App don't work normally.", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();;
    }


    class PagerAdapter extends FragmentStatePagerAdapter {

        List<android.support.v4.app.Fragment> datas;

        public PagerAdapter(FragmentManager frag, List<android.support.v4.app.Fragment> datas) {
            super(frag);
            this.datas = datas;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return datas.get(position);
        }

        @Override
        public int getCount() {
            return datas.size();
        }
    }

    private class GigaIotOAuthResponseTask extends AsyncTask<Void,Void,GiGaIotOAuthResponse>
    {
        ProgressDialog progressDialog;
        String id = "gusdndkrl";

        @Override
        protected GiGaIotOAuthResponse doInBackground(Void... params)
        {
            GigaIotOAuth gigaIoTOAuth = new GigaIotOAuth("j04aCFGFUL0JyuDz","rSJU7lMD9ahlYgZJ");
            GiGaIotOAuthResponse response = gigaIoTOAuth.loginWithPassword(id,"rla1231!!");
            Log.d("User's Log"," "+ response.getAccessToken());
            mGcm = GoogleCloudMessaging.getInstance(Main_Activity.this);
            return response;
        }

        @Override
        protected void onPostExecute (GiGaIotOAuthResponse result)
        {
            ApplicationPreference.getInstance().setPrefAccountId(id);

            ApplicationPreference.getInstance().setPrefAccessToken(result.getAccessToken());

            ApplicationPreference.getInstance().setPrefAccountMbrSeq(result.getMbrSeq());

            new GetGcmRegIdTask().execute();
        }
    }

    private class GetGcmRegIdTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            String temporaryRegId = null;
            try
            {
                temporaryRegId = mGcm.register("239371106048");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return temporaryRegId;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d("User's Log"," "+result);
            regId = result;
            new GetDeviceListTask().execute();
        }
    }

    private class GetDeviceListTask extends AsyncTask<Void,Void,DeviceApiResponse>
    {
        @Override
        protected DeviceApiResponse doInBackground(Void... params)
        {
            DeviceApi deviceApi = new DeviceApi(ApplicationPreference.getInstance().getPrefAccessToken());
            DeviceApiResponse response = deviceApi.getDeviceList(1,10);
            return response;
        }

        @Override
        protected void onPostExecute(DeviceApiResponse result)
        {
            Log.d("User's Log"," "+result.toString());
            Log.i("User's Log", "refresh() getDevices total = " + result.getTotal() + " | page = " + result.getPage() + " | rowNum = " + result.getRowNum()
                    + " | devices.size() = " + result.getDevices().size());
            if(result.getDevices() != null && result.getDevices().size() > 0){

                mDeivce = result.getDevices();
            }
            Device device = mDeivce.get(0);
            logPollingThread = new LogPollingThread(device);
            logPollingThread.startThread();
        }
    }

    private class PushSessionRegTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            ArrayList<PushTypePair> pushTypePairs = new ArrayList<>();
            pushTypePairs.add(new PushTypePair("서비스 대상 일련번호",PushApi.PUSH_MSG_TYPE_COLLECT));
            pushTypePairs.add(new PushTypePair("서비스 대상 일련번호",PushApi.PUSH_MSG_TYPE_OUTBREAK));

            PushApi pushApi = new PushApi(ApplicationPreference.getInstance().getPrefAccessToken());
            pushApi.gcmSessionRegistration("239371106048","fYnMPoHJRpdRIkLi",regId,pushTypePairs);
            return null;
        }
    }
    private class PushSessionDeleteTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            PushApi pushApi = new PushApi(ApplicationPreference.getInstance().getPrefAccessToken());
            pushApi.gcmSessionDelete(ApplicationPreference.getInstance().getPrefGcmRegId());
            return null;
        }
    }

    class LogPollingThread extends Thread {

        private boolean isRunning = false;

        private Device device;

        LogPollingThread(Device device) {
            this.device = device;
        }

        public void startThread() {
            isRunning = true;
            this.start();
        }

        public void stopThread() {
            isRunning = false;
        }

        @Override
        public void run() {
            while(true) {
                if (device != null) {
                    Log.d("User's Log", "I am in the thread!!");
                    Log.d("User's Log spotID", device.getSpotDevId());
                    Log.d("User's Log", "I am in the thread222222");
                    TagStrmApi tagStrmApi = new TagStrmApi(ApplicationPreference.getInstance().getPrefAccessToken());

                    //while(isRunning) {

                    try {
                        int count = 0;
                        TagStrmApiResponse tagstreamlist = tagStrmApi.getTagStrmList(device.getSpotDevId());
                        ArrayList<TagStrm> list = tagstreamlist.getTagStrms();
                        UserInfo.logStreams.clear();
                        TagStrmApiResponse response = tagStrmApi.getTagStrmLog(device.getSpotDevId(), "50000", "100");

                        for (int i = 0; i < list.size(); i++) {
                            TagStrm tag = list.get(i);
                            if (tag.getTagStrmPrpsTypeCd().equals(TagStrmApi.TAGSTRM_DATA)) {

                                LogStream logStream = new LogStream();
                                logStream.setTag(tag);

                                if (response.getLogs() != null) {

                                    ArrayList<com.kt.gigaiot_sdk.data.Log> logs = new ArrayList<>();


                                    for (com.kt.gigaiot_sdk.data.Log tmp : response.getLogs()) {

                                        if (tmp.getAttributes().get(tag.getTagStrmId()) != null) {
                                            logs.add(tmp);
                                        }

                                    }

                                    if (logs.size() > 0) {            //혆재 태그스트림의 데이터가 있어야 logStreams에 추가한다.

                                            /*for(Log tmp : logs) {

                                                android.util.Log.w(TAG, "TAG ID = " + tag.getTagStrmId() + " | value = " + tmp.getAttributes().get(tag.getTagStrmId()));
                                            }*/

                                        logStream.setLogList(logs);
                                    }
                                    UserInfo.logStreams.add(logStream);
                                }


                            }

                        }

                        Log.d("User's Log logstream", UserInfo.logStreams.toString());

                        UserInfo.TemperateDataArray.clear();
                        Log.d("User's Log size",String.valueOf(UserInfo.TemperateDataArray.size())+" "+String.valueOf(UserInfo.logStreams.size()));
                        UserInfo.TemperateDateArray.clear();
                        UserInfo.No2DataArray.clear();
                        UserInfo.No2DateArray.clear();
                        UserInfo.GasDataArray.clear();
                        UserInfo.GasDateArray.clear();
                        ArrayList<com.kt.gigaiot_sdk.data.Log> TempLog;
                        Stack<Double> TempDataStack = new Stack<>(),GasDataStack= new Stack<>(),No2DataStack= new Stack<>();
                        Stack<String> TempDateStack= new Stack<>(),GasDateStack= new Stack<>(),No2DateStack= new Stack<>();
                        for (int i = 0; i < UserInfo.logStreams.size(); i++) {
                            LogStream TempLogStream = UserInfo.logStreams.get(i);
                            TempLog = TempLogStream.getLogList();
                            Double tempnumber;
                            Log.d("User's Log", TempLog.get(i).toString());
                            for (com.kt.gigaiot_sdk.data.Log log : TempLog) {
                                count++;
                                if (log.getAttributes().get("tmprature") != null) {
                                    tempnumber = (Double) log.getAttributes().get("tmprature");
                                    TempDataStack.push(tempnumber);
                                    TempDateStack.push(log.getOccDt().substring(2,19));
                                } else if (log.getAttributes().get("Gascos2") != null) {
                                    tempnumber = (Double) log.getAttributes().get("Gascos2");
                                    GasDataStack.push(tempnumber);
                                    GasDateStack.push(log.getOccDt().substring(2,19));
                                } else if (log.getAttributes().get("gasmq") != null) {
                                    tempnumber = (Double) log.getAttributes().get("gasmq");
                                    No2DataStack.push(tempnumber);
                                    No2DateStack.push(log.getOccDt().substring(2,19));
                                }
                            }
                        }

                        for(int i=0;i<TempDataStack.size();i++)
                        {

                            UserInfo.TemperateDataArray.add(TempDataStack.pop());
                            UserInfo.TemperateDateArray.add(TempDateStack.pop());
                        }
                        for(int i=0;i<No2DataStack.size();i++)
                        {
                            UserInfo.No2DataArray.add(No2DataStack.pop());
                            UserInfo.No2DateArray.add(No2DateStack.pop());
                        }
                        for(int i=0;i<GasDataStack.size();i++)
                        {
                            UserInfo.GasDataArray.add(GasDataStack.pop());
                            UserInfo.GasDateArray.add(GasDateStack.pop());
                        }

                        fillTextView(R.id.txt_temp,String.valueOf((int)abs(UserInfo.TemperateDataArray.get(UserInfo.TemperateDataArray.size()-1))));
                        fillTextView(R.id.txt_hr,String.valueOf(UserInfo.GasDataArray.get(0)));


                        // 데이터 전송
                        Response.Listener<String> reponseListener = new Response.Listener<String>()
                        {
                            // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
                            @Override
                            public void onResponse(String response)
                            {
                                Log.v("DataResponse",response);
                                try
                                {
                                    // JSON 형식으로 값을 response 에 받아서 넘어온다.
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject Tempmessage = jsonResponse.getJSONObject("json");
                                    String message = Tempmessage.getString("message");
                                    Log.v("User'sLogJson",message);
                                    if(message.equals("ok"))
                                    {
                                        // 전송완료시 행할것들 넣기
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);      // 로그인 실패로 알림을 띄움
                                        dialog = builder.setMessage(message)
                                                .setNegativeButton("Try Again",null)
                                                .create();
                                        dialog.show();
                                    }
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DataTransferRequest dataTransferRequest = new DataTransferRequest(UserInfo.TemperateDateArray.get(0),abs(UserInfo.TemperateDataArray.get(0)),UserInfo.GasDataArray.get(0),reponseListener,Main_Activity.this);           // 위에서 작성한 리스너를 기반으로 요청하는 클래스를 선언.(LoginRequest참고)
                        RequestQueue queue = Volley.newRequestQueue(Main_Activity.this);            // Volley의 사용법으로 request queue로 queue를 하나 선언하고
                        queue.add(dataTransferRequest);

                        Thread.sleep(3000);
                        Log.d("User's Log", "endpoint");
                    } catch (Exception e) {
                        Log.d("User's Log", "catch");
                        e.printStackTrace();
                    }
                    //}

                }
            }
        }
    }
    private void fillTextView(int id,String text)
    {
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }

}