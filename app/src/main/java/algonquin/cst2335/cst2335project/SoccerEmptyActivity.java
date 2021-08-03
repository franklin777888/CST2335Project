package algonquin.cst2335.cst2335project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 *This class is to set up the empty activity for mobile
 */
public class SoccerEmptyActivity extends AppCompatActivity {
    /**
     * this method is to set up the onCreat function
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soccer_empty_activity);

        Bundle dataToPass = getIntent().getExtras();

        SoccerFragment soccerFragment = new SoccerFragment();
        soccerFragment.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.soccerFragmentLocation, soccerFragment)
                .commit();
    }
}

