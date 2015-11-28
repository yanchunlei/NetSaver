package com.sandstormweb.thenetsaver.ServicesAndReceivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sandstormweb.thenetsaver.MainActivity;
import com.sandstormweb.thenetsaver.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

public class MonitorService extends Service
{
    private static int mode;
    private static InfoService is;
    private static Thread monitorThread;

    public static final int MODE_CUSTOM = 0;
    public static final int MODE_HOME = 1;
    public static final int MODE_OUTDOOR = 2;
    public static final int MODE_WORK = 3;
    public static final int MODE_SLEEP = 4;

    private static final int ID_NOTIFICATION = 10;

    private static boolean isOn, isMonitorOn;

    ServiceConnection isc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                is = ((InfoService.MyBinder) iBinder).getService();

                if(monitorThread == null) startUsageMonitor();
                if(!monitorThread.isAlive()) startUsageMonitor();

                Log.d("data","MS is on : "+is.getCachedData().getContentByName("status"));
                if(is.getCachedData().getContentByName("status").equals("1") && !isOn)
                {
                    setMode(Integer.parseInt(is.getCachedData().getContentByName("mode")));
                    Log.d("data","set mode is called from service connection*********");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try{
                Log.d("data","internal receiver is called *****");

                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

                if(intent.getAction().equals("on")){
                    Intent i = new Intent(getApplicationContext(), OffReceiver.class);

                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

                    switch(mode)
                    {
                        case MODE_CUSTOM :
                            Log.d("data","what is wifi : "+is.getCachedData().getContentByName("customWifi"));
                            Log.d("data","what is mobile : "+is.getCachedData().getContentByName("customMobile"));

                            if(is.getCachedData().getContentByName("customWifi").equals("1")) setWifi(true);
                            if(is.getCachedData().getContentByName("customMobile").equals("1")) setMobileData(true);
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("customDuration")), pi);
                            break;
                        case MODE_HOME :
                            if(is.getCachedData().getContentByName("homeWifi").equals("1")) setWifi(true);
                            if(is.getCachedData().getContentByName("homeMobile").equals("1")) setMobileData(true);
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("homeDuration")), pi);
                            break;
                        case MODE_OUTDOOR :
                            if(is.getCachedData().getContentByName("outdoorWifi").equals("1")) setWifi(true);
                            if(is.getCachedData().getContentByName("outdoorMobile").equals("1")) setMobileData(true);
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("outdoorDuration")), pi);
                            break;
                        case MODE_WORK :
                            if(is.getCachedData().getContentByName("workWifi").equals("1")) setWifi(true);
                            if(is.getCachedData().getContentByName("workMobile").equals("1")) setMobileData(true);
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("workDuration")), pi);
                            break;
                        case MODE_SLEEP :
                            if(is.getCachedData().getContentByName("sleepWifi").equals("1")) setWifi(true);
                            if(is.getCachedData().getContentByName("sleepMobile").equals("1")) setMobileData(true);
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("sleepDuration")), pi);
                            break;
                    }
                }else if(intent.getAction().equals("off")){
                    Intent i = new Intent(getApplicationContext(), OnReceiver.class);

                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

                    setWifi(false);
                    setMobileData(false);

                    switch(mode)
                    {
                        case MODE_CUSTOM :
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("customInterval")), pi);
                            break;
                        case MODE_HOME :
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("homeInterval")), pi);
                            break;
                        case MODE_OUTDOOR :
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("outdoorInterval")), pi);
                            break;
                        case MODE_WORK :
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("workInterval")), pi);
                            break;
                        case MODE_SLEEP :
                            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("sleepInterval")), pi);
                            break;
                    }
                }else if(intent.getAction().equals("updateMe")){
                    updateWidget();
                }else if(intent.getAction().equals("widgetSelection")){
                    setMode(intent.getExtras().getInt("mode"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public class MyBinder extends Binder
    {
        public MonitorService getService()
        {
            try{
                return MonitorService.this;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            initializeServices();

            IntentFilter i = new IntentFilter();
            i.addAction("on");
            i.addAction("off");
            i.addAction("updateMe");
            i.addAction("widgetSelection");
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new MyReceiver(), i);

        }catch(Exception e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private void initializeServices()
    {
        try{
            Intent i = new Intent(getApplicationContext(), InfoService.class);
            startService(i);

            bindService(i,isc,0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setMobileData(Boolean on)
    {
        try{
            TelephonyManager tm;

            final ConnectivityManager conman = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            Method setMobileDataEnabledMethod;
            try {
                setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            }catch(Exception e){
                try{
                    ConnectivityManager dataManager;
                    dataManager  = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                    Method dataMtd = null;
                    try {
                            dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);

                    } catch (NoSuchMethodException exp) {
                        exp.printStackTrace();
                    }
                    dataMtd.setAccessible(true);

                    dataMtd.invoke(dataManager, on);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                return;
            }
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, on);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setWifi(Boolean on)
    {
        try{
            WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
            wm.setWifiEnabled(on);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try{
            unbindService(isc);

            Log.d("data","Service destroyed ***********");
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void startUsageMonitor()
    {
        try{
            isMonitorOn = true;

            monitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while(isMonitorOn)
                        {
                            if(isOn) updateNotification(true);
                            else updateNotification(false);

                            if(getMobileUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroMobile")) < Long.parseLong(is.getCachedData().getContentByName("totalMobile")))
                            {
                                is.getCachedData().setContentByName("zeroMobile",Long.toString(getMobileUsage()));
                                is.getCachedData().setContentByName("historyMobile",is.getCachedData().getContentByName("totalMobile"));
                                is.getCachedData().setContentByName("totalMobile",Long.toString(getMobileUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroMobile")) + Long.parseLong(is.getCachedData().getContentByName("historyMobile"))));
                                is.saveData();
                            }else{
                                is.getCachedData().setContentByName("totalMobile",Long.toString(getMobileUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroMobile")) + Long.parseLong(is.getCachedData().getContentByName("historyMobile"))));
                                is.saveData();
                            }

                            if(getWifiUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroWifi")) < Long.parseLong(is.getCachedData().getContentByName("totalWifi")))
                            {
                                is.getCachedData().setContentByName("zeroWifi",Long.toString(getWifiUsage()));
                                is.getCachedData().setContentByName("historyWifi",is.getCachedData().getContentByName("totalWifi"));
                                is.getCachedData().setContentByName("totalWifi",Long.toString(getWifiUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroWifi")) + Long.parseLong(is.getCachedData().getContentByName("historyWifi"))));
                                is.saveData();
                            }else{
                                is.getCachedData().setContentByName("totalWifi",Long.toString(getWifiUsage() - Long.parseLong(is.getCachedData().getContentByName("zeroWifi")) + Long.parseLong(is.getCachedData().getContentByName("historyWifi"))));
                                is.saveData();
                            }

                            Thread.sleep(1000);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            monitorThread.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private long getWifiUsage()
    {
        try{
            long rx = android.net.TrafficStats.getTotalRxBytes() - android.net.TrafficStats.getMobileRxBytes();
            long tx = android.net.TrafficStats.getTotalTxBytes() - android.net.TrafficStats.getMobileTxBytes();

            return rx + tx;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private long getMobileUsage()
    {
        try{
            return android.net.TrafficStats.getMobileRxBytes() + android.net.TrafficStats.getMobileTxBytes();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void setMode(int mode)
    {
        try{
            Log.d("data","setMode is called************");

            isOn = true;

            this.mode = mode;

            cancelMode();

            is.getCachedData().setContentByName("mode",Integer.toString(mode));
            is.saveData();

            setMobileData(false);
            setWifi(false);

            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

            Intent i = new Intent(getApplicationContext(), OnReceiver.class);

            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

            switch(mode)
            {
                case MODE_CUSTOM :
                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("customInterval")), pi);
                    Toast.makeText(getApplicationContext(), getString(R.string.custom_activated), Toast.LENGTH_LONG).show();
                    break;
                case MODE_HOME :
                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("homeInterval")), pi);
                    Toast.makeText(getApplicationContext(), getString(R.string.home_activated), Toast.LENGTH_LONG).show();
                    break;
                case MODE_OUTDOOR :
                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("outdoorInterval")), pi);
                    Toast.makeText(getApplicationContext(), getString(R.string.outdoor_activated), Toast.LENGTH_LONG).show();
                    break;
                case MODE_WORK :
                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("workInterval")), pi);
                    Toast.makeText(getApplicationContext(), getString(R.string.work_activated), Toast.LENGTH_LONG).show();
                    break;
                case MODE_SLEEP :
                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+Long.parseLong(is.getCachedData().getContentByName("sleepInterval")), pi);
                    Toast.makeText(getApplicationContext(), getString(R.string.sleep_activated), Toast.LENGTH_LONG).show();
                    break;
            }

            updateWidget();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getMode()
    {
        return this.mode;
    }

    private void cancelMode()
    {
        try{
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

            Intent i = new Intent(getApplicationContext(), OnReceiver.class);

            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

            am.cancel(pi);

            i = new Intent(getApplicationContext(), OffReceiver.class);

            pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

            am.cancel(pi);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void turnOff()
    {
        try{
            isOn = false;

            is.getCachedData().setContentByName("status","0");
            is.saveData();

            cancelMode();

            updateWidget();

            Toast.makeText(getApplicationContext(), getString(R.string.off_now), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isOn()
    {
        try{
            return isOn;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void updateWidget()
    {
        try{
            Intent i = new Intent();
            i.setAction("update");
            if(isOn) {
                i.putExtra("mode", is.getCachedData().getContentByName("mode"));
            }else{
                i.putExtra("mode", "-1");
            }

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateNotification(boolean isActive)
    {
        try{
//            Log.d("data","notification updated **********");

            if(isActive && is.getCachedData().getContentByName("notification").equals("1")){
                NotificationCompat.Builder ncb = new android.support.v7.app.NotificationCompat.Builder(getApplicationContext());
                ncb.setPriority(NotificationCompat.PRIORITY_MAX);

                ncb.setOngoing(true);
                ncb.setSmallIcon(R.drawable.icon_small);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
                ncb.setContentIntent(pi);

                RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification);
                switch(Integer.parseInt(is.getCachedData().getContentByName("mode")))
                {
                    case MODE_CUSTOM :
                        rv.setTextViewText(R.id.notification_title, getString(R.string.custom_mode));
                        ncb.setContentTitle("  "+getString(R.string.custom_mode));
                        break;
                    case MODE_HOME :
                        rv.setTextViewText(R.id.notification_title, getString(R.string.home_mode));
                        ncb.setContentTitle("  "+getString(R.string.home_mode));
                        break;
                    case MODE_OUTDOOR :
                        rv.setTextViewText(R.id.notification_title, getString(R.string.outdoor_mode));
                        ncb.setContentTitle("  "+getString(R.string.outdoor_mode));
                        break;
                    case MODE_WORK :
                        rv.setTextViewText(R.id.notification_title, getString(R.string.work_mode));
                        ncb.setContentTitle("  "+getString(R.string.work_mode));
                        break;
                    case MODE_SLEEP :
                        rv.setTextViewText(R.id.notification_title, getString(R.string.sleep_mode));
                        ncb.setContentTitle("  "+getString(R.string.sleep_mode));
                        break;
                }

                rv.setTextViewText(R.id.notification_title, getNotificationUsageText());
                ncb.setContentText(getNotificationUsageText());

//                ncb.setContent(rv);

                NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.notify(ID_NOTIFICATION, ncb.build());
            }else{
                NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(ID_NOTIFICATION);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String convertBytesToDataUsage(String usageBytes)
    {
        try{
            Double temp = Double.parseDouble(usageBytes);

            String tmp = "";

            if(temp < 1000000){
                temp /= 1000;
                return  doubleToString(temp)+" KB";
            }else if(temp < 1000000000){
                temp /= 1000000;
                return  doubleToString(temp)+" MB";
            }else{
                temp /= 1000000;
                return  doubleToString(temp)+" GB";
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String doubleToString(double input)
    {
        try{
            String tmp = Double.toString(input);

            String output = "";
            for(int i = 0; true; i++)
            {
                output += tmp.charAt(i);
                if(tmp.charAt(i) == '.')
                {
                    for(int j = 0; j < 2 && i < tmp.length()-1; j++)
                    {
                        i++;
                        output += tmp.charAt(i);
                    }
                    return output;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getNotificationUsageText()
    {
        try{
            return "         "+getString(R.string.mobile__)+convertBytesToDataUsage(is.getCachedData().getContentByName("totalMobile"))+"  "+getString(R.string.wifi__)+convertBytesToDataUsage(is.getCachedData().getContentByName("totalWifi"));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
