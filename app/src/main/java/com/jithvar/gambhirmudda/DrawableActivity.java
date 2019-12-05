package com.jithvar.gambhirmudda;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Arvindo Mondal on 4/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class DrawableActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout fullLayout;
    private FrameLayout frame_layout;
    private Toolbar toolbar;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        fullLayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.drawer_layout,null);
        frame_layout = (FrameLayout)fullLayout.findViewById(R.id.conetnt_frame);
        getLayoutInflater().inflate(layoutResID,frame_layout,true);
        super.setContentView(fullLayout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setCheckable(true);
        int id = item.getItemId();
        switch (id){
//            case R.id.help:
//                startActivity(new Intent(DrawableActivity.this, Help.class));
//                break;

            case R.id.about_us:
                startActivity(new Intent(DrawableActivity.this, AboutUs.class));
                break;
        }

        return true;
    }

}
