package com.django.mod.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.django.mod.Fragments.FriendsFragment;
import com.django.mod.Fragments.MusicFragment;
import com.django.mod.Fragments.StatusFragment;
import com.django.mod.Handler.MusicController;
import com.django.mod.Model.Song;
import com.django.mod.R;
import com.django.mod.Services.MusicService;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements  MusicFragment.OnFragmentInteractionListener,
                    StatusFragment.OnFragmentInteractionListener,
                    FriendsFragment.OnFragmentInteractionListener,
                    MusicService.EnableBottomLayout
                    //,MediaController.MediaPlayerControl

{


    MusicFragment musicFragment;
    TabItem musicTab;
    TabItem statusTab;
    TabItem friendsTab;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    SeekBar seekBar;
    View dummyView;
    SearchView searchView;
    //MediaPlayer mp;
    TextView playingSong;
    ImageView menuBtn;
    NavigationView navView;
    ImageView pauseBelow;
    ImageView playBelow;
    ImageView closeBtn;
    DrawerLayout drawer;
    TextView navUsername;
    TextView navEmail;
    //Song currSong;
    RelativeLayout bottomRelLayout;
   //MusicHandler musicHandler;
    public static MusicService musicSrv;
    private MusicController controller;
    private Handler mHandler;
    private Runnable runnable;

    Toolbar toolbar;
    private StatusFragment statusFragment;
    private FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // searchView=findViewById(R.id.search_btn);
        musicTab=findViewById(R.id.tab_music);

        statusTab=findViewById(R.id.tab_status);
        friendsTab=findViewById(R.id.tab_friends);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        seekBar=findViewById(R.id.seekBar);
        playingSong=findViewById(R.id.play_song_text);
        pauseBelow=findViewById(R.id.pause_btn_below);
        closeBtn=findViewById(R.id.close_btn);
        playBelow=findViewById(R.id.play_btn_below);
        bottomRelLayout=findViewById(R.id.bottom_dialog);
        dummyView=findViewById(R.id.dummy_view);
        navView=findViewById(R.id.nav_view);
        drawer=findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        navEmail=findViewById(R.id.nav_email);
        navUsername=findViewById(R.id.nav_username);

        mHandler=new Handler();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        musicFragment=new MusicFragment();
        statusFragment=new StatusFragment();
        friendsFragment=new FriendsFragment();
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return musicFragment;
                    case 1:
                        return statusFragment;
                    case 2:
                        return friendsFragment;



                }
                return null;
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        };

        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



       setNavigationDrawer();

        playBelow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBelow.setVisibility(View.INVISIBLE);
                pauseBelow.setVisibility(View.VISIBLE);
                //musicHandler.resumeMusic();
                musicSrv.resumeMusic();

            }
        });

        pauseBelow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //musicHandler.pauseMusic();
                    musicSrv.pauseMusic();
                    playBelow.setVisibility(View.VISIBLE);
                    pauseBelow.setVisibility(View.INVISIBLE);

            }

        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.stopMusic();
                musicSrv.stopForeground(true);
                musicSrv.stopSelf();
                bottomRelLayout.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                dummyView.setVisibility(View.GONE);
            }
        });




        bottomRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MediaPlayerActivity.class);
             //   intent.putExtra("musicService",musicSrv);
                startActivityForResult(intent,10);
            }
        });





        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    public void setNavigationDrawer() {


        View headerView=navView.getHeaderView(0);
        final TextView navunText=headerView.findViewById(R.id.nav_username);
        final TextView navemailText=headerView.findViewById(R.id.nav_email);
        Menu navMenu=navView.getMenu();
        final MenuItem signinItem=navMenu.findItem(R.id.signin_item);
        final MenuItem logoutItem=navMenu.findItem(R.id.logout_item);
        final MenuItem profileItem=navMenu.findItem(R.id.profile_item);
        final MenuItem followerItem=navMenu.findItem(R.id.follower_item);
        final MenuItem followingItem=navMenu.findItem(R.id.following_item);



        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            signinItem.setVisible(false);
            logoutItem.setVisible(true);
            profileItem.setVisible(true);
            followerItem.setVisible(true);
            followingItem.setVisible(true);
            navunText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            navemailText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        else{
            signinItem.setVisible(true);
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
            followerItem.setVisible(false);
            followingItem.setVisible(false);
            navunText.setText("No User Available");
            navemailText.setText("");
        }




            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId=menuItem.getItemId();
                if (itemId== R.id.logout_item) {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Logout Successful",Toast.LENGTH_LONG).show();
                         navunText.setText("No User Available");
                         navemailText.setText("");
                         signinItem.setVisible(true);
                         logoutItem.setVisible(false);
                        profileItem.setVisible(false);
                        followerItem.setVisible(false);
                        followingItem.setVisible(false);
                         statusFragment.chooseSigninOrStatusLayout();
                   // FragmentTransaction ft = getFragmentManager().beginTransaction();
                   // ft.detach(StatusFragment.class).attach(StatusFragment.this).commit();
                }

                else if (itemId==R.id.feedback_item){

                }

                else if (itemId==R.id.follower_item){
                    startActivity(new Intent(MainActivity.this,FollowersActivity.class));
                }
                else if (itemId==R.id.following_item){
                    startActivity(new Intent(MainActivity.this,FollowingActivity.class));

                }
                else if (itemId==R.id.signin_item){
                    startActivityForResult(new Intent(MainActivity.this,SignInActivity.class),1);

                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item=menu.findItem(R.id.search_btn);
        searchView=(SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                musicFragment.searchSongs(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){



            case R.id.play_btn:
                musicSrv.play();
                break;

            case R.id.shuffle_btn:
                musicSrv.shuffle();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void enableSeekBar (final MediaPlayer mpo) {

        seekBar.setVisibility(View.VISIBLE);
      //  if (mp!=null )
        if (musicFragment.musicBound && musicSrv.isPng())
            seekBar.setMax(musicSrv.getDur());

        mHandler.removeCallbacks(runnable);
        mHandler = new Handler();

//Make sure you update Seekbar on UI thread

        runnable=new Runnable() {
            @Override
            public void run() {
                if (musicFragment.musicBound  && musicSrv.isPng() && seekBar.isFocusable()) {
                    int mCurrentPosition = musicSrv.getPosn();
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 10);
            }
        };
        MainActivity.this.runOnUiThread(runnable);


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
                musicSrv.seek(seekBar.getProgress());
                seekBar.setFocusable(true);
                //seekBar.setEnabled(true);
            }
        });

    }


    @Override
    public void enablePlayText(Song currSong) {
        dummyView.setVisibility(View.VISIBLE);
        bottomRelLayout.setVisibility(View.VISIBLE);
        playingSong.setText(currSong.getCleantitle());
        if (musicSrv.isPng()) {
            playBelow.setVisibility(View.INVISIBLE);
            pauseBelow.setVisibility(View.VISIBLE);
        }
        else{
            playBelow.setVisibility(View.VISIBLE);
            pauseBelow.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10) {
            musicSrv.setEnableBottomLayout(this);
        }

        else if (requestCode==1){
            setNavigationDrawer();
            statusFragment.chooseSigninOrStatusLayout();
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }



}













