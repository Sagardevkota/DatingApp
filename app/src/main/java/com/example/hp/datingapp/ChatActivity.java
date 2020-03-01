package com.example.hp.datingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.BaseAdapter;


import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    TextView displayTextMessages;
    ListView LVNews;
    ListView lvlist;
    ArrayList<MessageAdapterItems> listnewsData = new ArrayList<MessageAdapterItems>();
    MyCustomAdapter myadapter;
ImageView ivImg;

    EditText userMessageInput;
    Button SendMessageButton;
    ScrollView mScrollView;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    DatabaseReference rootRef, UserRef, MessageRef;
    String chatUserName, currentUserID, currentUserName, currentDate, currentTIme, ChatUserID,chatuserid,chatPicturePath;
public static String chatuser_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lvlist = (ListView) findViewById(R.id.LVNews);
        myadapter = new MyCustomAdapter(listnewsData);
        lvlist.setAdapter(myadapter);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);



        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        SendMessageButton = (Button) findViewById(R.id.send_message_button);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

ivImg=findViewById(R.id.ivImg);
        Bundle b = getIntent().getExtras();
        chatUserName = b.getString("user_name");
        chatuser_name=chatUserName;
        chatPicturePath=b.getString("user_image");
        TextView header_username=(TextView)findViewById(R.id.header_username);
        header_username.setText(chatUserName);
        ImageView header_userimage=(ImageView)findViewById(R.id.header_user_image) ;
        try{
            URL url=new URL(chatPicturePath);
            String protocol = url.getProtocol();
            String file = url.getFile();
            String url2 = protocol +"://"+ listusers.DynamicIp + file;

            Picasso.get()
                    .load(url2)
                    .fit()
                    .into(header_userimage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Load","Successfull");

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Load","Error");
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });}
        catch (MalformedURLException e){}


        UserRef.orderByChild("name").equalTo(chatUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
 chatuserid="";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatUserID = snapshot.getKey();
                    chatuserid=ChatUserID;
                    loadMessages(chatuserid);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }


        });




        rootRef.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create if not
                if (!dataSnapshot.hasChild(ChatUserID)) {

                    HashMap<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    HashMap<String, Object> chatUserMap = new HashMap<>();

                    chatUserMap.put("Chat/" + currentUserID + "/" + ChatUserID, chatAddMap);
                    chatUserMap.put("Chat/" + ChatUserID + "/" + currentUserID, chatAddMap);
                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null)
                                Log.d("chat_log", databaseError.getMessage().toString());
                        }
                    });
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    // TODO: 9/19/2019
    private class MyCustomAdapter extends BaseAdapter {


        public ArrayList<MessageAdapterItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<MessageAdapterItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView = mInflater.inflate(R.layout.layout_message_lists, null);

            final MessageAdapterItems s = listnewsDataAdpater.get(position);
            TextView tvMessage=myView.findViewById(R.id.tvMessage);

            ImageView ivImg=myView.findViewById(R.id.ivImg);

if (s.from.equals(currentUserID)) {
    tvMessage.setGravity(Gravity.RIGHT);
    tvMessage.setBackgroundResource(R.drawable.buttonstyle);
    tvMessage.setTextColor(Color.WHITE);
    ivImg.setImageBitmap(HomeActivity.myBitmap);
    tvMessage.setText(s.chatMessage);

}

else {
    tvMessage.setGravity(Gravity.LEFT);

    tvMessage.setBackgroundResource(R.drawable.buttonstyle2);
    tvMessage.setTextColor(Color.BLACK);
    tvMessage.setText(s.chatMessage);
    try{
        URL url=new URL(chatPicturePath);
        String protocol = url.getProtocol();
        String file = url.getFile();
        String url2 = protocol +"://"+ listusers.DynamicIp + file;

        Picasso.get()
                .load(url2)
                .fit()
                .into(ivImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Load","Successfull");

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Load","Error");
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });}
    catch (MalformedURLException e){}


}





myadapter.notifyDataSetChanged();




            return myView;
        }





    }


    private void sendMessage() {

        String message = userMessageInput.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + currentUserID + "/" + ChatUserID;
            String chat_user_ref = "messages/" + ChatUserID + "/" + currentUserID;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(currentUserID).child(ChatUserID).push();


            String push_id = user_message_push.getKey();

            HashMap<String, Object> messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserID);


            HashMap<String, Object> messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            userMessageInput.setText("");

            rootRef.child("Chat").child(currentUserID).child(ChatUserID).child("seen").setValue(true);
            rootRef.child("Chat").child(currentUserID).child(ChatUserID).child("timestamp").setValue(ServerValue.TIMESTAMP);

            rootRef.child("Chat").child(ChatUserID).child(currentUserID).child("seen").setValue(false);
            rootRef.child("Chat").child(ChatUserID).child(currentUserID).child("timestamp").setValue(ServerValue.TIMESTAMP);

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());


                    }

                }
            });

        }
    }

    @Override
    protected void onStart() {

        super.onStart();

    }


    private void loadMessages(String chatuserid) {
        MessageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUserID).child(chatuserid);

        MessageRef.addChildEventListener(new ChildEventListener() {
                                             @Override
                                             public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                 if (dataSnapshot.exists()) {
                                                     DisplayMessages(dataSnapshot, s);
                                                 } else
                                                     Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
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
                                         }
        );


    }

    private void DisplayMessages(DataSnapshot dataSnapshot, String s) {




            Messages message = dataSnapshot.getValue(Messages.class);
            String from=message.getFrom();
            String chatmessage=message.getMessage();
            Boolean seen=message.isSeen();
            Long time=message.getTime();
            String type=message.getType();

         listnewsData.add(new MessageAdapterItems(from,chatmessage,seen,time,type));
         myadapter.notifyDataSetChanged();








    }


}






