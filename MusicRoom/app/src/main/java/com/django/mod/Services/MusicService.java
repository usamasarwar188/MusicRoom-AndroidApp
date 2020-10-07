package com.django.mod.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.django.mod.Activities.MainActivity;
import com.django.mod.Model.Song;
import com.django.mod.R;

import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnPreparedListener{



    private MediaPlayer mp;
    private ArrayList<Song>songsList;
    private int pos;
    boolean isReady;
    Song currSong;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle;
    boolean repeat;

    EnableBottomLayout enableBottomLayout;
    private static final int NOTIFY_ID=1;



    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pos=0;
        isReady=false;
        shuffle=false;
        repeat=false;
        mp = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        mp.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){

        songsList=theSongs;
        currSong=songsList.get(0);
        try{
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong.getId());
            mp.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

    }


    public void initEnableBottomLayout(EnableBottomLayout enableBottomLayout){
        this.enableBottomLayout=enableBottomLayout;

    }

    public void setEnableBottomLayout(EnableBottomLayout enableBottomLayout){
        this.enableBottomLayout=enableBottomLayout;
        this.enableBottomLayout.enablePlayText(currSong);
        this.enableBottomLayout.enableSeekBar(mp);

    }


    public void  playSong(){
/*        if (mp.isPlaying())
            mp.stop();*/

        mp.reset();
      //  if (isPng())
        //    mp.reset();
        Song playSong = songsList.get(pos);
        currSong=playSong;
        try{
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong.getId());
            mp.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        try {
            mp.prepareAsync();

        }catch (Exception e){
            Log.e("MUSIC SERVICE", "Preparing Exception", e);

        }
    }


    public void setSong(int songIndex){
        pos=songIndex;
    }


/*

    public void playMusic(Song song){
        currSong=song;
        if (mp.isPlaying())
            mp.stop();


       // mp=MediaPlayer.create(getApplicationContext(), Uri.parse(currSong.getPath()));
        try{
            mp.setDataSource(getApplicationContext(), Uri.parse(currSong.getPath()));
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        //mp.setOnPreparedListener(this);
        mp.prepareAsync();
       // mp.start();

        //mp.setOnCompletionListener(this);

        enableBottomLayout.enablePlayText(currSong);
        enableBottomLayout.enableSeekBar(mp);

    }


*/

    public void resumeMusic(){
        if (mp!=null)
            mp.start();

        enableBottomLayout.enablePlayText(currSong);
        enableBottomLayout.enableSeekBar(mp);
    }



    public void stopMusic(){
        if (mp!=null)
            mp.stop();
    }

    public void pauseMusic(){
        if (mp!=null && mp.isPlaying()){
            mp.pause();
        }
    }


    public int getPosn(){
       // if (mp.getAudioSessionId()!=0)
         //   if (isReady)
             return mp.getCurrentPosition();
           // else return 0;
        //return 0;
    }

    public int getDur(){
                return mp.getDuration();
    }

    public boolean isPng(){
        return mp.isPlaying();
    }

    public void pausePlayer(){
        mp.pause();
    }

    public void seek(int posn){
        mp.seekTo(posn);
    }

    public Song getCurrSong() {
        return currSong;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void go(){
        mp.start();
    }


    public void playPrev(){

        pos--;
        if(pos==-1) pos=songsList.size()-1;
        playSong();
    }

    public void playNext(){
        if (repeat){

        }
        else if (!shuffle) {
            pos++;
            if (pos == songsList.size()) pos = 0;
        }
        else {

            Random random = new Random();
            pos=random.nextInt(songsList.size()-1);

        }
        playSong();

    }


    public void play(){
        shuffle=false;
        playNext();
    }

    public void shuffle(){
        shuffle=true;
        playNext();
    }

    public void setShuffle(){
        if (shuffle) {
            shuffle=false;
        }

        else{
            shuffle=true;
        }
    }

    public void setRepeat(){
        if (repeat) {
            repeat=false;
        }

        else{
            repeat=true;
        }
    }

    public boolean getShuffle(){
        return shuffle;
    }

    public boolean getRepeat(){
        return repeat;
    }








    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mp.stop();
        mp.release();
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

            //playMusic(songsList.get(pos));
            playNext();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {


        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);




        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = getString(R.string.app_name);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(channelId);
        notificationChannel.setSound(null, null);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Notification.Builder builder = new Notification.Builder(this,channelId)
                .setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setTicker(this.songsList.get(pos).getTitle())
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(this.songsList.get(pos).getTitle());
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);

        enableBottomLayout.enablePlayText(currSong);
        enableBottomLayout.enableSeekBar(mp);

        //isReady=true;

    }





    public interface EnableBottomLayout{

        public void enableSeekBar(MediaPlayer mp);
        public void enablePlayText(Song currSong);

    }




}
