package com.example.hp.datingapp;

import java.util.List;

public class AdapterItems
{

    private List<Messages> mMessageList;
    public  String tvName;
    public  int tvAge;
    public String tvCity;
    public   String picture_path;
    public String user_id;
    //for news details
    AdapterItems( String tvName,int tvAge,String tvCity , String picture_path,String user_id )
    {

        this. tvName=tvName;
        this. tvAge=tvAge;
        this.tvCity=tvCity;
        this. picture_path=picture_path;
        this.user_id=user_id;

    }

    public AdapterItems(Messages message) {
        this.mMessageList = mMessageList;
    }
}