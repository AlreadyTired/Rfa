package com.example.kimhyunwoo.runwithme.MainActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleFragment extends Fragment {
    
    private Typeface tf;
    
    public SimpleFragment() {
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    protected LineData getComplexity(String DataType,ArrayList<String> DataInfo){                          // JSON Array 에서 받아온 데이터가지고 순서대로 집어넣는다.
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        List<Entry> list = new ArrayList<>();
        for(int i=0;i<DataInfo.size();i++)
        {
            list.add(new Entry(i,Float.parseFloat(DataInfo.get(i))));
        }

        LineDataSet ds1 = new LineDataSet(list,DataType+"                                                        Y Axis = Value  ,      X Axis = Date");
        // load DataSets from textfiles in assets folders
        sets.add(ds1);

        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    
    private String[] mLabels = new String[] { "Company A", "Company B", "Company C", "Company D", "Company E", "Company F" };
//    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };
    
    private String getLabel(int i) {
        return mLabels[i];
    }
}
