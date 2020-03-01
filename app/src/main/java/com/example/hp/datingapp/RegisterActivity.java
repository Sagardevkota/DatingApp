

package com.example.hp.datingapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;



import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    EditText etFirst_Name;
    EditText etLast_Name;
    EditText etEmail;
    EditText etPassword1;
    EditText etPassword2;
    RadioButton rbMale;
    RadioButton rbFemale;
    RadioButton rbOthers;
    EditText etAge;
    EditText etCity;
    EditText etUser_Name;
    Button buJoin;
    RadioGroup rbGroup;
    ImageView ivUserImage;
    CheckBox cbTerms;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;


    Context context;
    private static final int STORAGE_PERMISSION_CODE = 4655;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filepath;
    private Bitmap bitmap;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    public static final String UPLOAD_URL = "http://10.0.2.2/PartnerFinderWebServices/upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buJoin = (Button) findViewById(R.id.Confirm);
        etFirst_Name = (EditText) findViewById(R.id.etFirst_Name);
        etLast_Name = (EditText) findViewById(R.id.etLast_Name);
        etUser_Name=(EditText)findViewById(R.id.tvUser_Name);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword1 = (EditText) findViewById(R.id.etPassword1);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        etCity = (EditText) findViewById(R.id.tvCity);
        etAge = (EditText) findViewById(R.id.etAge);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rbOthers = (RadioButton) findViewById(R.id.rbOthers);
        rbGroup=(RadioGroup)findViewById(R.id.rbGroup);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        cbTerms=(CheckBox)findViewById(R.id.cbTerms);
        mAuth = FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        //Adding listners
        etFirst_Name.addTextChangedListener(loginTextWatcher);
        etLast_Name.addTextChangedListener(loginTextWatcher);
        etEmail.addTextChangedListener(loginTextWatcher);
        etPassword1.addTextChangedListener(loginTextWatcher);
        etPassword2.addTextChangedListener(loginTextWatcher);
        etCity.addTextChangedListener(loginTextWatcher);
        etAge.addTextChangedListener(loginTextWatcher);
        etUser_Name.addTextChangedListener(loginTextWatcher);
        cbTerms.addTextChangedListener(loginTextWatcher);


        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        requestStoragePermission();


    }
    private boolean validateFirstName(){
        String firstName = etFirst_Name.getText().toString().trim();
        if (firstName.isEmpty()) {
            etFirst_Name.setError("Field can't be empty");
            return false;}
        else{
            return true;
        }
    }
    private boolean validateCity(){
        String city = etCity.getText().toString().trim();
        if (city.isEmpty()) {
            etCity.setError("Field can't be empty");
            return false;}
        else{
            return true;
        }
    }
    private boolean validateUserName(){
        String username = etUser_Name.getText().toString().trim();
        if (username.isEmpty()) {
            etUser_Name.setError("Field can't be empty");
            return false;}
        else{
            return true;
        }
    }

    private boolean validateLastName(){
        String lastName = etLast_Name.getText().toString().trim();
        if (lastName.isEmpty()) {
            etLast_Name.setError("Field can't be empty");
            return false;}
        else{
            return true;
        }
    }
    private boolean validateAge(){
        String Age = etAge.getText().toString().trim();
        if (Age.isEmpty()) {
            etAge.setError("Field can't be empty");
            return false;
        }

        else{
            etAge.setError(null);
            return true;
        }
    }



    private boolean validateEmail() {
        String emailInput = etEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            etEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            etEmail.setError("Please enter a valid email address");
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }




    private boolean validatePassword1() {
        String passwordInput1 = etPassword1.getText().toString().trim();

        if (passwordInput1.isEmpty()) {
            etPassword1.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput1).matches()) {
            etPassword1.setError("Password too weak");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(passwordInput1).matches()) {
            etPassword1.setError("Password too weak");
            return false;
        }



        else {
            etPassword1.setError(null);
            return true;
        }
    }



    private boolean validatePassword2() {
        String passwordInput2 = etPassword2.getText().toString().trim();

        if (passwordInput2.isEmpty()) {
            etPassword2.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput2).matches()) {
            etPassword2.setError("Password too weak");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(passwordInput2).matches()) {
            etPassword2.setError("Password too weak");
            return false;
        }

        else {


            etPassword2.setError(null);
            return true;
        }
    }

    private boolean confirmPassword(){
        String passwordInput1 = etPassword1.getText().toString().trim();
        String passwordInput2 = etPassword2.getText().toString().trim();
        if (!passwordInput1.equals(passwordInput2)){
            etPassword2.setError("Password donot match");
            return false;}
        else
        {
            return true;
        }
    }
    private boolean validateCheckBox(){
        if (!cbTerms.isChecked()){
            cbTerms.setError("You must accept the terms");
            return false;
        }
        return true;
    }


    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {




        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



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

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                ivUserImage.setImageBitmap(bitmap);
            } catch (Exception ex) {

            }
        }
    }

    public void selectImage(View view) {
        ShowFileChooser();
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
        String first_Name = etFirst_Name.getText().toString().trim();//small
        final String firstName = first_Name.substring(0, 1).toUpperCase() + first_Name.substring(1);//cap

        String last_Name = etLast_Name.getText().toString().trim();//small
        final String lastName=last_Name.substring(0,1).toUpperCase()+ last_Name.substring(1);//cap
        final  String  email = etEmail.getText().toString().trim();
        final  String  password = etPassword1.getText().toString().trim();
        final String age = etAge.getText().toString().trim();
        final String userName = etUser_Name.getText().toString().trim();
        String Lowcity = etCity.getText().toString().trim();
        final String city=Lowcity.substring(0,1).toUpperCase()+Lowcity.substring(1);
        String path = getPath(filepath);
        String checkedGender = "";
        if (rbMale.isChecked()) {
            checkedGender = "Male";
        }
        if (rbFemale.isChecked()) {
            checkedGender = "Female";
        }
        if (rbOthers.isChecked()) {
            checkedGender = "Others";
        }
        final String finalCheckedGender = checkedGender;







        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("first_name", firstName)
                    .addParameter("last_name", lastName)
                    .addParameter("user_name", userName)
                    .addParameter("city", city)
                    .addParameter("email", email)
                    .addParameter("password", password)
                    .addParameter("age", age)
                    .addParameter("gender", checkedGender)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.d("Upload","Uploading");
                            showProgressDialog("Signing you up");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            try {
                                JSONObject json = new JSONObject(serverResponse.getBodyAsString());
                                if (json.getString("msg").equals("Email is already taken")) {
                                    Toast.makeText(getApplicationContext(),"Email is already taken",Toast.LENGTH_LONG).show();
                                }
                                if (json.getString("msg").equals("username is already taken")) {
                                    Toast.makeText(getApplicationContext(),"Username is already taken",Toast.LENGTH_LONG).show();
                                }

                                else {
                                    JSONArray userInfo = new JSONArray(json.getString("user_info"));
                                    JSONObject userCredentials = userInfo.getJSONObject(0);
                                    String id = userCredentials.getString("id");
                                    String fullName = userCredentials.getString("full_name");
                                    String userName = userCredentials.getString("user_name");
                                    String email = userCredentials.getString("email");



                                    Log.d("Id is", id);
                                    Log.d("Full name is", fullName);

                                    goToActivity(id, fullName, userName,email,password);

                                }
                            }
                            catch (JSONException e) {
                                Log.d("Jsonerror","jsonerror");
                            }

                            Log.d("Upload","Uploaded");
                            hideProgressDialog();



                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .setMaxRetries(3)
                    .startUpload();

        } catch (Exception ex) {


        }



    }



    public void buJoin(View view) {

        validateLastName();
        validateEmail();
        validatePassword1();
        validatePassword2();
        validateAge();
        validateCity();
        validateUserName();
        confirmPassword();
        validateCheckBox();


        if ( !validateEmail()  | !validatePassword1() | !validatePassword2() | !confirmPassword()| !validateFirstName()| !validateLastName()| !validateAge()| !validateCity()| !validateUserName()| !validateCheckBox() ) {

        }

        else {

            uploadImage();
        }

    }
    @VisibleForTesting
    public ProgressDialog mProgressDialog;


    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();


    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();


        }
    }
    public void goToActivity(String id, final String fullname, final String userName, String email, String password){


        FirebaseUser user = mAuth.getCurrentUser();

        sp.edit().putString("userID",id).apply();
        sp.edit().putString("FullName",fullname).apply();
        sp.edit().putString("UserName",userName).apply();
        sp.edit().putBoolean("logged",true).apply();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");

                            String currentUID=mAuth.getCurrentUser().getUid();
                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", fullname);
                            userMap.put("status", "i am online");
                            userMap.put("image", "default");
                            userMap.put("id",currentUID);

                            userMap.put("device_token", device_token);


                            rootRef.child("Users").child(currentUID).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"added to database",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(),"Registration is complete..Welcome "+fullname,Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            });






                            Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(),""+ task.getException(),Toast.LENGTH_LONG).show();


                        }

                        // ...
                    }
                });




    }

}
