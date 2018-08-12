package com.example.gaayathri.logindemo.viewholder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gaayathri.logindemo.R;

public class NoteViewHolderMyMaterials extends RecyclerView.ViewHolder {
    public TextView title, author, degree, specialization, mrp, price, entryName, location, timestamp, user, id;
    public ImageView bookpic;
    public Button edit;
    public Button delete;
    public LinearLayout onclicklinearLayout;

    public NoteViewHolderMyMaterials(View view) {
        super(view);
        title = view.findViewById(R.id.tvTitle);
        author = view.findViewById(R.id.tvAuthor);
        degree = view.findViewById(R.id.tvDegree);
        specialization = view.findViewById(R.id.tvSpecial);
        mrp = view.findViewById(R.id.tvMrp);
        price = view.findViewById(R.id.tvPrice);
        location = view.findViewById(R.id.tvLocation);
        onclicklinearLayout = view.findViewById(R.id.llOnClick);

        bookpic = view.findViewById(R.id.ivBookPic);

        edit = view.findViewById(R.id.btnEdit);
        delete = view.findViewById(R.id.btnDelete);

        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
