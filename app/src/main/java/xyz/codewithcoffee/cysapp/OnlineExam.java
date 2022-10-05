package xyz.codewithcoffee.cysapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static xyz.codewithcoffee.cysapp.MainActivity.TAG;

import java.util.ArrayList;
import xyz.codewithcoffee.cyc_app.R;
public class OnlineExam extends AppCompatActivity {

    private FirebaseDatabase fb_data = FirebaseDatabase.getInstance();
    private ArrayList<Integer> answers,choices;//MAX QUES = 50
    private int correct,wrong,total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answers = new ArrayList<>();
        choices = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            answers.add(0);
            choices.add(0);
        }
        correct = wrong = total = 0;

        setContentView(R.layout.activity_exam_start);
        bindExamStart();
    }

    private void initRv()
    {
        RecyclerView rv = findViewById(R.id.ques_rec);
        FirebaseRecyclerOptions<Question> options
                = new FirebaseRecyclerOptions.Builder<Question>()
                .setQuery(fb_data.getReference().child("exam").child("questions"), Question.class)
                .build();
        OnlineExam.QuesListAdapter adapter = new OnlineExam.QuesListAdapter(options);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();

        Button  confirm_submit = (Button) findViewById(R.id.confirm_submit);
        confirm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_exam_score);
                correct = wrong = total = 0;
                for (int i = 0; i < 55; i++) {
                    Log.d(TAG,"Q"+i+" - "+choices.get(i)+" : "+answers.get(i));
                    if(answers.get(i)!=0)
                    {
                        total++;
                        if(choices.get(i)==0||choices.get(i)==-1) continue;
                        if(choices.get(i)==answers.get(i))
                            correct++;
                        else
                            wrong++;
                    }
                }
                TextView tv_correct,tv_wrong,tv_final;
                tv_correct = (TextView) findViewById(R.id.correct_text);
                tv_wrong = (TextView) findViewById(R.id.wrong_text);
                tv_final = (TextView) findViewById(R.id.final_score);
                tv_correct.setText("Correct Answers : "+correct);
                tv_wrong.setText("Wrong Answers : "+wrong);
                tv_final.setText("Final Score : "+correct+"/"+total);
                findViewById(R.id.submit_score).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Score score = new Score(
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                correct,wrong,total);
                        Score.initUsername(score);
                        fb_data.getReference().child("exam").child("scores").child(
                                FirebaseAuth.getInstance().getCurrentUser().getUid()
                        ).setValue(score);
                        setContentView(R.layout.activity_exam_start);
                        bindExamStart();
                    }
                });
            }
        });
    }

    private void bindExamStart()
    {
        Button exam_start = (Button) findViewById(R.id.exam_start);
        exam_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Check if Valid Time
                /*
                    Button go_back = (Button) findViewById(R.id.just_go);
                    go_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setContentView(R.layout.activity_exam_start);
                        }
                    });
                */
                setContentView(R.layout.activity_question_page);
                initRv();
            }
        });
    }

    class QuesListAdapter extends FirebaseRecyclerAdapter<
            Question, OnlineExam.QuesListAdapter.ViewHolder> {

        public QuesListAdapter(FirebaseRecyclerOptions<Question> options)
        {
            super(options);
        }

        @Override
        public OnlineExam.QuesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                           int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_question, parent, false);
            return new OnlineExam.QuesListAdapter.ViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(OnlineExam.QuesListAdapter.ViewHolder holder,
                         @SuppressLint("RecyclerView") int position, Question model)
        {
            holder.questionPanel.setText(model.getNo()+") "+model.getQuestion());
            holder.a.setText(model.getOption_a());
            holder.b.setText(model.getOption_b());
            holder.c.setText(model.getOption_c());
            holder.d.setText(model.getOption_d());
            holder.clear.setText("Skip this Question");
            holder.rg.check(model.getLastCheck());
            answers.set(model.getNo(),model.getCorrect());
            holder.rg.setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group,
                                                     int checkedId)
                        {
                            switch(checkedId)
                            {
                                case R.id.option_a:
                                    choices.set(model.getNo(), 1);
                                    model.setLastCheck(R.id.option_a);
                                    break;
                                case R.id.option_b:
                                    choices.set(model.getNo(), 2);
                                    model.setLastCheck(R.id.option_b);
                                    break;
                                case R.id.option_c:
                                    choices.set(model.getNo(), 3);
                                    model.setLastCheck(R.id.option_c);
                                    break;
                                case R.id.option_d:
                                    choices.set(model.getNo(), 4);
                                    model.setLastCheck(R.id.option_d);
                                    break;
                                case R.id.clear:
                                    choices.set(model.getNo(), -1);
                                    model.setLastCheck(R.id.clear);
                                    break;
                            }

                        }
                    });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView questionPanel;
            RadioGroup rg;
            RadioButton a,b,c,d,clear;
            public ViewHolder(View vw)
            {
                super(vw);
                this.questionPanel = (TextView) itemView.findViewById(R.id.question_panel);
                this.rg = (RadioGroup)  itemView.findViewById(R.id.radioGroup);
                this.a = (RadioButton) itemView.findViewById(R.id.option_a);
                this.b = (RadioButton) itemView.findViewById(R.id.option_b);
                this.c = (RadioButton) itemView.findViewById(R.id.option_c);
                this.d = (RadioButton) itemView.findViewById(R.id.option_d);
                this.clear = (RadioButton) itemView.findViewById(R.id.clear);
            }
        }
    }
}