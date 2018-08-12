package com.example.gaayathri.logindemo;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.logindemo.model.NoteMyMaterials;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.gaayathri.logindemo.SellActivity.CAPTURE_CAMERA;
import static com.example.gaayathri.logindemo.SellActivity.MY_REQUEST_CAMERA;
import static com.example.gaayathri.logindemo.SellActivity.MY_REQUEST_GALLERY;
import static com.example.gaayathri.logindemo.SellActivity.MY_REQUEST_READ_GALLERY;
import static com.example.gaayathri.logindemo.SellActivity.MY_REQUEST_WRITE_CAMERA;
import static com.example.gaayathri.logindemo.SellActivity.MY_REQUEST_WRITE_GALLERY;

public class EditMyMaterialsActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";

    TextView edtTitle;
    TextView edtAuthor;
    TextView edtDegree;
    TextView edtSpecial;
    TextView edtPrice;
    TextView edtMrp;
    ImageView uploadImage;
    TextView edtSellerMsg;
    Button btAdd;
    private SimpleDraweeView swView;
    Dialog picDialog;
    public File filen = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private Toolbar toolbar;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    Uri photocUri;
    String downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notemymaterials);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        toolbar = findViewById(R.id.toolbarSell);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.main_color));
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        picDialog = new Dialog(this);
        picDialog.setContentView(R.layout.selectphotodialog);

        edtTitle = findViewById(R.id.etMaterialTitle);
        edtAuthor = findViewById(R.id.etAuthor);
        edtDegree = findViewById(R.id.etDegree);
        edtSpecial = findViewById(R.id.etSpecial);
        edtMrp = findViewById(R.id.etMrp);
        edtPrice = findViewById(R.id.etPrice);
        edtSellerMsg = findViewById(R.id.etNoteToBuyer);
        btAdd = findViewById(R.id.btnPlaceAd);
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

        firestoreDB = FirebaseFirestore.getInstance();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            Picasso.with(EditMyMaterialsActivity.this).load(bundle.getString("UpdateNoteDownloadUri")).fit().into(swView);

            edtTitle.setText(bundle.getString("UpdateNoteTitle"));
            edtAuthor.setText(bundle.getString("UpdateNoteAuthor"));
            edtDegree.setText(bundle.getString("UpdateNoteDegree"));
            edtSpecial.setText(bundle.getString("UpdateNoteSpecialization"));
            edtPrice.setText(bundle.getString("UpdateNotePrice"));
            edtMrp.setText(bundle.getString("UpdateNoteMrp"));
            edtSellerMsg.setText(bundle.getString("UpdateNoteSellerMsg"));
        }

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                swView.setDrawingCacheEnabled(true);
                swView.buildDrawingCache();
                Bitmap bitmap1 = swView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                Long tsLong = System.currentTimeMillis() / 1000;
                final String user = tsLong.toString();

                final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                final String entryNameUpdated = user + userEmail;

                StorageReference imageReference = storageReference.child(userEmail).child("bookimages").child(entryNameUpdated);
                UploadTask uploadTask = imageReference.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditMyMaterialsActivity.this, "Failed to upload image and ad!!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();

                        final Map<String, String> note = new HashMap<>();

                        final String entryNameOld = bundle.getString("UpdateNoteEntryName");
                        String title = edtTitle.getText().toString();
                        String author = edtAuthor.getText().toString();
                        String degree = edtDegree.getText().toString();
                        String special = edtSpecial.getText().toString();
                        String price = edtPrice.getText().toString();
                        String mrp = edtMrp.getText().toString();
                        String sellerMsg = edtSellerMsg.getText().toString();
                        String location = bundle.getString("UpdateNoteLocation");
                        String timeStampUpdated = userEmail;

                        note.put("title", title);
                        note.put("author", author);
                        note.put("degree", degree);
                        note.put("specialization", special);
                        note.put("mrp", mrp);
                        note.put("price", price);
                        note.put("user", userEmail);
                        note.put("location", location);
                        note.put("timestamp", user);
                        note.put("entryName", entryNameUpdated);
                        note.put("sellerMsg", sellerMsg);
                        note.put("downloadUri", downloadUri);
                        //updateNote(entryNameUpdated, entryNameOld, title, author, degree, special, mrp, price, location, user, timeStampUpdated, sellerMsg, downloadUri);

                        //Map<String, Object> note = (new NoteMyMaterials(entryNameUpdated, title, author, degree, special, mrp, price, location, user, timeStampUpdated, sellerMsg, downloadUri)).toMap();


                        firestoreDB.collection("books")
                                .document(entryNameOld)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Old material has been deleted!", Toast.LENGTH_SHORT).show();

                                        StorageReference imageReference1 = storageReference.child(userEmail).child("bookimages").child(entryNameOld);

                                        imageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(EditMyMaterialsActivity.this, "Book image deleted successfully!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(EditMyMaterialsActivity.this, "Cannot delete book image", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                        firestoreDB.collection("books")
                                .document(entryNameUpdated)
                                .set(note)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Ad updated successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "cannot update ad" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                        finish();

                    }
                });

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutMenu: {
                Logout();
            }
        }

        switch (item.getItemId()) {

            case R.id.sellMenu: {
                sellBook();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void sellBook() {
        Intent intent = new Intent(EditMyMaterialsActivity.this, SellActivity.class);
        startActivity(intent);
    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(EditMyMaterialsActivity.this, EntryActivity.class));
    }

    private void checkPermissionCA(){
        int permissionCheck = ContextCompat.checkSelfPermission(EditMyMaterialsActivity.this, android.Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    EditMyMaterialsActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_REQUEST_CAMERA);
        } else {
            catchPhoto();
        }
    }
    private void checkPermissionCW(){
        int permissionCheck = ContextCompat.checkSelfPermission(EditMyMaterialsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    EditMyMaterialsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_CAMERA);
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
                Toast.makeText(EditMyMaterialsActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditMyMaterialsActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionRG(){
        int permissionCheck = ContextCompat.checkSelfPermission(EditMyMaterialsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    EditMyMaterialsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
        } else {
            checkPermissionWG();
        }
    }
    private void checkPermissionWG(){
        int permissionCheck = ContextCompat.checkSelfPermission(EditMyMaterialsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    EditMyMaterialsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_GALLERY);
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