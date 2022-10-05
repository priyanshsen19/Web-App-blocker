package xyz.codewithcoffee.cysapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InvalidObjectException;
import xyz.codewithcoffee.cyc_app.R;
public class QuestionSet extends AppCompatActivity {

    int len;
    int no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_set_land);
        Button justGo = (Button) findViewById(R.id.just_go);
        EditText noQues = (EditText) findViewById(R.id.no_ques);
        FirebaseDatabase.getInstance().getReference().child("exam").child("questions")
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        justGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                len = Integer.parseInt(noQues.getText().toString());
                if (len == 0) {
                    startActivity(new Intent(QuestionSet.this, home_page_navigation.class));
                    Toast.makeText(getApplicationContext(), "No questions were submitted", Toast.LENGTH_LONG).show();
                    return;
                }
                no = 0;
                setContentView(R.layout.activity_question_set);
                ((EditText) findViewById(R.id.question_name)).setHint("Question No " + (++no) + " Title :");
                Button submit = (Button) findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (no > len) {
                            no = 0;
                            len = -1;
                            startActivity(new Intent(QuestionSet.this, home_page_navigation.class));
                            Toast.makeText(getApplicationContext(), "Thanks for submitting question set", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String question,option_a,option_b,option_c,option_d;
                        int correct;
                        EditText qv,oa,ob,oc,od,cr;
                        qv = (EditText) findViewById(R.id.question_name);
                        oa = (EditText) findViewById(R.id.option_a_edit);
                        ob = (EditText) findViewById(R.id.option_b_edit);
                        oc = (EditText) findViewById(R.id.option_c_edit);
                        od = (EditText) findViewById(R.id.option_d_edit);
                        cr = (EditText) findViewById(R.id.option_correct);
                        try {
                                question = qv.getText().toString();
                                option_a = oa.getText().toString();
                                option_b = ob.getText().toString();
                                option_c = oc.getText().toString();
                                option_d = od.getText().toString();
                                correct = Integer.parseInt(cr.getText().toString());
                                if(correct<1||correct>4) throw new InvalidObjectException("Correct input out of bounds");
                        }catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Invalid/Incorrect Format in Input",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Question ques = new Question(question,no,option_a,option_b,option_c,option_d,correct);
                        qv.setText("");
                        oa.setText("");
                        ob.setText("");
                        oc.setText("");
                        od.setText("");
                        cr.setText("");
                        FirebaseDatabase.getInstance().getReference().child("exam").child("questions").
                                child("q"+no).setValue(ques);
                        no++;
                        if (no > len) {
                            no = 0;
                            len = -1;
                            startActivity(new Intent(QuestionSet.this, home_page_navigation.class));
                            Toast.makeText(getApplicationContext(), "Thanks for submitting question set", Toast.LENGTH_LONG).show();
                            return;
                        }
                        qv.setHint("Question No "+no+" Title :");
                    }
                });
            }
        });
    }
}