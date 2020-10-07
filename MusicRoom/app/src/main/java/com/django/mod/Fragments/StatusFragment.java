package com.django.mod.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.django.mod.Activities.MainActivity;
import com.django.mod.Adapters.StatusListAdapter;
import com.django.mod.Model.Status;
import com.django.mod.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.SignInButtonImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RC_SIGN_IN = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private OnFragmentInteractionListener mListener;
    private GoogleSignInClient mGoogleSignInClient;
    MainActivity mainActivity;
    private SignInButtonImpl signInButton;
    private Button signoutBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference userRef;
    DatabaseReference postRef;
    RelativeLayout statusLayout;
    RelativeLayout signinLayout;
    private TextView statusText;
    ImageButton upBtn;
    RecyclerView rcv;


    public ArrayList<Status> statusArrayList;
    StatusListAdapter statusListAdapter;
    ChildEventListener postChildEventListener;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        }


        mainActivity=(MainActivity)getActivity();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(getActivity(),gso);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();


        database=FirebaseDatabase.getInstance();

        userRef=database.getReference().child("users");
        postRef=database.getReference().child("AllPosts");

           statusArrayList=new ArrayList<>();
           //addRandomStatus();
          //  initDatabaseListener();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_status, container, false);
        initStatusView();
        initSigninView();
        chooseSigninOrStatusLayout();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //chooseSigninOrStatusLayout();
    }

    public void chooseSigninOrStatusLayout(){
        user=FirebaseAuth.getInstance().getCurrentUser();

        if (user==null){
            setSignInLayout();
        }
        else{
            setStatusLayout();
            initDatabaseListener();
            initRecyclerView();

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //assert getFragmentManager() != null;
           // getFragmentManager().beginTransaction().detach(this).attach(this).commit();

//        chooseSigninOrStatusLayout();

        }
    }

    private void initDatabaseListener(){
        if (postChildEventListener==null){
            postChildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Status status=dataSnapshot.getValue(Status.class);
                    statusArrayList.add(status);
                    statusListAdapter.notifyItemChanged(statusArrayList.size()-1);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            postRef.addChildEventListener(postChildEventListener);
        }
    }

    private void addRandomStatus(){

        Date date=Calendar.getInstance().getTime();

        Status s=new Status("1",user.getDisplayName(),date.toString(),
                "VeRy GoOd SoNG :P"," - Atif Aslam - Aadat");
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);
        statusArrayList.add(s);

    }

    private void initStatusView() {
        statusLayout=view.findViewById(R.id.layout_status_rel);
        upBtn=view.findViewById(R.id.up_btn);
       // statusText=view.findViewById(R.id.status_text);
        // signoutBtn=view.findViewById(R.id.signout_btn);

    }
    private void initSigninView() {
        signinLayout=view.findViewById(R.id.layout_signin_rel);
        signInButton=view.findViewById(R.id.google_signin_btn);

    }


    public void setStatusLayout() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            statusLayout.setVisibility(View.VISIBLE);
            signinLayout.setVisibility(View.INVISIBLE);
            upBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (statusListAdapter.getItemCount() > 1) {
                        rcv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rcv.smoothScrollToPosition(0);
                            }
                        }, 10);
                    }
                }
            });
            //statusText.setText(user.getDisplayName());
        }
    }


    private void setSignInLayout() {
        statusLayout.setVisibility(View.INVISIBLE);
        signinLayout.setVisibility(View.VISIBLE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

    }


    public void initRecyclerView(){
        rcv=view.findViewById(R.id.stat_recycler_view);
        statusListAdapter=new StatusListAdapter(statusArrayList,getActivity());
        rcv.setAdapter(statusListAdapter);
        rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }






    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }


    private void signOut(){
        mAuth.signOut();
        setSignInLayout();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("GoogleSignIn", "Google sign in failed", e);
                // ...
            }
        }
    }


    private void updateUI(FirebaseUser user) {
        Toast.makeText(getContext(),user.getDisplayName(),Toast.LENGTH_LONG).show();
        chooseSigninOrStatusLayout();
        mainActivity.setNavigationDrawer();

    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SuccessSignin", "signInWithCredential:success");
                    user = mAuth.getCurrentUser();
                    updateUI(user);
                    saveInfotoFirebaseDatabase();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("FailedSignin", "signInWithCredential:failure", task.getException());
                    Toast.makeText(getContext(),"Sign in Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void saveInfotoFirebaseDatabase() {

        HashMap userMap=new HashMap();
        userMap.put("username",user.getDisplayName());
        userMap.put("email",user.getEmail());
        userRef.child(user.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            }
        });

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
