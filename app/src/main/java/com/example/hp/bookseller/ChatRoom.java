package com.example.hp.bookseller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoom extends AppCompatActivity
{
    private RecyclerView recyclerView;
    ArrayList<UserBean> userArrayList;
    LinearLayoutManager linearLayoutManager;
    ChatUserListAdapter chatUserListAdapter;

    // firebase Database
    private DatabaseReference mUsersTable;
    private DatabaseReference mChatsTable;

    private FirebaseAuth mAuth;

    int  arraylistPosition=0;



    ArrayList<String> chatList;


    static Dialog dialog;

    public static Dialog initDialog(Context context)
    {
        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setCancelable(false);
        dialog.show();
        LottieAnimationView animationView = (LottieAnimationView)dialog.findViewById(R.id.animation_view);
        animationView.setAnimation("pencil_write.json");
        animationView.loop(true);
        animationView.setProgress(0.5f);
        animationView.playAnimation();
        return dialog;
    }

    public static void dismissDialog(){
        dialog.dismiss();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    void init()
    {
        recyclerView = (RecyclerView)findViewById(R.id.chat_room_recycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        mUsersTable = FirebaseDatabase.getInstance().getReference().child(BookSellerUtil.JSON_USER);
        mChatsTable =FirebaseDatabase.getInstance().getReference().child(BookSellerUtil.JSON_CHAT);
        mAuth = FirebaseAuth.getInstance();

        recyclerView.addOnItemTouchListener( new ViewItemClick(getApplicationContext(), new ViewItemClick.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                arraylistPosition=position;
                UserBean userBean = userArrayList.get(arraylistPosition);

                Intent i = new Intent(ChatRoom.this,Chat.class);
                i.putExtra("reciver",userBean);
                Log.i("Reciver Info ",userBean.toString());
                startActivity(i);
            }
        }));




//        chatList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room );



        init();
        retrieveChatList();
        retrieveDataFromFirebase();
    }


    void retrieveChatList()
    {
        initDialog(ChatRoom.this);
        mUsersTable.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
//                HashMap<Integer,String> temp;

//                temp=(HashMap<Integer, String>)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("chats").getValue();


                try
                {
                    chatList =(ArrayList<String>)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("chats").getValue();
                }

                catch(Exception e)
                {

                    chatList=new ArrayList<String>();
                }



                Log.d("HCHATLIST",String.valueOf(chatList));

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                dismissDialog();

            }
        });
    }


    void retrieveDataFromFirebase ()
    {
        mUsersTable.addListenerForSingleValueEvent(new ValueEventListener()
        {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
//            Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
            userArrayList= new ArrayList<UserBean>();
//            Log.d("Data snapshot",String.valueOf(dataSnapshot));

            for(DataSnapshot snapshot : dataSnapshot.getChildren())
            {
                try
                {

                    UserBean user = snapshot.getValue(UserBean.class);

                    if (!TextUtils.equals(user.getUid(), String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())))
                    {
                        Log.d("If 1 ", user.toString());
                        Log.d("User Info", user.toString());
                        if (chatList.contains(user.getEmail()))
                        {
                            Log.d("If 2 ", user.toString());
                            userArrayList.add(user);
                        }
                    }
                    dismissDialog();


                }

                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("Exception  ", String.valueOf(e));
                    dismissDialog();
                }

            }
//            while (dataSnapshots.hasNext())
//            {
//                DataSnapshot dataSnapshotChild = dataSnapshots.next();
//                UserBean user = dataSnapshotChild.getValue(UserBean.class);
//                  Log.d("User Info",user.toString());
//                if (!TextUtils.equals(user.getUid(), String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())))
//                {
//                    Log.d("If 1 ",user.toString());
//                    if(chatList.contains(user.getEmail()))
//                    {
//                        Log.d("If 2 ",user.toString());
//                        userArrayList.add(user);
//                    }
//                    Log.d("User Info",user.toString());
//                }
//
//            }
            chatUserListAdapter = new ChatUserListAdapter(ChatRoom.this, R.layout.chat_room, userArrayList);
            recyclerView.setAdapter(chatUserListAdapter);
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {
            dismissDialog();

        }
    });

    }



}

