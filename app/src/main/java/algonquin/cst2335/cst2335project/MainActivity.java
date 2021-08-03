package algonquin.cst2335.cst2335project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button StationFinderButton = findViewById(R.id.StationFinderButton);
        StationFinderButton.setOnClickListener( clk->{
            Intent nextPage = new Intent(MainActivity.this, ChargingStationFinder.class);
            startActivity(nextPage);
        });

        Log.w( "MainActivity", "In onCreate() - Loading Widgets" );
        Log.d( TAG, "Message");
    }
}