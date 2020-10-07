package com.django.mod.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.django.mod.Model.Song;
import com.django.mod.R;
import com.django.mod.Services.MusicService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.django.mod.Activities.MainActivity.musicSrv;

public class MediaPlayerActivity extends AppCompatActivity implements MusicService.EnableBottomLayout{

    ImageView pauseBtn;
    ImageView playNextBtn;
    ImageView playBtn;
    ImageView playPrevBtn;
    TextView songTitle;
    TextView songArtist;
    SeekBar seekBar;
    TextView startTime;
    TextView endTime;
    TextView repeatText;
    TextView shuffleText;
    Handler mHandler;
    Runnable runnable;
    Handler mHandler2;
    Runnable runnable2;

    Button updateBtn;
    RelativeLayout postRelLayout;
    View blurView;
    View whiteView;
    EditText editText;
    Button sendBtn;
    Button shareBtn;
    ImageView shuffleBtn;
    TextView unameText;
    ImageView repeatBtn;
    TextView songEditText;
    SwitchCompat shareSwitch;

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference postRef;
    StorageReference storageReference;

    private boolean updatingFlag;
    String audioUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);

        pauseBtn=findViewById(R.id.pause_btn_large);
        playNextBtn=findViewById(R.id.next_btn_large);
        playBtn=findViewById(R.id.play_btn_large);
        playPrevBtn=findViewById(R.id.prev_btn_large);
        songTitle=findViewById(R.id.song_title);
        songArtist=findViewById(R.id.song_artist);
        seekBar=findViewById(R.id.seekBar2);
        startTime=findViewById(R.id.start_time);
        endTime=findViewById(R.id.end_time);
        updateBtn=findViewById(R.id.update_btn);
        postRelLayout=findViewById(R.id.med_post_editlayout);
        blurView=findViewById(R.id.trans_view);
        editText=findViewById(R.id.med_edit_text);
        sendBtn=findViewById(R.id.med_post_btn);
        repeatText=findViewById(R.id.repeat_text);
        repeatBtn=findViewById(R.id.repeat_btn_large);
        shuffleText=findViewById(R.id.shuffle_text);
        shareBtn=findViewById(R.id.med_share_btn);
        unameText=findViewById(R.id.med_username_text);
        songEditText=findViewById(R.id.med_songname_text);
        whiteView=findViewById(R.id.view_white);
        shareSwitch=findViewById(R.id.share_switch);
        shuffleBtn=findViewById(R.id.shuffle_btn_large);
        songTitle.setText(MainActivity.musicSrv.getCurrSong().getCleantitle());
        songArtist.setText(MainActivity.musicSrv.getCurrSong().getArtist());

        updatingFlag=false;
        audioUrl=null;

        mHandler=new Handler();
        mHandler2=new Handler();

        storageReference= FirebaseStorage.getInstance().getReference().child("shared_songs");

        MainActivity.musicSrv.setEnableBottomLayout(this);



        playPauseBtnVISIVILITY();


        //setTimeofSong();



        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.musicSrv.pauseMusic();
                playBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.INVISIBLE);

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mHandler.removeCallbacks(runnable);
                //mHandler2.removeCallbacks(runnable2);

                MainActivity.musicSrv.resumeMusic();
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
            }
        });


        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.setShuffle();
                boolean shuff=musicSrv.getShuffle();
                if (shuff)shuffleText.setText("Shuffle On");
                else shuffleText.setText("Shuffle Off");
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.setRepeat();
                boolean repeat=musicSrv.getRepeat();
                if (repeat)repeatText.setText("Repeat On");
                else repeatText.setText("Repeat Off");
            }
        });

        playPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mHandler.removeCallbacks(runnable);
                //mHandler2.removeCallbacks(runnable2);

                MainActivity.musicSrv.playPrev();
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
                songTitle.setText(MainActivity.musicSrv.getCurrSong().getCleantitle());
                songArtist.setText(MainActivity.musicSrv.getCurrSong().getArtist());

            }
        });

        playNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mHandler.removeCallbacks(runnable);
               // mHandler2.removeCallbacks(runnable2);

                MainActivity.musicSrv.playNext();
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
                songTitle.setText(MainActivity.musicSrv.getCurrSong().getCleantitle());
                songArtist.setText(MainActivity.musicSrv.getCurrSong().getArtist());
                Toast.makeText(getApplicationContext(),"Next",Toast.LENGTH_LONG).show();
            }
        });




        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();
        if (user!=null){
            postRef=database.getReference().child("AllPosts");

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatingFlag=true;
                    setPostEditLayout();
                }
            });
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shareSwitch.isChecked()) {
                    Uri file = Uri.fromFile(new File(musicSrv.getCurrSong().getPath()));

// Create the file metadata
/*               StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("audio/mpeg")
                        .build();*/

                    audioUrl = null;
// Upload file and metadata to the path 'audio/audio.mp3'
                    UploadTask uploadTask = storageReference.child("audio/" + file.getLastPathSegment()).putFile(file);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    audioUrl = uri.toString();
                                    //createNewPost(imageUrl);
                                    Log.d("URLD", "onSuccess: " + audioUrl);
                                    // Toast.makeText(getApplicationContext(),audioUrl,Toast.LENGTH_SHORT).show();
                                    updatePostToFirebase(audioUrl);

                                }
                            });
                        }
                    });
                    unsetPostEditLayout();
                }

                else {
                    updatePostToFirebase(null);
                }
            }
            });


    }


    private void updatePostToFirebase(String audioUrl){
        String currTime=Calendar.getInstance().getTime().toString();
        HashMap postMap=new HashMap();
        postMap.put("id",user.getUid());
        postMap.put("username",user.getDisplayName());
        postMap.put("dateTime", currTime);
        postMap.put("songName",songTitle.getText().toString());
        postMap.put("statusText",this.editText.getText().toString());
        postMap.put("audioUrl",audioUrl);
        postRef.child(user.getUid()+currTime).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (updatingFlag){
                    unsetPostEditLayout();
                    updatingFlag=false;
                }
                Toast.makeText(getApplicationContext(),"Status Updated!",Toast.LENGTH_LONG).show();

            }
        });


    }

    private void setPostEditLayout() {
        blurView.setVisibility(View.VISIBLE);
        whiteView.setVisibility(View.VISIBLE);

        postRelLayout.setVisibility(View.VISIBLE);
        unameText.setText(user.getDisplayName());
        songEditText.setText(" - "+songTitle.getText().toString());
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    }

    public void playPauseBtnVISIVILITY(){
        if (MainActivity.musicSrv.isPng()){
            playBtn.setVisibility(View.INVISIBLE);
        }
        else{
            pauseBtn.setVisibility(View.INVISIBLE);
        }
    }







    @Override
    public void enableSeekBar(final MediaPlayer mp) {

        if ( MainActivity.musicSrv.isPng())
            seekBar.setMax(MainActivity.musicSrv.getDur());

            // seekBar.setMax(10);
        mHandler.removeCallbacks(runnable);
        mHandler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                if ( MainActivity.musicSrv.isPng() && seekBar.isFocusable()) {
                    int mCurrentPosition = MainActivity.musicSrv.getPosn();
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 10);
            }
        };

        MediaPlayerActivity.this.runOnUiThread(runnable);
//Make sure you update Seekbar on UI thread
/*        MediaPlayerActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if ( MainActivity.musicSrv.isPng() && seekBar.isFocusable()) {
                    int mCurrentPosition = MainActivity.musicSrv.getPosn();
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 10);
            }
        });*/



            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    //Update the progress depending on seek bar
                    if(fromUser){
                        seekBar.setFocusable(false);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //mp.seekTo(seekBar.getProgress());
                    seekBar.setFocusable(false);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.musicSrv.seek(seekBar.getProgress());
                    seekBar.setFocusable(true);
                    //seekBar.setEnabled(true);
                }
            });

        }


    @Override
    public void enablePlayText(Song currSong) {
        songTitle.setText(
                MainActivity.musicSrv.getCurrSong().getCleantitle());
        songArtist.setText(MainActivity.musicSrv.getCurrSong().getArtist());
        setTimeofSong();
    }


    public void setTimeofSong(){
        int duration=0;
        if ( MainActivity.musicSrv.isPng())

            duration = MainActivity.musicSrv.getDur();

        String time = String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );


        endTime.setText(time);



        mHandler2.removeCallbacks(runnable2);
        mHandler2 = new Handler();
        runnable2=new Runnable() {
            @Override
            public void run() {
                if ( MainActivity.musicSrv.isPng() && seekBar.isFocusable()) {
                    int pos= MainActivity.musicSrv.getPosn();
                    final String sttime = String.format("%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(pos),
                            TimeUnit.MILLISECONDS.toSeconds(pos)-
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                                            pos))
                    );
                    startTime.setText(sttime);
                }
                mHandler2.postDelayed(this, 100);
            }
        };
        MediaPlayerActivity.this.runOnUiThread(runnable2);





     /*   new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int pos= MainActivity.musicSrv.getPosn();
                final String sttime = String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(pos),
                        TimeUnit.MILLISECONDS.toSeconds(pos)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                                        pos))
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startTime.setText(sttime);

                    }
                });
            }
        },0,100);*/

    }

    @Override
    public void onBackPressed() {
        if (updatingFlag){
            unsetPostEditLayout();
            updatingFlag=false;
        }
        else {
            super.onBackPressed();
            Intent intent = new Intent(MediaPlayerActivity.this, MainActivity.class);
            setResult(1, intent);
            finish();
        }
    }

    private void unsetPostEditLayout() {
        blurView.setVisibility(View.GONE);
        whiteView.setVisibility(View.GONE);

        postRelLayout.setVisibility(View.GONE);
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler2.removeCallbacks(runnable2);

    }
}
