package algonquin.cst2335.cst2335project;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CarMainActivity extends AppCompatActivity {
    RecyclerView stationListView;
    ArrayList<ChargingListItem> locationFound = new ArrayList<>();  //hold input location name
    chargingAdapter adt;
    SQLiteDatabase finderdb;
    finderOpenHelper opener;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charg_finder_search);

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
            drawer.closeDrawer(GravityCompat.START); // close the drawer
            return false;
        });


        Button finderBtn = findViewById(R.id.finderButton);
        EditText latitudeText = findViewById(R.id.latitudeTextField);
        EditText longitudeText = findViewById(R.id.longitudeTextField);
       stationListView = findViewById(R.id.chargingrecycler);
       stationListView.setLayoutManager(new LinearLayoutManager(this));




    }


   public class ChargingListItem {
        String locationTitle;
        double latitude;
        double longitude;
        Integer telNumber;
        Button mapBtn;

        public ChargingListItem(String locationTitle) {
            this.locationTitle=locationTitle;
        }
        public ChargingListItem(String locationTitle, double latitude, double longitude, int telNumber, Button mapBtn) {
            this.locationTitle = locationTitle;
            this.latitude = latitude;
            this.longitude=longitude;
            this.telNumber=telNumber;
            this.mapBtn = mapBtn;
        }
        public String getLocationTitle() {
            return locationTitle;
        }
        public void setLocationTitle(String locationTitle) {
            this.locationTitle= locationTitle;
        }
    }
    private class ChargRowViews extends RecyclerView.ViewHolder{
        TextView locationName;
        int position = -1;

        public ChargRowViews(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.loacationTitle);
            //get lacation detail info by click
            itemView.setOnClickListener(click->{
                int position = getAdapterPosition();
                userClickedMessage(locationFound.get(position));
            });
        }
    }

    private class chargingAdapter extends RecyclerView.Adapter<ChargRowViews>{

        @Override
        public ChargRowViews onCreateViewHolder( ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.charg_list_row,parent, false);
            ChargRowViews initRow = new ChargRowViews(loadedRow);
            return initRow;
        }

        @Override
        public void onBindViewHolder( ChargRowViews holder, int position) {
            holder.locationName.setText("");
            holder.locationName.setText("Location Title : " + locationFound.get(position).getLocationTitle());
        }

        @Override
        public int getItemCount() {
            return locationFound.size();
        }
    }

    public void userClickedMessage(ChargingListItem location){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CarMainActivity.this);
        dialog.setTitle("Getting data")
                .setMessage("Loading data...")
                .setView(new ProgressBar(CarMainActivity.this))
                .show();
    }






