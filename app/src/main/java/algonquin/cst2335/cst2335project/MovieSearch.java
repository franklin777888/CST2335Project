package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MovieSearch extends AppCompatActivity {

    RecyclerView movieList;
    ArrayList<ChatMessage> messages = new ArrayList<>();    // hold our typed messages
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
    String time = sdf.format(new Date());

    MyChatAdapter adt = new MyChatAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_search);

        TextView myText = findViewById(R.id.textView);
        EditText myEdit = findViewById(R.id.movieTextField);
        Button myButton = findViewById(R.id.searchButton);
        movieList = findViewById(R.id.myrecycler);
        Context context = getApplicationContext();

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String movieName = prefs.getString("movieName", "");
        myEdit.setText(movieName);

        myButton.setOnClickListener(    ( vw ) -> {
            String editString = myEdit.getText().toString();
            myText.setText("Your Movie Search is: " + editString);
            Toast.makeText(context, "Future use: search pass/fail",
                    Toast.LENGTH_SHORT).show();
            ChatMessage thisMessage = new ChatMessage( myEdit.getText().toString(),1, time);
            messages.add(thisMessage);
            adt.notifyItemInserted(messages.size() -1);
        }   );
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        EditText myEdit = findViewById(R.id.movieTextField);
        editor.putString("movieName", myEdit.getText().toString());
        editor.apply();
    }

    private class MyRowViews extends RecyclerView.ViewHolder{

        TextView messageText;
        TextView timeText;
        int position = -1;

        public MyRowViews(View itemView) {  // itemView is a ConstraintLayout, that has <TextView> as sub-item
            super(itemView);

            itemView.setOnClickListener( click -> {
                // messages.remove(position);
                AlertDialog.Builder builder = new AlertDialog.Builder( MovieSearch.this );
                builder.setMessage("Do you want to delete the message: " + messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .setPositiveButton("Yes", (dialog, cl) -> {

                            position = getAbsoluteAdapterPosition();

                            ChatMessage removedMessage = messages.get(position);
                            messages.remove(position);
                            adt.notifyItemRemoved(position);

                            Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        messages.add(position, removedMessage);
                                        adt.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();
            });

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }

        public void setPosition(int p) {
            position = p;
        }
    }

    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews>{
        MyRowViews holder;
        int position;

        public int getItemViewType(int position) {
            ChatMessage thisRow = messages.get(position);
            return thisRow.getSend();
        }

        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();  // for loading XML layouts
            int layoutID;
            layoutID = R.layout.sent_message;
            View loadedRow  = inflater.inflate(layoutID, parent, false   );
            return new MyRowViews(loadedRow);  // will initialize the TextView
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.messageText.setText( messages.get(position).getMessage() );
            holder.timeText.setText( sdf.format (messages.get(position).getTimeSent()));
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {

            return messages.size();
        }
    }

    private class ChatMessage {
        public String message;
        public int send;
        public String timeSent;

        public ChatMessage(String message, int send, String timeSent) {
            this.message = message;
            this.send = send;
            this.timeSent = timeSent;
        }

        public String getMessage() {
            return message;
        }

        public int getSend() {
            return send;
        }

        public String getTimeSent() {
            return timeSent;
        }
    }


}
