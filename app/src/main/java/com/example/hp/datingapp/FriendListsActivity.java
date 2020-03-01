package com.example.hp.datingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FriendListsActivity extends AppCompatActivity {
    ListView LVNews;
    MyCustomAdapter myadapter;
    TextView numOfFrndReq;
    SharedPreferences sp;
    ArrayList<FriendListsAdapterItems> listnewsData = new ArrayList<FriendListsAdapterItems>();
    ListView lvlist;
    Button btnUnlike;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_lists);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);

        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        String currentUID=mAuth.getUid();

         lvlist = (ListView) findViewById(R.id.LVNews);
        myadapter = new MyCustomAdapter(listnewsData);
        lvlist.setAdapter(myadapter);
        String url = "http://10.0.2.2/PartnerFinderWebServices/FriendLists.php?UserOne=" +sp.getString("userID","");
        new MyAsyncTaskgetNews().execute(url);

    }
    @Override
    public void onBackPressed() {

        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }
    private class MyCustomAdapter extends BaseAdapter {


        public ArrayList<FriendListsAdapterItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<FriendListsAdapterItems>  listnewsDataAdpater) {
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
            final View myView = mInflater.inflate(R.layout.layout_friends_lists, null);

            final FriendListsAdapterItems s = listnewsDataAdpater.get(position);
            TextView tvName=( TextView)myView.findViewById(R.id.tvMessage);
            tvName.setText(s.tvName);


            ImageView ivImg=(ImageView)myView.findViewById(R.id.ivImg);
            Button btnChat=(Button)myView.findViewById(R.id.btnChat);
            Button btnUnlike=myView.findViewById(R.id.btnUnlike);
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),s.tvName,Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                    finish();
                    String currentUID=mAuth.getCurrentUser().getUid();



                    intent.putExtra("user_name",s.tvName);
                    intent.putExtra("user_image",s.picture_path);
                    startActivity(intent);

                }
            });
            btnUnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://10.0.2.2/PartnerFinderWebServices/RejectRequest.php?UserOne=" + sp.getString("userID","") + "&UserTwo=" + s.user_id;
                    new MyAsyncTaskgetNews().execute(url);
                    Toast.makeText(getApplicationContext(), "Unliked", Toast.LENGTH_LONG).show();

                    listnewsData.remove(position);
                    myadapter.notifyDataSetChanged();
                }
            });
            ivImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),OthersProfileActivity.class);
                    intent.putExtra("UserID",s.user_id);
                    startActivity(intent);

                }
            });








            try{
                URL url=new URL(s.picture_path);
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

            return myView;
        }





    }
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    NewsData = ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            }catch (Exception ex){
                Log.d("error","eo");
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try {



                //display response data
                JSONArray userInfo = new JSONArray(progress[0]);
                for (int i = 0; i < userInfo.length(); i++) {
                    JSONObject userCredentials = userInfo.getJSONObject(i);
String full_name=userCredentials.getString("full_name");

                    listnewsData.add(new FriendListsAdapterItems(userCredentials.getString("full_name"),userCredentials.getString("picture_path"),userCredentials.getString("id")));



                }


                myadapter.notifyDataSetChanged();
                //display response data

            }




            catch (Exception ex) {
                Log.d("error is", ex.getMessage());
            }

        }


        protected void onPostExecute(String  result2){


        }




    }

    // this method convert any stream to string
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {

                linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }
}
