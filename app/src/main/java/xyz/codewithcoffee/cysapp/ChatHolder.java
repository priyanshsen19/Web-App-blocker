package xyz.codewithcoffee.cysapp;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import xyz.codewithcoffee.cyc_app.R;

public class ChatHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MainActivity.TAG;

    private View view;
    private TextView message_text,message_time,message_user;
    private static int[] txtColors = {
            Color.RED,
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.YELLOW
    };
    ChatHolder(View view)
    {
        super(view);
        this.view = view;
        message_text = (TextView) view.findViewById(R.id.message_text);
        message_time = (TextView) view.findViewById(R.id.message_time);
        message_user = (TextView) view.findViewById(R.id.message_user);
    }
    public void setMessage_text(String text) {
        this.message_text.setText(text);
    }

    public void setMessage_time(long time) {
        this.message_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                time));
    }

    public void setMessage_user(String user) {
        int col = user.hashCode();
        this.message_user.setText(user);
        int index = (Math.abs(col)%txtColors.length);
        //Log.d(TAG,"Ind - "+index+" : Len - "+txtColors.length);
        this.message_user.setTextColor(txtColors[index]);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
