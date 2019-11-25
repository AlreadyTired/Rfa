package com.example.kimhyunwoo.runwithme.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GcmBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        GCMIntentService.runIntentInService(context,intent);
    }
}
