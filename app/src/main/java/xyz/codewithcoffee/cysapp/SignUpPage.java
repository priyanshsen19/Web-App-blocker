package xyz.codewithcoffee.cysapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import xyz.codewithcoffee.cyc_app.R;
public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        Spinner spinner =  findViewById(R.id.spinner);
        String[] items = new String[]{"Student","Teacher","Professional"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,items);
        spinner.setAdapter(adapter);
        ((Button)findViewById(R.id.login_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
        ((ImageButton)findViewById(R.id.signup_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String name,email,password;
                    name = ((EditText)findViewById(R.id.editFullName)).getText().toString();
                    email = ((EditText)findViewById(R.id.editEmail)).getText().toString();
                    password = ((EditText)findViewById(R.id.editPassword)).getText().toString();
                    emailSignUp(FirebaseAuth.getInstance(), name, email, password);
                }
                catch (IllegalArgumentException e)
                {
                    Toast.makeText(getApplicationContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void gotoLogin() {
        Intent intent = new Intent(SignUpPage.this, LogInPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void gotoDashboard() {
        Intent intent = new Intent(SignUpPage.this, home_page_navigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void emailSignUp(FirebaseAuth mAuth,String name, String email,String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

// user account created successfully
                            Log.d(MainActivity.TAG, "signUpWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                            User user = new User(
                                    name,
                                    mAuth.getCurrentUser().getUid(),0
                            );
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                            ).setValue(user);
                            gotoDashboard();
                        } else {

// account creation failed
                            Log.w(MainActivity.TAG, "signUpWithEmail" + task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}