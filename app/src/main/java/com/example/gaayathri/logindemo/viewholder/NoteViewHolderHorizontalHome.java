package com.example.gaayathri.logindemo.viewholder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gaayathri.logindemo.R;

public class NoteViewHolderHorizontalHome extends RecyclerView.ViewHolder {

    public TextView title, author;
    public ImageView bookpic;
    public LinearLayout onclicklinearLayout;

    public NoteViewHolderHorizontalHome(View view) {
        super(view);
        title = view.findViewById(R.id.tvTitle);
        author = view.findViewById(R.id.tvAuthor);

        bookpic = view.findViewById(R.id.ivBookPic);

        onclicklinearLayout = view.findViewById(R.id.llOnClick);

    }

}
