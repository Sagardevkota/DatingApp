package com.example.hp.datingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class listusers extends AppCompatActivity {
    public static String DynamicIp="192.168.0.123";
    ArrayList<com.example.hp.datingapp.AdapterItems> listnewsData = new ArrayList<com.example.hp.datingapp.AdapterItems>();
    MyCustomAdapter myadapter;
    Context context;
    List<String> arr;
    SharedPreferences sp;
 AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listusers);


      final ListView lvlist=(ListView)findViewById(R.id.LVNews);
        myadapter=new MyCustomAdapter(listnewsData);
        lvlist.setAdapter(myadapter);
        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-7839793942942631~5534176360");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(),"Ad couldnt load",Toast.LENGTH_LONG).show();
                Log.d("error",String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });




        Spinner spSort=(Spinner)findViewById(R.id.spSort);
        arr=new ArrayList<String>();
        arr.add("By Name");
        arr.add("By Age");
        arr.add("Age between 18-25");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arr);
        spSort.setAdapter(arrayAdapter);
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arr.get(position).equals("By Name"))
                {

                    String url= "http://10.0.2.2/PartnerFinderWebServices/listusers.php?UserOne=" +sp.getString("userID", "")+"&sorting=By name";

                    new MyAsyncTaskgetNews().execute(url);
                    Toast.makeText(getApplicationContext(),"By Name",Toast.LENGTH_LONG).show();
                    listnewsData.clear();
                    myadapter.notifyDataSetChanged();
                }
                if (arr.get(position).equals("By Age"))
                {
                    String url= "http://10.0.2.2/PartnerFinderWebServices/listusers.php?UserOne=" +sp.getString("userID", "")+"&sorting=By age";
                    new MyAsyncTaskgetNews().execute(url);
                    Toast.makeText(getApplicationContext(),"By Age",Toast.LENGTH_LONG).show();
                    listnewsData.clear();
                    myadapter.notifyDataSetChanged();
                }
                if (arr.get(position).equals("Age between 18-25"))
                {
                    String url= "http://10.0.2.2/PartnerFinderWebServices/listusers.php?UserOne=" +sp.getString("userID", "")+"&sorting=Age between 18-25";
                    new MyAsyncTaskgetNews().execute(url);

                    Toast.makeText(getApplicationContext(),"Age between 18-25",Toast.LENGTH_LONG).show();
                    listnewsData.clear();
                    myadapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    private class MyCustomAdapter extends BaseAdapter {


        public ArrayList<com.example.hp.datingapp.AdapterItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<com.example.hp.datingapp.AdapterItems>  listnewsDataAdpater) {
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView = mInflater.inflate(R.layout.layout_ticket, null);

            final com.example.hp.datingapp.AdapterItems s = listnewsDataAdpater.get(position);
            TextView tvName=( TextView)myView.findViewById(R.id.tvMessage);
            tvName.setText(s.tvName+"," );
            TextView tvAge=( TextView)myView.findViewById(R.id.tvAge);
            tvAge.setText(String.valueOf(s.tvAge));
            TextView tvGender=( TextView)myView.findViewById(R.id.tvCity);
            tvGender.setText(s.tvCity);
            ImageView ivImg=(ImageView)myView.findViewById(R.id.ivImg);
              final ImageView reqBtn=myView.findViewById(R.id.reqBtn);
              reqBtn.setImageResource(R.drawable.ic_like);
             reqBtn.setOnClickListener(new View.OnClickListener() {
                 boolean a = false;
                 @Override
                 public void onClick(View v) {

                     if (a==false){

                         try{

                             String url = "http://10.0.2.2/PartnerFinderWebServices/CheckIfAction.php?UserOne=" + sp.getString("userID", "") + "&UserTwo=" + s.user_id;

                             URL url1=new URL(url);
                             String protocol = url1.getProtocol();
                             String file = url1.getFile();
                             String url2 = protocol +"://"+ DynamicIp + file;
                             new MyAsyncTaskgetNews1().execute(url);
}
                         catch (MalformedURLException e){}

                         Toast.makeText(getApplicationContext(),"Liked",Toast.LENGTH_LONG).show();
                         reqBtn.setImageResource(R.drawable.ic_liked);
                         a=true;

                     }

                       else{
                           String url2 = "http://10.0.2.2/PartnerFinderWebServices/RejectRequest.php?UserOne=" + sp.getString("userID","") + "&UserTwo=" +s.user_id ;
                           new MyAsyncTaskgetNews1().execute(url2);
                           Toast.makeText(getApplicationContext(),"Took back like",Toast.LENGTH_LONG).show();
                         reqBtn.setImageResource(R.drawable.ic_like);
                           a= false;

                       }
                 }
             });







            try{
            URL url=new URL(s.picture_path);
                String protocol = url.getProtocol();
                String file = url.getFile();
                String url2 = protocol +"://"+ DynamicIp + file;

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
                JSONArray json= new JSONArray(progress[0]);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject user = json.getJSONObject(i);
                    String picture_path=user.getString("picture_path");
                    listnewsData.add(new com.example.hp.datingapp.AdapterItems(user.getString("full_name"),user.getInt("age"),user.getString("city"),picture_path,user.getString("id")));

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

    public class MyAsyncTaskgetNews1 extends AsyncTask<String, String, String> {
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

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {


            try {
                JSONObject json = new JSONObject(progress[0]);
                //display response data
             String msg=json.getString("msg");


                if (json.getString("msg").equals("You have not sent friend request yet")) {
                    Log.d("request","req sent now");


                }


                else
                {
                    try{
                        JSONArray userInfo = new JSONArray(json.getString("user_info"));
                        JSONObject userCredentials = userInfo.getJSONObject(0);

                        String status=  userCredentials.getString("status");



                    }
                    catch (JSONException e){}


                }

            }
            catch (Exception ex) {
                Log.d("er", ex.getMessage());
            }

        }


        protected void onPostExecute(String  result2){


        }




    }




    }









