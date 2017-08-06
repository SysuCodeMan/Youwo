package com.example.davidwillo.youwo.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by davidwillo on 5/21/17.
 */

public class MyImageLoader {

    public static void displayImage(Context context, Object path, ImageView imageView) {
        Picasso.with(context).load((String) path).into(imageView);
    }
}
