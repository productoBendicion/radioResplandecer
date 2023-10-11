package com.resplandecer.baseActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.resplandecer.R;
import com.resplandecer.screens.declaracionAlDia.DeclaracionAlDia;
import com.resplandecer.screens.home.HomeActivity;
import com.resplandecer.screens.maranatha.MaranathaSongsActivity;
import com.resplandecer.screens.vdee.VDEE;
import com.resplandecer.screens.VdeeBilingue.VdeeBilingue;
import com.resplandecer.screens.VozQueClamaEnElDesierto.VozQueClamaEnElDesierto;
import com.resplandecer.utils.Constants;
import com.resplandecer.utils.ForegroundService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public abstract class BaseDrawerActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    protected ProgressDialog progressDialog;
    AlertDialog mMessageDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_drawer_layout);
        toolbar = findViewById(R.id.toolbar_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        onAttached();

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstance) {
        super.onPostCreate(savedInstance);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@Nullable Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        actionBarDrawerToggle.onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(@Nullable MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;
        int menuId = menuItem != null ? menuItem.getItemId() : -1;
        if (menuId == R.id.home) {
            intent = new Intent(this, HomeActivity.class);
        } else if (menuId == R.id.declaracion_al_dia) {
            intent = new Intent(this, DeclaracionAlDia.class);
        } else if (menuId == R.id.valverde_sr) {
            intent = new Intent(this, MaranathaSongsActivity.class);
        } else if (menuId == R.id.vdee) {
            intent = new Intent(this, VDEE.class);
        } else if (menuId == R.id.vdee_bilingue) {
            intent = new Intent(this, VdeeBilingue.class);
        } else if (menuId == R.id.vqceed) {
            intent = new Intent(this, VozQueClamaEnElDesierto.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }

        startActivity(intent);
        finish();

        return true;

    }


    public abstract void onAttached();


    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.setAction(Constants.ACTION.START_SERVICE);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.setAction(Constants.ACTION.STOP_SERVICE);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
    }

    public void showDialog() {
        if (progressDialog == null) {
            initProgressDialog();
        }

        progressDialog.show();
    }

    public void hideDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public void showNetworkError(String detailedError) {
        if (mMessageDialog == null) {
            mMessageDialog = new AlertDialog.Builder(this)
                    .setMessage(detailedError)
                    .setNeutralButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMessageDialog.hide();
                            mMessageDialog = null;
                        }
                    }).create();

        }
        mMessageDialog.show();
    }
}
