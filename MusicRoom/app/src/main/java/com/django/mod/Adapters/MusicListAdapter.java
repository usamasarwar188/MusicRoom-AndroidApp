package com.django.mod.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.django.mod.Model.Song;
import com.django.mod.R;

import java.util.ArrayList;
import java.util.Collection;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.viewHolder> implements Filterable {

    ArrayList<Song> songArrayList;
    ArrayList<Song> songArrayList2;
    Context context;
    OnSongClickListener onSongClickListener;

    public MusicListAdapter(ArrayList<Song> songArrayList, Context context, OnSongClickListener onSongClickListener) {
        this.songArrayList=songArrayList;
        this.songArrayList2 = new ArrayList<>(songArrayList);
        this.context = context;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item,parent,false);
        viewHolder vh=new viewHolder(view,onSongClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setView(position);
    }

    @Override
    public int getItemCount() {
        return this.songArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Song> filteredList=new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(songArrayList2);
            }

            else{
                for (Song song: songArrayList2){
                    if (song.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(song);
                    }
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songArrayList.clear();
            songArrayList.addAll((Collection<? extends Song>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnSongClickListener{

         void onSongClick(int position);
    }


    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView songName;
        TextView artistName;
        OnSongClickListener onSongClickListener;

        public viewHolder(@NonNull View itemView,OnSongClickListener onSongClickListener) {
            super(itemView);
            songName=itemView.findViewById(R.id.song_name);
            artistName=itemView.findViewById(R.id.artist_name);
            itemView.setOnClickListener(this);
            this.onSongClickListener=onSongClickListener;
        }

        public void setView(int position){
            songName.setText(songArrayList.get(position).getCleantitle());
            artistName.setText(songArrayList.get(position).getArtist());
        }

        @Override
        public void onClick(View view) {
            this.onSongClickListener.onSongClick(getAdapterPosition());
        }

    }










}
