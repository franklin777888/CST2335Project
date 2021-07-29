package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
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
    Toolbar myToolbar = null;
    EditText myEdit = null;
    Button myButton = null;
    TextView myText = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_search_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        TextView messageText = findViewById(R.id.message);
        TextView timeText = findViewById(R.id.time);


        switch(item.getItemId()) {
            case R.id.hide_views:
                messageText.setVisibility(View.INVISIBLE);
                timeText.setVisibility(View.INVISIBLE);
                myEdit.setText("");
                break;

            case R.id.helpMenu:
                runHelp();
                break;

            case 5:
                String movieName = item.getTitle().toString();
                myEdit.setText(movieName);
                runSearchMovie(movieName);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runHelp() {
        AlertDialog alertDialog = new AlertDialog.Builder(MovieSearch.this).create();
        alertDialog.setTitle("Help Menu");
        alertDialog.setMessage("App Instruction to be shown below");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void runSearchMovie(String movieName) {

        myText = findViewById(R.id.textView);
        myEdit = findViewById(R.id.movieTextField);
        movieList = findViewById(R.id.movieRecycler);
        Context context = getApplicationContext();
        myEdit.setText(movieName);

        movieList.setAdapter(movieAdapter);
        movieList.setLayoutManager(new LinearLayoutManager(this));

        String editString = myEdit.getText().toString();
        myText.setText("Your Movie Search is: " + editString);
            Toast.makeText(context, "Future use: search pass/fail", Toast.LENGTH_SHORT).show();
        MovieInfor thisMessage = new MovieInfor( myEdit.getText().toString(),1, time);
        movieInfors.add(thisMessage);
        movieAdapter.notifyItemInserted(movieInfors.size() -1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_search);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        myButton = findViewById(R.id.searchButton);
        myEdit = findViewById(R.id.movieTextField);
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item) -> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });

        myButton.setOnClickListener(clk -> {
            String movieName = myEdit.getText().toString();
            myToolbar.getMenu().add( 1, 5, 10, movieName).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runSearchMovie(movieName);
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        myEdit = findViewById(R.id.movieTextField);
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

    private class MovieAdapter extends RecyclerView.Adapter{

        public int getItemViewType(int position) {
            MovieInfor thisRow = movieInfors.get(position);
            return thisRow.getSend();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = getLayoutInflater();

            int layoutID = R.layout.sent_message;

            View loadedRow = layoutInflater.inflate(layoutID, parent, false);
            MyRowViews initRow = new MyRowViews((loadedRow));
            return initRow;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyRowViews thisRowLayout = (MyRowViews) holder;
            thisRowLayout.messageText.setText(movieInfors.get(position).getMessage());
            thisRowLayout.timeText.setText(movieInfors.get(position).getSearchTime());
            thisRowLayout.setPosition(position);
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
