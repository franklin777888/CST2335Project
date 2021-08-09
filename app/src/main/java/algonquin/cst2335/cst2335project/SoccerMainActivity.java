package algonquin.cst2335.cst2335project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *This is the main class of soccer news to set up main functions
 */
public class SoccerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * initialize variables. Use array to store the soccer news elements.
     */
    ArrayList<SoccerNews> elements = new ArrayList<>();
    MyAdapter adt=new MyAdapter();
    public static final String SOCCER_TITLE = "TITLE";
    public static final String SOCCER_IMG = "IMG";
    public static final String SOCCER_DATE = "DATE";
    public static final String SOCCER_DESC = "DESC";
    public static final String SOCCER_LINK = "LINK";
    FrameLayout frameLayout;
    private Button soccerIntro;
    private Switch soccerSw;
    SharedPreferences sp;
    EditText rateComment;
    RatingBar rateStar;
    private  SharedPreferences.Editor sharedPrefEditor;
    private boolean isTablet;
    ProgressBar progressBar;

    /**
     * This is the method to set up the onCreate functions.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soccer_main);

        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((item)-> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });


        frameLayout = findViewById(R.id.soccerFragmentLocation);
        isTablet = frameLayout != null;

        RecyclerView myList=findViewById(R.id.soccerNewsList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        myList.setLayoutManager(layoutManager);
        myList.setAdapter(adt);
        progressBar=findViewById(R.id.soccerProgressBar);
        alertDialog();


        Button soccerFavBtn = (Button)findViewById(R.id.soccerFavNews);
        Intent goToFavouriteList = new Intent(SoccerMainActivity.this,SoccerFavNews.class);
        soccerFavBtn.setOnClickListener(click -> {
            startActivity(goToFavouriteList);
        });

        soccerIntro = (Button)findViewById(R.id.soccerIntro);
        soccerIntro.setOnClickListener(click -> {
            Toast.makeText(this, getResources().getString(R.string.soccer_toast_message), Toast.LENGTH_LONG).show();
        });

        soccerSw = (Switch)findViewById(R.id.soccerSwitch);
        soccerSw.setOnCheckedChangeListener((CompoundButton switchBtn, boolean b)-> {
            if(b == true){
                Snackbar
                        .make(switchBtn, getResources().getString(R.string.soccer_snackbar_msg1), Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.soccerUndo), click->switchBtn.setChecked(!b))
                        .show();
            }else{
                Snackbar
                        .make(switchBtn, getResources().getString(R.string.soccer_snackbar_msg2), Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.soccerUndo), click->switchBtn.setChecked(!b))
                        .show();
            }
        });
    }

    /**
     * This method is used to load menu on toorlbar layout.
     * @param menu contents icons that will display on toolbar
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soccer_menu, menu);
        return true;
    }

    /**
     * This method is used to allow user to click icons on toolbar, than do the related actions.
     * @param item is icon on toolbar
     * @return boolean
     */
    public boolean onOptionsItemSelected(MenuItem item){
        String message = null;
        switch (item.getItemId()){
            case R.id.goFav:
                message = getResources().getString(R.string.favPage);
                startActivity(new Intent(SoccerMainActivity.this, SoccerFavNews.class));
                break;
            case R.id.goMovie:
                message = getResources().getString(R.string.moviePage);
                startActivity(new Intent(SoccerMainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                message = getResources().getString(R.string.busPage);
                startActivity(new Intent(SoccerMainActivity.this, Navigation.class));
                break;
            case R.id.goCar:
                message = getResources().getString(R.string.carPage);
                startActivity(new Intent(SoccerMainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                message = getResources().getString(R.string.soccerPage);
                startActivity(new Intent(SoccerMainActivity.this, SoccerMainActivity.class));
                break;
            case R.id.help:
                message = getResources().getString(R.string.soccerHelpMsg);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.help))
                        .setMessage(getResources().getString(R.string.helpSoccer))
                        .create().show();
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * This method is override onNavigationItemSelected method
     * @param item is the icon on Navigation
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String message = null;
        switch (item.getItemId()) {
            case R.id.goFav:
                message = getResources().getString(R.string.favPage);
                startActivity(new Intent(SoccerMainActivity.this, SoccerFavNews.class));
                break;
            case R.id.goMovie:
                message = getResources().getString(R.string.moviePage);
                startActivity(new Intent(SoccerMainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                message= getResources().getString(R.string.busPage);
                startActivity(new Intent(SoccerMainActivity.this, BusMainActivity.class));
                break;
            case R.id.goCar:
                message= getResources().getString(R.string.carPage);
                startActivity(new Intent(SoccerMainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                message= getResources().getString(R.string.soccerPage);
                startActivity(new Intent(SoccerMainActivity.this, SoccerMainActivity.class));
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this, getResources().getString(R.string.NavigationDrawer)+message, Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     * This method is set up the sharedPreferences for star rating and comment
     */
    private void saveRanking() {
        SharedPreferences sp = getSharedPreferences("inputNum", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor=sp.edit();
        sharedPrefEditor.putString("ranking", rateStar.getRating()+"");
        sharedPrefEditor.putString("rankingScore", rateComment.getText().toString());
        sharedPrefEditor.commit();
    }

    /**
     * This method is to create the alertDialog box and get the data using XmlPullParser.
     * locate tags such as START_DOCUMENT, START_TAG, END_TAG, TEXT to dedicate data.
     */
    private void alertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View newView = getLayoutInflater().inflate(R.layout.soccer_rate, null);
        rateComment = newView.findViewById(R.id.soccerComment);
        rateStar = newView.findViewById(R.id.soccerRatingStar);

        alertDialogBuilder.setView(newView);
        sp = getSharedPreferences("inputNum", Context.MODE_PRIVATE);
        String savedRanking = sp.getString("rankingScore", "");
        String savedR = sp.getString("ranking", "0");
        rateComment.setText(savedRanking);
        rateStar.setRating(Float.parseFloat(savedR));
        alertDialogBuilder.setMessage(getResources().getString(R.string.soccerBoxMsg))
                .setPositiveButton(getResources().getString(R.string.soccerYes), (click, arg) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    Executor newThread = Executors.newSingleThreadExecutor();
                    newThread.execute(() -> {
                        try {
                            URL url = new URL("https://www.goal.com/en/feeds/news?fmt=rss");
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream response = urlConnection.getInputStream();
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            factory.setNamespaceAware(false);
                            XmlPullParser xpp = factory.newPullParser();
                            xpp.setInput(response, "UTF-8");
                            int eventType = xpp.getEventType();
                            SoccerNews news = null;
                            String flag = "";
                            runOnUiThread(()->{
                                progressBar.setProgress(50);
                            });

                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    if (xpp.getName().equals("item")) {
                                        news = new SoccerNews();
                                    } else if (xpp.getName().equals("title")) {
                                        flag = "title";
                                    } else if (xpp.getName().equals("pubDate")) {
                                        flag = "date";
                                    } else if (xpp.getName().equals("link")) {
                                        flag = "link";
                                    } else if (xpp.getName().equals("description")) {
                                        flag = "description";
                                    } else if (xpp.getName().equals("media:thumbnail")) {
                                        news.setSoccerImg(xpp.getAttributeValue(null, "url"));
                                    }
                                } else if (eventType == XmlPullParser.TEXT) {
                                    if (flag.equals("title") && news != null) {
                                        news.setSoccerTitle(xpp.getText());
                                        flag = "";
                                    } else if (flag.equals("date") && news != null) {
                                        news.setSoccerDate(xpp.getText());
                                        flag = "";
                                    } else if (flag.equals("link") && news != null) {
                                        news.setSoccerLink(xpp.getText());
                                        flag = "";
                                    } else if (flag.equals("description") && news != null) {
                                        news.setSoccerDescription(xpp.getText());
                                        flag = "";
                                    }
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    if (xpp.getName().equals("item")) {
                                        elements.add(news);
                                    }
                                }
                                eventType = xpp.next();
                            }

                            runOnUiThread(()->{

                                adt.notifyDataSetChanged();
                            });
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                    saveRanking();
                })
                .create().show();
    }

    /**
     * Use RecyclerView to hold the soccer news elements
     */
    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView titleText;
        public MyRowViews(View itemView) {
            super(itemView);
            titleText=itemView.findViewById(R.id.soccerTitle);

            itemView.setOnClickListener(c->{
                int position=getAbsoluteAdapterPosition();
                Bundle dataToPass = new Bundle();
                dataToPass.putString(SOCCER_IMG,elements.get(position).getSoccerImg());
                dataToPass.putString(SOCCER_TITLE,elements.get(position).getSoccerTitle());
                dataToPass.putString(SOCCER_DATE,elements.get(position).getSoccerDate());
                dataToPass.putString(SOCCER_DESC,elements.get(position).getSoccerDescription());
                dataToPass.putString(SOCCER_LINK,elements.get(position).getSoccerLink());
                if(isTablet)
                {
                    SoccerFragment soccerFragment = new SoccerFragment();
                    soccerFragment.setArguments( dataToPass );
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.soccerFragmentLocation, soccerFragment)
                            .commit();
                }
                else
                {
                    Intent nextActivity = new Intent(SoccerMainActivity.this, SoccerEmptyActivity.class);
                    nextActivity.putExtras(dataToPass);
                    startActivity(nextActivity);
                }
            });
        }

    }

    /**
     * This is the class to set up adapter
     */
    private class MyAdapter extends RecyclerView.Adapter<MyRowViews>{

        /**
         * This method is to get the view
         * @param parent viewGroup
         * @param viewType int
         * @return View newView
         */
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.soccer_item,parent,false);
            MyRowViews row=new MyRowViews(view);
            return row;
        }


        /**
         * This method is to show the view
         * @param holder show the elements view
         * @param position int
         */
        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(elements.get(position).getSoccerTitle());
        }

        /**
         * This method is to count the size of the elements
         * @return int elements.size()
         */
        @Override
        public int getItemCount() {
            return elements.size();
        }
    }
}