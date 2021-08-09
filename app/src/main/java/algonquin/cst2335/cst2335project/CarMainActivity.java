package algonquin.cst2335.cst2335project;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * lab section: CST2335 022
 * This class is for main search page of the electric car charging station finder,
 * users input a latitude and longitude for search near by charging stations,
 * list of near by stations show on the screen.
 *
 */
public class CarMainActivity extends AppCompatActivity {
    //declarations for variables, call method and class
    ArrayList<ChargingInfo> locationFound = new ArrayList<>();  //hold input location name
    ChargingAdapter adapter=new ChargingAdapter();
    ChargOpenHelper opener;
    String stringURL;
    SharedPreferences prefs;
    public static final  String location_title = "LocationTitle";
    public static final String latitude_dt = "Latitude";
    public static final String longitude_dt = "Longitude";
    public static final String telNumber_dt= "TelNumber";

    /**
     * initialize toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only change the R.menu.file
        MenuInflater inflater=getMenuInflater();  //load the layout for the menu
        inflater.inflate(R.menu.charging_finder_activity_actions,menu);  //use this inflater object to load a Menu Layout file into the Menu object that is passed into the function
        return true;
    }
    /**
     * This function gets called by Android whenever the user clicks on a MenuItem.
     * The MenuItem parameter represents the item that was selected.
     * In this function, use a switch statement and check for different ids in the menu file
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
    TextView locationField = findViewById(R.id.loacationTitle);
    String message = null;
        switch (item.getItemId())
        {

            case R.id.goMovie:
                startActivity(new Intent(CarMainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                startActivity(new Intent(CarMainActivity.this, Navigation.class));
                break;
            case R.id.goCar:
                startActivity(new Intent(CarMainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                startActivity(new Intent(CarMainActivity.this, SoccerMainActivity.class));
                break;

            case R.id.favourites_sta:
                startActivity(new Intent(CarMainActivity.this, ChargFavActivey.class));
                break;

            case R.id.charging_help_menu:

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getResources().getString(R.string.finder_help))
                        .setMessage(getResources().getString(R.string.finder_help_menu))
                        .create().show();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * This function passes you a layoutInflater object.
     * @param savedInstanceState save instances in bundle
     */

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charg_finder_search);


        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        //getting a toolbar and drawerNavigationview
        Toolbar myToolbar = findViewById(R.id.cartoolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_Charg);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.charg_popout_menu);
        navigationView.setNavigationItemSelectedListener((item) -> {
            onOptionsItemSelected(item);  //call the function for the other toolbar
           // drawer.closeDrawer(GravityCompat.START); // close the drawer
            return false;
        });

        RecyclerView location = findViewById(R.id.chargingrecycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        location.setLayoutManager(layoutManager);
        location.setAdapter(adapter);
        EditText latitudeText = findViewById(R.id.latitudeTextField);
        EditText longitudeText = findViewById(R.id.longitudeTextField);


        SharedPreferences prefs = getSharedPreferences("MyData",Context.MODE_PRIVATE);
        String latitudeValue = prefs.getString("latitudeValue", "");
        latitudeText.setText(latitudeValue);

        String longitudeValue= prefs.getString("longitudeValue", "");
        longitudeText.setText(longitudeValue);

        //click finderBtn to search data from url
        Button finderBtn = findViewById(R.id.finderButton);
        finderBtn.setOnClickListener(clk -> {
            String latitudeNum = latitudeText.getText().toString();
            String longitudeNum = longitudeText.getText().toString();

            AlertDialog dialog = new AlertDialog.Builder(CarMainActivity.this)
                    .setTitle(R.string.loading_list)
                    .setMessage(R.string.loading)
                    .setView(new ProgressBar(CarMainActivity.this))
                    .show();

            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {

                // This runs on another thread
                try {
                    //connect to the server
                    stringURL = "https://api.openchargemap.io/v3/poi/?key=7e943c97096a9784391a981c4d878b22&output=xml&countrycode=CA&latitude="
                            + latitudeNum + "&longitude=" + longitudeNum
                            + "&maxresults=10";

                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  //use XML
                    factory.setNamespaceAware(false);  //ignore namespaces
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(in, "UTF-8");   //read from in, like scanner
                    String locationTitle = null;
                    String latitude = null;
                    String longitude = null;
                    String telNumber = null;
                    String field = "";
                    locationFound.clear();
                    while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                        switch (xpp.getEventType()) {
                            case XmlPullParser.START_TAG:
                                if (xpp.getName().equals("LocationTitle") ||
                                        xpp.getName().equals("Latitude") ||
                                        xpp.getName().equals("Longitude") ||
                                        xpp.getName().equals("ContactTelephone1")
                                ) {
                                    field = xpp.getName();
                                } else if (xpp.getName().equals("")) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), R.string.charging_station_not_found, Toast.LENGTH_LONG).show();
                                        dialog.hide();
                                    });

                                } else {
                                    field = "";
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (xpp.getName().equals("AddressInfo")) {
                                    ChargingInfo info = new ChargingInfo(locationTitle, latitude, longitude, telNumber);
                                    locationFound.add(info);
                                    locationTitle = "";
                                    latitude = "";
                                    longitude = "";
                                    telNumber = "";
                                }
                                break;
                            case XmlPullParser.TEXT:
                                if (field.equals("LocationTitle")) {
                                    locationTitle = xpp.getText();
                                } else if (field.equals("Latitude")) {
                                    latitude = xpp.getText();
                                } else if (field.equals("Longitude")) {
                                    longitude = xpp.getText();
                                } else if (field.equals("ContactTelephone1")) {
                                    telNumber = xpp.getText();
                                }
                                break;
                        }
                    }
                    runOnUiThread(() -> {
                        dialog.hide();
                        adapter.notifyDataSetChanged();
                    });
                } catch (IOException | XmlPullParserException ioe) {
                    Log.e("Connection error: ", ioe.getMessage());
                }

            });
            });
    }

    /**
     *this class that inherits from RecyclerView, which is used torepresent a location row
     */

    class ChargRowViews extends RecyclerView.ViewHolder {
        TextView locationName;
        int position = -1;

        /**
         * The view that is passed in as a parameter represents the ConstraintLayout that is the root of the row
         * @param itemView
         */
        public ChargRowViews(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationTitle);

            //get lacation detail info by click
            itemView.setOnClickListener(click -> {

                int position=getAbsoluteAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putString(location_title,locationFound.get(position).getLocationTitle());
                bundle.putString(latitude_dt,locationFound.get(position).getLatitude());
                bundle.putString(longitude_dt,locationFound.get(position).getLongitude());
                bundle.putString(telNumber_dt,locationFound.get(position).getTelNumber());

                Intent nextPage = new Intent(CarMainActivity.this, CarEmptyActivity.class);
                nextPage.putExtras(bundle);   //passing data from one activity to another activity
                startActivity(nextPage);
            });

        }

    }

    /**
     * This class is responsible for making a View for each station in the data set
     */
    class ChargingAdapter extends RecyclerView.Adapter<ChargRowViews> {

        /**
         * This function is responsible for creating a layout for a row, and setting the TextViews in code
         * @param parent parent is a parent view that can contain other views
         * @param viewType  the view type of the items
         * @return return the initiated object
         */
        @Override
        public ChargRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.charg_list_row, parent, false);
            ChargRowViews initRow = new ChargRowViews(loadedRow);
            return initRow;
        }

        /**
         * This function is call by RecycleView to display the data at the specified position.
         * @param holder holder of ChargRowViews
         * @param position the specified position of the station in data
         */
        @Override
        public void onBindViewHolder(ChargRowViews holder, int position) {
            holder.locationName.setText(getResources().getString(R.string.charg_sta) + locationFound.get(position).getLocationTitle());
        }

        /**
         * This function count the size of the locationFound array list
         * @return return the size of the Arroy of the location list
         */
        @Override
        public int getItemCount() {
            return locationFound.size();
        }


    }

    /**
     * This function keep the last input data.
     * It shows on the screen when reopen the app.
     */
    @Override
    protected void onPause() {
        super.onPause();
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        EditText latitudeText = findViewById(R.id.latitudeTextField);
        EditText longitudeText = findViewById(R.id.longitudeTextField);
        editor.putString("latitudeValue", latitudeText.getText().toString());
        editor.putString("longitudeValue", longitudeText.getText().toString());
        editor.apply();

    }
}