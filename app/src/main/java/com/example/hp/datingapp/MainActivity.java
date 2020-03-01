package com.example.hp.datingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity{
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;




    SharedPreferences sp;
    Context Context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();






        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);
      Button  newAccount= findViewById(R.id.newAccount);

        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        checkIfLoggedIn();


    }


    public void regClick(View view){

        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void buLogin(View view){
        final EditText  etEmail= findViewById(R.id.etEmail);
        final String email=etEmail.getText().toString().trim();
        final EditText   etPassword= findViewById(R.id.etPassword);
        final String password=etPassword.getText().toString().trim();
        if ((email.length()> 0)
                && (password.length()> 0)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "signInWithEmail:success");
                                Toast.makeText(getApplicationContext(), "Authentication successfull",Toast.LENGTH_LONG).show();


                                FirebaseUser user = mAuth.getCurrentUser();
                                String url = "http://10.0.2.2/PartnerFinderWebServices/login.php?email=" + email + "&password=" + password;
                                new MyAsyncTaskgetNews().execute(url);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed",Toast.LENGTH_LONG).show();

                            }

                            // ...
                        }
                    });

        }
        else
        {  Toast.makeText(this, "You did not enter email or password", Toast.LENGTH_SHORT).show();}


    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    // get news from server
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

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try{
                JSONObject json= new JSONObject(progress[0]);
                //display response data

                if (json.getString("msg").equals("Valid credentials")) {
                    String msg="Logging you in";

                    JSONArray userInfo=new JSONArray( json.getString("user_info"));
                    JSONObject userCredentials=userInfo.getJSONObject(0);
                    String userID=userCredentials.getString("id");
                    String FullName=userCredentials.getString("full_name");
                    String UserName=userCredentials.getString("user_name");
                    SaveUserInfo(userID,FullName,UserName);
                    showProgressDialog(msg);
                    goToHomeActivity();
                }
                else
                    Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_LONG).show();

            }


            catch (Exception ex) {
                Log.d("er", ex.getMessage());
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



    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to exit?").setTitle("Confirmation").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();


    }

    public void SaveUserInfo(String userID, String FullName, String UserName){
        sp.edit().putString("userID",userID).apply();
        sp.edit().putString("FullName",FullName).apply();
        sp.edit().putString("UserName",UserName).apply();
        sp.edit().putBoolean("logged",true).apply();

    }

    public void goToHomeActivity(){
        Intent i = new Intent(this,HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
public void checkIfLoggedIn(){
    FirebaseUser currentUser = mAuth.getCurrentUser();

       if (sp.getBoolean("logged",true)& currentUser!=null ){
           goToHomeActivity();
       }
    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        }
    });



}










}
