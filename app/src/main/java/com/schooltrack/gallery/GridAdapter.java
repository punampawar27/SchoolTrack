package com.schooltrack.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.schooltrack.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dell on 11/26/15.
 */
public class GridAdapter extends BaseAdapter  {

    private Activity activity;
    private ArrayList<String> imageUrl = new ArrayList<String>();
    private int imageWidth;

    public GridAdapter(Activity activity, ArrayList<String> imageUrl, int columnWidth) {
        this.activity = activity;
        this.imageUrl = imageUrl;
        this.imageWidth = columnWidth;
    }
    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(activity);
            grid = inflater.inflate(R.layout.grid_view_item, null);

           ImageView imageView=(ImageView)grid.findViewById(R.id.grid_image);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageWidth);
            imageView.setLayoutParams(layoutParams);
            layoutParams.setMargins(1, 1, 1, 1);


          /*  imageView.setLayoutParams(LayoutParams(imageWidth,
                    imageWidth));*/
            Picasso.with(activity).load(imageUrl.get(position)).into(imageView);



        } else {
            grid = (View) convertView;
        }

        return grid;
    }

    }

