package com.example.gaayathri.logindemo;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.gaayathri.logindemo.model.NoteHorizontal;
import com.example.gaayathri.logindemo.viewholder.NoteViewHolder;
import com.example.gaayathri.logindemo.viewholder.NoteViewHorizontal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.SetOptions;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;


import org.chat21.android.core.ChatManager;
import org.chat21.android.core.users.models.ChatUser;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondActivity extends AppCompatActivity implements OnLikeListener, OnAnimationEndListener {

    private DrawerLayout mDrawerLayout;

    private static final String TAG = "SecondActivity";

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private List<Note> notesList;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    private List<NoteHorizontal> notesListHorizontal;

    private Toolbar toolbar;
    Dialog myDialog;
    Dialog myDialog2;
    Dialog myDialog3;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.expandeddialog);

        myDialog2 = new Dialog(this);
        myDialog2.setContentView(R.layout.imageexpanded);

        TextView userName = myDialog.findViewById(R.id.userExp);

        final String userEmail = userName.getText().toString();

        myDialog3 = new Dialog(SecondActivity.this);
        myDialog3.setContentView(R.layout.userprofile);

        LikeButton likeButton = myDialog.findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        Button btnCallSeller = myDialog.findViewById(R.id.btnCallSeller);
        Button btnChatSeller = myDialog.findViewById(R.id.btnChatSeller);

        btnCallSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "Calling seller!!!", Toast.LENGTH_SHORT).show();
            }
        });

        btnChatSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "Chatting seller!!!", Toast.LENGTH_SHORT).show();
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
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.home:
                                Intent i0 = new Intent(SecondActivity.this, HomeActivity.class);
                                startActivity(i0);
                                break;
                            case R.id.nav_all:
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_category:
                                Intent intent = new Intent(SecondActivity.this, CategoryActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_profile:
                                Intent intent1 = new Intent(SecondActivity.this, MyProfileActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_materials:
                                Intent intent2 = new Intent(SecondActivity.this, MyMaterialsActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_fav:
                                Intent intent3 = new Intent(SecondActivity.this, MyFavActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(SecondActivity.this, SettingsActivity.class);
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

        fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.rvNoteList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        loadNotesList();

        firestoreListener = firestoreDB.collection("books").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked();
            }
        });

        firestoreListener = firestoreDB.collection("books").whereEqualTo("user", name)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(SecondActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        notesListHorizontal = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            NoteHorizontal noteHorizontal = doc.toObject(NoteHorizontal.class);
                            noteHorizontal.setEntryName(doc.getId());
                            notesListHorizontal.add(noteHorizontal);
                        }

                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                });

    }


    private void loadUserBooks(String userEmail) {

        String name = userEmail;

        final Query query = firestoreDB.collection("books").whereEqualTo("user", name);

        FirestoreRecyclerOptions<NoteHorizontal> response = new FirestoreRecyclerOptions.Builder<NoteHorizontal>()
                .setQuery(query, NoteHorizontal.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<NoteHorizontal, NoteViewHorizontal>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHorizontal holder, int position, NoteHorizontal model) {

                final NoteHorizontal noteHorizontal = notesListHorizontal.get(position);

                holder.title.setText(noteHorizontal.getTitle());
                holder.author.setText(noteHorizontal.getAuthor());

                Picasso.with(SecondActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

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

                        Picasso.with(SecondActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(SecondActivity.this).load(noteHorizontal.getDownloadUri()).fit().into(ivExpandedPic);
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

                        Toast.makeText(SecondActivity.this, entryName, Toast.LENGTH_SHORT).show();

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


    private void fabClicked() {

        Intent intent = new Intent(SecondActivity.this, SellActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firestoreListener.remove();
    }

    private void loadNotesList() {

        Query query = firestoreDB.collection("books").orderBy("timestamp", Query.Direction.DESCENDING);

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

                Picasso.with(SecondActivity.this).load(note.getDownloadUri()).fit().centerCrop().into(holder.bookpic);


                holder.onclicklinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressDialog.setMessage("Loading your favorite material...");
                        progressDialog.show();

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

                        titleExp.setText(note.getTitle());
                        authExp.setText(note.getAuthor());
                        degreeExp.setText(note.getDegree());
                        specialExp.setText(note.getSpecialization());
                        locationExp.setText(note.getLocation());
                        mrpExp.setText(note.getMrp());
                        priceExp.setText(note.getPrice());
                        userExp.setText(note.getuser());
                        sellerMsgExp.setText(note.getSellerMsg());

                        final String uid = note.getUid().toString();

                        Button chatSeller = myDialog.findViewById(R.id.btnChatSeller);

                        chatSeller.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                launchOneToOneChat(uid, "Bommi");

                            }
                        });

                        Picasso.with(SecondActivity.this).load(note.getDownloadUri()).fit().centerCrop().into(bookpic);

                        entryName = note.getEntryName();
                        downloadUri = note.getDownloadUri();

                        mrpExp.setPaintFlags(mrpExp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        checkFavorited();

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(SecondActivity.this).load(note.getDownloadUri()).fit().into(ivExpandedPic);
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
        startActivity(new Intent(SecondActivity.this, EntryActivity.class));
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
        Intent intent = new Intent(SecondActivity.this, SellActivity.class);
        startActivity(intent);
    }

    @Override
    public void liked(LikeButton likeButton) {

        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

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



        final Map<String, String> bookMap = new HashMap<>();

        bookMap.put("liked", entryName);
        bookMap.put("title", titleExp.getText().toString());
        bookMap.put("author", authExp.getText().toString());
        bookMap.put("degree", degreeExp.getText().toString());
        bookMap.put("specialization", specialExp.getText().toString());
        bookMap.put("location", locationExp.getText().toString());
        bookMap.put("price", priceExp.getText().toString());
        bookMap.put("mrp", mrpExp.getText().toString());
        bookMap.put("user", userExp.getText().toString());
        bookMap.put("sellerMsg", sellerMsgExp.getText().toString());
        bookMap.put("downloadUri", downloadUri);

        firestoreDB.collection("users").document(email).collection("favorites").document(entryName).set(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SecondActivity.this, "Added to favorites!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SecondActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SecondActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SecondActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
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

    private void checkFavorited() {

        //Intent intentExtras = getIntent();
        //Bundle bundle = intentExtras.getExtras();
        //String entryName = bundle.getString("arg");
        String email = firebaseAuth.getCurrentUser().getEmail();

        final LikeButton likeButton = myDialog.findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        DocumentReference docRef = firestoreDB.collection("users").document(email).collection("favorites").document(entryName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    likeButton.setLiked(true);

                } else {
                    likeButton.setLiked(false);
                }
            }
        });
    }

    private void launchOneToOneChat(String uid, String name){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        IChatUser iChatUser = new ChatUser(user.getUid(), user.getDisplayName());

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        ChatManager.Configuration mChatConfiguration =
                new ChatManager.Configuration.Builder(getString(R.string.google_app_id))
                        .firebaseUrl("https://login-demo-3c273.firebaseio.com/")
                        .storageBucket("gs://login-demo-3c273.appspot.com")
                        .build();

        ChatManager.start(this, mChatConfiguration, iChatUser);

        ChatUI.getInstance().setContext(this);

        ChatUI.getInstance().openConversationMessagesActivity(uid, name);

    }

}