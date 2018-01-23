package com.schooltrack;

import android.graphics.drawable.Drawable;

/**
 * Created by Gracie on 10/27/2015.
 */
public class DrawerItemBean{


    public Drawable icon;
    public String name;

    // Constructor.
    public DrawerItemBean(Drawable icon, String name) {

        this.icon = icon;
        this.name = name;
    }

}
