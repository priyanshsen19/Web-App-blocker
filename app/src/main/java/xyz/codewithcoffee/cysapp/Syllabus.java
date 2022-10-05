package xyz.codewithcoffee.cysapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import xyz.codewithcoffee.cyc_app.R;
public class Syllabus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        ImageButton c9, c10, c11, c12;
        c9 = findViewById(R.id.class9);
        c10 = findViewById(R.id.class10);
        c11 = findViewById(R.id.class11);
        c12 = findViewById(R.id.class12);
        String[] urls = {
                "http://edudel.nic.in/asg_file/2020_21/class_9_08122020.htm",
                "http://edudel.nic.in/asg_file/2020_21/class_10_08122020.htm",
                "http://edudel.nic.in/asg_file/2020_21/class_11_08122020.htm",
                "http://edudel.nic.in/asg_file/2020_21/class_12_08122020.htm"
        };
        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(urls[0]));
                startActivity(viewIntent);
            }
        });
        c10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(urls[1]));
                startActivity(viewIntent);
            }
        });
        c11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(urls[2]));
                startActivity(viewIntent);
            }
        });
        c12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(urls[3]));
                startActivity(viewIntent);
            }
        });
    }
}