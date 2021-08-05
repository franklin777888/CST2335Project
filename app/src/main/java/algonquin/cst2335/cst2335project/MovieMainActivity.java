package algonquin.cst2335.cst2335project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.navigation.NavigationView;

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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class to perform major functions for movie search app.
 */
public class MovieMainActivity extends AppCompatActivity {

    SharedPreferences prefs;
    Toolbar myToolbar = null;
    EditText myEdit = null;
    Button myButton = null;
    private String stringUrl;
    MovieDetailFragment detailFragment;

    /**
     * This method to create the options menu when user opens the menu for the 1st time
     * @param menu menu of movie app
     * @return true if successful, false if fail.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_search_actions, menu);
        return true;
    }

    /**
     * This method allows user to identify an item.
     * Select one of the four apps, Or view favourite list, or go for help menu.
     * @param item item of menu
     * @return true if successfully handle a menu item, false if fail.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String message = null;

        switch(item.getItemId()) {

            case R.id.goMovie:
                message = getResources().getString(R.string.moviePage);
                startActivity(new Intent(MovieMainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                message = getResources().getString(R.string.busPage);
                startActivity(new Intent(MovieMainActivity.this, BusMainActivity.class));
                break;
            case R.id.goCar:
                message = getResources().getString(R.string.carPage);
                startActivity(new Intent(MovieMainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                message = getResources().getString(R.string.soccerPage);
                startActivity(new Intent(MovieMainActivity.this, SoccerMainActivity.class));
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

    /**
     * This method to show help menu.
     */
    private void runHelp() {
        AlertDialog dialog = new AlertDialog.Builder(MovieMainActivity.this)
                .setTitle(R.string.help_title_movie)
                .setMessage(R.string.help_message_movie)
                .setPositiveButton("OK", (click, arg) ->{
                })
                .show();
    }

    /**
     * This method to save movies to favourites.
     */
    private void runFavouriteSave() {
        MovieFavoriteFragment favoriteFragment = new MovieFavoriteFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.replace(R.id.searchResult, favoriteFragment);
        tx.commit();
    }

    /**
     * This method to perform movie search.
     * @param movieName title of movie
     */
    private void runSearchMovie(String movieName) {

        AlertDialog dialog = new AlertDialog.Builder(MovieMainActivity.this)
                .setMessage(R.string.searching_message)
                .setView(new ProgressBar(MovieMainActivity.this))
                .show();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SearchTitle", movieName);
        editor.apply();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            /* This runs in a separate thread */
            try {
                MovieInfo searchResult;
                Bitmap image = null;
                int detailType = 1;
                stringUrl = "https://www.omdbapi.com/?apikey=6c9862c2&r=xml&t="
                        + URLEncoder.encode(movieName, "UTF-8");

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
                                    detailFragment = new MovieDetailFragment(finalSearchResult, finalImage, detailType);
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

    /**
     * This method to initialize all activities.
     * @param savedInstanceState the saved instance in the bundle.
     */
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


    /**
     * This function to keep a record of last entered movie title.
     */
    @Override
    protected void onPause() {
        super.onPause();
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        myEdit = findViewById(R.id.movieTextField);
        editor.putString("movieName", myEdit.getText().toString());
        editor.apply();
    }

    /**
     *
     * @param searchResult the movie search result
     * @param image image of movie searched
     * @param detailType a number to identify if this movie saved or not. equals 2 if already saved.
     */
    public void userClickedMessage(MovieInfo searchResult, Bitmap image, int detailType) {
        MovieDetailFragment detailFragment = new MovieDetailFragment(searchResult,image,detailType);
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.replace(R.id.searchResult, detailFragment);
        tx.commit();
    }


}
