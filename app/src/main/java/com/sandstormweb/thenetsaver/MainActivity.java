package com.sandstormweb.thenetsaver;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sandstormweb.thenetsaver.ServicesAndReceivers.InfoService;
import com.sandstormweb.thenetsaver.ServicesAndReceivers.MonitorService;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Toolbar tb;
    TextView mobile, wifi, mobileTitle, wifiTitle;
    ImageView onOff, custom, home, outdoor, work, sleep;
    Button customB, homeB, outdoorB, workB, sleepB, customE, homeE, outdoorE, workE, sleepE;
    Dialog d;
    InfoService is;
    MonitorService ms;
    AlertDialog ad;
    Menu menu;

    ServiceConnection isc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                is = ((InfoService.MyBinder) iBinder).getService();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    ServiceConnection msc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                ms = ((MonitorService.MyBinder) iBinder).getService();

                initializeComponents();

                initializeToolbar();

                if(is.getCachedData().getContentByName("status").equals("1")) setMode(ms.getMode());

                if (is.getCachedData().getContentByName("status").equals("0"))
                    changeMainVisibility();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            initializeServices();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.stateBar));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeComponents()
    {
        try{
            tb = (Toolbar)findViewById(R.id.main_toolbar);

            mobile = (TextView)findViewById(R.id.main_download);
            mobile.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

            wifi = (TextView)findViewById(R.id.main_wifi);
            wifi.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

            mobile.setText(convertBytesToDataUsage(is.getCachedData().getContentByName("totalMobile")));
            wifi.setText(convertBytesToDataUsage(is.getCachedData().getContentByName("totalWifi")));

            onOff = (ImageView)findViewById(R.id.main_onOff);
            custom = (ImageView)findViewById(R.id.main_image_custom);
            custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_CUSTOM);
                }
            });
            home = (ImageView)findViewById(R.id.main_image_home);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_HOME);
                }
            });
            outdoor = (ImageView)findViewById(R.id.main_image_outdoor);
            outdoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_OUTDOOR);
                }
            });
            work = (ImageView)findViewById(R.id.main_image_work);
            work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_WORK);
                }
            });
            sleep = (ImageView)findViewById(R.id.main_image_sleep);
            sleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_SLEEP);
                }
            });

            customB = (Button)findViewById(R.id.main_button_custom);
            customB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_CUSTOM);
                }
            });
            homeB = (Button)findViewById(R.id.main_button_home);
            homeB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_HOME);
                }
            });
            outdoorB = (Button)findViewById(R.id.main_button_outdoor);
            outdoorB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_OUTDOOR);
                }
            });
            workB = (Button)findViewById(R.id.main_button_work);
            workB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_WORK);
                }
            });
            sleepB = (Button)findViewById(R.id.main_button_sleep);
            sleepB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMode(MonitorService.MODE_SLEEP);
                }
            });

            customE = (Button)findViewById(R.id.main_edit_custom);
            customE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        d = new Dialog(MainActivity.this);
                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.edit);
                        d.getWindow().setLayout( getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                        d.show();

                        final CheckBox mobile = (CheckBox)d.findViewById(R.id.edit_mobile_data);
                        final CheckBox wifi = (CheckBox)d.findViewById(R.id.edit_wifi);
                        final Spinner interval = (Spinner)d.findViewById(R.id.edit_interval);
                        final Spinner duration = (Spinner)d.findViewById(R.id.edit_duration);
                        Button save = (Button)d.findViewById(R.id.edit_save);

                        String[] intervals = {getString(R.string.every_5),getString(R.string.every_15),getString(R.string.every_30),getString(R.string.every_1),getString(R.string.every_3),getString(R.string.every_6),getString(R.string.every_12),getString(R.string.every_24)};
                        ArrayAdapter iaa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, intervals);
                        interval.setAdapter(iaa);

                        String[] durations = {getString(R.string.for_30),getString(R.string.for_1),getString(R.string.for_2),getString(R.string.for_3),getString(R.string.for_4)};
                        ArrayAdapter daa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, durations);
                        duration.setAdapter(daa);

                        if(is.getCachedData().getContentByName("customMobile").equals("1")) mobile.setChecked(true);
                        if(is.getCachedData().getContentByName("customWifi").equals("1")) wifi.setChecked(true);
                        switch(is.getCachedData().getContentByName("customInterval"))
                        {
                            case "300000" :
                                interval.setSelection(0);
                                break;
                            case "900000" :
                                interval.setSelection(1);
                                break;
                            case "1800000" :
                                interval.setSelection(2);
                                break;
                            case "3600000" :
                                interval.setSelection(3);
                                break;
                            case "10800000" :
                                interval.setSelection(4);
                                break;
                            case "21600000" :
                                interval.setSelection(5);
                                break;
                            case "43000000" :
                                interval.setSelection(6);
                                break;
                            case "86400000" :
                                interval.setSelection(7);
                                break;
                        }
                        switch(is.getCachedData().getContentByName("customDuration"))
                        {
                            case "30000" :
                                duration.setSelection(0);
                                break;
                            case "60000" :
                                duration.setSelection(1);
                                break;
                            case "120000" :
                                duration.setSelection(2);
                                break;
                            case "180000" :
                                duration.setSelection(3);
                                break;
                            case "240000" :
                                duration.setSelection(4);
                                break;
                        }

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    if(mobile.isChecked()) is.getCachedData().setContentByName("customMobile","1");
                                    else is.getCachedData().setContentByName("customMobile","0");

                                    if(wifi.isChecked()) is.getCachedData().setContentByName("customWifi","1");
                                    else is.getCachedData().setContentByName("customWifi","0");

                                    switch(interval.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("customInterval","300000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("customInterval","900000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("customInterval","1800000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("customInterval","3600000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customInterval","10800000");
                                            break;
                                        case 5 :
                                            is.getCachedData().setContentByName("customInterval","21600000");
                                            break;
                                        case 6 :
                                            is.getCachedData().setContentByName("customInterval","43000000");
                                            break;
                                        case 7 :
                                            is.getCachedData().setContentByName("customInterval","86400000");
                                            break;
                                    }

                                    switch(duration.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("customDuration","30000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("customDuration","60000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("customDuration","120000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("customDuration","180000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customDuration","240000");
                                            break;
                                    }

                                    is.saveData();

                                    ms.setMode(MonitorService.MODE_CUSTOM);
                                    Log.d("data","set mode is called from customE*********");

                                    d.dismiss();

                                    Log.d("data","******after is wifi ok : "+is.getCachedData().getContentByName("customWifi"));
                                    Log.d("data","******after is mobile ok : "+is.getCachedData().getContentByName("customMobile"));
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            homeE = (Button)findViewById(R.id.main_edit_home);
            homeE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        d = new Dialog(MainActivity.this);
                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.edit);
                        d.getWindow().setLayout( getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                        d.show();

                        final CheckBox mobile = (CheckBox)d.findViewById(R.id.edit_mobile_data);
                        final CheckBox wifi = (CheckBox)d.findViewById(R.id.edit_wifi);
                        final Spinner interval = (Spinner)d.findViewById(R.id.edit_interval);
                        final Spinner duration = (Spinner)d.findViewById(R.id.edit_duration);
                        Button save = (Button)d.findViewById(R.id.edit_save);

                        String[] intervals = {getString(R.string.every_5),getString(R.string.every_15),getString(R.string.every_30),getString(R.string.every_1),getString(R.string.every_3),getString(R.string.every_6),getString(R.string.every_12),getString(R.string.every_24)};
                        ArrayAdapter iaa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, intervals);
                        interval.setAdapter(iaa);

                        String[] durations = {getString(R.string.for_30),getString(R.string.for_1),getString(R.string.for_2),getString(R.string.for_3),getString(R.string.for_4)};
                        ArrayAdapter daa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, durations);
                        duration.setAdapter(daa);

                        if(is.getCachedData().getContentByName("homeMobile").equals("1")) mobile.setChecked(true);
                        if(is.getCachedData().getContentByName("homeWifi").equals("1")) wifi.setChecked(true);
                        switch(is.getCachedData().getContentByName("homeInterval"))
                        {
                            case "300000" :
                                interval.setSelection(0);
                                break;
                            case "900000" :
                                interval.setSelection(1);
                                break;
                            case "1800000" :
                                interval.setSelection(2);
                                break;
                            case "3600000" :
                                interval.setSelection(3);
                                break;
                            case "10800000" :
                                interval.setSelection(4);
                                break;
                            case "21600000" :
                                interval.setSelection(5);
                                break;
                            case "43000000" :
                                interval.setSelection(6);
                                break;
                            case "86400000" :
                                interval.setSelection(7);
                                break;
                        }
                        switch(is.getCachedData().getContentByName("homeDuration"))
                        {
                            case "30000" :
                                duration.setSelection(0);
                                break;
                            case "60000" :
                                duration.setSelection(1);
                                break;
                            case "120000" :
                                duration.setSelection(2);
                                break;
                            case "180000" :
                                duration.setSelection(3);
                                break;
                            case "240000" :
                                duration.setSelection(4);
                                break;
                        }

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    if(mobile.isChecked()) is.getCachedData().setContentByName("homeMobile","1");
                                    else is.getCachedData().setContentByName("homeMobile","0");

                                    if(wifi.isChecked()) is.getCachedData().setContentByName("homeWifi","1");
                                    else is.getCachedData().setContentByName("homeWifi","0");

                                    switch(interval.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("homeInterval","300000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("homeInterval","900000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("homeInterval","1800000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("homeInterval","3600000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customInterval","10800000");
                                            break;
                                        case 5 :
                                            is.getCachedData().setContentByName("customInterval","21600000");
                                            break;
                                        case 6 :
                                            is.getCachedData().setContentByName("customInterval","43000000");
                                            break;
                                        case 7 :
                                            is.getCachedData().setContentByName("customInterval","86400000");
                                            break;
                                    }

                                    switch(duration.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("homeDuration","30000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("homeDuration","60000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("homeDuration","120000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("homeDuration","180000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("homeDuration","240000");
                                            break;
                                    }

                                    is.saveData();

                                    ms.setMode(MonitorService.MODE_HOME);

                                    d.dismiss();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            outdoorE = (Button)findViewById(R.id.main_edit_outdoor);
            outdoorE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        d = new Dialog(MainActivity.this);
                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.edit);
                        d.getWindow().setLayout( getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                        d.show();

                        final CheckBox mobile = (CheckBox)d.findViewById(R.id.edit_mobile_data);
                        final CheckBox wifi = (CheckBox)d.findViewById(R.id.edit_wifi);
                        final Spinner interval = (Spinner)d.findViewById(R.id.edit_interval);
                        final Spinner duration = (Spinner)d.findViewById(R.id.edit_duration);
                        Button save = (Button)d.findViewById(R.id.edit_save);

                        String[] intervals = {getString(R.string.every_5),getString(R.string.every_15),getString(R.string.every_30),getString(R.string.every_1),getString(R.string.every_3),getString(R.string.every_6),getString(R.string.every_12),getString(R.string.every_24)};
                        ArrayAdapter iaa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, intervals);
                        interval.setAdapter(iaa);

                        String[] durations = {getString(R.string.for_30),getString(R.string.for_1),getString(R.string.for_2),getString(R.string.for_3),getString(R.string.for_4)};
                        ArrayAdapter daa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, durations);
                        duration.setAdapter(daa);

                        if(is.getCachedData().getContentByName("outdoorMobile").equals("1")) mobile.setChecked(true);
                        if(is.getCachedData().getContentByName("outdoorWifi").equals("1")) wifi.setChecked(true);
                        switch(is.getCachedData().getContentByName("outdoorInterval"))
                        {
                            case "300000" :
                                interval.setSelection(0);
                                break;
                            case "900000" :
                                interval.setSelection(1);
                                break;
                            case "1800000" :
                                interval.setSelection(2);
                                break;
                            case "3600000" :
                                interval.setSelection(3);
                                break;
                            case "10800000" :
                                interval.setSelection(4);
                                break;
                            case "21600000" :
                                interval.setSelection(5);
                                break;
                            case "43000000" :
                                interval.setSelection(6);
                                break;
                            case "86400000" :
                                interval.setSelection(7);
                                break;
                        }
                        switch(is.getCachedData().getContentByName("outdoorDuration"))
                        {
                            case "30000" :
                                duration.setSelection(0);
                                break;
                            case "60000" :
                                duration.setSelection(1);
                                break;
                            case "120000" :
                                duration.setSelection(2);
                                break;
                            case "180000" :
                                duration.setSelection(3);
                                break;
                            case "240000" :
                                duration.setSelection(4);
                                break;
                        }

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    if(mobile.isChecked()) is.getCachedData().setContentByName("outdoorMobile","1");
                                    else is.getCachedData().setContentByName("outdoorMobile","0");

                                    if(wifi.isChecked()) is.getCachedData().setContentByName("outdoorWifi","1");
                                    else is.getCachedData().setContentByName("outdoorWifi","0");

                                    switch(interval.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("outdoorInterval","300000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("outdoorInterval","900000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("outdoorInterval","1800000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("outdoorInterval","3600000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customInterval","10800000");
                                            break;
                                        case 5 :
                                            is.getCachedData().setContentByName("customInterval","21600000");
                                            break;
                                        case 6 :
                                            is.getCachedData().setContentByName("customInterval","43000000");
                                            break;
                                        case 7 :
                                            is.getCachedData().setContentByName("customInterval","86400000");
                                            break;
                                    }

                                    switch(duration.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("outdoorDuration","30000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("outdoorDuration","60000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("outdoorDuration","120000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("outdoorDuration","180000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("outdoorDuration","240000");
                                            break;
                                    }

                                    is.saveData();

                                    ms.setMode(MonitorService.MODE_OUTDOOR);

                                    d.dismiss();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            workE = (Button)findViewById(R.id.main_edit_work);
            workE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        d = new Dialog(MainActivity.this);
                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.edit);
                        d.getWindow().setLayout( getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                        d.show();

                        final CheckBox mobile = (CheckBox)d.findViewById(R.id.edit_mobile_data);
                        final CheckBox wifi = (CheckBox)d.findViewById(R.id.edit_wifi);
                        final Spinner interval = (Spinner)d.findViewById(R.id.edit_interval);
                        final Spinner duration = (Spinner)d.findViewById(R.id.edit_duration);
                        Button save = (Button)d.findViewById(R.id.edit_save);

                        String[] intervals = {getString(R.string.every_5),getString(R.string.every_15),getString(R.string.every_30),getString(R.string.every_1),getString(R.string.every_3),getString(R.string.every_6),getString(R.string.every_12),getString(R.string.every_24)};
                        ArrayAdapter iaa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, intervals);
                        interval.setAdapter(iaa);

                        String[] durations = {getString(R.string.for_30),getString(R.string.for_1),getString(R.string.for_2),getString(R.string.for_3),getString(R.string.for_4)};
                        ArrayAdapter daa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, durations);
                        duration.setAdapter(daa);

                        if(is.getCachedData().getContentByName("workMobile").equals("1")) mobile.setChecked(true);
                        if(is.getCachedData().getContentByName("workWifi").equals("1")) wifi.setChecked(true);
                        switch(is.getCachedData().getContentByName("workInterval"))
                        {
                            case "300000" :
                                interval.setSelection(0);
                                break;
                            case "900000" :
                                interval.setSelection(1);
                                break;
                            case "1800000" :
                                interval.setSelection(2);
                                break;
                            case "3600000" :
                                interval.setSelection(3);
                                break;
                            case "10800000" :
                                interval.setSelection(4);
                                break;
                            case "21600000" :
                                interval.setSelection(5);
                                break;
                            case "43000000" :
                                interval.setSelection(6);
                                break;
                            case "86400000" :
                                interval.setSelection(7);
                                break;
                        }
                        switch(is.getCachedData().getContentByName("workDuration"))
                        {
                            case "30000" :
                                duration.setSelection(0);
                                break;
                            case "60000" :
                                duration.setSelection(1);
                                break;
                            case "120000" :
                                duration.setSelection(2);
                                break;
                            case "180000" :
                                duration.setSelection(3);
                                break;
                            case "240000" :
                                duration.setSelection(4);
                                break;
                        }

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    if(mobile.isChecked()) is.getCachedData().setContentByName("workMobile","1");
                                    else is.getCachedData().setContentByName("workMobile","0");

                                    if(wifi.isChecked()) is.getCachedData().setContentByName("workWifi","1");
                                    else is.getCachedData().setContentByName("workWifi","0");

                                    switch(interval.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("workInterval","300000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("workInterval","900000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("workInterval","1800000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("workInterval","3600000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customInterval","10800000");
                                            break;
                                        case 5 :
                                            is.getCachedData().setContentByName("customInterval","21600000");
                                            break;
                                        case 6 :
                                            is.getCachedData().setContentByName("customInterval","43000000");
                                            break;
                                        case 7 :
                                            is.getCachedData().setContentByName("customInterval","86400000");
                                            break;
                                    }

                                    switch(duration.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("workDuration","30000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("workDuration","60000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("workDuration","120000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("workDuration","180000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("workDuration","240000");
                                            break;
                                    }

                                    is.saveData();

                                    ms.setMode(MonitorService.MODE_WORK);

                                    d.dismiss();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            sleepE = (Button)findViewById(R.id.main_edit_sleep);
            sleepE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        d = new Dialog(MainActivity.this);
                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.edit);
                        d.getWindow().setLayout( getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                        d.show();

                        final CheckBox mobile = (CheckBox)d.findViewById(R.id.edit_mobile_data);
                        final CheckBox wifi = (CheckBox)d.findViewById(R.id.edit_wifi);
                        final Spinner interval = (Spinner)d.findViewById(R.id.edit_interval);
                        final Spinner duration = (Spinner)d.findViewById(R.id.edit_duration);
                        Button save = (Button)d.findViewById(R.id.edit_save);

                        String[] intervals = {getString(R.string.every_5),getString(R.string.every_15),getString(R.string.every_30),getString(R.string.every_1),getString(R.string.every_3),getString(R.string.every_6),getString(R.string.every_12),getString(R.string.every_24)};
                        ArrayAdapter iaa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, intervals);
                        interval.setAdapter(iaa);

                        String[] durations = {getString(R.string.for_30),getString(R.string.for_1),getString(R.string.for_2),getString(R.string.for_3),getString(R.string.for_4)};
                        ArrayAdapter daa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, durations);
                        duration.setAdapter(daa);

                        if(is.getCachedData().getContentByName("sleepMobile").equals("1")) mobile.setChecked(true);
                        if(is.getCachedData().getContentByName("sleepWifi").equals("1")) wifi.setChecked(true);
                        switch(is.getCachedData().getContentByName("sleepInterval"))
                        {
                            case "300000" :
                                interval.setSelection(0);
                                break;
                            case "900000" :
                                interval.setSelection(1);
                                break;
                            case "1800000" :
                                interval.setSelection(2);
                                break;
                            case "3600000" :
                                interval.setSelection(3);
                                break;
                            case "10800000" :
                                interval.setSelection(4);
                                break;
                            case "21600000" :
                                interval.setSelection(5);
                                break;
                            case "43000000" :
                                interval.setSelection(6);
                                break;
                            case "86400000" :
                                interval.setSelection(7);
                                break;
                        }
                        switch(is.getCachedData().getContentByName("sleepDuration"))
                        {
                            case "30000" :
                                duration.setSelection(0);
                                break;
                            case "60000" :
                                duration.setSelection(1);
                                break;
                            case "120000" :
                                duration.setSelection(2);
                                break;
                            case "180000" :
                                duration.setSelection(3);
                                break;
                            case "240000" :
                                duration.setSelection(4);
                                break;
                        }

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    if(mobile.isChecked()) is.getCachedData().setContentByName("sleepMobile","1");
                                    else is.getCachedData().setContentByName("sleepMobile","0");

                                    if(wifi.isChecked()) is.getCachedData().setContentByName("sleepWifi","1");
                                    else is.getCachedData().setContentByName("sleepWifi","0");

                                    switch(interval.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("sleepInterval","300000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("sleepInterval","900000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("sleepInterval","1800000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("sleepInterval","3600000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("customInterval","10800000");
                                            break;
                                        case 5 :
                                            is.getCachedData().setContentByName("customInterval","21600000");
                                            break;
                                        case 6 :
                                            is.getCachedData().setContentByName("customInterval","43000000");
                                            break;
                                        case 7 :
                                            is.getCachedData().setContentByName("customInterval","86400000");
                                            break;
                                    }

                                    switch(duration.getSelectedItemPosition())
                                    {
                                        case 0 :
                                            is.getCachedData().setContentByName("sleepDuration","30000");
                                            break;
                                        case 1 :
                                            is.getCachedData().setContentByName("sleepDuration","60000");
                                            break;
                                        case 2 :
                                            is.getCachedData().setContentByName("sleepDuration","120000");
                                            break;
                                        case 3 :
                                            is.getCachedData().setContentByName("sleepDuration","180000");
                                            break;
                                        case 4 :
                                            is.getCachedData().setContentByName("sleepDuration","240000");
                                            break;
                                    }

                                    is.saveData();

                                    ms.setMode(MonitorService.MODE_SLEEP);

                                    d.dismiss();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mobileTitle = (TextView)findViewById(R.id.main_text_mobile);
            wifiTitle = (TextView)findViewById(R.id.main_text_wifi);

            onOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(custom.isShown()){
                            is.getCachedData().setContentByName("status","0");
                            is.saveData();
                            ms.turnOff();
                        }else{
                            is.getCachedData().setContentByName("status","1");
                            is.saveData();
                            setMode(Integer.parseInt(is.getCachedData().getContentByName("mode")));
                            Log.d("data","set mode is called from onOff*********");
                        }
                        changeMainVisibility();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeToolbar()
    {
        try{
            tb.setLogo(getResources().getDrawable(R.drawable.icon_small));

            setSupportActionBar(tb);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeServices()
    {
        try{
            Intent i = new Intent(getApplicationContext(), InfoService.class);
            startService(i);

            bindService(i,isc,0);

            i = new Intent(getApplicationContext(), MonitorService.class);
            startService(i);

            bindService(i,msc,0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try{
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_main, menu);

            this.menu = menu;

            if(is.getCachedData().getContentByName("notification").equals("1")){
                menu.getItem(1).setTitle(R.string.disable_notification);
            }else{
                menu.getItem(1).setTitle(R.string.enable_notification);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch(item.getItemId())
            {
                case R.id.main_menu_about :
                    Intent i = new Intent(getApplicationContext(), About.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                case R.id.main_menu_reset :
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle(R.string.notice);
                    adb.setMessage(R.string.are_you_usage);
                    adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try{
                                is.getCachedData().setContentByName("zeroMobile", Long.toString(android.net.TrafficStats.getMobileRxBytes()+android.net.TrafficStats.getMobileTxBytes()));
                                is.getCachedData().setContentByName("zeroWifi",Long.toString(android.net.TrafficStats.getTotalRxBytes()+android.net.TrafficStats.getTotalTxBytes()-android.net.TrafficStats.getMobileRxBytes()-android.net.TrafficStats.getMobileTxBytes()));
                                is.getCachedData().setContentByName("totalMobile","0");
                                is.getCachedData().setContentByName("totalWifi","0");
                                is.getCachedData().setContentByName("historyMobile","0");
                                is.getCachedData().setContentByName("historyWifi","0");
                                is.saveData();
                                mobile.setText(convertBytesToDataUsage(is.getCachedData().getContentByName("totalMobile")));
                                wifi.setText(convertBytesToDataUsage(is.getCachedData().getContentByName("totalWifi")));
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try{
                                ad.dismiss();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                    ad = adb.create();
                    ad.show();

                    break;
                case R.id.main_menu_notification :
                    if(is.getCachedData().getContentByName("notification").equals("1")){
                        is.getCachedData().setContentByName("notification","0");
                        menu.getItem(1).setTitle(R.string.enable_notification);
                    }else{
                        is.getCachedData().setContentByName("notification","1");
                        menu.getItem(1).setTitle(R.string.disable_notification);
                    }
                    break;
                case R.id.main_menu_language :
                    final Dialog d = new Dialog(MainActivity.this);
                    d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.language);
                    d.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels*90/100, WindowManager.LayoutParams.WRAP_CONTENT);
                    d.show();

                    Button persian = (Button)d.findViewById(R.id.language_persian);
                    persian.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                Locale locale = new Locale("fa");
                                Configuration configuration =  new Configuration();
                                configuration.locale = locale;
                                getResources().updateConfiguration(configuration, null);
                                d.dismiss();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    Button english = (Button)d.findViewById(R.id.language_english);
                    english.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                Locale locale = new Locale("en_US");
                                Configuration configuration =  new Configuration();
                                configuration.locale = locale;
                                getResources().updateConfiguration(configuration, null);
                                d.dismiss();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        try{
            unbindService(isc);
            unbindService(msc);
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
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

    private void changeMainVisibility()
    {
        try{
            if(custom.isShown()){
                Animation a = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

                onOff.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                onOff.setImageResource(R.drawable.off_3);


                custom.startAnimation(a);
                home.startAnimation(a);
                outdoor.startAnimation(a);
                work.startAnimation(a);
                sleep.startAnimation(a);

                customB.startAnimation(a);
                homeB.startAnimation(a);
                outdoorB.startAnimation(a);
                workB.startAnimation(a);
                sleepB.startAnimation(a);

                customE.startAnimation(a);
                homeE.startAnimation(a);
                outdoorE.startAnimation(a);
                workE.startAnimation(a);
                sleepE.startAnimation(a);

                mobile.startAnimation(a);
                wifi.startAnimation(a);
                mobileTitle.startAnimation(a);
                wifiTitle.startAnimation(a);

                custom.setVisibility(View.INVISIBLE);
                home.setVisibility(View.INVISIBLE);
                outdoor.setVisibility(View.INVISIBLE);
                work.setVisibility(View.INVISIBLE);
                sleep.setVisibility(View.INVISIBLE);

                customB.setVisibility(View.INVISIBLE);
                homeB.setVisibility(View.INVISIBLE);
                outdoorB.setVisibility(View.INVISIBLE);
                workB.setVisibility(View.INVISIBLE);
                sleepB.setVisibility(View.INVISIBLE);

                customE.setVisibility(View.INVISIBLE);
                homeE.setVisibility(View.INVISIBLE);
                outdoorE.setVisibility(View.INVISIBLE);
                workE.setVisibility(View.INVISIBLE);
                sleepE.setVisibility(View.INVISIBLE);

                mobile.setVisibility(View.INVISIBLE);
                mobileTitle.setVisibility(View.INVISIBLE);
                wifi.setVisibility(View.INVISIBLE);
                wifiTitle.setVisibility(View.INVISIBLE);
            }else{
                Animation a = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);

                onOff.startAnimation(a);
                onOff.setImageResource(R.drawable.on_3);

                custom.startAnimation(a);
                home.startAnimation(a);
                outdoor.startAnimation(a);
                work.startAnimation(a);
                sleep.startAnimation(a);

                customB.startAnimation(a);
                homeB.startAnimation(a);
                outdoorB.startAnimation(a);
                workB.startAnimation(a);
                sleepB.startAnimation(a);

                customE.startAnimation(a);
                homeE.startAnimation(a);
                outdoorE.startAnimation(a);
                workE.startAnimation(a);
                sleepE.startAnimation(a);

                mobile.startAnimation(a);
                wifi.startAnimation(a);
                mobileTitle.startAnimation(a);
                wifiTitle.startAnimation(a);

                custom.setVisibility(View.VISIBLE);
                home.setVisibility(View.VISIBLE);
                outdoor.setVisibility(View.VISIBLE);
                work.setVisibility(View.VISIBLE);
                sleep.setVisibility(View.VISIBLE);

                customB.setVisibility(View.VISIBLE);
                homeB.setVisibility(View.VISIBLE);
                outdoorB.setVisibility(View.VISIBLE);
                workB.setVisibility(View.VISIBLE);
                sleepB.setVisibility(View.VISIBLE);

                customE.setVisibility(View.VISIBLE);
                homeE.setVisibility(View.VISIBLE);
                outdoorE.setVisibility(View.VISIBLE);
                workE.setVisibility(View.VISIBLE);
                sleepE.setVisibility(View.VISIBLE);

                mobile.setVisibility(View.VISIBLE);
                mobileTitle.setVisibility(View.VISIBLE);
                wifi.setVisibility(View.VISIBLE);
                wifiTitle.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setMode(int mode)
    {
        try{
            switch(mode)
            {
                case MonitorService.MODE_CUSTOM :
                    custom.setImageResource(R.drawable.user_on);
                    home.setImageResource(R.drawable.home);
                    outdoor.setImageResource(R.drawable.outdoor);
                    work.setImageResource(R.drawable.work);
                    sleep.setImageResource(R.drawable.sleep);
                    customB.setTextColor(getResources().getColor(R.color.text_color_4));
                    homeB.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorB.setTextColor(getResources().getColor(R.color.text_color_3));
                    workB.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepB.setTextColor(getResources().getColor(R.color.text_color_3));
                    customE.setTextColor(getResources().getColor(R.color.text_color_4));
                    homeE.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorE.setTextColor(getResources().getColor(R.color.text_color_3));
                    workE.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepE.setTextColor(getResources().getColor(R.color.text_color_3));
                    if(ms.getMode() != MonitorService.MODE_CUSTOM || !ms.isOn()) ms.setMode(MonitorService.MODE_CUSTOM);
                    break;
                case MonitorService.MODE_HOME :
                    custom.setImageResource(R.drawable.user);
                    home.setImageResource(R.drawable.home_on);
                    outdoor.setImageResource(R.drawable.outdoor);
                    work.setImageResource(R.drawable.work);
                    sleep.setImageResource(R.drawable.sleep);
                    customB.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeB.setTextColor(getResources().getColor(R.color.text_color_4));
                    outdoorB.setTextColor(getResources().getColor(R.color.text_color_3));
                    workB.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepB.setTextColor(getResources().getColor(R.color.text_color_3));
                    customE.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeE.setTextColor(getResources().getColor(R.color.text_color_4));
                    outdoorE.setTextColor(getResources().getColor(R.color.text_color_3));
                    workE.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepE.setTextColor(getResources().getColor(R.color.text_color_3));
                    if(ms.getMode() != MonitorService.MODE_HOME || !ms.isOn()) ms.setMode(MonitorService.MODE_HOME);
                    break;
                case MonitorService.MODE_OUTDOOR :
                    custom.setImageResource(R.drawable.user);
                    home.setImageResource(R.drawable.home);
                    outdoor.setImageResource(R.drawable.outdoor_on);
                    work.setImageResource(R.drawable.work);
                    sleep.setImageResource(R.drawable.sleep);
                    customB.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeB.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorB.setTextColor(getResources().getColor(R.color.text_color_4));
                    workB.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepB.setTextColor(getResources().getColor(R.color.text_color_3));
                    customE.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeE.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorE.setTextColor(getResources().getColor(R.color.text_color_4));
                    workE.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepE.setTextColor(getResources().getColor(R.color.text_color_3));
                    if(ms.getMode() != MonitorService.MODE_OUTDOOR || !ms.isOn()) ms.setMode(MonitorService.MODE_OUTDOOR);
                    break;
                case MonitorService.MODE_WORK :
                    custom.setImageResource(R.drawable.user);
                    home.setImageResource(R.drawable.home);
                    outdoor.setImageResource(R.drawable.outdoor);
                    work.setImageResource(R.drawable.work_on);
                    sleep.setImageResource(R.drawable.sleep);
                    customB.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeB.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorB.setTextColor(getResources().getColor(R.color.text_color_3));
                    workB.setTextColor(getResources().getColor(R.color.text_color_4));
                    sleepB.setTextColor(getResources().getColor(R.color.text_color_3));
                    customE.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeE.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorE.setTextColor(getResources().getColor(R.color.text_color_3));
                    workE.setTextColor(getResources().getColor(R.color.text_color_4));
                    sleepE.setTextColor(getResources().getColor(R.color.text_color_3));
                    if(ms.getMode() != MonitorService.MODE_WORK || !ms.isOn()) ms.setMode(MonitorService.MODE_WORK);
                    break;
                case MonitorService.MODE_SLEEP :
                    custom.setImageResource(R.drawable.user);
                    home.setImageResource(R.drawable.home);
                    outdoor.setImageResource(R.drawable.outdoor);
                    work.setImageResource(R.drawable.work);
                    sleep.setImageResource(R.drawable.sleep_on);
                    customB.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeB.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorB.setTextColor(getResources().getColor(R.color.text_color_3));
                    workB.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepB.setTextColor(getResources().getColor(R.color.text_color_4));
                    customE.setTextColor(getResources().getColor(R.color.text_color_3));
                    homeE.setTextColor(getResources().getColor(R.color.text_color_3));
                    outdoorE.setTextColor(getResources().getColor(R.color.text_color_3));
                    workE.setTextColor(getResources().getColor(R.color.text_color_3));
                    sleepE.setTextColor(getResources().getColor(R.color.text_color_4));
                    if(ms.getMode() != MonitorService.MODE_SLEEP || !ms.isOn()) ms.setMode(MonitorService.MODE_SLEEP);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
