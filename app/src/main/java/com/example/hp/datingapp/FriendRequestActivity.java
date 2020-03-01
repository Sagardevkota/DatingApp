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

public class FriendRequestActivity extends AppCompatActivity {
    Button accBtn;
    Button rejBtn;
    SharedPreferences sp;
    ListView LVNews;
    MyCustomAdapter myadapter;

    TextView numOfFrndReq;
    ArrayList<FriendRequestsAdapterItems> listnewsData = new ArrayList<FriendRequestsAdapterItems>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);


        ListView lvlist = (ListView) findViewById(R.id.LVNews);
        myadapter = new MyCustomAdapter(listnewsData);
        lvlist.setAdapter(myadapter);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = "http://10.0.2.2/PartnerFinderWebServices/RequestLists.php?UserOne="+ sp.getString("userID","") ;
        new MyAsyncTaskgetNews().execute(url);
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

startActivity(intent);

    }
    private class MyCustomAdapter extends BaseAdapter {


        public ArrayList<FriendRequestsAdapterItems> listnewsDataAdpater;

        public MyCustomAdapter(ArrayList<FriendRequestsAdapterItems> listnewsDataAdpater) {
            this.listnewsDataAdpater = listnewsDataAdpater;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView = mInflater.inflate(R.layout.layout_friend_requests, null);

            final FriendRequestsAdapterItems s = listnewsDataAdpater.get(position);
            TextView tvName = (TextView) myView.findViewById(R.id.tvMessage);
            tvName.setText(s.tvName);

            ImageView ivImg = (ImageView) myView.findViewById(R.id.ivImg);
            final Button reqBtn = (Button) myView.findViewById(R.id.accBtn);
            final Button rejBtn = (Button) myView.findViewById(R.id.rejBtn);

            reqBtn.setOnClickListener(new View.OnClickListener() {
                boolean a = false;

                @Override
                public void onClick(View v) {


                    String url = "http://10.0.2.2/PartnerFinderWebServices/AcceptRequest.php?UserOne=" + sp.getString("userID","") + "&UserTwo=" + s.user_id;
                    new MyAsyncTaskgetNews1().execute(url);
                    Toast.makeText(getApplicationContext(), "Friend request Accepted", Toast.LENGTH_LONG).show();

                    reqBtn.setText("Accepted");
                    listnewsData.remove(position);
                    myadapter.notifyDataSetChanged();


                }
            });

            rejBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String url = "http://10.0.2.2/PartnerFinderWebServices/RejectRequest.php?UserOne=" + sp.getString("userID","") + "&UserTwo=" + s.user_id;
                    new MyAsyncTaskgetNews1().execute(url);
                    Toast.makeText(getApplicationContext(), "Friend request Rejected", Toast.LENGTH_LONG).show();

                    reqBtn.setText("Rejected");
                    listnewsData.remove(position);
                    myadapter.notifyDataSetChanged();


                }
            });


            try {
                URL url = new URL(s.picture_path);
                String protocol = url.getProtocol();
                String file = url.getFile();
                String url2 = protocol + "://" + listusers.DynamicIp + file;

                Picasso.get()
                        .load(url2)
                        .fit()
                        .into(ivImg, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Load", "Successfull");

                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d("Load", "Error");
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (MalformedURLException e) {
            }

            return myView;
        }
    }

    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }

        @Override
        protected String doInBackground(String... params) {
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

            } catch (Exception ex) {
                Log.d("Error", "errorInConnection");
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {

            try {
                numOfFrndReq = (TextView) findViewById(R.id.numOfFrndReq);

                JSONObject json = new JSONObject(progress[0]);
                //display response data
                if (json.getString("msg").equals(" You have no friend request "))
                {

                    numOfFrndReq.setText("You have no friend request!!!!");
                }
                if (json.getString("msg").equals("friend requests"))
                {
                    String numrow = json.getString("row_info");

                    if (numrow.equals("")){

                    }
                    numOfFrndReq.setText(numrow);



                    JSONArray userInfo = new JSONArray(json.getString("user_info"));
                    for (int i = 0; i < userInfo.length(); i++) {
                        JSONObject userCredentials = userInfo.getJSONObject(i);

                        String picture_path = userCredentials.getString("picture_path");
                        String full_name = userCredentials.getString("full_name");
                        String id = userCredentials.getString("id");
                        listnewsData.add(new FriendRequestsAdapterItems(userCredentials.getString("full_name"), userCredentials.getString("picture_path"), userCredentials.getString("id")));
                    }
                }

                myadapter.notifyDataSetChanged();


                //display response data

            }
            catch (Exception ex) {
                Log.d("error is", ex.getMessage());
            }

        }


        protected void onPostExecute(String result2) {

        }


    }



    // this method convert any stream to string
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String linereultcal = "";

        try {
            while ((line = bureader.readLine()) != null) {

                linereultcal += line;

            }
            inputStream.close();


        } catch (Exception ex) {
            Log.d("Stream error", "error");
        }

        return linereultcal;
    }


    public class MyAsyncTaskgetNews1 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }

        @Override
        protected String doInBackground(String... params) {
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

            } catch (Exception ex) {
                Log.d("error", "eo");
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {

            try {
                JSONObject json = new JSONObject(progress[0]);
                if (json.getString("msg").equalsIgnoreCase("You have accepted friend request")) {
                    Toast.makeText(getApplicationContext(),"You have accepted request",Toast.LENGTH_LONG).show();
                }
                if (json.getString("msg").equalsIgnoreCase("You rejected friend request")) {
                    Toast.makeText(getApplicationContext(),"You have accepted request",Toast.LENGTH_LONG).show();
                }




            }

            catch (Exception ex) {
                Log.d("error is", ex.getMessage());
            }

        }


        protected void onPostExecute(String result2) {


        }



    }



}
