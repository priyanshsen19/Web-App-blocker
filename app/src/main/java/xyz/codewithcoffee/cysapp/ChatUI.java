package xyz.codewithcoffee.cysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import xyz.codewithcoffee.cyc_app.R;
public class ChatUI extends AppCompatActivity {

    private static final String TAG = MainActivity.TAG;
    private FirebaseDatabase fb_data = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        initRv();
        ImageButton allusers = (ImageButton) findViewById(R.id.allusersbg);
        allusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_chatui);
                initChatUi(fb_data.getReference().child("chats"),"All Chat");
            }
        });
    }

    private void initRv()
    {
        RecyclerView rv = findViewById(R.id.rec_view);
        FirebaseRecyclerOptions<User> options
                = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(fb_data.getReference().child("users"), User.class)
                .build();
        UserlistAdapter adapter = new UserlistAdapter(options);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }


    private void initChatUi(DatabaseReference db,String user)
    {
        setContentView(R.layout.activity_chatui);
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);
                pushDb(input.getText().toString());
                input.setText("");
            }

            private void pushDb(String inp)
            {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
                rootRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uname = snapshot.getValue(String.class);
                        db.push().setValue(new ChatMessage(inp, uname));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "loadPost:onCancelled", error.toException());
                    }
                });
            }
        });
        ((TextView)findViewById(R.id.recipient)).setText(user);
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(db, ChatMessage.class)
                .build();
        FirebaseRecyclerAdapter<ChatMessage, ChatHolder> adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatMessage model) {
                holder.setMessage_text(model.getMessageText());
                holder.setMessage_user(model.getMessageUser());
                holder.setMessage_time(model.getMessageTime());
            }

            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listmessage, parent, false);

                return new ChatHolder(view);
            }
        };
        adapter.startListening();
        RecyclerView rv = findViewById(R.id.recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        rv.smoothScrollToPosition(adapter.getItemCount());
    }

    class UserlistAdapter extends FirebaseRecyclerAdapter<
            User, UserlistAdapter.ViewHolder> {

        // RecyclerView recyclerView;
    /*public UserlistAdapter(Context context,ArrayList<String> username, ArrayList<String> uid) {
        this.context = context;
        this.user_name = username;
        this.uid = uid;
    }*/

        public UserlistAdapter(FirebaseRecyclerOptions<User> options)
        {
            super(options);
        }

    /*@Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.listuser, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }*/

        @Override
        public ViewHolder
        onCreateViewHolder(ViewGroup parent,
                           int viewType)
        {
            View view
                    = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listuser, parent, false);
            return new UserlistAdapter.ViewHolder(view);
        }

        @Override
        protected void
        onBindViewHolder(ViewHolder holder,
                         int position,User model)
        {
            holder.user_name.setText(model.getUsername());
            holder.uid.setText(model.getUid());
            holder.msg_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setContentView(R.layout.activity_chatui);
                    String uid1,uid2;
                    uid1 = model.getUid();
                    uid2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String loc;
                    if(uid1.compareTo(uid2)>=0)
                    {
                        loc = uid1+uid2;
                    }
                    else
                    {
                        loc = uid2+uid1;
                    }
                    initChatUi(fb_data.getReference().child("p2p_chats").child(loc),model.getUsername());
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView user_name,uid;
            ImageButton msg_user;
            public ViewHolder(View vw)
            {
                super(vw);
                this.user_name = (TextView) itemView.findViewById(R.id.user_name);
                this.uid = (TextView) itemView.findViewById(R.id.uid);
                this.msg_user = (ImageButton) itemView.findViewById(R.id.msg_user);
            }
        }
    }
}
