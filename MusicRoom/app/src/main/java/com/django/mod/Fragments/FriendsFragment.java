package com.django.mod.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.django.mod.Model.FindPeople;
import com.django.mod.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference userRef;
    View view;
    EditText searchEdit;
    Button searchBtn;
    RecyclerView recyclerViewPeeps;
    boolean added;
    FirebaseRecyclerAdapter<FindPeople, FindPeopleViewHolder>firebaseRecyclerAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        userRef=database.getReference().child("users");
        added=false;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_friends, container, false);
        user=mAuth.getCurrentUser();
        if (user!=null)
            setFriendsLayout();
        return view;
    }

    private void setFriendsLayout() {
        searchEdit=view.findViewById(R.id.search_edit);
        searchBtn=view.findViewById(R.id.search_btn);
        recyclerViewPeeps=view.findViewById(R.id.recycler_view_peeps);

        //recyclerViewPeeps.setHasFixedSize(true);
        recyclerViewPeeps.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchPeopleAndFriends("");

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText=searchEdit.getText().toString();
                //searchPeopleAndFriends(searchText);
                if (!added)
                    addRandomUsersData();
                else removeRandomUserData();
            }
        });

    }

    public void addRandomUsersData(){
        int i=3;
        while (i<100){
            HashMap userMap=new HashMap();
            userMap.put("username","Random"+i);
            userMap.put("email","random"+i+"@gmail.com");
            userRef.child("RandomID"+i).updateChildren(userMap);
            i+=1;
        }
        added=true;


    }

    public void removeRandomUserData(){
        int i=3;
        while (i<100){

            userRef.child("RandomID"+i).removeValue();
            i+=1;
        }
        added=false;


    }

    private void searchPeopleAndFriends(String searchText) {

        Query query;

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");

        if (!TextUtils.isEmpty(searchText)) {
            query=query.orderByChild("username").startAt(searchText);
        }
        /*       FirebaseRecyclerOptions<FindPeople> options =
                new FirebaseRecyclerOptions.Builder<FindPeople>()
                        .setQuery(query, new SnapshotParser<FindPeople>() {
                            @NonNull
                            @Override
                            public FindPeople parseSnapshot(@NonNull DataSnapshot snapshot) {
                                String email=snapshot.child("email").getValue().toString();
                                String  name=snapshot.child("username").getValue().toString();

                                return new FindPeople(snapshot.child("email").getValue().toString(),
                                        snapshot.child("username").getValue().toString());
                            }
                        })
                        .build();
*/


        FirebaseRecyclerOptions<FindPeople> options =
                new FirebaseRecyclerOptions.Builder<FindPeople>()
                        .setQuery(query, FindPeople.class)
                        .build();



                firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindPeople, FindPeopleViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull FindPeopleViewHolder findPeopleViewHolder, int i, @NonNull FindPeople findPeople) {

                        findPeopleViewHolder.setView(findPeople.getEmail(),findPeople.getUsername());
                    }

                    @NonNull
                    @Override
                    public FindPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.people_list_item, parent, false);

                        return new FindPeopleViewHolder(view);                    }
                };
            recyclerViewPeeps.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();


    }



    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView userNameText;
        public FindPeopleViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            userNameText=mView.findViewById(R.id.user_name_text);
        }

        public void setView(String email, String username) {
            userNameText.setText(username);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (user!=null)
            firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (user!=null)
            firebaseRecyclerAdapter.stopListening();
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
