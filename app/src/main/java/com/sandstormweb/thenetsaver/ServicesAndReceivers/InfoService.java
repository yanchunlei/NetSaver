package com.sandstormweb.thenetsaver.ServicesAndReceivers;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.sandstormweb.thenetsaver.DataTypes.InfoBundle;
import com.sandstormweb.thenetsaver.DataTypes.InfoItem;

import java.util.ArrayList;

public class InfoService extends Service
{
    private SQLiteDatabase myDatabase;
    private static InfoBundle infoBundle;

    public interface OnChangeListener
    {
        public void onChange();
    }
    private ArrayList<OnChangeListener> listeners = new ArrayList();

    public class MyBinder extends Binder
    {
        public InfoService getService()
        {
            try{
                return InfoService.this;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            myDatabase = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);

            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS info(name TEXT, content TEXT)");

            if(infoBundle == null) {
                loadData();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private synchronized void loadData()
    {
        try{
            Cursor c = myDatabase.rawQuery("SELECT name, content FROM info WHERE 1", null);
            if(c.moveToFirst()) {

                ArrayList<InfoItem> data = new ArrayList();
                for (int i = 0; i < c.getCount(); i++) {
                    InfoItem temp = new InfoItem(c.getString(0), c.getString(1));
                    data.add(temp);
                    c.moveToNext();
                }

                infoBundle = new InfoBundle(data);

                c.close();
            }else{
                initializeInfo();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void saveData()
    {
        try{
            for(int i = 0; i < infoBundle.getSize(); i++)
            {
                InfoItem temp = infoBundle.getInfoItem(i);

                ContentValues cv = new ContentValues();
                cv.put("name", temp.getName());
                cv.put("content", temp.getContent());

                if(isRecordExists(temp.getName())){
                    myDatabase.update("info", cv, "name=\""+temp.getName()+"\"", null);
                }else{
                    myDatabase.insert("info", null, cv);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized InfoBundle getCachedData()
    {
        return infoBundle;
    }

    private synchronized boolean isRecordExists(String name)
    {
        try{
            Cursor c = myDatabase.rawQuery("SELECT name FROM info WHERE name=\""+name+"\"", null);
            if(c.moveToFirst()) {
                c.close();
                return true;
            } else{
                c.close();
                return false;
            }


        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void addOnChangeListener(OnChangeListener onChangeListener)
    {
        try{
            this.listeners.add(onChangeListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private synchronized void dispatchChange()
    {
        try{
            int exSize = listeners.size();

            for(int i = 0; i < exSize; i++)
            {
                listeners.get(i).onChange();
            }

            for(int i = 0; i < exSize; i++)
            {
                listeners.remove(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private synchronized void initializeInfo()
    {
        try{
            ArrayList<InfoItem> data = new ArrayList();

            InfoItem ii = new InfoItem("zeroMobile",Long.toString(android.net.TrafficStats.getMobileRxBytes()+android.net.TrafficStats.getMobileTxBytes()));
            data.add(ii);
            ii = new InfoItem("zeroWifi",Long.toString(android.net.TrafficStats.getTotalRxBytes()+android.net.TrafficStats.getTotalTxBytes()-android.net.TrafficStats.getMobileRxBytes()-android.net.TrafficStats.getMobileTxBytes()));
            data.add(ii);
            ii = new InfoItem("totalMobile","0");
            data.add(ii);
            ii = new InfoItem("totalWifi","0");
            data.add(ii);
            ii = new InfoItem("historyMobile","0");
            data.add(ii);
            ii = new InfoItem("historyWifi","0");
            data.add(ii);
            ii = new InfoItem("mode","0");
            data.add(ii);
            ii = new InfoItem("customInterval","300000");
            data.add(ii);
            ii = new InfoItem("customDuration","60000");
            data.add(ii);
            ii = new InfoItem("customMobile","1");
            data.add(ii);
            ii = new InfoItem("customWifi","0");
            data.add(ii);
            ii = new InfoItem("homeInterval","900000");
            data.add(ii);
            ii = new InfoItem("homeDuration","120000");
            data.add(ii);
            ii = new InfoItem("homeMobile","0");
            data.add(ii);
            ii = new InfoItem("homeWifi","1");
            data.add(ii);
            ii = new InfoItem("outdoorInterval","1800000");
            data.add(ii);
            ii = new InfoItem("outdoorDuration","60000");
            data.add(ii);
            ii = new InfoItem("outdoorMobile","1");
            data.add(ii);
            ii = new InfoItem("outdoorWifi","0");
            data.add(ii);
            ii = new InfoItem("workInterval","3600000");
            data.add(ii);
            ii = new InfoItem("workDuration","60000");
            data.add(ii);
            ii = new InfoItem("workMobile","1");
            data.add(ii);
            ii = new InfoItem("workWifi","0");
            data.add(ii);
            ii = new InfoItem("sleepInterval","10800000");
            data.add(ii);
            ii = new InfoItem("sleepDuration","120000");
            data.add(ii);
            ii = new InfoItem("sleepMobile","0");
            data.add(ii);
            ii = new InfoItem("sleepWifi","1");
            data.add(ii);
            ii = new InfoItem("status","0");
            data.add(ii);
            ii = new InfoItem("notification","1");
            data.add(ii);

            infoBundle = new InfoBundle(data);

            saveData();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try{
            myDatabase.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
