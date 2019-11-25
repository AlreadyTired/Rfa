package com.example.kimhyunwoo.runwithme;

import android.graphics.Color;
import android.widget.EditText;

public class ManagementUtil {

    public void setEditColor(EditText editText, String color)
    {
        editText.setTextColor(Color.parseColor(color));
    }

    public void setEditHintColor(EditText editText, String color)
    {
        editText.setHintTextColor(Color.parseColor(color));
    }
}
