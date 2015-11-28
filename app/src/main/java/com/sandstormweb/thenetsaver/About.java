package com.sandstormweb.thenetsaver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class About extends AppCompatActivity
{
    Toolbar tb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.about);

            initializeToolbar();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeToolbar()
    {
        try{
            tb = (Toolbar)findViewById(R.id.about_toolbar);

            setSupportActionBar(tb);

            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            if(item.getItemId() == android.R.id.home)
            {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }
}
