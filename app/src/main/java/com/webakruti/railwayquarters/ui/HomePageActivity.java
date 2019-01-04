package com.webakruti.railwayquarters.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webakruti.railwayquarters.R;
import com.webakruti.railwayquarters.fragments.ColonyFragment;
import com.webakruti.railwayquarters.fragments.ContactUsFragment;
import com.webakruti.railwayquarters.fragments.HomeFragment;
import com.webakruti.railwayquarters.fragments.RaiseComplaintFragment;
import com.webakruti.railwayquarters.model.UserResponse;
import com.webakruti.railwayquarters.utils.SharedPreferenceManager;


public class HomePageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragManager;

    private ImageView imageViewSwachhataKendra;
    private ImageView imageViewRequestImage;

    private TextView toolbarUserDetailsHomeTitle;

    private TextView textViewName;
    private TextView textViewMobileNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarUserDetailsHomeTitle = (TextView) findViewById(R.id.toolbarUserDetailsHomeTitle);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        SharedPreferenceManager.setApplicationContext(HomePageActivity.this);
        UserResponse user = SharedPreferenceManager.getUserObjectFromSharedPreference();

        View headerLayout = navigationView.getHeaderView(0);

        textViewName = (TextView) headerLayout.findViewById(R.id.textViewName);
        textViewMobileNo = (TextView) headerLayout.findViewById(R.id.textViewMobileNo);

        imageViewSwachhataKendra = (ImageView) findViewById(R.id.imageViewSwachhataKendra);
        imageViewSwachhataKendra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(HomePageActivity.this, SwachhataKendraPageActivity.class);
                startActivity(intent);*/
            }
        });
        imageViewRequestImage = (ImageView) findViewById(R.id.imageViewRequestImage);
        imageViewRequestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MyRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Menu menu = navigationView.getMenu();

        MenuItem navigationLogout = menu.findItem(R.id.navigationLogout);

        if (user != null) {
            textViewMobileNo.setVisibility(View.VISIBLE);
            textViewName.setText(user.getSuccess().getUser().getName());
            textViewMobileNo.setText(user.getSuccess().getUser().getMobile());

            navigationLogout.setVisible(true);
        } else {
            textViewMobileNo.setVisibility(View.INVISIBLE);

            textViewName.setText("Welcome, Guest");
            navigationLogout.setVisible(false);

        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {

                    case R.id.navigationHome:
                        toolbarUserDetailsHomeTitle.setText("Home");
                        // toolbarStudentDetailsHomeTitle.setText("My details");
                        // SwachhataKendraFragment fragment = new SwachhataKendraFragment();
                        fragManager.beginTransaction().replace(R.id.home_container, new HomeFragment()).commit();
                        break;


                    case R.id.navigationRaiseComplaint:
                        toolbarUserDetailsHomeTitle.setText("Raise Complaint");
                        fragManager.beginTransaction().replace(R.id.home_container, new RaiseComplaintFragment()).commit();
                        break;


                    case R.id.navigationContactUs:
                        toolbarUserDetailsHomeTitle.setText("Contact us");
                        fragManager.beginTransaction().replace(R.id.home_container, new ContactUsFragment()).commit();
                        break;


                    case R.id.navigationLogout:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePageActivity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Logout");
                        // Setting Dialog Message
                        alertDialog.setMessage("Are you sure you want to logout?");
                        // Setting Icon to Dialog
                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferenceManager.clearPreferences();
                                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        // Setting Negative "NO" Button
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();


                        break;
                }
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        fragManager = getSupportFragmentManager();
        fragManager.beginTransaction().replace(R.id.home_container, new HomeFragment()).commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

}
