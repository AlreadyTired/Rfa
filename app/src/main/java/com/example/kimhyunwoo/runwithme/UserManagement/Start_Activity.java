package com.example.kimhyunwoo.runwithme.UserManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.kimhyunwoo.runwithme.R;

public class Start_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView touchtoscreentext = (TextView) findViewById(R.id.presstostart);
        Animation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        touchtoscreentext.startAnimation(anim);


    }

    public void on(View v)
    {
        switch(v.getId()){
            case R.id.start_layout:{
                Intent i=new Intent(this,Login_Activity.class);
                startActivity(i);
                break;
            }

            default:

                break;
        }
    }
}
