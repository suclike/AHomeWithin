package org.ahomewithin.ahomewithin.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.ahomewithin.ahomewithin.R;
import org.ahomewithin.ahomewithin.fragments.AboutUsFragment;
import org.ahomewithin.ahomewithin.fragments.HomeFragment;
import org.ahomewithin.ahomewithin.fragments.LearnMoreFragment;
import org.ahomewithin.ahomewithin.fragments.MapFragment;
import org.ahomewithin.ahomewithin.fragments.EventsFragment;
import org.ahomewithin.ahomewithin.fragments.StreamPagerFragment;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.dlDrawer) DrawerLayout dlDrawer;
    @Bind(R.id.nvView) NavigationView nvView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        HomeFragment homeFragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, homeFragment)
                .commit();

        drawerToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.drawer_open,
                        R.string.drawer_close);
        dlDrawer.setDrawerListener(drawerToggle);

        setupDrawerContent(nvView);

        if (!isOnline()) {
            showSnackbar("Oops!  Please check internet connection!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                dlDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        dlDrawer.closeDrawers();

        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.miLibrary:
                fragment = StreamPagerFragment.newInstance(StreamPagerFragment.ViewType.LIBRARY);
                break;
            case R.id.miEvents:
                fragment = EventsFragment.newInstance();
                break;
            case R.id.miLearnMore:
                fragment = LearnMoreFragment.newInstance();
                break;
//            case R.id.miAccount:
//                Intent i = new Intent(this, UserActivity.class);
//                startActivity(i);
//                break;
            case R.id.miAboutUs:
                fragment = AboutUsFragment.newInstance();
                break;
            default:
                fragment = HomeFragment.newInstance();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .commit();

        }
        dlDrawer.closeDrawers();
    }
    
    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private void showSnackbar(String message) {
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.dlDrawer),
                message, Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    public void onSelectLibrary(View v) {
        Fragment streamPagerFragment = StreamPagerFragment.newInstance(StreamPagerFragment.ViewType.LIBRARY);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, streamPagerFragment)
                .addToBackStack("store")
                .commit();
    }

    public void onSelectNearYou(View v) {
        Fragment mapFragment = MapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, mapFragment)
                .addToBackStack("map")
                .commit();
    }

    public void onSelectToolsAndTechniques(View v) {
        Fragment streamPagerFragment = StreamPagerFragment.newInstance(StreamPagerFragment.ViewType.STORE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, streamPagerFragment)
                .addToBackStack("store")
                .commit();
    }

}
