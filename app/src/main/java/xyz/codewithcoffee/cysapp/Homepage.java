//package xyz.codewithcoffee.cyc_app;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.firebase.ui.auth.AuthUI;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class Homepage extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_page);
//
//        setButtonOnclicks();
//    }
//
//    private void setButtonOnclicks()
//    {
//
//        Button about_us = (Button)findViewById(R.id.about_us);
//        about_us.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, AboutUs.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button app_blocking = (Button)findViewById(R.id.app_blocking);
//        app_blocking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, AppBlocking.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button chat_app = (Button)findViewById(R.id.chat_app);
//        chat_app.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, ChatUI.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button website_blocking = (Button)findViewById(R.id.website_blocking);
//        website_blocking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, WebsiteBlocking.class);
//                startActivity(intent);
//            }
//        });
//
//        Button onlineexam = (Button)findViewById(R.id.onlineexam);
//        onlineexam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, OnlineExam.class);
//                startActivity(intent);
//            }
//        });
//
//        Button questionset = (Button)findViewById(R.id.questionset);
//        questionset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, QuestionSet.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button contact_us = (Button)findViewById(R.id.contact_us);
//        contact_us.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, ContactUs.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button rate = (Button)findViewById(R.id.rate);
//        rate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, RateUs.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button share_app = (Button)findViewById(R.id.share_app);
//        share_app.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, ShareApp.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button timetable = (Button)findViewById(R.id.timetable);
//        timetable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Homepage.this, Timetable.class);
//                startActivity(intent);
//            }
//        });
//
//
//        Button signout = (Button)findViewById(R.id.signout);
//        signout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AuthUI.getInstance().signOut(Homepage.this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(Homepage.this,
//                                        "You have been signed out.",
//                                        Toast.LENGTH_LONG)
//                                        .show();
//                                Intent intent7=new Intent(Homepage.this, LogInPage.class);
//                                startActivity(intent7);
//                                // Close activity
//                                finish();
//                            }
//                        });
//            }
//        });
//    }
//}