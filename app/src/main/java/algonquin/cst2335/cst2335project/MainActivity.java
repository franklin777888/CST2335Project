package algonquin.cst2335.cst2335project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myMovieButton = findViewById(R.id.MovieButton);
        myMovieButton.setOnClickListener( clk->{
            Intent nextPage = new Intent(MainActivity.this, MovieMainActivity.class);
            startActivity(nextPage);
        });

        Log.w( "MainActivity", "In onCreate() - Loading Widgets" );
        Log.d( TAG, "Message");
    }
}