package com.example.gaayathri.logindemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.logindemo.model.NoteHorizontal;
import com.example.gaayathri.logindemo.model.NoteHorizontal1;
import com.example.gaayathri.logindemo.viewholder.NoteViewHorizontal;
import com.example.gaayathri.logindemo.viewholder.NoteViewHorizontal1;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
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

import ss.com.bannerslider.Slider;

import static com.example.gaayathri.logindemo.SplashScreenActivity.City;
import static com.example.gaayathri.logindemo.SplashScreenActivity.Degree;
import static com.example.gaayathri.logindemo.SplashScreenActivity.mypreference;


public class HomeActivity extends AppCompatActivity implements OnLikeListener, OnAnimationEndListener {

    private DrawerLayout mDrawerLayout;
    private static final String TAG = "SecondActivity";
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    //private RecyclerView recyclerView2;
    //private RecyclerView recyclerView3;
    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter1;
    private FirestoreRecyclerAdapter adapter2;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private ListenerRegistration firestoreListener1;
    private List<NoteHorizontal> notesList;
    private List<NoteHorizontal1> notesList1;
    private ProgressDialog progressDialog;
    private Slider slider;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private Toolbar toolbar;
    Dialog myDialog;
    Dialog myDialog2;
    String entryName;
    String downloadUri;

    String userNameL;
    String userPhoneL;
    String userDegreeL;
    String userSpecialL;
    String userCityL;
    String userProfilePicL;

    String UpdateNoteTitle;
    String UpdateNoteAuthor;
    String UpdateNoteDegree;
    String UpdateNoteSpecialization;
    String UpdateNotePrice;
    String UpdateNoteLocation;
    String UpdateNoteUser;
    String UpdateNoteMrp;
    String UpdateNoteSellerMsg;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    public static final String City = "cityKey";
    public static final String Degree = "degreeKey";

    String userCity;
    String userDegree;

    LinearLayout llAds;
    LinearLayout llFavs;

    TextView noAdsPlaced;
    TextView noLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Slider.init(new PicassoImageLoadingService(this));

        slider = findViewById(R.id.banner_slider1);
        slider.setAdapter(new MainSliderAdapter());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.expandeddialog);

        LikeButton likeButton = myDialog.findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        myDialog2 = new Dialog(this);
        myDialog2.setContentView(R.layout.imageexpanded);

        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.rvNoteList);
        recyclerView1 = findViewById(R.id.rvHome1);
        recyclerView2 = findViewById(R.id.rvHome1);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(mLayoutManager1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());

        NavigationView navigationView = findViewById(R.id.nav_view);

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
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_all:
                                Intent intent = new Intent(HomeActivity.this, SecondActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_category:
                                Intent intent1 = new Intent(HomeActivity.this, CategoryActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_profile:
                                Intent intent0 = new Intent(HomeActivity.this, MyProfileActivity.class);
                                startActivity(intent0);
                                break;
                            case R.id.nav_materials:
                                Intent intent2 = new Intent(HomeActivity.this, MyMaterialsActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_chat:

                                checkPermissionCA();

                                break;
                            case R.id.nav_fav:
                                Intent intent3 = new Intent(HomeActivity.this, MyFavActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(HomeActivity.this, SettingsActivity.class);
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

        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final String mail = firebaseAuth.getCurrentUser().getEmail();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        TextView test = findViewById(R.id.testTV);

        //final Bundle bundle = getIntent().getExtras();
        //if (bundle != null) {
        if (sharedpreferences.contains(City)) {

            //test.setText(bundle.getString("userCityL"));
            //userCity = bundle.getString("userCityL");
            userCity = sharedpreferences.getString(City, "");

            firestoreListener = firestoreDB.collection("books").whereEqualTo("location", userCity)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots1, FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(HomeActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            notesList1 = new ArrayList<>();

                            for (DocumentSnapshot doc1 : documentSnapshots1) {
                                NoteHorizontal1 noteHorizontal1 = doc1.toObject(NoteHorizontal1.class);
                                noteHorizontal1.setEntryName(doc1.getId());
                                notesList1.add(noteHorizontal1);
                            }

                            adapter1.notifyDataSetChanged();
                            recyclerView1.setAdapter(adapter1);
                        }
                    });

            loadNotesList1(userCity);
        }

        if (sharedpreferences.contains(Degree)) {

            //test.setText(bundle.getString("userCityL"));
            //userCity = bundle.getString("userCityL");
            userDegree = sharedpreferences.getString(Degree, "");

            firestoreListener = firestoreDB.collection("books").whereEqualTo("degree", userDegree)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(HomeActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            notesList = new ArrayList<>();

                            for (DocumentSnapshot doc : documentSnapshots) {
                                NoteHorizontal noteHorizontal = doc.toObject(NoteHorizontal.class);
                                noteHorizontal.setEntryName(doc.getId());
                                notesList.add(noteHorizontal);
                            }

                            adapter2.notifyDataSetChanged();
                            recyclerView2.setAdapter(adapter2);
                        }
                    });

            loadNotesList2(userDegree);
        }

        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firestoreListener = firestoreDB.collection("books").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(HomeActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
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


        loadNotesList();

        ImageView im1 = findViewById(R.id.imSell1);
        ImageView im2 = findViewById(R.id.imSell2);

        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SellActivity.class);
                startActivity(intent);
            }
        });

        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SellActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadNotesList1(String userCity) {

        TextView test = findViewById(R.id.testTV);
        test.setText(userCity);


        final Query query1 = firestoreDB.collection("books").whereEqualTo("location", userCity);

        FirestoreRecyclerOptions<NoteHorizontal1> response = new FirestoreRecyclerOptions.Builder<NoteHorizontal1>()
                .setQuery(query1, NoteHorizontal1.class)
                .build();

        adapter1 = new FirestoreRecyclerAdapter<NoteHorizontal1, NoteViewHorizontal1>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHorizontal1 holder, int position, NoteHorizontal1 model) {

                final NoteHorizontal1 noteHorizontal1 = notesList1.get(position);

                holder.title.setText(noteHorizontal1.getTitle());
                holder.author.setText(noteHorizontal1.getAuthor());

                Picasso.with(HomeActivity.this).load(noteHorizontal1.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

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

                        UpdateNoteTitle = noteHorizontal1.getTitle();
                        titleExp.setText(UpdateNoteTitle);

                        UpdateNoteAuthor = noteHorizontal1.getAuthor();
                        authExp.setText(UpdateNoteAuthor);

                        UpdateNoteDegree = noteHorizontal1.getDegree();
                        degreeExp.setText(UpdateNoteDegree);

                        UpdateNoteSpecialization = noteHorizontal1.getSpecialization();
                        specialExp.setText(UpdateNoteSpecialization);

                        UpdateNoteLocation = noteHorizontal1.getLocation();
                        locationExp.setText(UpdateNoteLocation);

                        UpdateNoteMrp = noteHorizontal1.getMrp();
                        mrpExp.setText(UpdateNoteMrp);

                        UpdateNotePrice = noteHorizontal1.getPrice();
                        priceExp.setText(UpdateNotePrice);

                        UpdateNoteSellerMsg = noteHorizontal1.getSellerMsg();
                        sellerMsgExp.setText(UpdateNoteSellerMsg);

                        UpdateNoteUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        userExp.setText(UpdateNoteUser);

                        entryName = noteHorizontal1.getEntryName();

                        mrpExp.setPaintFlags(mrpExp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        checkFavorited();

                        Picasso.with(HomeActivity.this).load(noteHorizontal1.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(HomeActivity.this).load(noteHorizontal1.getDownloadUri()).fit().into(ivExpandedPic);
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

                        Toast.makeText(HomeActivity.this, entryName, Toast.LENGTH_SHORT).show();

                    }
                });

                //Button chat = myDialog.findViewById(R.id.btnChatSeller);
                //Button call = myDialog.findViewById(R.id.btnCallSeller);
                //LikeButton likeButton = myDialog.findViewById(R.id.heart_button);

                //likeButton.setOnLikeListener();
                //likeButton.setOnAnimationEndListener(this);


            }

            @Override
            public NoteViewHorizontal1 onCreateViewHolder(ViewGroup parent1, int viewType) {
                View view = LayoutInflater.from(parent1.getContext())
                        .inflate(R.layout.horizontal_item, parent1, false);

                return new NoteViewHorizontal1(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter1.notifyDataSetChanged();
        recyclerView1.setAdapter(adapter1);
        adapter1.startListening();

    }

    private void loadNotesList2(String userDegree) {

        TextView test = findViewById(R.id.testTV1);
        test.setText(userDegree);


        final Query query = firestoreDB.collection("books").whereEqualTo("degree", userDegree);

        FirestoreRecyclerOptions<NoteHorizontal> response = new FirestoreRecyclerOptions.Builder<NoteHorizontal>()
                .setQuery(query, NoteHorizontal.class)
                .build();

        adapter2 = new FirestoreRecyclerAdapter<NoteHorizontal, NoteViewHorizontal>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHorizontal holder, int position, NoteHorizontal model) {

                final NoteHorizontal noteHorizontal = notesList.get(position);

                holder.title.setText(noteHorizontal.getTitle());
                holder.author.setText(noteHorizontal.getAuthor());

                Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

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

                        checkFavorited();

                        Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().into(ivExpandedPic);
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

                        Toast.makeText(HomeActivity.this, entryName, Toast.LENGTH_SHORT).show();

                    }
                });

                //Button chat = myDialog.findViewById(R.id.btnChatSeller);
                //Button call = myDialog.findViewById(R.id.btnCallSeller);
                //LikeButton likeButton = myDialog.findViewById(R.id.heart_button);

                //likeButton.setOnLikeListener();
                //likeButton.setOnAnimationEndListener(this);


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

        adapter2.notifyDataSetChanged();
        recyclerView2.setAdapter(adapter2);
        adapter2.startListening();

    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HomeActivity.this, EntryActivity.class));
    }

    private void loadNotesList() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();

        final Query query = firestoreDB.collection("books").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<NoteHorizontal> response = new FirestoreRecyclerOptions.Builder<NoteHorizontal>()
                .setQuery(query, NoteHorizontal.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<NoteHorizontal, NoteViewHorizontal>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHorizontal holder, int position, NoteHorizontal model) {

                final NoteHorizontal noteHorizontal = notesList.get(position);

                holder.title.setText(noteHorizontal.getTitle());
                holder.author.setText(noteHorizontal.getAuthor());

                Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(holder.bookpic);

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

                        checkFavorited();

                        Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(HomeActivity.this).load(noteHorizontal.getDownloadUri()).fit().into(ivExpandedPic);
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

                        Toast.makeText(HomeActivity.this, entryName, Toast.LENGTH_SHORT).show();

                    }
                });

                //Button chat = myDialog.findViewById(R.id.btnChatSeller);
                //Button call = myDialog.findViewById(R.id.btnCallSeller);
                //LikeButton likeButton = myDialog.findViewById(R.id.heart_button);

                //likeButton.setOnLikeListener();
                //likeButton.setOnAnimationEndListener(this);


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
            Toast.makeText(HomeActivity.this, "Edit clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                Toast.makeText(HomeActivity.this, "Added to favorites!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HomeActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
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

    private void checkPermissionCA(){
        int permissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    HomeActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            checkPermissionCA();
        } else {
            launchChat();
        }
    }

    private void launchChat(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        IChatUser iChatUser = new ChatUser(user.getUid(), user.getDisplayName());

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        ChatManager.Configuration mChatConfiguration =
                new ChatManager.Configuration.Builder(getString(R.string.google_app_id))
                        .firebaseUrl("https://login-demo-3c273.firebaseio.com/")
                        .storageBucket("gs://login-demo-3c273.appspot.com")
                        .build();

        ChatManager.start(this, mChatConfiguration, iChatUser);

        ChatUI.getInstance().setContext(this);

        ChatUI.getInstance().openConversationsListActivity();

    }
}
