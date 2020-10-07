package com.django.mod.Handler;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.django.mod.Model.Song;
import com.django.mod.Services.MusicService;

import java.io.Serializable;
import java.util.ArrayList;

public class MusicHandler implements Serializable {

    Song currSong;
    ArrayList<Song> songsQueue;
    int currPos;
    boolean shuffle;
    MediaPlayer mediaPlayer;
    Context context;
    EnableBottomLayout enableBottomLayout;





    public MusicHandler (Context context,EnableBottomLayout enableBottomLayout){
        this.context=context;
        this.enableBottomLayout=enableBottomLayout;
        //this.musicActivity=(Activity)context;
        //this.musicActivity= (Activity) MainActivity;
        //mediaPlayer=MediaPlayer.create(context, Uri.parse(currSong.getPath()));

    }

    public void init(final ArrayList<Song> songsQueue){
       this.songsQueue=songsQueue;
        shuffle=false;

        this.currSong=songsQueue.get(0);
        this.currPos=0;
       mediaPlayer=MediaPlayer.create(context, Uri.parse(currSong.getPath()));



    }


    public void playMusic(Song song){
        Intent intent=new Intent(context, MusicService.class);
//        intent.putExtra();
        context.startService(intent);
        currSong=song;
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

            mediaPlayer=MediaPlayer.create(context, Uri.parse(currSong.getPath()));


        mediaPlayer.start();
        oncompleteListener();
        enableBottomLayout.enablePlayText(currSong);
        enableBottomLayout.enableSeekBar(mediaPlayer);

    }

    public void oncompleteListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!shuffle)
                {

                    currPos+=1;
                    playMusic(songsQueue.get(currPos));
                }
            }
        });
    }

    public void resumeMusic(){
        if (mediaPlayer!=null)
            mediaPlayer.start();
        oncompleteListener();
        enableBottomLayout.enablePlayText(currSong);
        enableBottomLayout.enableSeekBar(mediaPlayer);
    }

    public void pauseMusic(){
        if (mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public Song getCurrSong(){
        return  currSong;
    }

    public void startPlay(int pos){
        shuffle=false;
        currPos=pos;
        playMusic(songsQueue.get(currPos));

    }

    public void startShufflePlay(){

    }




    public interface EnableBottomLayout{

        public void enableSeekBar(MediaPlayer mp);
        public void enablePlayText(Song currSong);

    }


}
