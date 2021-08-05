package algonquin.cst2335.cst2335project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Navigation extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        //toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView=findViewById(R.id.popout_menu);
        //close drawer when select an item
        navigationView.setNavigationItemSelectedListener((item)-> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.OCTransport:
                //open OCTransport activity
                Intent oct=new Intent(Navigation.this,OCTransport.class);
                startActivity(oct);
                break;
            case R.id.help:
                //help info displayed
                AlertDialog.Builder builder = new AlertDialog.Builder( Navigation.this );
                builder.setMessage("Enter four-digit stop number (such as 3017,3045,3050) and if it exists, a list of the route passing this stop will be displayed. " +
                        "Please refer to https://www.octranspo.com/en/plan-your-trip/travel-tools/bus-stop-number-list#560StationsOCT for " +
                        "the full list. Click each item can get detail info about the route. In some cases, the server gives no detail data, then the message that no data available will be displayed." +
                        "Long click the item in the list will delete it.");
                builder.setTitle("Help Information");
                builder.setNegativeButton("OK",(dialog,cl)->{
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
