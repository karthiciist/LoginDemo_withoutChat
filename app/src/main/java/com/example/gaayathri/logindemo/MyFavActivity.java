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
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.logindemo.model.Note;
import com.example.gaayathri.logindemo.viewholder.NoteViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavActivity extends AppCompatActivity implements OnLikeListener, OnAnimationEndListener {

    private DrawerLayout mDrawerLayout;

    private static final String TAG = "SecondActivity";

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private List<Note> notesList;
    private ProgressDialog progressDialog;

    private Toolbar toolbar;
    Dialog myDialog;
    Dialog myDialog2;

    String entryName;
    String downloadUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fav);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.favexpandeddialog);

        myDialog2 = new Dialog(this);
        myDialog2.setContentView(R.layout.imageexpanded);

        LikeButton likeButton = myDialog.findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        likeButton.setLiked(true);

        Button btnCallSeller = myDialog.findViewById(R.id.btnCallSeller);
        Button btnChatSeller = myDialog.findViewById(R.id.btnChatSeller);

        btnCallSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyFavActivity.this, "Calling seller!!!", Toast.LENGTH_SHORT).show();
            }
        });

        btnChatSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyFavActivity.this, "Chatting seller!!!", Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog = new ProgressDialog(this);

        NavigationView navigationView = findViewById(R.id.nav_view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();
        TextView txtProfileName = navigationView.getHeaderView(0).findViewById(R.id.tvProfileName);
        txtProfileName.setText(name);

        ImageView ivProfilePic = navigationView.getHeaderView(0).findViewById(R.id.ivProfilePic);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        ivProfilePic.setImageDrawable(mDrawable);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        menuItem.setChecked(true);

                        mDrawerLayout.closeDrawers();

                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.home:
                                Intent i0 = new Intent(MyFavActivity.this, HomeActivity.class);
                                startActivity(i0);
                                break;
                            case R.id.nav_all:
                                Intent intent0 = new Intent(MyFavActivity.this, SecondActivity.class);
                                startActivity(intent0);
                                break;
                            case R.id.nav_category:
                                Intent intent = new Intent(MyFavActivity.this, CategoryActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_profile:
                                Intent intent1 = new Intent(MyFavActivity.this, MyProfileActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_materials:
                                Intent intent2 = new Intent(MyFavActivity.this, MyMaterialsActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_fav:
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(MyFavActivity.this, SettingsActivity.class);
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


        recyclerView = findViewById(R.id.rvNoteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        loadNotesList();

        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

        firestoreListener = firestoreDB.collection("books").whereEqualTo(uuid, true).orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(MyFavActivity.this, "Cannot retrieve your favorites", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        notesList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            Note note = doc.toObject(Note.class);
                            note.setId(doc.getId());
                            notesList.add(note);
                        }

                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firestoreListener.remove();
    }


    private void loadNotesList() {

        String uuid = firebaseAuth.getCurrentUser().getUid();

        CollectionReference booksRef = firestoreDB.collection("books");

        Query query = booksRef.whereEqualTo(uuid, true).orderBy("timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Note> response = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHolder holder, int position, Note model) {
                final Note note = notesList.get(position);

                holder.title.setText(note.getTitle());
                holder.author.setText(note.getAuthor());
                holder.degree.setText(note.getDegree());
                holder.specialization.setText(note.getSpecialization());
                holder.mrp.setText(note.getMrp());
                holder.price.setText(note.getPrice());
                holder.location.setText(note.getLocation());

                Picasso.with(MyFavActivity.this).load(note.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

                holder.onclicklinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressDialog.setMessage("Loading your favorite material...");
                        progressDialog.show();

                        TextView titleFav = myDialog.findViewById(R.id.titleFav);
                        TextView authFav = myDialog.findViewById(R.id.authorFav);
                        TextView degreeFav = myDialog.findViewById(R.id.degreeFav);
                        TextView specialFav = myDialog.findViewById(R.id.specialFav);
                        TextView locationFav = myDialog.findViewById(R.id.locationFav);
                        TextView mrpFav = myDialog.findViewById(R.id.mrpPriceFav);
                        TextView priceFav = myDialog.findViewById(R.id.priceFav);
                        TextView userFav = myDialog.findViewById(R.id.userFav);
                        TextView sellerMsgFav = myDialog.findViewById(R.id.sellerMsgFav);

                        ImageView bookpic = myDialog.findViewById(R.id.ivBookPic);

                        titleFav.setText(note.getTitle());
                        authFav.setText(note.getAuthor());
                        degreeFav.setText(note.getDegree());
                        specialFav.setText(note.getSpecialization());
                        locationFav.setText(note.getLocation());
                        mrpFav.setText(note.getMrp());
                        priceFav.setText(note.getPrice());
                        userFav.setText(note.getuser());
                        sellerMsgFav.setText(note.getSellerMsg());

                        entryName = note.getEntryName();

                        mrpFav.setPaintFlags(mrpFav.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        Picasso.with(MyFavActivity.this).load(note.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(MyFavActivity.this).load(note.getDownloadUri()).fit().into(ivExpandedPic);
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

                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note, parent, false);

                return new NoteViewHolder(view);
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


    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MyFavActivity.this, EntryActivity.class));
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

        switch (item.getItemId()){

            case R.id.sellMenu:{
                sellBook();
            }

        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sellBook() {
        Intent intent = new Intent(MyFavActivity.this, SellActivity.class);
        startActivity(intent);
    }



    @Override
    public void liked(LikeButton likeButton) {

        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

        TextView titleFav = myDialog.findViewById(R.id.titleFav);
        TextView authFav = myDialog.findViewById(R.id.authorFav);
        TextView degreeFav = myDialog.findViewById(R.id.degreeFav);
        TextView specialFav = myDialog.findViewById(R.id.specialFav);
        TextView locationFav = myDialog.findViewById(R.id.locationFav);
        TextView mrpFav = myDialog.findViewById(R.id.mrpPriceFav);
        TextView priceFav = myDialog.findViewById(R.id.priceFav);
        TextView userFav = myDialog.findViewById(R.id.userFav);
        TextView sellerMsgFav = myDialog.findViewById(R.id.sellerMsgFav);
        ImageView bookpic = myDialog.findViewById(R.id.ivBookPic);



        final Map<String, String> bookMap = new HashMap<>();

        bookMap.put("liked", entryName);
        bookMap.put("title", titleFav.getText().toString());
        bookMap.put("author", authFav.getText().toString());
        bookMap.put("degree", degreeFav.getText().toString());
        bookMap.put("specialization", specialFav.getText().toString());
        bookMap.put("location", locationFav.getText().toString());
        bookMap.put("price", priceFav.getText().toString());
        bookMap.put("mrp", mrpFav.getText().toString());
        bookMap.put("user",userFav.getText().toString());
        bookMap.put("sellerMsg", sellerMsgFav.getText().toString());
        bookMap.put("downloadUri", downloadUri);

        firestoreDB.collection("users").document(email).collection("favorites").document(entryName).set(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyFavActivity.this, "Added to favorites!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyFavActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> data = new HashMap<>();
        data.put(uuid, true);

        firestoreDB.collection("books").document(entryName)
                .set(data, SetOptions.merge());

    }

    @Override
    public void unLiked(LikeButton likeButton) {

        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

        firestoreDB.collection("users").document(email).collection("favorites").document(entryName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MyFavActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyFavActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                    }
                });

        Map<String, Object> data = new HashMap<>();
        data.put(uuid, false);

        firestoreDB.collection("books").document(entryName)
                .set(data, SetOptions.merge());

    }

    @Override
    public void onAnimationEnd(LikeButton likeButton) {
        Log.d("ExpandedView", "Animation End for %s" + likeButton);
    }


}

