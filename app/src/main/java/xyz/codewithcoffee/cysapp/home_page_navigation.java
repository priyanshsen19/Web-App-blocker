package xyz.codewithcoffee.cysapp;

import static xyz.codewithcoffee.cysapp.MainActivity.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import xyz.codewithcoffee.cyc_app.R;
import xyz.codewithcoffee.cyc_app.databinding.ActivityHomePageNavigationBinding;

public class home_page_navigation extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageNavigationBinding binding;
    boolean currAdmin;
    boolean userExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userExists = false;
        Tym timi = Timetable.getCurrTime();
        File rtFile = new File(this.getFilesDir(), "restrict_time.txt");
        writeTextData(rtFile, ((timi.getHour() - 11 + 24) % 24) + " "
                + timi.getMin() + " \n"
                + ((timi.getHour() + 24 + 11) % 24) + " "
                + timi.getMin() + " \n"
        );
        setSupportActionBar(binding.appBarHomePageNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                /*R.id.nav_home, R.id.about_us, R.id.contact_us*/)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_dialog_dialer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Pressed Toolbar",Toast.LENGTH_SHORT).show();
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.contact_us:
                        intent = new Intent(home_page_navigation.this, ContactUs.class);
                        startActivity(intent);
                        return true;
                    case R.id.chat:
                        intent = new Intent(home_page_navigation.this, ChatUI.class);
                        startActivity(intent);
                        return true;
                    case R.id.web_blocking:
                        intent = new Intent(home_page_navigation.this, WebsiteBlocking.class);
                        startActivity(intent);
                        return true;
                    case R.id.app_blocking:
                        intent = new Intent(home_page_navigation.this, AppBlocking.class);
                        startActivity(intent);
                        return true;
                    case R.id.online_exam:
                        intent = new Intent(home_page_navigation.this, OnlineExam.class);
                        startActivity(intent);
                        return true;
                    case R.id.syllabus:
                        intent = new Intent(home_page_navigation.this, Syllabus.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_home:
                        intent = new Intent(home_page_navigation.this, home_page_navigation.class);
                        startActivity(intent);
                        return true;
                    case R.id.about_us:
                        intent = new Intent(home_page_navigation.this, AboutUs.class);
                        startActivity(intent);
                        return true;
                    case R.id.rate:
                        intent = new Intent(home_page_navigation.this, RateUs.class);
                        startActivity(intent);
                        return true;
                    case R.id.share:
                        intent = new Intent(home_page_navigation.this, ShareApp.class);
                        startActivity(intent);
                        return true;
                    case R.id.sign_out:
                        AuthUI.getInstance().signOut(home_page_navigation.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(home_page_navigation.this,
                                                "You have been signed out.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        Intent intent2 = new Intent(home_page_navigation.this, LogInPage.class);
                                        startActivity(intent2);
                                        // Close activity
                                        finish();
                                    }
                                });
                }
                return false;
            }
        });
        ((ImageButton) findViewById(R.id.timetable_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"GOGOGO",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(home_page_navigation.this, Timetable.class);
                startActivity(intent);
            }
        });
        ((ImageButton) findViewById(R.id.img_admin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currAdmin) {
                    Intent intent = new Intent(home_page_navigation.this, QuestionSet.class);
                    startActivity(intent);
                }
            }
        });
        //switch
        /*@SuppressLint("UseSwitchCompatOrMaterialCode") Switch simpleSwitch = (Switch) findViewById(R.id.switch1);
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferencesManager.getInstance().putBoolean("track_screen",true);
                    Intent intent = new Intent(getApplicationContext(), YourService.class);
                    startService(intent);}*/
              /*      if(!isMyServiceRunning(AppMonitorService.class))
                    {
                          switchstatus=true;
                        Intent intent = new Intent(getApplicationContext(), AppMonitorService.class);
                        Bundle extras = new Bundle();
                        extras.putBoolean("switch_status", switchstatus);
                        intent.putExtras(extras);
                        startActivity(intent);
                        Toast.makeText(home_page_navigation.this, "Checked", Toast.LENGTH_SHORT).show();
                    }*/

              /*      switchstatus=true;
                    Intent intent = new Intent(getApplicationContext(),
                            AppBlocking.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("switch_status", switchstatus);
                    intent.putExtras(extras);
                    startActivity(intent);
                    Toast.makeText(home_page_navigation.this, "Checked", Toast.LENGTH_SHORT).show();
                */
                /*else{
                    PreferencesManager.getInstance().putBoolean("track_screen",false);
                    Intent intent = new Intent(getApplicationContext(), YourService.class);
                    intent.putExtra("close",true);
                    startService(intent);
                    //  stopService(new Intent(getApplicationContext(), AppMonitorService.class));
                }*/
         /*   }
        });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        initDB();
        getMenuInflater().inflate(R.menu.home_page_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initUser(User user, DatabaseReference rootRef) {
        Log.d(TAG, "User Exist Called");
        linkUnameField(findViewById(R.id.account_name));
        ImageView pro_img = findViewById(R.id.main_img);
        Glide.with(home_page_navigation.this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(pro_img);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null) {
            Glide.with(home_page_navigation.this)
                    .load(R.drawable.profile_icon)
                    .into(pro_img);
        }
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "User Exist Actual Called");
                if (!(snapshot.hasChild(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                ))) {
                    rootRef.child(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                    ).setValue(user);
                    userExists = false;
                } else {
                    userExists = true;
                }
                Log.d(TAG, "User Exists : " + userExists);
                rootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer value = snapshot.getValue(Integer.class);
                        currAdmin = (value > 0);
                        Log.d(TAG, "Admin Access Updated " + currAdmin);
                        if (currAdmin) {
                            ((ImageButton) findViewById(R.id.img_admin))
                                    .setImageDrawable(getResources()
                                            .getDrawable(android.R.drawable.ic_menu_add));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(home_page_navigation.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });
                TextView welc = findViewById(R.id.greet);
                if (userExists) welc.setText("Welcome Back !");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(home_page_navigation.this, "Fail to retrieve user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void linkUnameField(TextView tv) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
        rootRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uname = snapshot.getValue(String.class);
                tv.setText(uname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void initDB() {
        User user = new User(
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(), 0
        );
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users");
        initUser(user, userRef);
    }

    private View.OnClickListener getOCL(Class aca) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home_page_navigation.this, aca));
            }
        };
    }

    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Log.d(TAG, "Wrote to " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}






