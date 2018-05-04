package com.example.hp.bookseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.bookseller.fcm.FcmNotificationBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class Chat extends AppCompatActivity implements TextView.OnEditorActionListener
{
    private static final String TAG="Messages Sent";

    private RecyclerView mRecyclerViewChat;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ChatBean> chatBeanArrayList;

    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatAdapter mChatRecyclerAdapter;

    UserBean sender,reciver;


    private FirebaseAuth mAuth;

    private DatabaseReference mChat;
    private DatabaseReference mUser;

    SharedPreferences preferences;


    void init()
    {
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mRecyclerViewChat.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerViewChat.setLayoutManager(linearLayoutManager);
        mETxtMessage = (EditText)findViewById(R.id.edit_text_message);

        mProgressDialog = new ProgressDialog(getApplicationContext());
        mProgressDialog.setTitle("Loading....");
        mProgressDialog.setMessage("Pease wait....");
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mAuth = FirebaseAuth.getInstance();
        mChat = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseDatabase.getInstance().getReference();

        preferences = getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);
        sender = new UserBean();
        sender.setEmail(preferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""));
        sender.setToken(preferences.getString(BookSellerUtil.SHAREDPREFS_SENDERTOKEN,""));
        sender.setUid(mAuth.getCurrentUser().getUid());

    }


    void addUserToChatListSender()
    {
       mUser.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent
               (
               new ValueEventListener()
               {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot)
                   {
                       HashMap<Integer,String> temp ;
                       ArrayList<String> tempList;
                       if((ArrayList)dataSnapshot.child("chats").getValue()==null)
                       {
                             tempList= new ArrayList<String>();
                       }
                       else

                       {
                           tempList=(ArrayList)dataSnapshot.child("chats").getValue();
                       }

                       if(tempList!=null)
                       {
                           if (!tempList.contains(reciver.email))
                               tempList.add(reciver.getEmail());
                       }


                       mUser.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).child("chats").setValue(tempList);

                       Log.d("LIST SENDER",String.valueOf(tempList));
//                       Toast.makeText(getApplicationContext(),"LIST "+String.valueOf(tempList),Toast.LENGTH_LONG).show();

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {}
               }
       );
    }

    void addUserToChatListReciver()
    {
        mUser.child(BookSellerUtil.JSON_USER).child(reciver.getUid()).addListenerForSingleValueEvent
                (
                        new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {

                                ArrayList<String> tempList;
                                if((ArrayList)dataSnapshot.child("chats").getValue()==null)
                                {
                                    tempList= new ArrayList<String>();
                                }
                                else
                                {

                                    tempList= (ArrayList)dataSnapshot.child("chats").getValue();
                                }

                                if(tempList!=null)
                                {
                                    if (!tempList.contains(sender.email))
                                        tempList.add(sender.getEmail());
                                }

                                mChat.child(BookSellerUtil.JSON_USER).child(reciver.getUid()).child("chats").setValue(tempList);

                                Log.d("LIST RECIVER",String.valueOf(tempList));
//                                Toast.makeText(getApplicationContext(),"LIST RECIVER "+String.valueOf(tempList),Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        }
                );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        chatBeanArrayList = new ArrayList<>();

        Intent getIntent = getIntent();
        reciver =(UserBean)getIntent.getSerializableExtra("reciver");


        getMessageFromFirebaseUser(sender.getUid(),reciver.getUid());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    // for On Editor Text
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (actionId == EditorInfo.IME_ACTION_SEND)
        {
            sendMessage();
            return true;
        }
        return false;
    }


    ChatBean chat;

    private void sendMessage()
    {
        String message = mETxtMessage.getText().toString();
        String receiver = reciver.getEmail();
        String receiverUid = reciver.getUid();

        addUserToChatListSender();
        addUserToChatListReciver();


        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chat = new ChatBean(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                String.valueOf(System.currentTimeMillis()));

        storeMessageFirebase();

        mETxtMessage.setText("");
    }


    void storeMessageFirebase()
    {
        final String room_type_1 = chat.getSenderUid() + "_" + chat.getReceiverUid();
        final String room_type_2 = chat.getReceiverUid() + "_" + chat.getSenderUid();

        mChat.child(BookSellerUtil.JSON_CHAT).getRef().addListenerForSingleValueEvent((new ValueEventListener()
        {

            /*
            In this function data is added to firebase under the value name chats
            with in chats have object name room_type_1 or room_type_2 under the respective room
            there is another object store which has name timestamp at which message was sent and
            other this time stamp data is stored in form of cha bean
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(room_type_1))
                {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);


                }
                else if (dataSnapshot.hasChild(room_type_2))
                {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_2).child(String.valueOf(chat.getTimestamp())).setValue(chat);

                }
                else
                {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                    getMessageFromFirebaseUser(chat.getSenderUid(), chat.getReceiverUid());

                }

                // send push notification to the receiver
                sendPushNotificationToReceiver
                        (chat.sender,
                         chat.getMessage(),
                         chat.getSenderUid(),
                         sender.getToken(),
                         reciver.getToken());

                Toast.makeText(getApplicationContext(),"Notification Sent",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        }));
    }


    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken)
    {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    public void getMessageFromFirebaseUser(String senderUid, String receiverUid)
    {
        Toast.makeText(getApplicationContext(),"In Method getMessageFromFirebaseUser",Toast.LENGTH_LONG).show();
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(BookSellerUtil.JSON_CHAT).getRef().addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.hasChild(room_type_1))
                {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(BookSellerUtil.JSON_CHAT)
                            .child(room_type_1).addChildEventListener(new ChildEventListener()
                    {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            ChatBean chat = dataSnapshot.getValue(ChatBean.class);

                            Log.e(TAG,"getMessageFromFirebaseUserMethod "+String.valueOf(chat));
                            onGetMessagesSuccess(chat);

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot)
                        {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s)
                        {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
                        }
                    });
                }
                else if (dataSnapshot.hasChild(room_type_2))
                {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(BookSellerUtil.JSON_CHAT)
                            .child(room_type_2).addChildEventListener(new ChildEventListener()
                    {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            ChatBean chat = dataSnapshot.getValue(ChatBean.class);
                            Log.e(TAG,"getMessageFromFirebaseUserMethod "+String.valueOf(chat));
                            onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
                        }
                    });
                }
                else
                {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
            }
        });
    }


    public void onGetMessagesSuccess(ChatBean chat)
    {
        if (mChatRecyclerAdapter == null)
        {
            mChatRecyclerAdapter = new ChatAdapter(getApplicationContext(), R.layout.chat_room,chatBeanArrayList);
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);

        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }

}
