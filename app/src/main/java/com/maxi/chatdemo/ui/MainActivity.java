package com.maxi.chatdemo.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.maxi.chatdemo.R;
//import com.maxi.chatdemo.app.PlayingMusicServices;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        playingMusic();
    }

    private void init() {
        ((Button) findViewById(R.id.listview_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListViewChatActivity.class));
            }
        });
    }

//    private void playingMusic() {
//        Intent intent=new Intent(this, PlayingMusicServices.class);
//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//
//            }
//        }, Context.BIND_AUTO_CREATE);
////        startService(intent);
//    }
}
