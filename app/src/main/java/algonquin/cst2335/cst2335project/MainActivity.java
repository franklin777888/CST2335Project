package algonquin.cst2335.cst2335project;

        import androidx.appcompat.app.ActionBarDrawerToggle;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.core.view.GravityCompat;
        import androidx.drawerlayout.widget.DrawerLayout;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        ImageView soccerEntry = findViewById(R.id.soccerMain);
        soccerEntry.setOnClickListener( clk->{
            Intent nextPage = new Intent(MainActivity.this, SoccerMainActivity.class);
            startActivity(nextPage);
        });

        Log.w( "MainActivity", "In onCreate() - Loading Widgets" );
        Log.d( TAG, "Message");
    }

    /**
     * This method is used to load menu on toorlbar layout.
     * @param menu contents icons that will display on toolbar
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
            case R.id.goMovie:
                message = getResources().getString(R.string.moviePage);
                startActivity(new Intent(MainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                message = getResources().getString(R.string.busPage);
                startActivity(new Intent(MainActivity.this, Navigation.class));
                break;
            case R.id.goCar:
                message = getResources().getString(R.string.carPage);
                startActivity(new Intent(MainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                message = getResources().getString(R.string.soccerPage);
                startActivity(new Intent(MainActivity.this, SoccerMainActivity.class));
                break;
            case R.id.help:
                message = getResources().getString(R.string.helpPage);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.help))
                        .setMessage(getResources().getString(R.string.helpPage))
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
            case R.id.goMovie:
                message = getResources().getString(R.string.moviePage);
                startActivity(new Intent(MainActivity.this, MovieMainActivity.class));
                break;
            case R.id.goBus:
                message= getResources().getString(R.string.busPage);
                startActivity(new Intent(MainActivity.this, Navigation.class));
                break;
            case R.id.goCar:
                message= getResources().getString(R.string.carPage);
                startActivity(new Intent(MainActivity.this, CarMainActivity.class));
                break;
            case R.id.goSoccer:
                message= getResources().getString(R.string.soccerPage);
                startActivity(new Intent(MainActivity.this, SoccerMainActivity.class));
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this, getResources().getString(R.string.NavigationDrawer)+message, Toast.LENGTH_LONG).show();
        return false;
    }

}