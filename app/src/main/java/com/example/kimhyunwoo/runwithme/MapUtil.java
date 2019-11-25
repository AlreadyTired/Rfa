package com.example.kimhyunwoo.runwithme;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.MainActivity.ExerciseInfoRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Locale;

public class MapUtil {
    private final String DEFAULT_LOCALE_STRING = "en_US";

    private Marker delete = null;

//    private LatLng startLat = null;
//    private LatLng endLat = null;
    private LatLng startLat = null;
    private LatLng endLat = null;

    private Timestamp startTime = null;
    private Timestamp endTime = null;

    public  int zoomLevel = 18;
    private double distance = 0d;

    //  request
    private AlertDialog dialog;
    public final int SEND_SUCCESS = 1;
    public final int SEND_READY = 0;
    public final int SEND_FAILED = -1;

    int sendResult = SEND_READY;

    public void setReset(){
        this.startLat = null;
        this.endLat = null;
    }

    public void setStart(LatLng lat){
        if(lat == null){
            return;
        }
        this.startLat = lat;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getStartTime(){
        return this.startTime;
    }

    public void setEnd(LatLng lat){
        this.endLat = lat;
        this.endTime = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getEndTime(){
        return this.endTime;
    }
    public void polylineOnMap(GoogleMap map, LatLng previousCoordinate, LatLng currentCoordinate){
        if(map == null || previousCoordinate == null || currentCoordinate == null)
        {
            Log.e("User's Log", "Error polyLineOnMap : null check!!!");
            return;
        }

        // 사용자가 운동한 경로를 선으로 연결함
        map.addPolyline(new PolylineOptions().color(0xffff0000).width(30.0f).
                geodesic(true).add(previousCoordinate).add(currentCoordinate));

    }

    public void deleteMarker(GoogleMap map, MarkerOptions markerOptions){

        if(delete != null)
        {
            delete.remove();
        }
        delete = map.addMarker(markerOptions);
    }

    public Configuration setLocaleResources() {
        String languageToLoad = DEFAULT_LOCALE_STRING;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        return config;
    }

    public double getDistance(LatLng beforCoordinate, LatLng currentCoordinate){

        //  startLat, endLat이 null일 때 앱 튕김 방지
        if(beforCoordinate == null || currentCoordinate == null) {
            return distance;
        }

        //distance = SphericalUtil.computeDistanceBetween(beforCoordinate, currentCoordinate);

        return distance;
    }

    public int getSendResult(){
        return this.sendResult;
    }

    public void setSendResult(int result){
        this.sendResult = result;
    }

    public void Request(final Context context, String speed, String distance){
        Response.Listener<String> reponseListener = new Response.Listener<String>()
        {
            // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
            @Override
            public void onResponse(String response)
            {
                Log.v("User's Log", "ExerciseInfoRequest : "+ response);
                try
                {
                    // JSON 형식으로 값을 response 에 받아서 넘어온다.
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");
                    Log.v("message",message);
                    if(message.equals("ok"))
                    {
                        //  TODO 서버에 등록이 성공하면 어떤 데이터를 받을지 이야기하자
//                        String temp = jsonResponse.getString("temp");
                        sendResult = SEND_SUCCESS;
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);      // 로그인 실패로 알림을 띄움
                        dialog = builder.setMessage(message)
                                .setNegativeButton("Try Again press Reset button",null)
                                .create();
                        dialog.show();
                        sendResult = SEND_FAILED;
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        Log.v("User's Log", "startTime : " + Long.toString(startTime.getTime()/1000)
                + "endTime : " + Long.toString(endTime.getTime()/1000)
                + "speed : " + speed.toString()
                + "distance : "+ distance.toString());

        ExerciseInfoRequest exerciseInfoRequest = new ExerciseInfoRequest(Long.toString(startTime.getTime()/1000), Long.toString(endTime.getTime()/1000),speed.toString(),distance.toString(),reponseListener,context);           // 위에서 작성한 리스너를 기반으로 요청하는 클래스를 선언.(LoginRequest참고)
        RequestQueue queue = Volley.newRequestQueue(context);            // Volley의 사용법으로 request queue로 queue를 하나 선언하고
        queue.add(exerciseInfoRequest);
    }
}
