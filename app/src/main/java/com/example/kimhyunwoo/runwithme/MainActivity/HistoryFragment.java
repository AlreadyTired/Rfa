package com.example.kimhyunwoo.runwithme.MainActivity;


import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends SimpleFragment {

    public static Fragment newInstance(){ return new HistoryFragment();}

    private LineChart mChart;
    private AlertDialog dialog;                             // 다이얼로그 메시지띄울때 사용
    private TextView DateText;                              // 날짜지정할수있는 변수
    private Spinner PeriodSpinner,SelectDataSpinner;                    // 스피너
    private Button DataRequestButton;                   // OK 버튼
    private ArrayAdapter PeriodspinnerAdapter,DataTypeSpinnerAdapter;   // 데이터타입과 기간을 지정하는 스피너에 목록을 넣을떄 사용하는 어레이변수
    private String PeriodString = "7", DataTypeStirng ="so2";
    public String DateString;    // 데이터 전송,수신시 어떤타입을 송,수신 하는지 저장하는 변수
    private ArrayList<String> DataTypeInfoFromServer,DateInfoFromServer,XAixsLabels;
    private String[] DateTempArray;
    XAxis xAxis;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        DateText = (TextView)v.findViewById(R.id.HistoryDataDateViewText);
        PeriodSpinner = (Spinner)v.findViewById(R.id.HistoryDataPeriodSelectSpinner);
        SelectDataSpinner = (Spinner)v.findViewById(R.id.HistoryDataSelectSpinner);
        DataRequestButton = (Button)v.findViewById(R.id.HistoryDataRequestButton);
        DataTypeInfoFromServer = new ArrayList<String>();
        DateInfoFromServer = new ArrayList<String>();
        XAixsLabels = new ArrayList<String>();
        DateTempArray= new String[DateInfoFromServer.size()];

        // 현재날짜를 default 값으로 설정.
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        DateText.setText(sdf.format(date));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateString = sdf.format(date);

        // 데이터 목록을 저장하는 어레이 변수
        ArrayList<String> DataTypeList = new ArrayList<>();
        DataTypeList.add("SO2");
        DataTypeList.add("SO2 AQI");
        DataTypeList.add("CO");
        DataTypeList.add("CO AQI");
        DataTypeList.add("NO2");
        DataTypeList.add("NO2 AQI");
        DataTypeList.add("O3");
        DataTypeList.add("O3 AQI");
        DataTypeList.add("PM2.5");
        DataTypeList.add("PM2.5 AQI");
        DataTypeList.add("Temp");
        DataTypeList.add("HeartRate");

        // 기간을 저장하는 어레이 변수
        ArrayList<String> periodlist = new ArrayList<>();
        periodlist.add("1 Week");
        periodlist.add("1 Month");
        periodlist.add("3 Month");

        PeriodspinnerAdapter = new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,periodlist);
        PeriodSpinner.setAdapter(PeriodspinnerAdapter);
        PeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) { PeriodString = "7";}
                if(position == 1) { PeriodString = "30";}
                if(position == 2) { PeriodString = "90";}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DataTypeSpinnerAdapter = new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,DataTypeList);
        SelectDataSpinner.setAdapter(DataTypeSpinnerAdapter);
        SelectDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) { DataTypeStirng = "so2";}
                if(position == 1) { DataTypeStirng = "so2aqi";}
                if(position == 2) { DataTypeStirng = "co";}
                if(position == 3) { DataTypeStirng = "coaqi";}
                if(position == 4) { DataTypeStirng = "no2";}
                if(position == 5) { DataTypeStirng = "no2aqi";}
                if(position == 6) { DataTypeStirng = "o3";}
                if(position == 7) { DataTypeStirng = "o3aqi";}
                if(position == 8) { DataTypeStirng = "pm25";}
                if(position == 9) { DataTypeStirng = "pm25aqi";}
                if(position == 10) { DataTypeStirng = "temp";}
                if(position == 11) { DataTypeStirng = "heart";}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 달력띄우기
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month+=1;
                        String Monthstring = month<10?"0"+String.valueOf(month):String.valueOf(month);
                        String Daystring = dayOfMonth<10?"0"+String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                        String date = Monthstring+"-"+Daystring+"-"+String.valueOf(year);
                        DateString = String.valueOf(year)+Monthstring+Daystring;
                        long now = System.currentTimeMillis();
                        Date Todaydate = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String TodayString = sdf.format(Todaydate);

                        if(Integer.parseInt(DateString)>Integer.parseInt(TodayString))
                        {
                            sdf = new SimpleDateFormat("MM-dd-yyyy");
                            date = sdf.format(Todaydate);
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                            DateString = sdf.format(Todaydate);
                        }
                        else
                        {
                            DateString = String.valueOf(year)+"-"+Monthstring+"-"+Daystring;
                        }
                        Log.v("DateString",DateString);
                        DateText.setText(date);
                    }
                },yy,mm,dd);
                datePicker.show();
            }
        });

        DataRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // 리퀘스트 때리면 됨
                DateInfoFromServer.clear();
                DataTypeInfoFromServer.clear();
                Response.Listener<String> reponseListener = new Response.Listener<String>()
                {
                    // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
                    @Override
                    public void onResponse(String response)
                    {
                        Log.v("JSONResponse",response);
                        JSONObject jsonResponse;
                        try
                        {
                            // JSON 형식으로 값을 response 에 받아서 넘어온다.
                            jsonResponse = new JSONObject(response);
                            Log.v("JSONResponse",jsonResponse.toString());
                            String message = jsonResponse.getString("message");
                            if(message.equals("ok"))
                            {
                                JSONArray array = jsonResponse.getJSONArray("data");
                                for(int i=array.length()-1;i>=0;i--)
                                {
                                    JSONObject object = array.getJSONObject(i);
                                    String data = object.getString(DataTypeStirng);
                                    String datedata = object.getString("date");
                                    datedata = datedata.substring(5,10);
                                    DataTypeInfoFromServer.add(data);
                                    DateInfoFromServer.add(datedata);
                                }
                                Log.v("DateInfoFromServer",DateInfoFromServer.toString());
                                mChart.clear();
                                mChart.setData(getComplexity(DataTypeStirng,DataTypeInfoFromServer));
                                mChart.invalidate();
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(DateInfoFromServer));
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                dialog = builder.setMessage(message)
                                        .setNegativeButton("OK",null)
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
                DataGetRequest datagetRequest = new DataGetRequest(DateString,DataTypeStirng,PeriodString,reponseListener,getContext());           // 위에서 작성한 리스너를 기반으로 요청하는 클래스를 선언.(LoginRequest참고)
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());            // Volley의 사용법으로 request queue로 queue를 하나 선언하고
                queue.add(datagetRequest);
            }
        });

        mChart = (LineChart)v.findViewById(R.id.myLineChart);
        mChart.getDescription().setEnabled(false);

        mChart.setDrawGridBackground(false);

        mChart.animateX(3000);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");

        Legend l = mChart.getLegend();
        l.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);

        mChart.getAxisRight().setEnabled(false);

        xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(DateInfoFromServer));   // 여기서 X축 라벨넣는다.

        return v;
    }
}