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
    ArrayList<MovieInfor> movieInfors = new ArrayList<>();    // hold our typed messages
    SharedPreferences prefs;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
    String time = sdf.format(new Date());
    MovieAdapter movieAdapter = new MovieAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_search);

        TextView myText = findViewById(R.id.textView);
        EditText myEdit = findViewById(R.id.movieTextField);
        Button myButton = findViewById(R.id.searchButton);
        movieList = findViewById(R.id.movieRecycler);
        Context context = getApplicationContext();
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        String movieName = prefs.getString("movieName", "");
        myEdit.setText(movieName);


        myButton.setOnClickListener(    ( vw ) -> {
            String editString = myEdit.getText().toString();
            myText.setText("Your Movie Search is: " + editString);
            Toast.makeText(context, "Future use: search pass/fail",
                    Toast.LENGTH_SHORT).show();
            MovieInfor thisMessage = new MovieInfor( myEdit.getText().toString(),1, time);
            movieInfors.add(thisMessage);
            movieAdapter.notifyItemInserted(movieInfors.size() -1);
        }   );
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
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

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);

            itemView.setOnClickListener( click -> {

                AlertDialog.Builder builder = new AlertDialog.Builder( MovieSearch.this );
                builder.setMessage("Do you want to delete the movie: " + messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .setPositiveButton("Yes", (dialog, cl) -> {

                            position = getAbsoluteAdapterPosition();

                            MovieInfor removedMessage = movieInfors.get(position);
                            movieInfors.remove(position);
                            movieAdapter.notifyItemRemoved(position);

                            Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        movieInfors.add(position, removedMessage);
                                        movieAdapter.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();
            });
        }

        public void setPosition(int p) {
            position = p;
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MyRowViews>{

        public int getItemViewType(int position) {
            MovieInfor thisRow = movieInfors.get(position);
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
            holder.messageText.setText( movieInfors.get(position).getMessage() );
            holder.timeText.setText( sdf.format (movieInfors.get(position).getSearchTime()));
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {

            return movieInfors.size();
        }
    }

    private class MovieInfor {
        public String message;
        public int send;
        public String searchTime;

        public MovieInfor(String message, int send, String searchTime) {
            this.message = message;
            this.send = send;
            this.searchTime = searchTime;
        }

        public String getMessage() {
            return message;
        }

        public int getSend() {
            return send;
        }

        public String getSearchTime() {
            return searchTime;
        }
    }


}
