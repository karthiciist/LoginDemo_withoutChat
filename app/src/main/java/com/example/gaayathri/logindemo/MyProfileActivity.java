package com.example.gaayathri.logindemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.logindemo.model.NoteHorizontal;
import com.example.gaayathri.logindemo.viewholder.NoteViewHorizontal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyProfileActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private static final String TAG = "SecondActivity";
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private List<NoteHorizontal> notesList;
    private ProgressDialog progressDialog;

    private Toolbar toolbar;
    Dialog myDialog;
    Dialog myDialog2;
    String entryName;
    String downloadUri;

    String UpdateNoteTitle;
    String UpdateNoteAuthor;
    String UpdateNoteDegree;
    String UpdateNoteSpecialization;
    String UpdateNotePrice;
    String UpdateNoteLocation;
    String UpdateNoteUser;
    String UpdateNoteMrp;
    String UpdateNoteSellerMsg;

    String userName;
    String name;

    String userNameL;
    String userPhoneL;
    String userDegreeL;
    String userSpecialL;
    String userCityL;
    String userProfilePicL;

    LinearLayout llAds;
    LinearLayout llFavs;

    TextView noAdsPlaced;
    TextView noLikes;
    TextView userNametv;
    TextView userCity;
    TextView userDegree;
    TextView userSpecial;
    TextView userPhoneNo;
    TextView userEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();

        String userName = user.getDisplayName();

        userNametv = findViewById(R.id.tvUserName);
        userCity = findViewById(R.id.tvUserCity);
        userDegree = findViewById(R.id.tvUserDegree);
        userSpecial = findViewById(R.id.tvUserSpecial);
        userPhoneNo = findViewById(R.id.tvUserPhoneNo);
        userEmailId = findViewById(R.id.tvUserEmailId);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.expandeddialog);

        myDialog2 = new Dialog(this);
        myDialog2.setContentView(R.layout.imageexpanded);

        progressDialog = new ProgressDialog(this);

        noAdsPlaced = findViewById(R.id.tvNoOfAdsPlaced);
        noLikes = findViewById(R.id.tvNoOfLikes);

        NavigationView navigationView = findViewById(R.id.nav_view);

        TextView txtProfileName = navigationView.getHeaderView(0).findViewById(R.id.tvProfileName);
        txtProfileName.setText(name);

        ImageView ivProfilePic = findViewById(R.id.ivProfilePic);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        ivProfilePic.setImageDrawable(mDrawable);

        ImageView ivProfilePicDraw = navigationView.getHeaderView(0).findViewById(R.id.ivProfilePic);
        Bitmap bitmapDraw = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        RoundedBitmapDrawable mDrawableDraw = RoundedBitmapDrawableFactory.create(getResources(), bitmapDraw);
        mDrawableDraw.setCircular(true);
        ivProfilePicDraw.setImageDrawable(mDrawable);

        llAds = findViewById(R.id.llAds);
        llAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, MyMaterialsActivity.class);
                startActivity(intent);
            }
        });

        llFavs = findViewById(R.id.llFavs);
        llFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, MyFavActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.home:
                                Intent i0 = new Intent(MyProfileActivity.this, HomeActivity.class);
                                startActivity(i0);
                                break;
                            case R.id.nav_all:
                                Intent intent = new Intent(MyProfileActivity.this, SecondActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_category:
                                Intent intent1 = new Intent(MyProfileActivity.this, CategoryActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_profile:
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_materials:
                                Intent intent2 = new Intent(MyProfileActivity.this, MyMaterialsActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_fav:
                                Intent intent3 = new Intent(MyProfileActivity.this, MyFavActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(MyProfileActivity.this, SettingsActivity.class);
                                startActivity(intent4);
                                break;
                            case R.id.nav_logout:
                                Logout();
                                break;
                        }

                        return true;
                    }
                });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.main_color));
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final String mail = firebaseAuth.getCurrentUser().getEmail();

        DocumentReference docRef = firestoreDB.collection("users").document(mail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        userNameL = document.getString("UserName");
                        userPhoneL = document.getString("UserPhoneNo");
                        userDegreeL = document.getString("UserDegree");
                        userSpecialL = document.getString("UserSpecial");
                        userCityL = document.getString("UserCity");
                        userProfilePicL = document.getString("downloadUri");

                        userNametv.setText(userNameL);
                        userCity.setText(userCityL);
                        userDegree.setText(userDegreeL);
                        userSpecial.setText(userSpecialL);
                        userPhoneNo.setText(userPhoneL);
                        userEmailId.setText(mail);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        recyclerView = findViewById(R.id.rvNoteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadNotesList();

        firestoreListener = firestoreDB.collection("books").whereEqualTo("user", name)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(MyProfileActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        notesList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            NoteHorizontal noteHorizontal = doc.toObject(NoteHorizontal.class);
                            noteHorizontal.setEntryName(doc.getId());
                            notesList.add(noteHorizontal);
                        }

                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                });

        progressDialog.setMessage("Loading your profile...");
        progressDialog.show();

        firestoreDB.collection("books")
                .whereEqualTo("user", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int adsPlacedSize = querySnapshot.size();
                            String adsPlacedSizeString = String.valueOf(adsPlacedSize);
                            noAdsPlaced.setText(adsPlacedSizeString);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        firestoreDB.collection("users").document(name).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int likesSize = querySnapshot.size();
                            String likesSizeString = String.valueOf(likesSize);
                            noLikes.setText(likesSizeString);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        progressDialog.dismiss();
    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MyProfileActivity.this, EntryActivity.class));
    }

    private void loadNotesList() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();

        final Query query = firestoreDB.collection("books").whereEqualTo("user", name);

        FirestoreRecyclerOptions<NoteHorizontal> response = new FirestoreRecyclerOptions.Builder<NoteHorizontal>()
                .setQuery(query, NoteHorizontal.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<NoteHorizontal, NoteViewHorizontal>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHorizontal holder, int position, NoteHorizontal model) {

                final NoteHorizontal noteHorizontal = notesList.get(position);

                holder.title.setText(noteHorizontal.getTitle());
                holder.author.setText(noteHorizontal.getAuthor());

                Picasso.with(MyProfileActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

                holder.onclicklinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView titleExp = myDialog.findViewById(R.id.titleExp);
                        TextView authExp = myDialog.findViewById(R.id.authorExp);
                        TextView degreeExp = myDialog.findViewById(R.id.degreeExp);
                        TextView specialExp = myDialog.findViewById(R.id.specialExp);
                        TextView locationExp = myDialog.findViewById(R.id.locationExp);
                        TextView mrpExp = myDialog.findViewById(R.id.mrpPriceExp);
                        TextView priceExp = myDialog.findViewById(R.id.priceExp);
                        TextView userExp = myDialog.findViewById(R.id.userExp);
                        TextView sellerMsgExp = myDialog.findViewById(R.id.sellerMsgExp);

                        ImageView bookpic = myDialog.findViewById(R.id.ivBookPic);

                        UpdateNoteTitle = noteHorizontal.getTitle();
                        titleExp.setText(UpdateNoteTitle);

                        UpdateNoteAuthor = noteHorizontal.getAuthor();
                        authExp.setText(UpdateNoteAuthor);

                        UpdateNoteDegree = noteHorizontal.getDegree();
                        degreeExp.setText(UpdateNoteDegree);

                        UpdateNoteSpecialization = noteHorizontal.getSpecialization();
                        specialExp.setText(UpdateNoteSpecialization);

                        UpdateNoteLocation = noteHorizontal.getLocation();
                        locationExp.setText(UpdateNoteLocation);

                        UpdateNoteMrp = noteHorizontal.getMrp();
                        mrpExp.setText(UpdateNoteMrp);

                        UpdateNotePrice = noteHorizontal.getPrice();
                        priceExp.setText(UpdateNotePrice);

                        UpdateNoteSellerMsg = noteHorizontal.getSellerMsg();
                        sellerMsgExp.setText(UpdateNoteSellerMsg);

                        UpdateNoteUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        userExp.setText(UpdateNoteUser);

                        entryName = noteHorizontal.getEntryName();

                        mrpExp.setPaintFlags(mrpExp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        Picasso.with(MyProfileActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(MyProfileActivity.this).load(noteHorizontal.getDownloadUri()).fit().into(ivExpandedPic);
                                myDialog2.show();
                            }
                        });

                        ImageView tvClose = myDialog2.findViewById(R.id.tvClose);

                        tvClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog2.dismiss();
                            }
                        });

                        Toast.makeText(MyProfileActivity.this, entryName, Toast.LENGTH_SHORT).show();

                    }
                });

                Button chat = myDialog.findViewById(R.id.btnChatSeller);
                Button call = myDialog.findViewById(R.id.btnCallSeller);
                LikeButton likeButton = myDialog.findViewById(R.id.heart_button);

                chat.setVisibility(View.GONE);
                call.setVisibility(View.GONE);
                likeButton.setVisibility(View.GONE);

            }

            @Override
            public NoteViewHorizontal onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.horizontal_item, parent, false);

                return new NoteViewHorizontal(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit) {
            updateNote();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNote() {

        Intent intent = new Intent(this, EditMyProfileActivity.class);

        intent.putExtra("UpdateUserName", userName);
        intent.putExtra("UpdateUserCity", userCity.getText().toString());
        intent.putExtra("UpdateUserDegree", userDegree.getText().toString());
        intent.putExtra("UpdateUserSpecial", userSpecial.getText().toString());
        intent.putExtra("UpdateUserPhoneNo", userPhoneNo.getText().toString());
        intent.putExtra("UpdateUserEmailId", userEmailId.getText().toString());
        intent.putExtra("UpdateNoteDownloadUri", downloadUri);
        startActivity(intent);
    }

}
