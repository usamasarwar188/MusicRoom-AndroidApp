package com.django.mod.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.django.mod.Model.Status;
import com.django.mod.R;

import java.io.IOException;
import java.util.ArrayList;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.viewHolder> {
    ArrayList<Status> statusArrayList;
    Context context;

    MediaPlayer mp;
    boolean mpStat;
    int prevPos;



    public StatusListAdapter(ArrayList<Status> statusArrayList, Context context) {
        this.statusArrayList = statusArrayList;
        this.context = context;
        mp=new MediaPlayer();
        mpStat=false;
        prevPos=-10;
    }



    @NonNull
    @Override
    public StatusListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.status_list_item,parent,false);
        viewHolder vh=new viewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull StatusListAdapter.viewHolder holder, int position) {
        holder.setView(position,mp);

    }

    @Override
    public int getItemCount() {
        return this.statusArrayList.size();
    }




    public class viewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView songName;
        TextView timeDate;
        TextView statusText;
        RelativeLayout statSharedSongLayout;
        ImageView playBtn;
        ImageView pauseBtn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.stat_username_text);
            songName=itemView.findViewById(R.id.stat_songname_text);
            timeDate=itemView.findViewById(R.id.stat_dateTime);
            statusText=itemView.findViewById(R.id.stat_status_text);
            statSharedSongLayout=itemView.findViewById(R.id.stat_sharedSongLayout);
            playBtn=itemView.findViewById(R.id.stat_play_btn);
            pauseBtn=itemView.findViewById(R.id.stat_pause_btn);
        }

        public void setView(int position, final MediaPlayer mp){
            songName.setText(statusArrayList.get(position).getSongName());
            userName.setText(statusArrayList.get(position).getUsername());
            timeDate.setText(statusArrayList.get(position).getDateTime());
            statusText.setText(statusArrayList.get(position).getStatusText());
            if (statusArrayList.get(position).getAudioUrl()==null){
                statSharedSongLayout.setVisibility(View.GONE);
            }



            else{
                statSharedSongLayout.setVisibility(View.VISIBLE);


                final MediaPlayer mpf=mp;
                final int pos=position;
                mpf.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });


                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playBtn.setVisibility(View.INVISIBLE);
                        pauseBtn.setVisibility(View.VISIBLE);
                        if (pos!=prevPos) {
                            try {

                                mpf.reset();
                                mpf.setDataSource(statusArrayList.get(pos).getAudioUrl());
                                if (prevPos>=0){
                                    setPlayPauseBtnPrev(prevPos);
                                }
                                prevPos=pos;
                                mpStat=false;

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if(!mpStat) {
                            mpf.prepareAsync();
                            mpStat=true;
                        }
                        else {
                            mpf.start();
                        }
                    }
                });

                pauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pauseBtn.setVisibility(View.INVISIBLE);
                        playBtn.setVisibility(View.VISIBLE);
                        mpf.pause();
                    }
                });



            }




        }

        public void setPlayPauseBtnPrev(int prevPos){

        }


    }





}
