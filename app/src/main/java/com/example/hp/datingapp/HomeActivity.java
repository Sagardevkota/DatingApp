package com.example.hp.datingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sp;
    private static final int STORAGE_PERMISSION_CODE = 4655;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filepath;
    private Bitmap bitmap;
    Context context;
    EditText etPost;
    TextView header_UserName;
    ImageView ivShow;
    ImageView header_user_image;
    public static String counter;
    private FirebaseAuth mAuth;
    DatabaseReference rootRef;


    InterstitialAd mInterstitialAd;

    TextView textCartItemCount;
    private NotificationManagerCompat notificationManager;



    public static Bitmap myBitmap;
    MyCustomAdapter myadapter;
    ArrayList<PostAdapterItems> listnewsData = new ArrayList<PostAdapterItems>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7839793942942631~5534176360");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        sp = getSharedPreferences("login", MODE_PRIVATE);
        requestStoragePermission();

        ImageView ivShow = (ImageView) findViewById(R.id.ivShow);
        myadapter = new MyCustomAdapter(this, listnewsData);
        ListView lsNews = (ListView) findViewById(R.id.LVNews);
        lsNews.setAdapter(myadapter);

        listnewsData.add(new PostAdapterItems(null, null, null,
                "add", null, null, null));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headview = navigationView.getHeaderView(0);
        ImageView header_user_image = (ImageView) headview.findViewById(R.id.header_user_image);
        header_user_image.setImageResource(R.drawable.ic_launcher_background);
        TextView header_UserName = (TextView) headview.findViewById(R.id.header_UserName);
        header_UserName.setText(sp.getString("FullName", "0"));
        navigationView.setNavigationItemSelectedListener(this);
        String url2 = "http://10.0.2.2/PartnerFinderWebServices/loadposts.php?UserOne=" + sp.getString("userID", "");
        new MyAsyncTaskgetNews().execute(url2);
        String url1 = "http://10.0.2.2/PartnerFinderWebServices/RequestLists.php?UserOne=" + sp.getString("userID", "");
        new MyAsyncTaskgetNews().execute(url1);

        String url = "http://10.0.2.2/PartnerFinderWebServices/image/IMG_" + sp.getString("UserName", "") + ".png";

        new SendHttpRequestTask().execute(url);

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to exit?").setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

         final MenuItem menuItem = menu.findItem(R.id.requests);


       View actionView =  menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        if (counter!=null)
        textCartItemCount.setText(counter);
        else textCartItemCount.setVisibility(View.GONE);

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });





        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.requests) {
          Log.d("request","clicked");

            Intent intent=new Intent(getApplicationContext(),FriendRequestActivity.class);
            startActivity(intent);
            return true;

        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.nav_other_users) {
            Intent intent = new Intent(getApplicationContext(), listusers.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent=new Intent(getApplicationContext(),FriendListsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about_us) {
           Intent intent = new Intent(getApplicationContext(), AboutUs.class);
           startActivity(intent);

        } else if (id == R.id.nav_logout) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }


            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to log out?").setTitle("Confirmation").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    sp.edit().putBoolean("logged", false).apply();
                    showProgressDialog("Logging out");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void headerImageClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);

    }

    public class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);


                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                return myBitmap;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headview = navigationView.getHeaderView(0);
            ImageView header_user_image = (ImageView) headview.findViewById(R.id.header_user_image);

            header_user_image.setImageBitmap(result);

        }
    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<PostAdapterItems> listnewsDataAdpater;
        Context context;

        public MyCustomAdapter(Context context, ArrayList<PostAdapterItems> listnewsDataAdpater) {
            this.listnewsDataAdpater = listnewsDataAdpater;
            this.context = context;
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
        public View getView(int position, View convertView, ViewGroup parent) {

            final PostAdapterItems s = listnewsDataAdpater.get(position);

            if (s.post_date.equals("add")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.post_add, null);


                final EditText etPost = (EditText) myView.findViewById(R.id.etPost);

                ImageView iv_post = (ImageView) myView.findViewById(R.id.iv_post);


                ImageView iv_attach = (ImageView) myView.findViewById(R.id.iv_attach);

                iv_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        etPost.setText("");
                        ShowFileChooser();

                    }
                });

                iv_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        uploadImage();


                    }
                });


                return myView;
            } else if (s.post_date.equals("loading")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.post_loading, null);
                return myView;
            } else if (s.post_date.equals("nopost")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.post_msg, null);
                return myView;
            } else {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.post_item, null);

                TextView txtUserName = (TextView) myView.findViewById(R.id.txtUserName);
                txtUserName.setText(s.first_name);


                txtUserName.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {


                    }

                });


                TextView txt_post = (TextView) myView.findViewById(R.id.txt_post);
                txt_post.setText(s.post_text);

                TextView txt_post_date = (TextView) myView.findViewById(R.id.txt_post_date);
                txt_post_date.setText(s.post_date);
                ImageView picture_path = (ImageView) myView.findViewById(R.id.picture_path);
                ImageView post_picture = (ImageView) myView.findViewById(R.id.post_picture);

if (s.picture_path == null)
{
    picture_path.setImageBitmap(myBitmap);
}

                try {
                    URL url = new URL(s.post_picture);
                    String protocol = url.getProtocol();
                    String file = url.getFile();
                    String url2 = protocol + "://" + listusers.DynamicIp + file;
                    Picasso
                            .get()

                            .load(url2).fit()
                            .into(post_picture);

                } catch (MalformedURLException e) {
                }
                try {
                    URL url = new URL(s.picture_path);
                    String protocol = url.getProtocol();
                    String file = url.getFile();
                    String url2 = protocol + "://" + listusers.DynamicIp + file;
                    Picasso
                            .get()

                            .load(url2).fit()
                            .into(picture_path);

                } catch (MalformedURLException e) {
                }

                picture_path.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),OthersProfileActivity.class);
                        intent.putExtra("UserID",s.user_id);
                        startActivity(intent);

                    }
                });
                return myView;


            }
        }


        //load image
    }

    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivShow = (ImageView) findViewById(R.id.ivShow);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                ivShow.setImageBitmap(bitmap);

            } catch (Exception ex) {

            }
        }
    }


    private String getPath(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadImage() {


        String path = getPath(filepath);
        showProgressDialog("adding post");
        LayoutInflater mInflater = getLayoutInflater();
        View myView = mInflater.inflate(R.layout.post_add, null);


        EditText etPost = findViewById(R.id.etPost);
        final String text_post = etPost.getText().toString();


        String UPLOAD_URL = "http://10.0.2.2/PartnerFinderWebServices/posts.php";
        String user_id = sp.getString("userID", "0");


        try {

            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("user_id", user_id)
                    .addParameter("post_text", text_post)

                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.d("Upload", "Uploading");


                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {


                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            try {
                                JSONObject json = new JSONObject(serverResponse.getBodyAsString());
                                JSONObject userInfo = new JSONObject(json.getString("user_info"));


                                String post_picture = userInfo.getString("post_picture");
                                String post_date = userInfo.getString("post_date");
                                String post_text = userInfo.getString("post_text");

                                listnewsData.add(new PostAdapterItems(null, post_text, post_picture, post_date, sp.getString("userID", "0"), sp.getString("UserName", "0"), null));

                                Log.d("details", post_date);
                            } catch (JSONException e) {
                                Log.d("error", "jsonerror");

                            }
                            myadapter.notifyDataSetChanged();

                            Log.d("Upload", "Completed");
                        }


                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .setMaxRetries(3)
                    .startUpload();


        } catch (Exception ex) {


        }

        hideProgressDialog();
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
                JSONObject json = new JSONObject(progress[0]);
                if (json.getString("msg").equalsIgnoreCase("You dont have any posts")){
                    listnewsData.add(new PostAdapterItems(null, null, null,
                            "nopost", null, null, null));
                }
                if (json.getString("msg").equalsIgnoreCase("friend requests"))

                {
                counter=json.getString("row_info");

                if (counter!=null){
                    JSONArray userInfo = new JSONArray(json.getString("user_info"));
                    for (int i = 0; i < userInfo.length(); i++) {
                        JSONObject userCredentials = userInfo.getJSONObject(i);


                        String full_name = userCredentials.getString("full_name");
                        Intent intent=new Intent(getApplicationContext(),FriendRequestActivity.class);
                        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,0);


                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.couple)
                            .setContentTitle("Like request")
                            .setContentText(full_name+" has liked you")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setColor(Color.rgb(235, 52, 143))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();

                    notificationManager.notify(null,1, notification);
                    }}
                }
                if (json.getString("msg").equalsIgnoreCase("You have posts"))
                {
                    JSONArray postInfo=new JSONArray(json.getString("post_info"));

                    for (int i = 0; i < postInfo.length(); i++) {
                        JSONObject user = postInfo.getJSONObject(i);

                        String picture_path = user.getString("picture_path");
                        String post_picture = user.getString("post_picture");
                        String post_date = user.getString("post_date");
                        String post_text = user.getString("post_text");
                        String user_name = user.getString("user_name");
                        String user_id=user.getString("id");

                        listnewsData.add(new PostAdapterItems(null, post_text, post_picture, post_date,user_id , user_name, picture_path));
                    }
                }
                myadapter.notifyDataSetChanged();





            } catch (Exception ex) {
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



    }












