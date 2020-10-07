package com.django.mod.Fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.django.mod.Activities.MainActivity;
import com.django.mod.Adapters.MusicListAdapter;
import com.django.mod.Model.Song;
import com.django.mod.R;
import com.django.mod.Services.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements MusicListAdapter.OnSongClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public ArrayList<Song> musicList;
    View view;
    Cursor cursor;
    MusicListAdapter musicListAdapter;
    //public MediaPlayer mp;
    MainActivity mainActivity;
    //MusicHandler musicHandler;





    private Intent playIntent;
   public boolean musicBound;









    public MusicFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //musicHandler=(MusicHandler) getArguments().getSerializable("musicHandler");
        }
        boolean perm = mediaPermissions();
        if (perm) {
            musicList = new ArrayList<>();
          //  initArrayListRandom();
            mainActivity=(MainActivity)getActivity();
            //musicHandler=mainActivity.getMusicHandler();
            getAllAudioFromDevice(mainActivity);

        }

    }




    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            MainActivity.musicSrv = binder.getService();
            //pass list
          //  Toast.makeText(getContext(),""+MainActivity.musicSrv,Toast.LENGTH_LONG).show();
            MainActivity.musicSrv.setList(musicList);
             MainActivity.musicSrv.initEnableBottomLayout(mainActivity);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null) {
            playIntent = new Intent(mainActivity, MusicService.class);
            this.mainActivity.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //musicBound=true;
            this.mainActivity.startService(playIntent);
        }
    }







    private void initArrayListRandom() {
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
        musicList.add(new Song(1,"Title1","","",""));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music, container, false);
        initRecyclerView();



       // mainActivity.getMusicHandler().init(musicList);
        return view;

    }


    public void searchSongs(String newText){
        musicListAdapter.getFilter().filter(newText);

    }

    public List<Song> getAllAudioFromDevice(final Context context) {

        //final List<String> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns._ID, MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,};
         cursor = context.getContentResolver().query(uri, projection,null,null, MediaStore.Audio.AudioColumns.DATA+" ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Song song=new Song(id,title,album,artist,path);

                musicList.add(song);
              //  tempAudioList.add(name);
            }
            //c.close();
        }

        Collections.sort(musicList);

        return musicList;
    }



    public boolean mediaPermissions(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }
        }
        else{
            return true;
        }
        return false;
    }



    public void initRecyclerView(){
        RecyclerView rcv=view.findViewById(R.id.recycler_view);
        musicListAdapter=new MusicListAdapter(musicList,getActivity(),this);
        rcv.setAdapter(musicListAdapter);
        rcv.setLayoutManager(new LinearLayoutManager(getActivity()));

    }




    @Override
    public void onDestroy() {

        super.onDestroy();
        cursor.close();
        mainActivity.unbindService(musicConnection);
        mainActivity.stopService(playIntent);
        musicBound=false;
        MainActivity.musicSrv=null;
    }

    @Override
    public void onStop() {
      //  mainActivity.stopService(playIntent);
      //  mainActivity.musicSrv=null;
        super.onStop();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSongClick(int position) {
        // mainActivity.getMusicHandler().startPlay(position);
     //   Toast.makeText(getContext(),""+position,Toast.LENGTH_LONG).show();
        MainActivity.musicSrv.setSong(position);
        MainActivity.musicSrv.playSong();


     //   mainActivity.musicSrv.startPlay(position);


    }













    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
