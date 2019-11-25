package com.example.kimhyunwoo.runwithme.MainActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.kimhyunwoo.runwithme.ServerInfo.*;
import static com.example.kimhyunwoo.runwithme.UserInfo.getUserToken;

public class ExerciseInfoRequest extends StringRequest {
    final static private String URL = serverURL;                          // URL 헤더파일로 묶어서 수정해야함.
    private Map<String,String> parameters;                                                          // Map 형식으로 데이터를 저장(JSON 이기에 Stirng2개)

    // Volley의 StringRequest를 선언받아서 사용해야 하는데 내가 사용할 값들과 부모의 인자값까지 사용해야함. 앞서 선언한 map 에 필요한 값들을 JSON형식의 String 2개값을 넣어서 보냄.
    public ExerciseInfoRequest(String startTimestamp, String endTimestamp, String speed, String distacne, Response.Listener<String> listener, final Context context)
    {
        super(Request.Method.POST,URL,listener,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    dialog = builder.setMessage("Connect Error, Please Try again later")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .create();
                    dialog.show();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        parameters = new HashMap<>();
        JSONObject informationObject = new JSONObject();
        try{
            informationObject.put("startTime",startTimestamp);
            informationObject.put("endTime",endTimestamp);
            informationObject.put("speed",speed);
            informationObject.put("distance",distacne);
            informationObject.put("token",getUserToken());
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        parameters.put("json",informationObject.toString());
        Log.v("User's Log",parameters.toString());
    }



    public Map<String,String>getParams()
    {
        return parameters;
    }
}