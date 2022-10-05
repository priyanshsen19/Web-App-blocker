package xyz.codewithcoffee.cysapp;

import static xyz.codewithcoffee.cysapp.MainActivity.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import xyz.codewithcoffee.cyc_app.R;

public class Dashboard extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<CardElement> cards;
    MyAdapter adapter;
    Uri profile_img;
    boolean currAdmin;
    boolean userExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userExists = false;
        initDB();
        fillCards();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this,ChatUI.class));
            }
        });
        linkUnameField(findViewById(R.id.username));
        profile_img = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        //ImageView pfp = findViewById(R.id.profile_img);
        //pfp.setImageURI(img);
        Tym timi = Timetable.getCurrTime();
        File rtFile = new File(this.getFilesDir(),"restrict_time.txt");
        writeTextData(rtFile,((timi.getHour()-11+24)%24)+" "
                + timi.getMin()+" \n"
                +((timi.getHour()+24+11)%24)+" "
                +timi.getMin()+" \n"
        );
    }

    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Log.d(TAG, "Wrote to " + file.getAbsolutePath().toString());
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

    private void fillCards()
    {
        int[] colors = {
                Color.parseColor("#E66030A8"),
                Color.parseColor("#E6714CA8"),

        };
        int c=0;
        cards = new ArrayList<>();
        if(currAdmin) {
            cards.add(new CardElement(getOCL(QuestionSet.class), R.drawable.bg_setquestion, R.drawable.ic_setquestion, "Set Question", colors[(c++ % colors.length)]));
        }
        cards.add(new CardElement(getOCL(OnlineExam.class),R.drawable.bg_onlineexam,R.drawable.ic_onlineexam,"Online Exam",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(WebsiteBlocking.class),R.drawable.bg_websiteblocking,R.drawable.ic_websiteblocking,"Site Blocking",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(AppBlocking.class),R.drawable.bg_appblocking,R.drawable.ic_appblocking,"App Blocking",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(Timetable.class),R.drawable.bg_timetable,R.drawable.ic_timetable,"Timetable",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(RateUs.class),R.drawable.bg_rateus,R.drawable.ic_rateus,"Rate Us",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(ContactUs.class),R.drawable.bg_contactus,R.drawable.ic_contactus,"Contact Us",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(AboutUs.class),R.drawable.bg_aboutus,R.drawable.ic_aboutus,"About Us",colors[(c++%colors.length)]));
        cards.add(new CardElement(getOCL(ShareApp.class),R.drawable.bg_shareapp,R.drawable.ic_shareapp,"Share App",colors[(c++%colors.length)]));
        cards.add(new CardElement(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AuthUI.getInstance().signOut(Dashboard.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Dashboard.this,
                                                "You have been signed out.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        Intent intent7=new Intent(Dashboard.this, LogInPage.class);
                                        startActivity(intent7);
                                        // Close activity
                                        finish();
                                    }
                                });
                    }
                },
                R.drawable.transparent, R.drawable.logout_small, "Sign Out", colors[(c++%colors.length)]));

        rv=(RecyclerView)findViewById(R.id.rec);
        adapter = new MyAdapter(cards,this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this,3));
        rv.setHasFixedSize(true);
    }

    private void initUser(User user, DatabaseReference rootRef)
    {
        Log.d(TAG,"User Exist Called");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG,"User Exist Actual Called");
                if (!(snapshot.hasChild(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                ))) {
                    rootRef.child(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                    ).setValue(user);
                    userExists = false;
                }
                else
                {
                    userExists = true;
                }
                Log.d(TAG,"User Exists : "+ userExists);
                rootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer value = snapshot.getValue(Integer.class);
                        currAdmin = (value>0);
                        fillCards();
                        Log.d(TAG,"Admin Access Updated "+currAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Dashboard.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });
                TextView welc = findViewById(R.id.welc_txt);
                if(userExists) welc.setText("Welcome Back !");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Fail to retrieve user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void linkUnameField(TextView tv)
    {
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
                FirebaseAuth.getInstance().getCurrentUser().getUid(),0
        );
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users");
        initUser(user, userRef);
    }

    private View.OnClickListener getOCL(Class aca)
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this,aca));
            }
        };
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<CardElement> myListList;
        private Context ct;

        public MyAdapter(){super();}

        public MyAdapter(List<CardElement> myListList, Context ct) {
            super();
            this.myListList = myListList;
            this.ct = ct;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_card_item,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CardElement myList=myListList.get(position);
            holder.text_tv.setText(myList.getText());
            holder.icon_img.setImageDrawable(ct.getResources().getDrawable(myList.getIcon()));
            holder.bg_img.setImageDrawable(ct.getResources().getDrawable(myList.getBg()));
            holder.bg_img.setOnClickListener(myList.getOcl());
            holder.back.setBackgroundColor(myList.getColor());
        }

        @Override
        public int getItemCount() {
            return myListList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageButton bg_img;
            ImageView icon_img;
            TextView text_tv;
            RelativeLayout back;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bg_img = itemView.findViewById(R.id.bg_img);
                icon_img = itemView.findViewById(R.id.icon_img);
                text_tv = itemView.findViewById(R.id.text_tv);
                back = itemView.findViewById(R.id.back);
            }
        }
    }
}