package com.example.gaayathri.logindemo;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.banner1);
                //viewHolder.bindImageSlide("https://firebasestorage.googleapis.com/v0/b/login-demo-3c273.appspot.com/o/sliderimages%2F1%20(3).jpg?alt=media&token=5bd61585-571f-4746-83dd-5f68a4c092cf");
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.banner2);
                //viewHolder.bindImageSlide("https://firebasestorage.googleapis.com/v0/b/login-demo-3c273.appspot.com/o/sliderimages%2F2.jpg?alt=media&token=9ca32487-80f8-4eac-a32d-71fba93f6ccf");
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.banner3);
                //viewHolder.bindImageSlide("https://firebasestorage.googleapis.com/v0/b/login-demo-3c273.appspot.com/o/sliderimages%2F3.jpg?alt=media&token=0b2e742a-007b-4a28-8082-6df6c17bd9dc");
                break;
        }
    }
}
