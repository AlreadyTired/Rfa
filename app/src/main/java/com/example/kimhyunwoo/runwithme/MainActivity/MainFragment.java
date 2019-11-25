package com.example.kimhyunwoo.runwithme.MainActivity;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.MapUtil;
import com.example.kimhyunwoo.runwithme.R;
import com.example.kimhyunwoo.runwithme.UserInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnMarkerClickListener {
    //===================================================================
    //  구글 맵 변수

    private LineChart linechart;
    XAxis xAxis;

    MapUtil mapUtil = null;

    LocationManager manager;

    GoogleMap map = null;
    MarkerOptions markerOptions = new MarkerOptions();

    //  TODO(db 연동 시에 db에서 마지막 위치를 받아 오도록 하자)
    //  현재는 db에 연동이 안되어 임의로 설정함
    LatLng savedCoordinate = null;
    LatLng currentCoordinate = null;

    Button buttonStart = null;
    Button buttonEnd = null;
    Button buttonReset = null;

    private AlertDialog dialog = null;

    private static final int markerRequstCode = 1000;

    double currentLng = 0d;
    double currentLat = 0d;
    //===================================================================
    boolean exercisingFlag = false;
    //  속도 계산
    double speed = 01d;
    double sumDistance = 0d;

    Handler timeHandle = null;
    double timer = 0d;

    double dist = 0d;

    //===================================================================
    //  메인 차트
    TextView textCO;
    TextView textSO2;
    TextView textNO2;
    TextView textO3;
    TextView textPM25;

    TextView textCOAQI;
    TextView textSO2AQI;
    TextView textNO2AQI;
    TextView textO3AQI;
    TextView textPM25AQI;
    TextView textTotal;

    //===================================================================
    //  블루투스 변수
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    private String mConnectedDeviceName = null;

    private ArrayAdapter<String> mConversationArrayAdapter;

    private StringBuffer mOutStringBuffer;

    //===================================================================
    //  데이터 전송
    //AqiDataTansfer aqiDataTansfer;
   // RealTimeDataTransfer realTimeDataTransfer;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //  Set google map util
        mapUtil = new MapUtil();

       // aqiDataTansfer = new AqiDataTansfer();
        //realTimeDataTransfer = new RealTimeDataTransfer();
        // Get local Bluetooth adapter

        // If the adapter is null, then Bluetooth is not supported
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // 프래그먼트 안에서 프래그먼트를 가져올 때 사용
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_google);
        // 맵이 사용할 준비가 되면 onMapReady 함수를 자동으로 호출

        // 버튼을 만들기 위해서 생성
        buttonStart = (Button)view.findViewById(R.id.btn_start);
        buttonEnd = (Button)view.findViewById(R.id.btn_end);
        buttonReset = (Button)view.findViewById(R.id.btn_reset);

        // 리스너에 버튼을 등록함
        buttonStart.setOnClickListener(this);
        buttonEnd.setOnClickListener(this);
        buttonReset.setOnClickListener(this);

        linechart = (LineChart)view.findViewById(R.id.Linchartdo);

        //  구글맵 쓰레드 시작ㅈ
        mapFragment.getMapAsync(this);

        //  프래그먼트가 호출된 상위 액티비티를 가져올수있음
        //  MainActivity를 호출한 액티비티를 가져옴
        //  getActivity는 MainActivity를 가지고있는 액티비티
        //  상위 액티비티의 자원을 사용하기 위해서 Activity를 가져옴
        Main_Activity activity = (Main_Activity) getActivity();
        manager = activity.getLocationManager();
/*
        textCO = view.findViewById(R.id.txt_co);
        textSO2 = view.findViewById(R.id.txt_so2);
        textNO2 = view.findViewById(R.id.txt_no2);
        textO3 = view.findViewById(R.id.txt_o3);
        textPM25 = view.findViewById(R.id.txt_pm25);
        textCOAQI = view.findViewById(R.id.txt_coAQI);
        textSO2AQI = view.findViewById(R.id.txt_so2AQI);
        textNO2AQI = view.findViewById(R.id.txt_no2AQI);
        textO3AQI = view.findViewById(R.id.txt_o3AQI);
        textPM25AQI = view.findViewById(R.id.txt_pm252AQI);
        textTotal = view.findViewById(R.id.txt_total);
        RealTimeDataTransfer.setTextView(textHR);
*/
        linechart.getDescription().setEnabled(false);
        linechart.setDrawGridBackground(false);
        linechart.animateX(3000);

        Legend l = linechart.getLegend();

        linechart.getAxisRight().setEnabled(false);

        xAxis = linechart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(UserInfo.TemperateDateArray));

        chartupdate chart = new chartupdate();
        chart.startThread();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    // 현재 프래그먼트가 러닝직전
    // 생명주기를 생각하면 onResume
    @Override
    public void onResume() {
        super.onResume();

        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너 해제
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // GPS 리스너 등록
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, //위치제공자
                1000, //변경사항 체크 주기 millisecond 단위임
                1, //변경사항 체크 거리 meter단위
                locationListener //locationListener 함수를 호출 함
        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonStart) {
            String toastText = "";

            do {
                if(currentCoordinate == null){
                    toastText = "Please wait until you get your current location.";
                    break;
                }
                //  sendResult가 true면 서버로 보낼 운동 데이터가 남은 상태로 간주
                if (exercisingFlag != true) {
                    mapUtil.setStart(currentCoordinate);

                    exercisingFlag = true;

                    timeHandle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            timeHandle.sendEmptyMessageDelayed(0, 1000);
                            timer++;

                            if (savedCoordinate != null && currentCoordinate != null) {
                                Log.v("User's Log", "distance : " + sumDistance);
                                Log.v("User's Log", "timer : " + sumDistance);
                                Log.v("User's Log", "lang: " + savedCoordinate.latitude + " before long : " + savedCoordinate.longitude);

                            }

                            //  현재 좌표에 마커를 찍기 위해서 옵션에 저장
                            markerOptions.position(currentCoordinate);

                            if (savedCoordinate != null) {
                                mapUtil.polylineOnMap(map, savedCoordinate, currentCoordinate);
                            }

                            savedCoordinate = currentCoordinate;

                            //  마커 삭제
                            mapUtil.deleteMarker(map, markerOptions);

                            //  카메라 움직임
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinate, mapUtil.zoomLevel));

//                            Context context = getActivity().getApplicationContext();
//                            Toast toast = Toast.makeText(context,"now time :"+ timer, Toast.LENGTH_SHORT);
//                            toast.show();

//                        super.handleMessage(msg);
                        }
                    };

                    timeHandle.sendEmptyMessage(0);

                    toastText = "exercise start!!";

                    break;
                }

                    toastText = "already start!!";
            }while(false);

            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,toastText, Toast.LENGTH_SHORT);
            toast.show();

        }else if (v == buttonEnd){
            String toastText;

            do {
                if(currentCoordinate == null){
                    toastText = "Please wait until you get your current location.";
                    break;
                }

                if (exercisingFlag != false) {
                    exercisingFlag = false;
                    mapUtil.setEnd(currentCoordinate);

                    //  속도 = 총 시간 / 총 거리
                    //  소수 2자리 계산
                    if (timer != 0) {
                        speed = sumDistance / timer;
                    }
                    timeHandle.removeMessages(0);

                    //  운동이 끝났고, 전송에 성공 했으면 전송 준비 상태
                    if (mapUtil.getSendResult() == mapUtil.SEND_READY && speed > 0) {
                        mapUtil.Request(getContext(), Double.toString(speed), Double.toString(sumDistance));
                    }

                    toastText = "exercise end!!";
                    break;
                }
                    toastText = "press start button!!";

            }while(false);

            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,toastText, Toast.LENGTH_SHORT);
            toast.show();

        }else if (v == buttonReset) {
            Context context = getActivity().getApplicationContext();
            String toastText = "Press start button";;

            do {
                Log.v("[TEST3]", "sendresult : " + mapUtil.getSendResult());

                if(mapUtil.getSendResult() == mapUtil.SEND_SUCCESS){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());      // 로그인 실패로 알림을 띄움
                    dialog = builder.setMessage("Successfully save your data!! :)" +
                            "\n\nStart Time\n" +
                            "* " + mapUtil.getStartTime() +
                            "\nEnd Time\n" +
                            "* " + mapUtil.getEndTime() +
                            "\n\nSpeed\n" +
                            "* " + speed + " m/s" +
                            "\nDistance\n" +
                            "* " + dist + " M")
                            .setNegativeButton("Ok", null)
                            .create();
                    dialog.show();

                    map.clear();
                    mapUtil.setSendResult(mapUtil.SEND_READY);
                    mapUtil.setReset();
                    sumDistance = 0d;
                    timer = 0d;
                    speed = 0d;

                    toastText = "Saved data";
                    break;
                }

                //  운동이 끝났고, 전송에 실패 했으면 재전송
                if (mapUtil.getSendResult() == mapUtil.SEND_FAILED) {
                    mapUtil.Request(getContext(), Double.toString(speed), Double.toString(sumDistance));

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());      // 저장 실패로 알림을 띄움
                    dialog = builder.setMessage("Check the internet and click again the reset button")
                            .setNegativeButton("Ok", null)
                            .create();
                    dialog.show();

                    toastText = "Check the internet and re-click the reset button";
                    break;
                }

                //  운동 중이면 전송하면 안됨
                if(exercisingFlag == true) {
                    toastText = "Press stop button!!";
                }

            }while(false);
            Toast toast = Toast.makeText(context,toastText, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // 다이얼이 끝나고 여기로 결과가 전송됨
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case markerRequstCode:
                // ok를 눌렀을 경우
                if (resultCode == Activity.RESULT_OK) {
                    // intent 에서 id 키를 받아서 여기로 가져옴
                    String id = data.getExtras().getString("id");
                    Toast.makeText(getActivity(), "Sending Friend request!\n to "+id ,Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //  현재는 사용 안함.
        //  사용하면 다시 활성화 하자.
        //  좌표 적용
        //  마커생성
//        markerOptions.position(savedCoordinate); //좌표
//        markerOptions.title("임시 마커");
//        //  마커를 화면에 그림
//        map.addMarker(markerOptions);
//        //  맵의 중심을 해당 좌표로 이동
//        //  savedCoordinate : 좌표
//        //  v: 줌레벨
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(savedCoordinate,mapUtil.zoomLevel));
        map.setOnMarkerClickListener(this);
    }

    //GPS 사용을 위해서 좌표 리스너를 생성
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //경도
            currentLng = location.getLongitude();
            //위도
            currentLat = location.getLatitude();

           // RealTimeDataTransfer.setGPS(Double.toString(currentLat),Double.toString(currentLng));
            //  바뀐 현재 좌표
            currentCoordinate = new LatLng(currentLat ,currentLng);
            //  거리 계산 후 0으로 초기화

            if(exercisingFlag == true) {
                dist = mapUtil.getDistance(savedCoordinate, currentCoordinate);
                sumDistance += dist;
                dist = 0d;

                return;
            }

            savedCoordinate = currentCoordinate;

            //  현재 좌표에 마커를 찍기 위해서 옵션에 저장
            markerOptions.position(currentCoordinate);


            //  마커 삭제
            mapUtil.deleteMarker(map, markerOptions);
            //  카메라 움직임
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinate,mapUtil.zoomLevel));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 위치 공급자의 상태가 바뀔 때 호출
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 위치 공급자가 사용 가능해질(enabled) 때 호출
        }

        @Override
        public void onProviderDisabled(String provider) {
            //  위치 공급자가 사용 불가능해질(disabled) 때 호출
        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        //dialog fragment class 생성
        /*MarkerClickFragment newFragment = new MarkerClickFragment();
        // onActivityResult에서 1234 라는 요청 코드를 받아서 처리할 수 있도록 설정
        newFragment.setTargetFragment(this, markerRequstCode );
        //"dialog"라는 태그를 갖는 프래그먼트 생성
        newFragment.show(getFragmentManager(), "dialog");
*/
        return true;
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */

    private void DeviceRegistryRequest(){

        // 센서 서버에 등록하는 리퀘스트,리스폰스.
        Response.Listener<String> reponseListener = new Response.Listener<String>() {

            // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // JSON 형식으로 값을 response 에 받아서 넘어온다.
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");
                    if(message.equals("ok"))
                    {
                        Toast.makeText(getContext(), "Device registration complete!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage(message)
                                .setNegativeButton("Try Again Bluetooth Connect!",null)
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
        //  맥, 이름 날라옴(btSingletion.getDeviceAdress(),btSingletion.getDeviceName())
    }

    protected LineData getComplexity(String DataType, ArrayList<Double> DataInfo){                          // JSON Array 에서 받아온 데이터가지고 순서대로 집어넣는다.
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        List<Entry> list = new ArrayList<>();
        for(int i=0;i<DataInfo.size();i++)
        {
            list.add(new Entry(i,(DataInfo.get(i).floatValue())));
        }

        LineDataSet ds1 = new LineDataSet(list,DataType+"                                                        Y Axis = Value  ,      X Axis = Date");
        // load DataSets from textfiles in assets folders

        sets.add(ds1);

        LineData d = new LineData(sets);
        return d;
    }

    class chartupdate extends Thread
    {
        private boolean isRunning = false;
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
                try{
                    linechart.clear();
                    linechart.setData(getComplexity("No2",UserInfo.No2DataArray));
                    linechart.invalidate();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(UserInfo.No2DateArray));
                    Thread.sleep(5000);
                }catch(Exception e)
                {

                }
            }
        }
    }

}
