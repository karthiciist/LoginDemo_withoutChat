package com.example.gaayathri.logindemo;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SellActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CAMERA   = 10;
    public static final int MY_REQUEST_WRITE_CAMERA   = 11;
    public static final int CAPTURE_CAMERA   = 12;

    public static final int MY_REQUEST_READ_GALLERY   = 13;
    public static final int MY_REQUEST_WRITE_GALLERY   = 14;
    public static final int MY_REQUEST_GALLERY   = 15;

    private SimpleDraweeView swView;

    public File filen = null;

    private FirebaseAuth firebaseAuth;
    private EditText title;
    private EditText author;
    private EditText degree;
    private EditText specialization;
    private EditText mrp;
    private EditText price;
    private EditText sellerMsg;
    private Button placead;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;
    Toolbar toolbarSell;
    Dialog picDialog;
    Dialog myDialog;
    private StorageReference storageReference;
    Uri photocUri;
    String downloadUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_sell);

        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        picDialog = new Dialog(this);
        picDialog.setContentView(R.layout.selectphotodialog);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swView = findViewById(R.id.img1);

        swView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picDialog.show();

            }
        });

        Button btnCamera = picDialog.findViewById(R.id.btnCamera);
        Button btnGallery = picDialog.findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionCW();
                picDialog.dismiss();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionRG();
                picDialog.dismiss();
            }
        });


        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.adplaceddialog);

        Button btnLHome = myDialog.findViewById(R.id.btnHome);
        Button btnLAnotherAd = myDialog.findViewById(R.id.btnAnotherAd);

        btnLHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        btnLAnotherAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellActivity.this, SellActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        toolbarSell = findViewById(R.id.toolbarSell);
        setSupportActionBar(toolbarSell);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.main_color));
        }

        progressDialog = new ProgressDialog(this);

        title = findViewById(R.id.etMaterialTitle);
        author = findViewById(R.id.etAuthor);
        degree = findViewById(R.id.etDegree);
        specialization = findViewById(R.id.etSpecialization);
        mrp = findViewById(R.id.etMrp);
        price = findViewById(R.id.etExpectedPrice);
        placead = findViewById(R.id.btnPlaceAd);
        sellerMsg = findViewById(R.id.etNoteToBuyer);

        Long tsLong = System.currentTimeMillis()/1000;
        final String timeStamp = tsLong.toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();

        final String entryName = timeStamp + email;


        DocumentReference docRef = firestore.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        final String llocation = document.getString("city");


                        placead.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {

                                progressDialog.setMessage("Placing your ad...");
                                progressDialog.show();

                                swView.setDrawingCacheEnabled(true);
                                swView.buildDrawingCache();
                                Bitmap bitmap1 = swView.getDrawingCache();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data1 = baos.toByteArray();

                                StorageReference imageReference = storageReference.child(firebaseAuth.getCurrentUser().getEmail()).child("bookimages").child(entryName);
                                UploadTask uploadTask = imageReference.putBytes(data1);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SellActivity.this, "Failed to upload image and ad!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        downloadUri = taskSnapshot.getDownloadUrl().toString();

                                        final Map<String, String> bookMap = new HashMap<>();

                                        final String ltitle = title.getText().toString();
                                        String lauthor = author.getText().toString();
                                        String ldegree = degree.getText().toString();
                                        String lspecialization = specialization.getText().toString();
                                        String lmrp = mrp.getText().toString();
                                        String lprice = price.getText().toString();
                                        String entryName = timeStamp + email;
                                        String lsellerMsg = sellerMsg.getText().toString();

                                        bookMap.put("title", ltitle);
                                        bookMap.put("author", lauthor);
                                        bookMap.put("degree", ldegree);
                                        bookMap.put("specialization", lspecialization);
                                        bookMap.put("mrp", "₹ " + lmrp);
                                        bookMap.put("price", "₹ " + lprice);
                                        bookMap.put("user", email);
                                        bookMap.put("location", llocation);
                                        bookMap.put("timestamp", timeStamp);
                                        bookMap.put("entryName", entryName);
                                        bookMap.put("sellerMsg", lsellerMsg);
                                        bookMap.put("downloadUri", downloadUri);

                                        firestore.collection("books").document(timeStamp + email).set(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                firestore.collection("users").document(email).collection("books").document(ltitle).set(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        myDialog.show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SellActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                            }
                        });

                    } else {
                        Toast.makeText(SellActivity.this, "location unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SellActivity.this, "location unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SellActivity.this, EntryActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionCA(){
        int permissionCheck = ContextCompat.checkSelfPermission(SellActivity.this, android.Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SellActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_REQUEST_CAMERA);
        } else {
            catchPhoto();
        }
    }
    private void checkPermissionCW(){
        int permissionCheck = ContextCompat.checkSelfPermission(SellActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SellActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_CAMERA);
        } else {
            checkPermissionCA();
        }
    }
    private void catchPhoto() {
        filen = getFile();
        if(filen!=null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                Uri photocUri = Uri.fromFile(filen);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photocUri);
                startActivityForResult(intent, CAPTURE_CAMERA);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(SellActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SellActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionRG(){
        int permissionCheck = ContextCompat.checkSelfPermission(SellActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SellActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
        } else {
            checkPermissionWG();
        }
    }
    private void checkPermissionWG(){
        int permissionCheck = ContextCompat.checkSelfPermission(SellActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SellActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_GALLERY);
        } else {
            getPhotos();
        }
    }
    private void getPhotos() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, MY_REQUEST_GALLERY);
    }

    public File getFile(){

        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        if (!fileDir.exists()){
            if (!fileDir.mkdirs()){
                return null;
            }
        }

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        File mediaFile = new File(fileDir.getPath() + File.separator + ts + ".jpg");
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK){
            Log.e("msg", "photo not get");
            return;
        }

        switch (requestCode) {

            case CAPTURE_CAMERA:

                swView.setImageURI(Uri.parse("file:///" + filen));
                break;


            case MY_REQUEST_GALLERY:
                try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    filen = getFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(filen);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    swView.setImageURI(Uri.parse("file:///" + filen));//fresco library

                } catch (Exception e) {

                    Log.e("", "Error while creating temp file", e);
                }
                break;

        }
    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions,  int[] grantResults)
    {

        switch (requestCode) {
            case MY_REQUEST_CAMERA:
                catchPhoto();
                break;
            case MY_REQUEST_WRITE_CAMERA:
                checkPermissionCA();
                break;
            case MY_REQUEST_READ_GALLERY:
                checkPermissionWG();
                break;
            case MY_REQUEST_WRITE_GALLERY:
                getPhotos();
                break;
        }
    }

}
