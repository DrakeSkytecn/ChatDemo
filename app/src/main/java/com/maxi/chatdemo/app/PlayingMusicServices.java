//package com.maxi.chatdemo.app;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import com.maxi.chatdemo.R;
//
//
//public class PlayingMusicServices extends Service {
//
//    private MediaPlayer mediaPlayer;
//    private boolean isStop=true;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        mediaPlayer=MediaPlayer.create(this, R.raw.bg);
//        mediaPlayer.start();
//        mediaPlayer.setLooping(true);
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        if (mediaPlayer==null){
//            mediaPlayer=new MediaPlayer();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//
//                    Intent intent=new Intent();
//                    intent.setAction("com.complete");
//                    sendBroadcast(intent);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mediaPlayer.stop();
//    }
//}
