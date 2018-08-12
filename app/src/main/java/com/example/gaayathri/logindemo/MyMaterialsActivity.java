package com.example.gaayathri.logindemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.example.gaayathri.logindemo.model.NoteMyMaterials;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.gaayathri.logindemo.viewholder.NoteViewHolderMyMaterials;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyMaterialsActivity extends AppCompatActivity {

    private static final String TAG = "MyMaterialsActivity";

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;
    private List<NoteMyMaterials> notesList;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    Dialog myDialog;
    Dialog myDialog2;
    String entryName;

    String UpdateNoteTitle;
    String UpdateNoteAuthor;
    String UpdateNoteDegree;
    String UpdateNoteSpecialization;
    String UpdateNotePrice;
    String UpdateNoteLocation;
    String UpdateNoteUser;
    String UpdateNoteMrp;
    String UpdateNoteSellerMsg;
    String downloadUri;

    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymaterials);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.mymaterialexpandeddialog);

        myDialog2 = new Dialog(this);
        myDialog2.setContentView(R.layout.imageexpanded);

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
                                Intent i0 = new Intent(MyMaterialsActivity.this, HomeActivity.class);
                                startActivity(i0);
                                break;
                            case R.id.nav_all:
                                Intent i = new Intent(MyMaterialsActivity.this, SecondActivity.class);
                                startActivity(i);
                                break;
                            case R.id.nav_category:
                                Intent intent = new Intent(MyMaterialsActivity.this, CategoryActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_profile:
                                Intent intent1 = new Intent(MyMaterialsActivity.this, MyProfileActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_materials:
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_fav:
                                Intent intent3 = new Intent(MyMaterialsActivity.this, MyFavActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(MyMaterialsActivity.this, SettingsActivity.class);
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

        firestoreListener = firestoreDB.collection("books").whereEqualTo("user", name)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(MyMaterialsActivity.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        notesList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            NoteMyMaterials noteMyMaterials = doc.toObject(NoteMyMaterials.class);
                            noteMyMaterials.setEntryName(doc.getId());
                            notesList.add(noteMyMaterials);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();

        Query query = firestoreDB.collection("books").whereEqualTo("user", name);


        FirestoreRecyclerOptions<NoteMyMaterials> response = new FirestoreRecyclerOptions.Builder<NoteMyMaterials>()
                .setQuery(query, NoteMyMaterials.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<NoteMyMaterials, NoteViewHolderMyMaterials>(response) {
            @Override
            protected void onBindViewHolder(NoteViewHolderMyMaterials holder, int position, NoteMyMaterials model) {

                final NoteMyMaterials noteMyMaterials = notesList.get(position);

                holder.title.setText(noteMyMaterials.getTitle());
                holder.author.setText(noteMyMaterials.getAuthor());
                holder.degree.setText(noteMyMaterials.getDegree());
                holder.specialization.setText(noteMyMaterials.getSpecialization());
                holder.mrp.setText(noteMyMaterials.getMrp());
                holder.price.setText(noteMyMaterials.getPrice());
                holder.location.setText(noteMyMaterials.getLocation());

                downloadUri = noteMyMaterials.getDownloadUri();

                Picasso.with(MyMaterialsActivity.this).load(downloadUri).fit().centerCrop().into(holder.bookpic);

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

                        UpdateNoteTitle = noteMyMaterials.getTitle();
                        titleExp.setText(UpdateNoteTitle);

                        UpdateNoteAuthor = noteMyMaterials.getAuthor();
                        authExp.setText(UpdateNoteAuthor);

                        UpdateNoteDegree = noteMyMaterials.getDegree();
                        degreeExp.setText(UpdateNoteDegree);

                        UpdateNoteSpecialization = noteMyMaterials.getSpecialization();
                        specialExp.setText(UpdateNoteSpecialization);

                        UpdateNoteLocation = noteMyMaterials.getLocation();
                        locationExp.setText(UpdateNoteLocation);

                        UpdateNoteMrp = noteMyMaterials.getMrp();
                        mrpExp.setText(UpdateNoteMrp);

                        UpdateNotePrice = noteMyMaterials.getPrice();
                        priceExp.setText(UpdateNotePrice);

                        UpdateNoteSellerMsg = noteMyMaterials.getSellerMsg();
                        sellerMsgExp.setText(UpdateNoteSellerMsg);

                        UpdateNoteUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        userExp.setText(UpdateNoteUser);

                        entryName = noteMyMaterials.getEntryName();

                        mrpExp.setPaintFlags(mrpExp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        Picasso.with(MyMaterialsActivity.this).load(downloadUri).fit().centerCrop().into(bookpic);

                        myDialog.show();

                        bookpic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageView ivExpandedPic = myDialog2.findViewById(R.id.ivImage);
                                Picasso.with(MyMaterialsActivity.this).load(downloadUri).fit().into(ivExpandedPic);
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

                        Toast.makeText(MyMaterialsActivity.this, entryName, Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }
                });

                Button edit = myDialog.findViewById(R.id.btnEdit);
                Button delete = myDialog.findViewById(R.id.btnDelete);
                LikeButton likeButton = myDialog.findViewById(R.id.heart_button);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNote();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteNote();
                    }
                });
                likeButton.setVisibility(View.GONE);

            }

            @Override
            public NoteViewHolderMyMaterials onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notemymaterials, parent, false);

                return new NoteViewHolderMyMaterials(view);
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
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
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

    private void updateNote() {

        Intent intent = new Intent(this, EditMyMaterialsActivity.class);

        intent.putExtra("UpdateNoteEntryName", entryName);
        intent.putExtra("UpdateNoteTitle", UpdateNoteTitle);
        intent.putExtra("UpdateNoteAuthor", UpdateNoteAuthor);
        intent.putExtra("UpdateNoteDegree", UpdateNoteDegree);
        intent.putExtra("UpdateNoteSpecialization", UpdateNoteSpecialization);
        intent.putExtra("UpdateNotePrice", UpdateNotePrice);
        intent.putExtra("UpdateNoteLocation", UpdateNoteLocation);
        intent.putExtra("UpdateNoteUser", UpdateNoteUser);
        intent.putExtra("UpdateNoteMrp", UpdateNoteMrp);
        intent.putExtra("UpdateNoteSellerMsg", UpdateNoteSellerMsg);
        intent.putExtra("UpdateNoteDownloadUri", downloadUri);
        startActivity(intent);
        myDialog.dismiss();
    }

    private void sellBook() {
        Intent intent = new Intent(MyMaterialsActivity.this, SellActivity.class);
        startActivity(intent);
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MyMaterialsActivity.this, EntryActivity.class));
    }



    private void deleteNote() {

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        StorageReference imageReference = storageReference.child(firebaseAuth.getCurrentUser().getEmail()).child("bookimages").child(entryName);

        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyMaterialsActivity.this, "Book image deleted successfully!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MyMaterialsActivity.this, "Cannot delete book image", Toast.LENGTH_SHORT).show();
            }
        });

        firestoreDB.collection("books")
                .document(entryName)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Material has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
        firestoreDB.collection("users")
                .document(userEmail)
                .collection("books")
                .document(UpdateNoteTitle)
                .delete();

        myDialog.dismiss();
    }

}
