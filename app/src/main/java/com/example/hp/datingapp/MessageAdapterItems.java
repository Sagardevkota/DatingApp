package com.example.hp.datingapp;

import com.google.firebase.database.DataSnapshot;

public class MessageAdapterItems {
    public  String from;

    public   String chatMessage;
    public Boolean seen;
    public Long time;
    public String text;
    //for news details
    MessageAdapterItems(String from, String chatMessage,Boolean seen, Long time,String text)
    {

        this. from=from;
        this. chatMessage=chatMessage;
        this.seen=seen;
        this.time=time;
        this.text=text;

    }
}
