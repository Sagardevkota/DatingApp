package com.example.hp.datingapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sp;
    android.content.Context Context;
    ImageView user_image;
    TextView tvFullName;
    TextView tvUser_Name;
    TextView tvCity;
    TextView tvAge;
    TextView tvGender;
    TextView tvEmail;
    Bitmap profBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
               checkIfLoggedIn();

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
                urlConnection.setConnectTimeout(7800);//set timeout to 5 seconds

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

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try {
                JSONArray json= new JSONArray(progress[0]);

                    JSONObject user = json.getJSONObject(0);
                   String fullName= user.getString("full_name");
                int age= user.getInt("age");
                String gender= user.getString("gender");
                String email= user.getString("email");
                String username= user.getString("user_name");
                String city= user.getString("city");



                    profileDisplay(fullName,age,gender,email,username,city,HomeActivity.myBitmap);

                }

                //display response data





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
    public void checkIfLoggedIn(){

        String id=sp.getString("userID","0");
            String url = "http://10.0.2.2/PartnerFinderWebServices/profile.php?id=" + id;
            new MyAsyncTaskgetNews().execute(url);
        }
        public void profileDisplay(String fullName, int age, String gender, String email,String username,String city, Bitmap result) {

            tvFullName=(TextView)findViewById(R.id.tvFullName);
            tvUser_Name=(TextView)findViewById(R.id.tvUser_Name);
            tvEmail=(TextView)findViewById(R.id.tvEmail);
             tvAge=(TextView)findViewById(R.id.tvAge);
             tvGender=(TextView)findViewById(R.id.tvGender);
            tvCity=(TextView)findViewById(R.id.tvCity);
            user_image=(ImageView)findViewById(R.id.user_image);


            //setting values
            tvFullName.setText( fullName);
            tvUser_Name.setText( "Username: "+ username);
            tvAge.setText( "Age: " + String.valueOf(age));
            tvGender.setText("Gender : "+ gender);
            tvCity.setText("City: "+  city);
            tvEmail.setText("Email: "+email);
            user_image.setImageBitmap(result);

        }


















}
