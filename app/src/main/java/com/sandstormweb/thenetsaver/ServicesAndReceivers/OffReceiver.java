package com.sandstormweb.thenetsaver.ServicesAndReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class OffReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try{
            Log.d("data","Off is called *****");

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);

            Intent i = new Intent();
            i.setAction("off");

            lbm.sendBroadcast(i);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
