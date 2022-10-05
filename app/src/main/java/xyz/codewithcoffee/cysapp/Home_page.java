package xyz.codewithcoffee.cysapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import xyz.codewithcoffee.cyc_app.R;
public class Home_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

    }

    public void goto_App_Blocker(View view) {
        Intent intent = new Intent(view.getContext(), AppBlocking.class);
        startActivity(intent);
    }

    public void goto_Website_Blocker(View view) {
        Intent intent = new Intent(view.getContext(), WebsiteBlocking.class);
        startActivity(intent);
    }
}