package algonquin.cst2335.cst2335project;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private String stringUrl;
    MovieSearchResult detailFragment;

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

            case R.id.help_menu:
                runHelp();
                break;

            case R.id.my_favourite:
                runFavouriteSave();
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
        AlertDialog dialog = new AlertDialog.Builder(MovieSearch.this)
                .setTitle(R.string.help_title_movie)
                .setMessage(R.string.help_message_movie)
                .setPositiveButton("OK", (click, arg) ->{
                })
                .show();
    }

    private void runFavouriteSave() {
        Context context = getApplicationContext();
        Toast.makeText(context, "To be added: this movie saved to my favourite", Toast.LENGTH_SHORT).show();
    }

    private void runSearchMovie(String movieTitle) {

        AlertDialog dialog = new AlertDialog.Builder(MovieSearch.this)
                .setTitle(R.string.searching_message)
                .setMessage("We\'re working hard to search: " + movieTitle)
                .setView(new ProgressBar(MovieSearch.this))
                .show();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SearchTitle", movieTitle);
        editor.apply();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            /* This runs in a separate thread */
            try {
                MovieInfo searchResult;
                Bitmap image = null;
                int detailType = 1;
                stringUrl = "https://www.omdbapi.com/?apikey=6c9862c2&r=xml&t="
                        + URLEncoder.encode(movieTitle, "UTF-8");

                URL url = new URL(stringUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");

                String title = null;
                String year = null;
                String runtime = null;
                String actors = null;
                String plot = null;
                String poster = null;
                String rating_imd = null;
                String rating_meta = null;

                while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("movie")) {
                                title = xpp.getAttributeValue(null, "title");
                                year = xpp.getAttributeValue(null, "year");
                                runtime = xpp.getAttributeValue(null, "runtime");
                                actors = xpp.getAttributeValue(null, "actors");
                                plot = xpp.getAttributeValue(null, "plot");
                                poster = xpp.getAttributeValue(null, "poster");
                                rating_meta = xpp.getAttributeValue(null, "metascore");
                                rating_imd = xpp.getAttributeValue(null, "imdbRating");
                                searchResult = new MovieInfo(title, year, rating_imd, runtime, actors, plot, poster);

                                File file = new File(getFilesDir(), title + ".png");
                                if (file.exists()) {
                                    image = BitmapFactory.decodeFile(getFilesDir() + "/" + title + ".png");
                                } else {
                                    URL imgUrl = new URL(poster);
                                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                                    connection.connect();
                                    int responseCode = connection.getResponseCode();
                                    if (responseCode == 200) {
                                        image = BitmapFactory.decodeStream(connection.getInputStream());
                                        image.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput(title + ".png", Activity.MODE_PRIVATE));
                                    }

                                    FileOutputStream fOut = null;
                                    try {
                                        fOut = openFileOutput(title + ".png", Context.MODE_PRIVATE);
                                        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }

                                MovieInfo finalSearchResult = searchResult;
                                Bitmap finalImage = image;
                                runOnUiThread(() -> {
                                    detailFragment = new MovieSearchResult(finalSearchResult, finalImage, detailType);
                                    FragmentManager fMgr = getSupportFragmentManager();
                                    FragmentTransaction tx = fMgr.beginTransaction();
                                    tx.replace(R.id.searchResult, detailFragment);
                                    tx.commit();

                                    dialog.hide();
                                });


                            } else if (xpp.getName().equals("error")) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), R.string.movie_not_found, Toast.LENGTH_LONG).show();
                                    dialog.hide();
                                });

                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }
                }
            } catch (IOException | XmlPullParserException ioe) {
                Log.e("Connection error: ", ioe.getMessage());
            }
        });
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
            String movieTitle = myEdit.getText().toString();
            myToolbar.getMenu().add( 1, 5, 10, movieTitle).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runSearchMovie(movieTitle);
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
